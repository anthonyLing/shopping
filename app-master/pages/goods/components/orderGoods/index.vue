<template>
	<view class="orderGoods borRadius14">
		<navigator :url="`/pages/merchant/home/index?id=`+ orderInfo.merId" hover-class="none">
			<view class='total'>
				<text class="mr10 iconfont icon-shangjiadingdan"></text>
				<text class="mr10">{{orderInfo.merName}}</text>
				<text class="iconfont icon-xiangyou" style="font-size: 20rpx;"></text>
			</view>
		</navigator>
		<view class='goodWrapper pad24'>
			<view class="item" v-for="(item,index) in cartInfo" :key="index">
				<view class='acea-row row-between-wrapper'
					@click="jumpCon(item)">
					<view class='pictrue'>
						<image :src='item.image'></image>
					</view>
					<view class='text'>
						<view class='acea-row row-between-wrapper'>
							<view class='name line1'>{{item.productName}}</view>
							<view class='num'>x {{item.payNum ? item.payNum : item.cartNum}}</view>
						</view>

						<view class='attr' v-if="item.sku">{{item.sku}}</view>
						<view class='acea-row row-between-wrapper'>
							<view class='money'>￥{{item.vipPrice ? item.vipPrice : item.price}}</view>
						</view>
					</view>
				</view>
				<!-- 订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消） -->
				<view class="acea-row evaluateBox">
					<view v-show="(Number(item.applyRefundNum) + Number(item.refundNum))>0" class="text">申请售后（ 数量：{{(Number(item.applyRefundNum) + Number(item.refundNum))}} ）</view>
					<view class='evaluate'
						v-if="orderData.status>0 && orderData.status<6 && (Number(item.applyRefundNum) + Number(item.refundNum))< item.payNum"
						@click.stop="goRefund(item)">申请退款
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
	import * as filters from '@/filters'
	export default {
		props: {
			//订单详情
			orderData: {
				type: Object,
				default: function() {
					return {};
				}
			},
			//商品信息
			cartInfo: {
				type: Array,
				default: function() {
					return [];
				}
			},
			orderNo: {
				type: String,
				default: '',
			},
			jump: {
				type: Boolean,
				default: false,
			},
			orderProNum: {
				type: Number,
				default: function() {
					return 0;
				}
			},
			//商户整条对象信息
			orderInfo: {
				type: Object,
				default: function() {
					return {};
				}
			},
		},
		data() {
			return {
				totalNmu: ''
			};
		},
		watch: {
			cartInfo: function(nVal, oVal) {
				let num = 0
				nVal.forEach((item, index) => {
					num += item.cartNum
				})
				this.totalNmu = num
			}
		},
		methods: {
			/**
			 * 退款申请
			 */
			goRefund(item) {
				this.$Cache.set('productInfo', item)
				uni.navigateTo({
					url: `/pages/goods/goods_return/index?orderNo=${this.orderNo}&orderId=${item.id}`
				})
			},
			jumpCon: function(item) {
				let type = filters.orderTypeValFilter(item.productType);
				if (this.jump) {
					uni.navigateTo({
						url: `/pages/goods/goods_details/index?id=${item.productId}&type=${type}`
					})
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	.evaluateBox{
		position: relative;
		margin-top: 10rpx;
		.text{
			font-size: 20rpx;
			color: #868686;
		}
	}
	.evaluate {
		width: auto !important;
		border-radius: 20rpx !important;
		padding: 0 10rpx;
		font-size: 24rpx;
	}

	.pad24 {
		padding: 20rpx 24rpx 20rpx 24rpx;
	}

	.orderGoods {
		background-color: #fff;
		margin-top: 15rpx;
	}

	.money {
		@include price_color(theme);
	}

	.orderGoods .total {
		width: 100%;
		height: 86rpx;
		padding: 0 24rpx;
		border-bottom: 2rpx solid #f0f0f0;
		font-size: 30rpx;
		color: #282828;
		line-height: 86rpx;
		box-sizing: border-box;
	}

	.pictrue image {
		background: #f4f4f4;
	}

	.goodWrapper {
		.item {
			margin-bottom: 30rpx;
		}

		.item:nth-last-child(1) {
			margin-bottom: 0;
		}

		.name {
			width: 90%;
		}
	}
</style>
