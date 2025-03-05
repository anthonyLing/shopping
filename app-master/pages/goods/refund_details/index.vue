<template>
	<view :data-theme="theme">
		<view class='order-details'>
			<!-- 给header上与data上加on为退款订单-->
			<view class='header bg_color on'>
				<view class='picTxt acea-row row-middle'>
					<view class='data on'>
					    <view class="acea-row row-middle">
							<view class='state'>{{orderInfo.refundStatus | refundStatusFilter}}</view>
							<view v-show="orderInfo.refundStatus===3">，<text class="font-color refundPrice">{{orderInfo.refundPrice}}元</text></view>
						</view>
						<view v-if="orderInfo.refundReasonTime !== null">{{orderInfo.refundReasonTime}}</view>
						<view v-if="orderInfo.createTime">{{orderInfo.createTime}}</view>
					</view>
				</view>
			</view>

			<view class="borderPad">
				<!-- 退款状态头部信息 "-->
				<view class='nav refund'>
					<view class="title">
						<image src="../static/images/shuoming.png" mode=""></image>
						{{orderInfo.refundStatus==0?$t(`page.users.userReturnList.examine`): orderInfo.refundStatus==1?$t(`page.users.userReturnList.refuse`):orderInfo.refundStatus==2?$t(`page.users.userReturnList.refunding`):$t(`page.users.userReturnList.refunded`)}}
					</view>
					<view class="con borderPad">
					    {{orderInfo.refundStatus==0 ? $t(`page.users.userReturnList.examineInfo`): orderInfo.refundStatus==1? $t(`page.users.userReturnList.refuseReason`) + orderInfo.refundReason : $t(`page.users.userReturnList.refuseReason2`)}}
					</view>
				</view>
			</view>
			<view class='wrapper-head borRadius-top'>
				<view class='item'>退款信息</view>
			</view>
			<view class="borderPad">
				<view class='wrapper-info borRadius-bottom item flex justify-between'>
					<view class='pictrue'>
						<image :src='orderInfo.image'></image>
					</view>
					<view class='text'>
						<view class='flex justify-between'>
							<view class='name line1'>{{orderInfo.productName}}</view>
							<view class='num'>x {{orderInfo.applyRefundNum}}</view>
						</view>
						<view class='attr' v-if="orderInfo.sku">{{orderInfo.sku}}</view>
						<view class='acea-row row-between-wrapper'>
							<view class='price mr20'>￥{{orderInfo.refundPrice}}
							</view>
						</view>
					</view>
				</view>
			</view>
            <!-- 订单信息 "-->
			<view class="borderPad">
				<!-- 退款订单详情 "-->
				<view v-if="orderInfo.refundGainIntegral || orderInfo.refundUseIntegral" class='wrapper borRadius14'>
					<view class='item acea-row row-between' v-if="orderInfo.refundGainIntegral">
						<view>扣除赠送积分：</view>
						<view class='conter'>{{orderInfo.refundGainIntegral}}</view>
					</view>
					<view class='item acea-row row-between' v-if="orderInfo.refundUseIntegral">
						<view>退还使用积分：</view>
						<view class='conter'>{{orderInfo.refundUseIntegral}}</view>
					</view>
				</view>
				<view class='wrapper borRadius14'>
					<view class='item acea-row row-between'>
						<view>退款编号：</view>
						<view class='conter acea-row row-middle row-right'><text
								class="text-overflow">{{orderInfo.refundOrderNo}}</text>
							<!-- #ifndef H5 -->
							<text class='copy' @tap='copy'>复制</text>
							<!-- #endif -->
							<!-- #ifdef H5 -->
							<text class='copy copy-data' :data-clipboard-text="orderInfo.refundOrderNo">复制</text>
							<!-- #endif -->
						</view>
					</view>
					<view class='item acea-row row-between'>
						<view>申请时间：</view>
						<view class='conter'>{{(orderInfo.createTime || 0)}}</view>
					</view>
					<view class='item acea-row row-between'>
						<view>退回方式：</view>
						<view class='conter'>原支付返回</view>
					</view>
					<view v-if="orderInfo.paid" class='item acea-row row-between'>
						<view>支付状态：</view>
						<view class='conter' v-if="orderInfo.paid">已支付</view>
						<view class='conter' v-else>未支付</view>
					</view>
					<view v-if="orderInfo.payType" class='item acea-row row-between'>
						<view>支付方式：</view>
						<view class='conter'>{{orderInfo.payType | payTypeFilter}}</view>
					</view>
				</view>
				<!-- 退款订单详情 "-->
				<view class='wrapper borRadius14'>
					<view class='item flex justify-between' v-if="orderInfo.refundReasonWap">
						<view>退款原因：</view>
						<view class='conter-refund'>{{orderInfo.refundReasonWap}}</view>
					</view>
					<view class='item acea-row row-between' v-if="orderInfo.refundReasonWapExplain">
						<view>退款说明：</view>
						<view class='conter-refund text-left'>{{orderInfo.refundReasonWapExplain}}</view>
					</view>
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
		getOrderDetail,
		orderTake,
		orderDel,
		orderCancel,
		orderRefundInfo
	} from '@/api/order.js';
	import orderGoods from "../components/orderGoods";
	import ClipboardJS from "@/plugin/clipboard/clipboard.js";
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js'
	// #endif
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import {
		Debounce
	} from '@/utils/validate.js'
	const app = getApp();
	export default {
		components: {
			orderGoods
		},
		data() {
			return {
				orderNo: '',
				orderInfo: {}, //订单详情
				theme: app.globalData.theme,
				chatConfig: {
					consumer_hotline: '',
					telephone_service_switch: 'false'
				}, //客服配置
			};
		},
		computed: mapGetters(['isLogin', 'chatUrl', 'userInfo']),
		onLoad: function(options) {
			options.type == undefined || options.type == null ? this.type = 'normal' : this.type = options.type;
			if (!options.orderNo) return this.$util.Tips({
				title: '缺少参数'
			}, {
				tab: 3,
				url: 1
			});
			this.$set(this, 'orderNo', options.orderNo);
			this.bgColor = setThemeColor();
			this.$set(this, 'chatConfig', this.$Cache.getItem('chatConfig'));
		},
		onShow() {
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor: "#666",
			});
			if (this.isLogin) {
				this.orderRefundInfo();
			} else {
				toLogin();
			}
		},
		onHide: function() {
			this.isClose = true;
		},
		// 滚动监听
		onPageScroll(e) {
		   // 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
		   uni.$emit('scroll');
		},
		onReady: function() {
			// #ifdef H5
			this.$nextTick(function() {
				const clipboard = new ClipboardJS(".copy-data");
				clipboard.on("success", () => {
					this.$util.Tips({
						title: '复制成功'
					});
				});
			});
			// #endif

		},
		methods: {
			/**
			 * 退款申请
			 */
			goRefund(){
				uni.navigateTo({
					url: '/pages/goods/goods_return/index?orderId='+ this.orderNo
				})
				if(this.orderInfo.status !==0 ||this.orderInfo.status !==9){
					this.$Cache.set('productInfo',this.orderInfo.merchantOrderList[0].orderInfoList)
				}
			},
			kefuClick() {
				if (this.chatConfig.telephone_service_switch === 'true') {
					uni.makePhoneCall({
						phoneNumber: this.chatConfig.consumer_hotline //仅为示例
					});
				} else {
					// #ifdef APP-PLUS
					uni.navigateTo({
						url: '/pages/users/web_page/index?webUel=' + this.chatUrl + '&title=客服'
					})
					// #endif
					// #ifndef APP-PLUS
					location.href = this.chatUrl;
					// #endif
				}
			},
			/**
			 * 拨打电话
			 */
			makePhone: function(e) {
				uni.makePhoneCall({
					phoneNumber: e
				})
			},
			/**
			 * 获取退款订单详细信息
			 *
			 */
			orderRefundInfo(){
				let that = this;
				uni.showLoading({
					title: "正在加载中"
				});
				orderRefundInfo(that.orderNo).then(res => {
					uni.hideLoading();
					let data = res.data;
					that.$set(that, 'orderInfo', data);
				}).catch(err => {
					that.$util.Tips({
						title: err
					}, {
						tab: 4,
						url: '/pages/user/index'
					});
				});
			},
			/**
			 * 获取订单详细信息
			 *
			 */
			getOrderInfo: function() {
				let that = this;
				uni.showLoading({
					title: "正在加载中"
				});
				getOrderDetail(that.orderNo).then(res => {
					uni.hideLoading();
					let data = res.data;
					that.$set(that, 'orderInfo', data);


					that.$set(that, 'system_store', data.systemStore);
					that.$set(that, 'id', data.id);
					that.userAddress = data.merchantOrderList.filter(item => {
						return item.shippingType === 1
					})
					if (that.orderInfo.refundStatus > 0) {
						uni.setNavigationBarColor({
							frontColor: '#fff',
							backgroundColor: '#666666'
						})
					}
				}).catch(err => {
					that.$util.Tips({
						title: err
					}, {
						tab: 4,
						url: '/pages/user/index'
					});
				});
			},
			/**
			 *
			 * 剪切订单号
			 */
			// #ifndef H5
			copy: function() {
				let that = this;
				uni.setClipboardData({
					data: this.orderInfo.refundOrderNo
				});
			},
			// #endif
			/**
			 * 打电话
			 */
			goTel: function() {
				uni.makePhoneCall({
					phoneNumber: this.orderInfo.deliveryId
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.refundPrice{
		font-size: 35rpx;
		font-weight: bold;
	}
	.qs-btn {
		width: auto;
		height: 60rpx;
		text-align: center;
		line-height: 60rpx;
		border-radius: 50rpx;
		color: #fff;
		font-size: 27rpx;
		padding: 0 3%;
		color: #aaa;
		border: 1px solid #ddd;
		margin-right: 20rpx;
	}

	.text-overflow {
		width: 392rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.shuoming {
		width: 32rpx;
		height: 32rpx;
	}

	.mp-header {
		width: 100%;
		@include main_bg_color(theme);
	}

	.goodCall {
		@include main_color(theme);
		text-align: center;
		width: 100%;
		height: 86rpx;
		padding: 0 30rpx;
		border-bottom: 1rpx solid #eee;
		font-size: 30rpx;
		line-height: 86rpx;
		background: #fff;

		.icon-kefu {
			font-size: 36rpx;
			margin-right: 15rpx;
		}

		/* #ifdef MP */
		button {
			display: flex;
			align-items: center;
			justify-content: center;
			height: 86rpx;
			font-size: 30rpx;
			@include main_color(theme);
		}

		/* #endif */
	}

	.justify-between {
		justify-content: space-between;
	}

	.align-center {
		align-items: center;
	}

	.order-details .header {
		height: 250rpx;
		padding: 0 30rpx;
	}

	.bg_color {
		@include main_bg_color(theme);
	}

	.order-details .header.on {
		background-color: #666 !important;
	}

	.order-details .header .pictrue {
		width: 110rpx;
		height: 110rpx;
	}

	.order-details .header .pictrue image {
		width: 100%;
		height: 100%;
	}

	.order-details .header .data {
		color: rgba(255, 255, 255, 0.8);
		font-size: 24rpx;
		margin-left: 27rpx;
	}

	.order-details .header .data.on {
		margin-left: 0;
		padding-top: 34rpx;
	}

	.order-details .header .data .state {
		font-size: 30rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 7rpx;
	}

	.order-details .header .data .time {
		margin-left: 20rpx;
	}

	.picTxt {
		//height: 232rpx;
	}

	.order-details .nav {
		background-color: #fff;
		font-size: 26rpx;
		color: #282828;
		padding: 27rpx 0;
		width: 100%;
		border-radius: 14rpx;
		margin: -100rpx auto 0 auto;
	}

	.order-details .nav .navCon {
		padding: 0 40rpx;
	}

	.order-details .nav .on {
		@include main_color(theme);
	}

	.font_color {
		@include main_color(theme);
	}

	.order-details .nav .progress {
		padding: 0 65rpx;
		margin-top: 10rpx;
	}

	.order-details .nav .progress .line {
		width: 100rpx;
		height: 2rpx;
		background-color: #939390;
	}

	.order-details .nav .progress .iconfont {
		font-size: 25rpx;
		color: #939390;
		margin-top: -2rpx;
	}

	.order-details .address {
		font-size: 26rpx;
		color: #868686;
		background-color: #fff;
		padding: 30rpx 0;
		.phone{
			margin-left: 20rpx;
		}
	}

	.order-details .address .name {
		font-size: 30rpx;
		color: #333;
		margin-bottom: 15rpx;
	}

	.order-details .line {
		width: 100%;
		height: 3rpx;
	}

	.order-details .line image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-details .wrapper {
		background-color: #fff;
		margin-top: 12rpx;
		padding: 30rpx 24rpx;
	}

	.order-details .wrapper .item {
		font-size: 28rpx;
		color: #282828;
	}

	.order-details .wrapper .item~.item {
		margin-top: 20rpx;
	}

	.order-details .wrapper .item .conter {
		color: #868686;
		text-align: right;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.mark_show {
		color: #868686;
		width: 470rpx;
		text-align: right;
	}

	.order-details .wrapper .item .conter .copy {
		font-size: 20rpx;
		color: #333;
		border-radius: 20rpx;
		border: 1rpx solid #666;
		padding: 3rpx 15rpx;
		margin-left: 24rpx;
	}

	.order-details .wrapper .actualPay {
		border-top: 1rpx solid #eee;
		margin-top: 30rpx;
		padding-top: 30rpx;
	}

	.order-details .wrapper .actualPay .money {
		font-weight: bold;
		font-size: 30rpx;
		@include price_color(theme);
	}

	.order-details .footer {
		width: 100%;
		height: 100rpx;
		position: fixed;
		bottom: 0;
		left: 0;
		background-color: #fff;
		padding-right: 30rpx;
		box-sizing: border-box;
	}

	.order-details .footer .bnt {
		width: 158rpx;
		height: 54rpx;
		text-align: center;
		line-height: 54rpx;
		border-radius: 50rpx;
		color: #fff;
		font-size: 27rpx;
	}

	.order-details .footer .bnt.cancel {
		color: #aaa;
		border: 1rpx solid #ddd;
	}

	.order-details .footer .bnt~.bnt {
		margin-left: 18rpx;
	}

	.order-details .writeOff {
		background-color: #fff;
		margin-top: 15rpx;
		padding-bottom: 50rpx;
	}

	.order-details .writeOff .title {
		font-size: 30rpx;
		color: #282828;
		height: 87rpx;
		border-bottom: 1px solid #f0f0f0;
		padding: 0 24rpx;
		line-height: 87rpx;
	}

	.order-details .writeOff .grayBg {
		background-color: #f2f5f7;
		width: 590rpx;
		height: 384rpx;
		border-radius: 20rpx 20rpx 0 0;
		margin: 50rpx auto 0 auto;
		padding-top: 55rpx;
	}

	.order-details .writeOff .grayBg .pictrue {
		width: 290rpx;
		height: 290rpx;
		margin: 0 auto;
	}

	.order-details .writeOff .grayBg .pictrue image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-details .writeOff .gear {
		width: 590rpx;
		height: 30rpx;
		margin: 0 auto;
	}

	.order-details .writeOff .gear image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-details .writeOff .num {
		background-color: #f0c34c;
		width: 590rpx;
		height: 84rpx;
		color: #282828;
		font-size: 48rpx;
		margin: 0 auto;
		border-radius: 0 0 20rpx 20rpx;
		text-align: center;
		padding-top: 4rpx;
	}

	.order-details .writeOff .rules {
		margin: 46rpx 30rpx 0 30rpx;
		border-top: 1px solid #f0f0f0;
		padding-top: 10rpx;
	}

	.order-details .writeOff .rules .item {
		margin-top: 20rpx;
	}

	.order-details .writeOff .rules .item .rulesTitle {
		font-size: 28rpx;
		color: #282828;
	}

	.order-details .writeOff .rules .item .rulesTitle .iconfont {
		font-size: 30rpx;
		color: #333;
		margin-right: 8rpx;
		margin-top: 5rpx;
	}

	.order-details .writeOff .rules .item .info {
		font-size: 28rpx;
		color: #999;
		margin-top: 7rpx;
	}

	.order-details .writeOff .rules .item .info .time {
		margin-left: 20rpx;
	}

	.order-details .map {
		font-size: 30rpx;
		color: #282828;
		margin-top: 15rpx;
		background-color: #fff;
		padding: 0 24rpx;
		.title{
			line-height: 86rpx;
			border-bottom: 1px solid #f0f0f0;
		}
	}

	.order-details .map .place {
		font-size: 26rpx;
		width: 176rpx;
		height: 50rpx;
		border-radius: 25rpx;
		line-height: 50rpx;
		text-align: center;
	}

	.order-details .map .place .iconfont {
		font-size: 27rpx;
		height: 27rpx;
		line-height: 27rpx;
		margin: 2rpx 3rpx 0 0;
	}

	.order-details .address .name .iconfont {
		font-size: 34rpx;
		margin-left: 10rpx;
	}

	.refund {
		padding: 0 !important;
		margin-top: 15rpx;
		background-color: #fff;

		.title {
			display: flex;
			align-items: center;
			font-size: 30rpx;
			color: #333;
			height: 86rpx;
			border-bottom: 1px solid #f5f5f5;
			font-weight: 400;
			padding: 0 24rpx;

			image {
				width: 32rpx;
				height: 32rpx;
				margin-right: 10rpx;
			}
		}

		.con {
			font-size: 26rpx;
			color: #666666;
			padding: 30rpx 24rpx;
		}
	}
	.pictrue {
		width: 130rpx;
		height: 130rpx;
		border-radius: 14rpx;
		overflow: hidden;
		image {
			width: 100%;
			height: 100%;
		}
	}
	.text {
		width: 490rpx;
		// position: relative;
		.name {
			font-size: 28rpx;
			color: #282828;
		}
		.attr {
			font-size: 16rpx;
			color: #868686;
			margin-top: 7rpx
		}
	}
	.wrapper-head {
		background-color: #fff;
		margin-top: 12rpx;
		padding: 30rpx 24rpx;
		margin: 12rpx 12px 0;
	}
	.wrapper-info {
		background-color: #fff;
		margin-top: 2rpx;
		padding: 15px 12px;
		.name {
			width: 414rpx;
			color: #333;
		}
		.num {
			color: #999;
		}
		.attr {
			color: #999;
			font-size: 20rpx;
			margin: 7rpx 0 16rpx 0;
		}
		.price {
			font-size: 26rpx;
			color: #E93323;
		}
	}
	.borRadius-top {
		border-radius: 14rpx 14rpx 0px 0px;
	}
	.borRadius-bottom {
		border-radius: 0px 0px 14rpx 14rpx;
	}
	.conter-refund {
		width: 76%;
		text-align: right;
		color: #868686;
	}

</style>
