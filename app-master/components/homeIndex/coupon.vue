<template>
	<!-- 优惠券 -->
	<view>
		<view class="indexList" :style="[boxStyle]" @click="goPage()">
			<view class='title acea-row row-between-wrapper'>
				<view class='text line1 tui-skeleton-rect acea-row'>
					<image :src="logoUrl"></image>
					<text class='label' :style="[titleColor]">{{titleText}}</text>
				</view>
				<view class='more tui-skeleton-rect' :style="[moreColor]">
					MORE
					<text class="iconfont icon-jiantou"></text>
				</view>
			</view>
			<view class='tips mb20'>Receive coupons</view>
			<!-- 优惠券 -->
			<block v-if="listStyle == 0">
				<view class="scroll_box">
					<scroll-view scroll-x="true" style="white-space: nowrap; display: flex">
						<view class="item" :style="[itemBgColor]" v-for="item in couponList" :key="item.id">
							<view class="itemCon acea-row row-between-wrapper">
								<view class="text">
									<view class="money"><text>¥</text>{{item.money}}</view>
									<view class="info">满{{item.minPrice}}元可用</view>
								</view>
								<view class="bnt"><text>立即领取</text></view>
							</view>
						</view>
					</scroll-view>
				</view>
			</block>
			<block v-if="listStyle == 1">
				<view class="scroll_box">
					<scroll-view scroll-x="true" style="white-space: nowrap; display: flex">
						<view class="item1" :style="[itemBgColor]" v-for="item in couponList" :key="item.id">
							<view class="text">
								<view class="money"><text>¥</text>{{item.money}}</view>
								<view class="info line1">满{{item.minPrice}}元可用</view>
							</view>
							<view class="get_btn">领取</view>
						</view>
					</scroll-view>
				</view>
			</block>
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
		getIndexCoupon
	} from "@/api/api.js"
	export default {
		name: 'homeCoupon',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				couponList: [],
			};
		},
		computed: {
			//最外层盒子的样式
			boxStyle() {
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
					padding: this.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '10px' + ' ' + this.dataConfig.downConfig
						.val * 2 + 'rpx'
				}
			},
			//内容边距
			itemBgColor() {
				return {
					"background-color": this.dataConfig.themeColor.color[0].item,
					"margin-right": this.dataConfig.contentConfig.val + 'px'
				}
			},
			//展示数量
			limit() {
				return this.dataConfig.numConfig.val
			},
			//展示样式
			listStyle() {
				return this.dataConfig.tabConfig.tabVal
			},
			//图标地址
			logoUrl() {
				return this.dataConfig.logoConfig.url
			},
			//标题
			titleText() {
				return this.dataConfig.titleConfig.val
			},
			//标题颜色
			titleColor() {
				return {
					color: this.dataConfig.titleColor.color[0].item
				}
			},
			//更多颜色
			moreColor() {
				return {
					color: this.dataConfig.moreColor.color[0].item
				}
			}
		},
		mounted() {
			this.getList();
		},
		methods: {
			//优惠券列表
			getList() {
				getIndexCoupon(this.limit).then(res => {
					this.couponList = res.data;
				})
			},
			//去更多
			goPage() {
				this.$util.navigateTo(this.dataConfig.linkConfig.val ? this.dataConfig.linkConfig.val :
					'/pages/activity/couponList/index')
			}
		}
	};
</script>

<style lang="scss" scoped>
	@import '@/static/css/diyMain.scss';

	.scroll_box .item {
		width: 284rpx;
		height: 140rpx;
		background-color: #bbb;
		border-radius: 8rpx;
		color: #fff;
		position: relative;
		display: inline-block;
		position: relative;
		background-image:
			radial-gradient(circle at 220rpx top, #fff, #fff 10rpx, transparent 11rpx),
			radial-gradient(circle at 220rpx bottom, #fff, #fff 10rpx, transparent 11rpx);

		.itemCon {
			height: 100%;
			width: 100%;

			.text {
				width: 220rpx;

				.money {
					font-size: 52rpx;
					text-align: center;

					text {
						font-size: 30rpx;
					}
				}

				.info {
					font-size: 24rpx;
					text-align: center;
				}
			}

			.bnt {
				position: relative;
				height: 100%;
				font-size: 26rpx;
				display: block;
				writing-mode: vertical-lr;
				line-height: 1.2;
				width: 64rpx;
				border-left: 1px dashed #fff;

				text {
					position: absolute;
					left: 56%;
					top: 50%;
					transform: translate(-50%, -50%);
				}
			}
		}
	}

	.scroll_box .item1 {
		display: inline-block;
		width: 190rpx;
		height: 194rpx;
		background-color: #bbb;
		border-radius: 14rpx;
		color: #fff;
		margin-right: 20rpx;
		-webkit-mask: radial-gradient(circle at 10rpx 134rpx, transparent 10rpx, red 0) -10rpx;

		.text {
			padding: 16rpx 0;
			border-bottom: 1px dashed rgba(255, 255, 255, 0.5);

			.money {
				font-size: 52rpx;
				text-align: center;

				text {
					font-size: 30rpx;
				}
			}

			.info {
				font-size: 26rpx;
				text-align: center;
			}
		}

		.get_btn {
			font-size: 28rpx;
			text-align: center;
			line-height: 30px;
		}

	}

	.no_use {
		background-color: #d6b16c !important;
	}
</style>