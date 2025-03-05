<template>
	<!-- 标题 -->
	<view v-if="titleConfig">
		<view class='title' :style="[boxStyle]" @click="goPage()">{{titleConfig}}</view>
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
	export default {
		name: 'titles',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
		},
		data() {
			return {

			};
		},
		computed: {
			//标题
			titleConfig() {
				return this.dataConfig.titleConfig.val
			},
			//最外层盒子的样式
			boxStyle() {
				return {
					'border-radius': this.dataConfig.bgTopStyle.val + 'px' + ' ' + this.dataConfig.bgTopStyle.val + 'px' +
						' ' + this.dataConfig.bgDownStyle.val + 'px' + ' ' + this.dataConfig.bgDownStyle.val + 'px',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
					padding: this.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '10px' + ' ' + this.dataConfig.downConfig
						.val * 2 + 'rpx',
					fontSize: this.dataConfig.fontSize.val + 'px' || '12px',
					color: this.dataConfig.fontColor.color[0].item,
					'text-align': this.dataConfig.textPosition.list[this.dataConfig.textPosition.tabVal].style,
					'font-style': this.dataConfig.textStyle.list[this.dataConfig.textStyle.tabVal].style,
				}
			},
		},
		methods: {
			goPage() {
				this.$util.navigateTo(this.dataConfig.linkConfig.val)
			}
		}
	}
</script>

<style scoped lang="scss">
	.title {
		font-size: 32rpx;
		color: #000;
		text-align: center;

		&.left {
			text-align: left;
		}

		&.right {
			text-align: right;
		}

		&.blod {
			font-weight: bold;
		}

		&.italics {
			font-style: italic;
		}
	}
</style>