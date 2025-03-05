<template>
	<view :data-theme="theme">
		<view class='nav acea-row row-around'>
			<view class='item' :class='orderStatus==-1 ? "on": ""' @click="statusClick(-1)">
				<view>售后申请</view>
			</view>
			<view class='item' :class='orderStatus==0 ? "on": ""' @click="statusClick(0)">
				<view>处理中</view>
			</view>
			<view class='item' :class='orderStatus==1 ? "on": ""' @click="statusClick(1)">
				<view>申请记录</view>
			</view>
		</view>
		<view class='return-list borderPad' v-if="orderList.length">
			<view class='goodWrapper borRadius14' v-for="(items,index) in orderList" :key="index">
				<view v-if="orderStatus ==-1" class='orderNum acea-row'>
					<text class="mr10 iconfont icon-shangjiadingdan"></text>
					<text class="no mr10">{{items.merName}}</text>
				</view>
				<view v-else class='orderNum acea-row'>
					<view class="tit mr10">{{$t(`page.users.userReturnList.orderId`)}}: </view>
					<view class="no">{{items.refundOrderNo}}</view>
				</view>
				<view @click='goOrderDetails(orderStatus ==-1?items.orderNo:items.refundOrderNo)' class='item acea-row row-between-wrapper'>
					<view class='pictrue'>
						<image :src='items.image'></image>
					</view>
					<view class='text'>
						<view class='name line2'>{{items.productName}}</view>
						<view class='acea-row row-between-wrapper'>
							<view class='num mr20'>{{orderStatus ==-1?'数量:'+items.payNum:'申请数量:'+items.applyRefundNum}}
							</view>
							<view v-show="orderStatus !==-1" class='attr font-color'>{{items.refundStatus===3?'已退款':'申请退款'}}：{{items.refundPrice}}</view>

						</view>
					</view>
					<view class="status">
						<!-- 0审核中 1审核未通过 2退款中 3已退款 -->
						<block v-if="items.refundStatus == 0">
							<text class="iconfont icon-shenhezhong1"></text>
						</block>
						<block v-if="items.refundStatus == 1">
							<text class="iconfont icon-yijujue"></text>
						</block>
						<block v-if="items.refundStatus == 2">
							<text class="iconfont icon-tuihuozhong"></text>
						</block>
						<block v-if="items.refundStatus == 3">
							<text class="iconfont icon-yituikuan font-color"></text>
						</block>
					</view>
				</view>
				<view v-if="orderStatus!==1" class="btn-box acea-row row-between">
					<view class="applyRefundNum"><text v-if="items.applyRefundNum>0">已申请退款数量：{{items.applyRefundNum}}</text></view>
					<view v-if="orderStatus==-1" class="btn bg-color" @click="goRefund(items)">申请售后</view>
				</view>
			</view>
		</view>
		<view class='loadingicon acea-row row-center-wrapper'>
			<text class='loading iconfont icon-jiazai'
				:hidden='loading==false'></text>{{orderList.length>0?loadTitle:''}}
		</view>
		<view class='noCart' v-if="orderList.length == 0 && !loading">
			<view class='pictrue text-center'>
				<image src='../static/images/noReturnOrder.png'></image>
				<view class="default_txt">暂无售后订单哦~</view>
			</view>
		</view>
	</view>
</template>

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
    import {
		orderAfterSaleList,
		orderRefundList
	} from '@/api/order.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import animationType from '@/utils/animationType.js'
	let app = getApp();
	export default {
		data() {
			return {
				loading: false,
				loadend: false,
				loadTitle: this.$t('page.goodsList.more'), //提示语
				orderList: [], //订单数组
				orderStatus: -1, //订单状态
				page: 1,
				limit: 20,
				theme: app.globalData.theme,
			};
		},
		computed: mapGetters(['isLogin']),
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						//this.getOrderAfterSaleList();
					}
				},
				deep: true
			}
		},
		onLoad() {
			uni.setNavigationBarTitle({
				title: this.$t(`page.users.userReturnList.navTitle`)
			})
			if (this.isLogin) {
				this.getOrderAfterSaleList();
			} else {
				toLogin();
			}
		},
		/**
		 * 页面上拉触底事件的处理函数
		 */
		onReachBottom: function() {
			this.getOrderAfterSaleList();
		},
		methods: {
			/**
			 * 退款申请
			 */
			goRefund(item) {
				uni.navigateTo({
					url: `/pages/goods/goods_return/index?orderNo=${item.orderNo}&orderId=${item.id}`
				})
				this.$Cache.set('productInfo', item)
			},
			statusClick(status) {
				this.orderStatus = status;
				this.loadend = false;
				this.loading = false;
				this.page = 1;
				this.orderList = [];
				if (status === -1) {
					this.getOrderAfterSaleList();
				} else {
					this.getOrderRefundList();
				}
			},
			/**
			 * 去订单详情
			 */
			goOrderDetails: function(order_id) {
				if (!order_id) return this.$util.Tips({
					title: this.$t('message.pay.errorOrder')
				});
				let url;
				if(this.orderStatus==-1){
					url = '/pages/goods/order_details/index?orderNo=' + order_id
				}else{
					url = '/pages/goods/refund_details/index?orderNo=' + order_id
				}
				// #ifdef MP
				uni.navigateTo({
					url: url
				})
				// #endif
				// #ifndef MP
				uni.navigateTo({
					animationType: animationType.type,
					animationDuration: animationType.duration,
					url: url
				})
				// #endif
			},

			/**
			 * 获取售后申请列表
			 */
			getOrderAfterSaleList: function() {
				let that = this;
				if (that.loadend) return;
				if (that.loading) return;
				that.loading = true;
				that.loadTitle = "";
				orderAfterSaleList({
					page: that.page,
					limit: that.limit,
				}).then(res => {
					let list = res.data.list || [];
					let loadend = list.length < that.limit;
					that.orderList = that.$util.SplitArray(list, that.orderList);
					that.$set(that, 'orderList', that.orderList);
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? this.$t('page.goodsList.nono') : this.$t('page.goodsList.more');
					that.page = that.page + 1;
				}).catch(err => {
					that.loading = false;
					that.loadTitle = this.$t(`page.goodsList.more`);
				});
			},
			/**
			 * 获取退款列表
			 */
			getOrderRefundList: function() {
				let that = this;
				if (that.loadend) return;
				if (that.loading) return;
				that.loading = true;
				that.loadTitle = "";
				orderRefundList({
					page: that.page,
					limit: that.limit,
					type: that.orderStatus
				}).then(res => {
					let list = res.data.list || [];
					let loadend = list.length < that.limit;
					that.orderList = that.$util.SplitArray(list, that.orderList);
					that.$set(that, 'orderList', that.orderList);
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? this.$t('page.goodsList.nono') : this.$t('page.goodsList.more');
					that.page = that.page + 1;
				}).catch(err => {
					that.loading = false;
					that.loadTitle = this.$t(`page.goodsList.more`);
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.applyRefundNum {
	    font-size: 10px;
	    color: #666;
	}
	.status {
		position: absolute;
		right: 15rpx;
		top: 0;

		.iconfont {
			font-size: 120rpx;
			opacity: .3;
		}
	}

	.btn-box {
		display: flex;
		padding: 20rpx 0;
		line-height: 60rpx;

		.btn {
			width: 176rpx;
			height: 60rpx;
			line-height: 60rpx;
			margin-left: 18rpx;
			text-align: center;
			border-radius: 30rpx;
			color: #fff;
			font-size: 28rpx;

			&.gray {
				border: 1px solid #ddd;
				background: transparent;
				color: #aaa;
			}
		}
	}

	.nav {
		background-color: #fff;
		width: 100%;
		height: 90rpx;
		line-height: 88rpx;

		.item {
			text-align: center;
			font-size: 30rpx;
			color: #282828;

			.num {
				margin-top: 18rpx;
			}
		}

		.on {
			@include main_color(theme);
			font-weight: bold;
			@include tab_border_bottom(theme);
		}
	}

	.mr8 {
		margin-right: 8rpx;
	}

	.return-list .goodWrapper {
		background-color: #fff;
		margin-top: 20rpx;
		position: relative;
		padding: 0rpx 24rpx;
	}

	.return-list .goodWrapper .orderNum {
		border-bottom: 1px solid #eee;
		height: 86rpx;
		line-height: 86rpx;

		.tit {
			font-size: 22rpx;
			color: #999999;
		}

		.no {
			font-size: 30rpx;
			color: #333333;
		}
	}

	.return-list .goodWrapper .item {
		border-bottom: 0;
		padding: 25rpx 0;

		.name {
			height: 78rpx !important;
			line-height: 40rpx;
			margin-bottom: 14rpx;
		}

		.money {
			color: #999999;
			font-weight: 26rpx;
			margin-top: 0;
		}

		.attr,
		.num {
			font-size: 20rpx;
			color: #999999;
			margin-top: 0;
		}
	}

	.return-list .goodWrapper .totalSum {
		padding: 0 0 32rpx 0;
		// text-align: right;
		font-size: 26rpx;
		color: #282828;
		display: flex;
		justify-content: space-between;
	}

	.return-list .goodWrapper .totalSum .price {
		font-size: 28rpx;
		font-weight: bold;
		@include price_color(theme);
	}

	.hui {
		color: #CCCCCC;
		font-size: 24rpx;
	}

	.return-list .goodWrapper .powder {
		@include price_color(theme);
		//font-size: 24rpx;
	}

	.align-center {
		margin-top: 32rpx;
	}

	.noCart {
		margin-top: 50%;
		.pictrue image {
			width: 410rpx !important;
			height: 334rpx !important;
		}
	}
	.default_txt {
		font-size: 26rpx;
		color: #999;
		text-align: center;
	}
</style>
