<template>
	<view :style="{ backgroundImage: `url(${productInfo.activityStyle})` }" class='nav acea-row row-between-wrapper '>
		<view class='money skeleton-rect'>￥<text class='num'>{{productInfo.price}}</text><text
				class='y-money'>￥{{productInfo.otPrice}}</text></view>
		<view class='acea-row row-middle skeleton-rect'>
		</view>
	</view>
</template>

<script>
	export default {
		props: {
			productInfo: {
				type: Object,
				default: () => {},
			}
		}

	}
</script>

<style lang="scss" scoped>
	.nav {
		background-repeat: no-repeat;
		background-size: 100% 100%;
		width: 100%;
		height: 100rpx;
		padding: 0 24px;
		box-sizing: border-box;

		.money {
			font-size: 14px;
			color: #fff;
		}

		.num {
			font-size: 24px;
		}

		.y-money {
			font-size: 26rpx;
			margin-left: 10rpx;
			text-decoration: line-through;
		}
	}
</style>
