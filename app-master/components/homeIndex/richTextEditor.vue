<template>
	<!-- 富文本 -->
	<view v-if="description" :style="[boxStyle]">
		<view class="rich_text">
			<!-- #ifdef MP || APP-PLUS -->
			<rich-text :nodes="description"></rich-text>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<view v-html="description"></view>
			<!-- #endif -->
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
	export default {
		name: 'richText',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
		},
		computed: {
			//最外层盒子的样式
			boxStyle() {
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
				}
			},
			//富文本内容
			description() {
				return this.dataConfig.richText.val
			}
		},
		methods: {

		}
	}
</script>

<style lang="scss" scoped>
	.rich_text {
		padding: 10px;
		width: 100%;
		overflow: hidden;
		/deep/uni-video, /deep/video, /deep/.wscnph, /deep/img {
			width: 100% !important;
		}
	}
</style>