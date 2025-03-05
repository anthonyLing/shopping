<template>
	<view :data-theme="theme">
		<!-- #ifndef APP-PLUS || MP -->
		<nav-bar :navTitle='navTitle' @getNavH='getNavH'></nav-bar>
		<!-- #endif -->
		<view class="mp-bg">
			<scroll-view class="scroll-view_x" scroll-x style="white-space: nowrap; vertical-align: middle;"
				show-scrollbar="false">
				<view class='nav borRadius14 acea-row'>
					<text class='item' :class='orderStatus==-1 ? "on": ""' @click="statusClick(-1)">
						<text>全部</text>
					</text>
					<text class='item' :class='orderStatus==0 ? "on": ""' @click="statusClick(0)">
						<text>{{$t(`page.users.orderList.orderStatus[0].name`)}}</text>
					</text>
					<text class='item' :class='orderStatus==1 ? "on": ""' @click="statusClick(1)">
						<text>{{$t(`page.users.orderList.orderStatus[1].name`)}}</text>
					</text>
					<text class='item' :class='orderStatus==3 ? "on": ""' @click="statusClick(3)">
						<text>待核销</text>
					</text>
					<text class='item' :class='orderStatus==4 ? "on": ""' @click="statusClick(4)">
						<text>{{$t(`page.users.orderList.orderStatus[2].name`)}}</text>
					</text>
					<text class='item' :class='orderStatus==9 ? "on": ""' @click="statusClick(9)">
						<text>已取消</text>
					</text>
				</view>
			</scroll-view>
		</view>
		<view class='my-order' :style="'margin-top:'+(marTop)+'rpx;'">
			<view class='list'>
				<view class='item' v-for="(item,index) in orderList" :key="index">
					<view class='title acea-row row-between-wrapper'>
						<view class="acea-row row-middle">
							<view v-if="item.merName">
								<navigator :url="`/pages/merchant/home/index?id=${item.merId}`" hover-class="none">
									<text class='iconfont icon-shangjiadingdan mr10'></text>
									<text class="mr10">{{item.merName}}</text>
									<text class='iconfont icon-xiangyou'></text>
								</navigator>
							</view>
							<view v-else>
								<text class="mr10">{{item.orderNo}}</text>
							</view>
						</view>
						<view class='font_color'>{{item.status | orderStatusFilter}}</view>
					</view>
					<view @click='goOrderDetails(item.orderNo)' class='item-info acea-row row-between row-top'
						v-for="(items,indexs) in item.orderInfoList" :key="indexs">
						<view class='pictrue'>
							<easy-loadimage mode="widthFix" :image-src="items.image"></easy-loadimage>
						</view>
						<view class='text acea-row row-between'>
							<view class="nameBox">
								<view class='name line2'>
									<span v-if="Number(item.type)>0" class="activity bg_color">{{item.type | orderTypeFilter}}</span>
									<span>{{items.productName}}</span>
								</view>
								<view class="sku line1">{{items.sku}}</view>
							</view>
							<view class='money'>
								<view>￥{{items.price}}</view>
								<view>x{{items.payNum}}</view>
							</view>
						</view>
					</view>
					<view class='totalPrice'>{{item.totalNum}}
						{{$t(`page.users.orderList.item`)}}，{{$t(`page.users.orderList.totalPay`)}}
						<text class='money'> ￥{{item.payPrice}}</text>
					</view>
					<!-- 订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消） -->
					<view class='bottom acea-row row-right row-middle'>
						<view class='bnt cancelBnt' v-if="item.status === 0" @click='cancelOrder(index,item.orderNo)'>
							{{$t(`page.users.orderList.cancelOrder`)}}
						</view>
						<view class='bnt bg_color' v-if="item.status === 0" @click='goPay(item)'>
							{{$t(`page.users.orderList.pay`)}}
						</view>
						<view class='bnt bg_color' v-if="item.paid || item.status == 9"
							@click='goOrderDetails(item.orderNo)'>{{$t(`page.users.orderList.viewDetails`)}}</view>
						<view class='bnt cancelBnt' v-if="item.status==6 || item.status==9"
							@click='delOrder(item.orderNo,index)'>
							{{$t(`page.users.orderList.delete`)}}
						</view>
					</view>

				</view>
			</view>

			<view class='loadingicon acea-row row-center-wrapper'>
				<text class='loading iconfont icon-jiazai'
					:hidden='loading==false'></text>{{orderList.length>0?loadTitle:''}}
			</view>
			<view class='noCart' v-if="orderList.length == 0 && isShow && !loading">
				<view class='pictrue'>
					<image :src="urlDomain+'crmebimage/presets/noOrdernow.png'"></image>
					<view class="default_txt">暂无订单信息~</view>
				</view>
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
		Debounce
	} from '@/utils/validate.js'
	import {
		getOrderList,
		orderCancel,
		orderDel
	} from '@/api/order.js';
	import {
		openOrderSubscribe
	} from '@/utils/SubscribeMessage.js';
	import navBar from '@/components/navBar';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import animationType from '@/utils/animationType.js';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	const app = getApp();
	export default {
		components: {
			navBar,
			easyLoadimage
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				orderData: {},
				navTitle: '我的订单',
				loading: false, //是否加载中
				loadend: false, //是否加载完毕
				loadTitle: '加载更多', //提示语
				orderList: [], //订单数组
				orderStatus: -1, //订单状态
				page: 1,
				limit: 20,
				pay_order_id: '',
				totalPrice: '0',
				isShow: false,
				theme: app.globalData.theme,
				bgColor: '#e93323',
				marTop: 0
			};
		},
		computed: mapGetters(['isLogin', 'userInfo']),
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		},
		onShow() {
			let that = this;
			that.bgColor = setThemeColor();
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor: that.bgColor,
			});
			if (this.isLogin) {
				this.loadend = false;
				this.page = 1;
				this.$set(this, 'orderList', []);
				this.getOrderList();
			} else {
				toLogin();
			}
		},
		methods: {
			getNavH(marTop) {
				this.marTop = marTop;
			},
			/**
			 * 生命周期函数--监听页面加载
			 */
			onLoad: function(options) {
				if (options.status) this.orderStatus = options.status;
			},
			/**
			 * 取消订单
			 *
			 */
			cancelOrder: function(index, orderNo) {
				let that = this;
				if (!orderNo) return that.$util.Tips({
					title: '缺少订单号无法取消订单'
				});
				uni.showModal({
					content: '确定取消该订单',
					cancelText: "取消",
					confirmText: "确定",
					showCancel: true,
					confirmColor: '#f55850',
					success: (res) => {
						if (res.confirm) {
							uni.showLoading({
								title: '正在取消中'
							});
							orderCancel(orderNo).then(res => {
								uni.hideLoading();
								return that.$util.Tips({
									title: '取消成功',
									icon: 'success'
								}, function() {
									that.orderList.splice(index, 1);
									that.$set(that, 'orderList', that.orderList);
									that.$set(that.orderData, 'unpaid_count', that
										.orderData.unpaid_count - 1);
									that.getOrderData();
								});
							}).catch(err => {
								return that.$util.Tips({
									title: err
								});
							});
						} else {

						}
					},
				})
			},
			/**
			 * 打开支付组件
			 *
			 */
			goPay: Debounce(function(item) {
				uni.showLoading({
					title: this.$t(`message.tips.loding`)
				});

				uni.navigateTo({
					url: `/pages/goods/order_payment/index?orderNo=${item.orderNo}&payPrice=${item.payPrice}`
				});
			}),
			/**
			 * 去订单详情
			 */
			goOrderDetails: function(orderNo, status) {
				if (!orderNo) return that.$util.Tips({
					title: '缺少订单号无法查看订单详情'
				});
				// #ifdef MP
				uni.navigateTo({
					url: '/pages/goods/order_details/index?orderNo=' + orderNo
				})
				// #endif
				// #ifndef MP
				uni.navigateTo({
					animationType: animationType.type,
					animationDuration: animationType.duration,
					url: '/pages/goods/order_details/index?orderNo=' + orderNo
				})
				// #endif
			},
			/**
			 * 切换类型
			 */
			statusClick: function(status) {
				if (status == this.orderStatus) return;
				this.orderStatus = status;
				this.loadend = false;
				this.page = 1;
				this.$set(this, 'orderList', []);
				this.getOrderList();
			},
			/**
			 * 获取订单列表
			 */
			getOrderList: function() {
				let that = this;
				if (that.loadend) return;
				if (that.loading) return;
				that.loading = true;
				that.loadTitle = "加载更多";
				getOrderList({
					status: that.orderStatus,
					page: that.page,
					limit: that.limit,
				}).then(res => {
					let list = res.data.list || [];
					let loadend = list.length < that.limit;
					that.orderList = that.$util.SplitArray(list, that.orderList);
					that.$set(that, 'orderList', that.orderList);
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? "我也是有底线的" : '加载更多';
					that.page = that.page + 1;
					that.isShow = true;
				}).catch(err => {
					that.loading = false;
					that.loadTitle = "加载更多";
				})
			},

			/**
			 * 删除订单
			 */
			delOrder: function(orderNo, index) {
				uni.showModal({
					content: '确定删除该订单',
					cancelText: "取消",
					confirmText: "确定",
					showCancel: true,
					confirmColor: '#f55850',
					success: (res) => {
						if (res.confirm) {
							let that = this;
							orderDel(orderNo).then(res => {
								that.orderList.splice(index, 1);
								that.$set(that, 'orderList', that.orderList);
								that.$set(that.orderData, 'unpaid_count', that.orderData
									.unpaid_count - 1);
								that.getOrderData();
								return that.$util.Tips({
									title: '删除成功',
									icon: 'success'
								});
							}).catch(err => {
								return that.$util.Tips({
									title: err
								});
							})
						} else {

						}
					},
				})
			},
		},
		onReachBottom: function() {
			this.getOrderList();
		}
	}
</script>
s
<style scoped lang="scss">
	.activity {
		width: 60rpx;
		height: 30rpx;
		border-radius: 4px 4px 4px 4px;
		opacity: 1;
		display: inline-block;
		color: #fff;
		margin-right: 8rpx;
		line-height: 30rpx;
		text-align: center;
		font-size: 20rpx;
	}

	.mp-bg {
		top: 0;
		position: fixed;
		left: 0;
		/* #ifdef H5 */
		top: 74rpx;
		/* #endif */
		width: 100%;
		height: 120rpx;
		@include index-gradient(theme);
		padding: 30rpx 30rpx 0 30rpx;
		z-index: 99;
	}

	.my-order {
		/* #ifdef H5 */
		margin-top: 214rpx !important;
		/* #endif */
		/* #ifdef MP || APP-PLUS */
		margin-top: 140rpx !important;

		/* #endif */
		.header {
			height: 250rpx;
			padding: 0 30rpx;
		}
	}

	.bg_color {
		@include main_bg_color(theme);
	}

	.my-order .header .picTxt {
		height: 190rpx;
	}

	.my-order .header .picTxt .text {
		color: rgba(255, 255, 255, 0.8);
		font-size: 26rpx;
		font-family: 'Guildford Pro';
	}

	.my-order .header .picTxt .text .name {
		font-size: 34rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 20rpx;
	}

	.my-order .header .picTxt .pictrue {
		width: 120rpx;
		height: 120rpx;
	}

	.my-order .header .picTxt .pictrue image {
		width: 100%;
		height: 100%;
	}

	.nav {
		background-color: #fff;
		padding: 0 50rpx;
		justify-content: space-between;
	}

	.nav .item {
		font-size: 26rpx;
		color: #282828;
		padding: 25rpx 0;
		display: inline-block;

	}

	.nav .item.on {
		/* #ifdef H5 || MP */
		font-weight: bold;
		/* #endif */
		/* #ifdef APP-PLUS */
		color: #000;
		/* #endif */
		position: relative;
		@include tab_border_bottom(theme);
	}

	// .nav .item.on ::after {
	// 	content: '';
	// 	width: 78rpx;
	// 	height: 4rpx;
	// 	@include main_bg_color(theme);
	// 	position: absolute;
	// 	bottom: 2rpx;
	// 	left: 0;
	// }

	.nav .item .num {
		margin-top: 18rpx;
	}

	.my-order .list {
		width: 690rpx;
		margin: 14rpx auto 0 auto;
	}

	.my-order .list .item {
		background-color: #fff;
		border-radius: 14rpx;
		margin-bottom: 14rpx;
	}

	.my-order .list .item .title {
		height: 84rpx;
		padding: 0 24rpx;
		border-bottom: 1rpx solid #eee;
		font-size: 28rpx;
		color: #282828;
	}

	.my-order .list .item .title .sign {
		font-size: 24rpx;
		padding: 0 13rpx;
		height: 36rpx;
		margin-right: 15rpx;
		border-radius: 18rpx;
		@include coupons_border_color(theme);
		@include main_color(theme);
	}

	.my-order .list .item .item-info {
		padding: 0 30rpx;
		margin-top: 22rpx;
	}

	.my-order .list .item .item-info .pictrue {
		width: 120rpx;
		height: 120rpx;
	}

	.my-order .list .item .item-info .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
		overflow: hidden;
	}

	.my-order .list .item .item-info {
		.nameBox {
			position: relative;
			height: 120rpx;
		}

		.sku {
			font-size: 20rpx;
			position: absolute;
			bottom: 4rpx;
			width: 420rpx;
		}

		.text {
			width: 496rpx;
			font-size: 28rpx;
			color: #999;
		}
	}

	.my-order .list .item .item-info .text .name {
		width: 326rpx;
		color: #282828;
	}

	.my-order .list .item .item-info .text .money {
		text-align: right;
	}

	.font_color {
		@include main_color(theme);
	}

	.my-order .list .item .totalPrice {
		font-size: 26rpx;
		color: #282828;
		text-align: right;
		margin: 27rpx 0 0 30rpx;
		padding: 0 30rpx 30rpx 0;
		border-bottom: 1rpx solid #eee;
	}

	.my-order .list .item .totalPrice .money {
		font-size: 28rpx;
		font-weight: bold;
		@include price_color(theme);
	}

	.my-order .list .item .bottom {
		height: 107rpx;
		padding: 0 30rpx;
	}

	.my-order .list .item .bottom .bnt {
		width: 176rpx;
		height: 60rpx;
		text-align: center;
		line-height: 60rpx;
		color: #fff;
		border-radius: 50rpx;
		font-size: 27rpx;
	}

	.my-order .list .item .bottom .bnt.cancelBnt {
		border: 1rpx solid #ddd;
		color: #aaa;
	}

	.my-order .list .item .bottom .bnt~.bnt {
		margin-left: 17rpx;
	}

	.noCart {
		margin-top: 171rpx;
		padding-top: 0.1rpx;
	}

	.noCart .pictrue {
		width: 414rpx;
		height: 336rpx;
		margin: 78rpx auto 56rpx auto;
	}

	.noCart .pictrue image {
		width: 100%;
		height: 100%;
	}

	.icon-xiangyou {
		font-size: 24rpx;
	}

	.default_txt {
		font-size: 26rpx;
		color: #999;
		text-align: center;
	}
</style>
