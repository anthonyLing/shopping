<template>
	<view :data-theme="theme">
		<view class='bill-details'>
			<view class='nav acea-row'>
				<view class='item' :class='type==="all" ? "on":""' @click='changeType("all")'>全部</view>
				<view class='item' :class='type==="expenditure" ? "on":""' @click='changeType("expenditure")'>支出</view>
				<view class='item' :class='type==="recharge" ? "on":""' @click='changeType("recharge")'>充值</view>
				<view class='item' :class='type==="income" ? "on":""' @click='changeType("income")'>收入</view>
			</view>
			<view class='sign-record'>
				<view class='list borderPad' v-for="(item,index) in userBillList" :key="index">
					<view class='item'>
						<view class='data'>{{item.month}}</view>
						<view class='listn borRadius14'>
							<view class="itemn" v-for="(vo,indexn) in item.list":key="indexn">
								<view class='acea-row row-between-wrapper'>
									<view>
										<view class='name line1'>{{vo.balance}}</view>
										<view>{{vo.createTime}}</view>
									</view>
									<view class='num font_color' v-if="vo.type == 1">+{{vo.amount}}</view>
									<view class='num' v-else>-{{vo.amount}}</view>
								</view>
								<view class="remark">说明：{{vo.remark}}</view>
							</view>
						</view>
					</view>
				</view>
				<view class='loadingicon acea-row row-center-wrapper'>
					<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{userBillList.length > 0?loadTitle:''}}
				</view>
				<view v-if="userBillList.length == 0 && !loading">
					<emptyPage title="暂无账单的记录哦～">
						<div slot='emptysrc'>
							<image src="../static/images/noMoney.png"></image>
						</div>
					</emptyPage>
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
		getBillList
	} from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import emptyPage from '@/components/emptyPage.vue';
	let app = getApp();
	export default {
		components: {
			emptyPage
		},
		data() {
			return {
				loadTitle: '加载更多',
				loading: true,
				loadend: false,
				page: 1,
				limit: 12,
				type: 'all',
				userBillList: [],
				theme:app.globalData.theme,
			};
		},
		computed: mapGetters(['isLogin']),
		onShow() {
			if (this.isLogin) {
				this.getUserBillList();
			} else {
				toLogin();
			}
		},
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			this.type = options.type;
		},
		/**
		 * 页面上拉触底事件的处理函数
		 */
		onReachBottom: function() {
			this.getUserBillList();
		},
		methods: {
			/**
			 * 获取账户明细
			 */
			getUserBillList: function() {
				let that = this;
				if (that.loadend) return;
				that.loading = true;
				that.loadTitle = "";
				let data = {
					page: that.page,
					limit: that.limit,
					type: that.type
				}
				getBillList(data).then(function(res) {
					let list = res.data.list ? res.data.list : [],
					loadend = res.data.totalPage <= that.page;
					for (let i = 0; i < list.length; i++) {
						let time1 = list[i].month;
						let array1 = list[i].list;
						let isEquals = false;
						for (let j = 0; j < that.userBillList.length; j++) {
							let time2 = that.userBillList[j].month;
							let array2 = that.userBillList[j].list;
							if (time1 == time2) {
								array2.push.apply(array2, array1);
								that.userBillList[j].list = array2;
								isEquals = true;
								break;
							}
						}
						if (!isEquals) {
							that.userBillList.push({
								month: time1,
								list: array1
							})
						}
					}
                    that.$set(that, 'userBillList', that.userBillList);
					that.page += 1;
					that.loadend = loadend;
					that.loadTitle = loadend ? "哼😕~我也是有底线的~" : "加载更多";
					that.loading = false;
				}, function(res) {
					that.loadTitle = '加载更多';
					that.loading = false;
				});
			},
			/**
			 * 切换导航
			 */
			changeType: function(type) {
				this.type = type;
				this.loadend = false;
				this.page = 1;
				this.$set(this, 'userBillList', []);
				this.getUserBillList();
			},
		}
	}
</script>

<style scoped lang='scss'>
	.remark{
		font-size: 24rpx;
		margin-top: 10rpx;
	}
	.bill-details .nav {
		background-color: #fff;
		height: 90rpx;
		width: 100%;
		line-height: 90rpx;
	}
	.bill-details .nav .item {
		flex: 1;
		text-align: center;
		font-size: 30rpx;
		color: #282828;
	}

	.bill-details .nav .item.on {
		@include main_color(theme);
		@include tab_border_bottom(theme);
	}
	.font_color{
		color: #E93323 !important;
	}
</style>
