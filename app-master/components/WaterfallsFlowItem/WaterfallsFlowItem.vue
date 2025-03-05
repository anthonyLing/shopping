<template>
	<view class='list acea-row row-between-wrapper' :data-theme="theme">
		<view class='item' hover-class='none' @click="goDetail(item)">
			<view class='pictrue'>
				<easy-loadimage mode="widthFix" :image-src="item.image"></easy-loadimage>
				<view v-if="item.activityStyle" :style="{ backgroundImage: `url(${item.activityStyle})` }" class="border-picture"></view>
			</view>
			<view class='texts'>
				<view class='names line2'><view v-if="item.isSelf"
					class="bg-color bt-color mr10 self_min merType">自营</view>{{item.name}}</view>
				<view class='money'>
					<view class='num'>￥{{item.price}}</view>
					<view><text v-if="item.merTypeId"
							class="bg-color bt-color mr10 merType">{{item.merTypeId | merchantTypeFilter}}</text>
					</view>
					<view class="sold">
						<text v-if="Math.floor(item.replyNum)>0">{{item.replyNum}}条评论</text>
						<text v-if="item.replyNum===0">暂无评论</text>
						<text v-if="Number(item.positiveRatio)>0" class="m-l-8">好评{{Number(item.positiveRatio)*100}}%</text>
						
					</view>
					<view class="sold">已售 {{ (Math.floor(item.sales) + Math.floor(item.ficti)) || 0 }} {{item.unitName}}
					</view>
				</view>
				<view class="company" v-if="item.merName" @click.stop="goStore(item.merId)">
					<text class='name line1'>{{item.merName}}</text>
					<view class="flex">
						进店
						<text class="iconfont icon-xiangyou"></text>
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
		mapGetters
	} from "vuex";
	import {
		goShopDetail
	} from '@/libs/order.js'
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	let app = getApp();
	export default {
		components: {
			easyLoadimage
		},
		props: {
			item: {
				type: Object,
				require: true
			},
			type: {
				type: Number,
				default: 0
			},
			isStore: {
				type: [String, Number],
				default: '1'
			},
			isLogin: {
				type: Boolean,
				require: false
			}
		},
		data() {
			return {
				theme: app.globalData.theme
			}
		},
		methods: {
			// 去详情页
			goDetail(item) {
				goShopDetail(item.id);
			},
			goStore(id) {
				uni.navigateTo({
					url: `/pages/merchant/home/index?id=${id}`
				})
			},
			authOpen() {
				this.$emit('authOpen');
			},
			followToggle(item) {
				this.$emit('followToggle', item);
			}
		}
	}
</script>
<style lang="scss" scoped>
	.merType{
		@include main_bg_color(theme);
	}
	.border-picture {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		border-radius: 16rpx 16rpx 0 0;
		background: center/cover no-repeat;
	}
	.list {
		.item {
			width: 100%;
			.pictrue {
				/deep/image,/deep/.easy-loadimage,uni-image {
					width: 100%;
					height: 100%;
					border-radius: 20rpx 20rpx 0 0;
					overflow: hidden;
				}
			}

		}

		.texts {
			padding: 16rpx;
			font-size: 30rpx;
			color: #222;

			.names {
				font-size: 28rpx;
				font-weight: 400;
				color: #333333;
			}

			.money {
				.num {
					font-size: 38rpx;
					font-weight: 900;
					@include price_color(theme);
				}

				.y-money {
					font-size: 26rpx;
					color: #888888;
					text-decoration: line-through;
					margin-left: 14rpx;
				}
			}
		}
	}
	.company {
		display: flex;
		align-items: center;
		font-size: 26rpx;

		.name {
			display: inline-block;
			color: #666 !important;
			height: auto !important;
			overflow: hidden;
			white-space: nowrap;
			text-overflow: ellipsis;
			margin-bottom: 0 !important;
			margin-right: 10rpx;
		}

		.flex {
			display: flex;
			align-items: center;
			color: #282828 !important;

			.iconfont {
				font-size: 16rpx;
				margin-top: 4rpx;
			}
		}
	}

	.sold {
		font-size: 22rpx;
		color: #666;
	}
	.m-l-8 {
		margin-left: 8rpx;
	}

	.list .item .pictrue,
	.easy-loadimage,
	image,
	uni-image {
		position: relative;
		width: 100%;
		height: 330rpx;
		border-radius: 16rpx 16rpx 0 0;
		overflow: hidden;

		/deep/.easy-loadimage,
		uni-image,
		image {
			height: 330rpx;
			border-radius: 16rpx 16rpx 0 0;
		}

		margin: 0 !important;
	}
</style>
