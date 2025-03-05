<template>
	<view :data-theme="theme">
		<view @click="goDetail(items)" class="wf-page2">
			<view class='pictrue skeleton-rect'>
				<!-- <image :src='items.cover' mode="widthFix"></image> -->
				<!-- <image :src='item.image[0]' mode="widthFix"></image> -->
				<easy-loadimage mode="widthFix" :image-src="items.cover"></easy-loadimage>
				<view class="video_img" v-if="items.type == 2">
					<text class="iconfont icon-24gf-play"></text>
				</view>
				<!-- 审核状态:0-待审核，1-审核通过，2-审核失败，3-平台关闭 -->
				<view class="approval_status" v-if="items.auditStatus != 1">
					<view v-if="items.auditStatus == 2" class="approval_title">审核未通过</view>
					<view v-if="items.auditStatus == 3" class="approval_title">平台关闭</view>
					<view v-if="items.auditStatus == 0" class="approval_title">正在审核</view>
					<text v-if="items.auditStatus == 2 || items.auditStatus == 3" class="approval_info">查看未通过原因</text>
					<text v-if="items.auditStatus == 0" class="approval_info">通过后将展示在列表</text>
				</view>
			</view>
			<view class='text'>
				<view v-if="items.title"class='name skeleton-rect mt30'>
					<text class="text_name line2">
						{{items.title}}
					</text>
				</view>
				<view class="count acea-row skeleton-rect">
					<view class="author acea-row">
						<image class="image" :src="items.authorAvatar ? items.authorAvatar : urlDomain+'crmebimage/presets/morenT.png'" mode="widthFix"></image>
						<text class="author_name line1">{{items.authorName}}</text>
					</view>
					<view class="like" @click.stop="likeToggle(items)">
						<view class="like_count">
							<text class="iconfont" :class="items.userIsLike ? 'icon-shoucang1' : 'icon-dianzan'"></text>
							<text
								class="collect">{{items.likeNum ? items.likeNum < 10000 ? items.likeNum : (items.likeNum / 10000).toFixed(2) + 'w' : '' }}</text>
						</view>
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
	// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------	
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		discoverNoteLike
	} from '@/libs/follow.js';
	import easyLoadimage from '@/components/base/easy-loadimage.vue'
	import {
		mapGetters
	} from "vuex";
	let app = getApp();
	export default {
		components: {
			easyLoadimage
		},
		computed: {
			...mapGetters(['uid', 'isLogin'])
		},
		props: {
			// 区分从发现列表、我的主页作品进去，点进去内容列表，home从我的主页点进去
			fromTo: {
				type: String,
				default: ''
			},
			items: {
				type: Object,
				default: {}
			},
			type: {
				type: Number,
				default: 0
			},
			tab: {
				type: Number,
				default: 1
			}
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				theme: app.globalData.theme
			};
		},
		methods: {
			//点赞
			likeToggle(item) {
				if (this.isLogin) {
					discoverNoteLike(item.id).then(() => {
						this.$set(item, 'userIsLike', !item.userIsLike);
						if (!item.userIsLike) {
							item.likeNum--;
							item.likeNum = item.likeNum == 0 ? 0 : item.likeNum
						} else {
							item.likeNum++;
						}
					});
				} else {
					toLogin();
				}
			},
			goDetail(item) {
				if (item.type == 1) {
					if (this.fromTo === 'home') {
						uni.navigateTo({
							url: `/pages/discover/discover_details/index?noteId=${item.id}`
						});
					} else {
						uni.navigateTo({
							url: `/pages/discover/discover_recommend/index?noteId=${item.id}`
						});
					}
				}else{
						uni.navigateTo({
							//#ifdef APP
							url: `/pages/discover/discover_video/appVideo/index?noteId=${item.id}&fromTo=${this.fromTo}`
							//#endif
							//#ifndef APP
							url: `/pages/discover/discover_video/routineVideo/index?noteId=${item.id}&fromTo=${this.fromTo}`
							//#endif
							
						});
				}
			}
		}
	}
</script>
<style lang="scss" scoped>
	.pictrue {
		height: 339rpx;
		border-radius: 16rpx;
		position: relative;

		/deep/image,
		/deep/.easy-loadimage,
		uni-image {
			width: 100%;
			max-width: 339rpx;
			height: 339rpx;
			border-radius: 16rpx;
		}

		.video_img {
			position: absolute;
			top: 16rpx;
			right: 16rpx;
			z-index: 10;
			width: 40rpx;
			height: 40rpx;
			line-height: 40rpx;
			background: (rgba(0, 0, 0, .5));
			border-radius: 50%;
			color: #fff;
			text-align: center;

			.iconfont {
				    font-size: 20rpx;
				    position: absolute;
				    left: 12rpx;
			}
		}

		.plant-show {
			width: 42rpx;
			height: 42rpx;
			border-radius: 100%;
			background: rgba(0, 0, 0, .5);
			display: flex;
			align-items: center;
			justify-content: center;
			position: absolute;
			top: 10rpx;
			left: 10rpx;

			.iconfont {
				font-size: 20rpx;
				color: #fff;

			}
		}
	}

	.loadfail-img {
		width: 100%;
		height: 345rpx;
	}

	.text {
		padding-bottom: 6rpx;
		width: 345rpx;

		.name {
			padding: 0 20rpx;
			color: #333333;
			font-size: 28rpx;
			font-weight: bold;
		}

		.count {
			padding: 0 20rpx;
			margin-top: 22rpx;
			justify-content: space-between;

			.author {
				align-items: center;
			}

			.author_name {
				margin-left: 10rpx;
				max-width: 120rpx;
				font-size: 24rpx;
				color: #333333;
				max-width: 180rpx;
			}

			.author_time {
				color: #666666;
			}

			.like {
				font-size: 24rpx;
				color: #999999;
				display: flex;

				.like_count {
					display: flex;
					align-items: center;
				}

				.iconfont {
					font-size: 30rpx;
				}

				.icon-shoucang1 {
					@include main_color(theme);
				}

				.collect {
					font-size: 24rpx;
					margin-left: 5rpx;
				}
			}
		}
	}

	.wf-page2 .author .image,
	.wf-page2 .author uni-image {
		width: 46rpx;
		height: 46rpx;
		border-radius: 100%;
	}

	.approval_status {
		width: 100%;
		height: 100%;
		background: rgba(0, 0, 0, .4);
		position: absolute;
		top: 0;
		left: 0;
		border-radius: 16rpx;
		color: #fff;
		text-align: center;
		z-index: 5;

		.approval_title {
			font-size: 28rpx;
			margin-top: 135rpx;
			font-weight: bold;
		}

		.approval_info {
			font-size: 24rpx;
			margin-top: 24rpx;
		}
	}
</style>