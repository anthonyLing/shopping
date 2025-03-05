<template>
	<view id="store" class="store">
		<view class='pictrue'>
			<image :src="merchantInfoNew.avatar" class=""></image>
		</view>
		<view class="text">
			<navigator :url="`/pages/merchant/${type}/index?id=${merid}`" hover-class="none">
				<view class="flex merchantInfo">
					<text v-if="merchantInfoNew.isSelf" class="font-bg-red bt-color mr10 self_min merType">自营</text>
					<text v-if="isShowTypeId && !merchantInfoNew.isSelf"
						class="font-bg-red bt-color mr10 merType">{{merchantInfo.typeId | merchantTypeFilter}}</text>
					<text class="name">{{merchantInfoNew.name}}</text>
					<text v-if="isShowTypeId" class="iconfont icon-jiantou"></text>
				</view>
			</navigator>
			<view class="score">
				<view class='starsList'>
					<block v-for="(itemn, indexn) in merchantInfoNew.starLevel" :key="indexn">
						<text class='iconfont icon-pingfen font-color'></text>
					</block>
					<block v-show="Number(merchantInfoNew.starLevel)<5">
						<text v-for="(itemn, indexn) in noStarLevel" :key="indexn" class='iconfont icon-pingfen noCheck'></text>
					</block>
				</view>
			</view>
		</view>
		<button v-if="type!=='home'"  hover-class="none" class="merCollect" :class="merchantInfoNew.isCollect ? 'care' : ''" @click="followToggle">
			<text v-if="!merchantInfoNew.isCollect" class="iconfont icon-guanzhu"></text>
			{{ merchantInfoNew.isCollect ? '已关注' : '关注' }}
		</button>
		<navigator v-if="!isShowTypeId" :url="`/pages/merchant/${type}/index?id=${merid}`" hover-class="none">
			<button class="merCollect" hover-class="none">进店</button>
		</navigator>
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
		getMerCollectAddApi,
		getMerCollectCancelApi
	} from '@/api/merchant.js';
	import {
		followMer
	} from '@/libs/follow.js';
	import {
		mapGetters
	} from "vuex";
	import {
		toLogin
	} from '@/libs/login.js';
	export default {
		data() {
			return {
				skeletonShow: true,
				isShow: true,
				avatar: '',
				merchantInfoNew: this.merchantInfo,
				noStarLevel: 0
			}
		},
		computed: {
			...mapGetters(["merchantClassify", "merchantType", 'isLogin', 'uid']),
		},
		props: {
			merchantInfo: {
				type: Object,
				default: () => {}
			},
			merid: {
				type: Number,
				default: () => 0
			},
			type: {
				type: String,
				default: () => 'detail'
			},
			isShowTypeId: {
				type: Boolean,
				default: () => true
			}
		},
		watch: {
			merchantInfo: function(nVal, oVal) {
				this.merchantInfoNew = JSON.parse(JSON.stringify(nVal));
				if(parseInt(this.merchantInfoNew.starLevel)<5) this.noStarLevel = 5-parseInt(this.merchantInfoNew.starLevel);
			}
		},
		mounted: function() {
			const query = uni.createSelectorQuery().in(this);
			query.select('#store').boundingClientRect(data => {
				//this.storeHeight = data.height;
				this.$emit('merHomeHeight', data.height)
			}).exec();
		},
		methods: {
			// 设置是否关注
			followToggle: function() {
				if (this.isLogin === false) {
					toLogin();
				} else {
					followMer(this.merchantInfoNew.isCollect, this.merid).then(() => {
						this.$set(this.merchantInfoNew, 'isCollect', !this.merchantInfoNew.isCollect);
					});
				}
			}
		}
	};
</script>

<style lang="scss" scoped>
	.merType{
		@include main_bg_color(theme);
	}
	.noCheck{
		color: #ddd;
	}
	.care {
		border: 1px solid #FFFFFF;
		background: inherit !important;
	}

	.font-color {
		@include main_color(theme);
	}

	.iconfont {
		//font-size: 24rpx !important;
	}

	.icon-pingweifen {
		color: #ccc;
	}

	.merchantInfo {
		align-items: center;
		margin-bottom: 6rpx;
	}

	.store {
		position: relative;
		z-index: 5;
		display: flex;
		align-items: center;
		padding: 24rpx;

		// top:12rpx;
		.pictrue {
			width: 86rpx;
			height: 86rpx;
			border-radius: 6rpx;
		}

		.easy-loadimage,
		image,
		uni-image {
			width: 100%;
			height: 100%;
		}

		.text {
			flex: 1;
			min-width: 0;
			margin-right: 20rpx;
			margin-left: 20rpx;

			navigator {
				// display: inline-flex;
				align-items: center;
				max-width: 100%;

				.name {
					min-width: 0;
					overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
					font-weight: bold;
					font-size: 30rpx;
					line-height: 1;
					color: #FFFFFF;
				}

				.iconfont {
					margin-left: 10rpx;
					font-size: 17rpx;
					color: #FFFFFF;
				}
			}

			.score {
				display: flex;
				align-items: center;
				margin-top: 8rpx;
				font-weight: 500;
				font-size: 24rpx;
				line-height: 1;

				.iconfont {
					font-size: 20rpx;
				}
			}
		}

	}

	.self_min {
		min-width: 56rpx;
		text-align: center;
	}
</style>
