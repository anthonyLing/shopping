<template>
	<view :data-theme="theme">
		<view v-if="InvoiceList.length" class="logistics-title">
			<text></text>
			<text>当前订单已拆分成<text>{{num}}</text>个包裹，<text>{{deliveryNum}}</text>个已发出</text>
		</view>
		<view class="borderPad">
			<view v-for="item in InvoiceList" :key="item.id" class='wrapper borRadius14'>
				<navigator class='bnt cancel' hover-class='none' :url="'/pages/goods/goods_logistics/index?invoiceId='+ item.id">
					<view class="acea-row mb30 row-between">
						<view>
							<text></text>
							<text class="wrapper-title">{{item.expressName}}</text>
						</view>
						<view class="wrapper-title">单号：{{item.trackingNumber}}</view>
					</view>
					<view class="wrapper-pro acea-row">
						<view v-for="j in item.detailList" :key="j.id" class="wrapper-img">
							<easy-loadimage mode="widthFix" :image-src="j.image"></easy-loadimage>
							<!-- <image :src="j.image"></image> -->
						</view>
					</view>
					<view class="wrapper-num">共{{item.totalNum}}件商品</view>
				</navigator>

			</view>
		</view>
	<view class='noCommodity' v-if="!InvoiceList.length">
		<view class='pictrue text-center'>
			<image src='../static/images/nologistics.png'></image>
			<view class="default_txt">暂无物流信息~</view>
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
		orderInvoiceListInfo
	} from '@/api/order.js';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	const app = getApp();
	export default {
		components: {
			easyLoadimage
		},
		data() {
			return {
				theme: app.globalData.theme,
				orderNo: '',
				InvoiceList: [],
				num: 0,
				deliveryNum: 0
			}
		},
		onLoad: function(options) {
			this.$set(this, 'orderNo', options.orderNo);
			this.getOrderInvoiceListInfo(options.orderNo);
		},
		methods: {
			getOrderInvoiceListInfo(orderNo) {
				uni.showLoading({
					title: "正在加载中"
				});
				orderInvoiceListInfo(orderNo).then(res => {
					uni.hideLoading();
					let data = res.data;
					this.deliveryNum = data.deliveryNum;
					this.num = data.num;
					this.$set(this, 'InvoiceList', data.invoiceList);
				}).catch(err => {
					this.$util.Tips({
						title: err
					});
				});
			}
		}
	}
</script>

<style scoped lang="scss">
	.noCommodity{
		padding-top: 50% !important;
	}
	.logistics {
		&-title {
			height: 62rpx;
			line-height: 62rpx;
			background: #FCFBE7;
			padding: 0 30rpx;
			color: #9F560C;
			font-size: 24rpx;
		}
	}

	.wrapper {
		background-color: #fff;
		margin-top: 14rpx;
		padding: 20rpx 24rpx;

		&-num {

			font-size: 20rpx;
			color: #999999;
		}

		&-title {

			color: #666666;
			font-size: 24rpx;
		}

		&-img {
			width: 120rpx;
			height: 120rpx;
			margin-right: 20rpx;
			border-radius: 14rpx;
			overflow: hidden;
			margin-bottom: 20rpx;

			image {
				width: 100%;
				height: 100%;
			}

			&:nth-child(5n) {
				margin-right: 0;
			}
		}

	}
	.default_txt {
		font-size: 26rpx;
		color: #999;
		text-align: center;
	}
</style>
