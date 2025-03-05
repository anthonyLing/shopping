<template>
	<!-- 图片魔方 -->
	<view class="pictureCube skeleton-rect" :style="[boxStyle]" v-if="picList.length">
		<view class="grid_box" :style="[gridColumns]" v-if="[0,1,2,4,5].includes(style)">
			<image v-for="(item,index) in picList" :key="index" @click="goDetail(item)"
			 :src="item.image" mode="widthFix" :style="{'height':imageH * 2 + 'rpx'}"></image>
		</view>
		<view class="advertItem04 acea-row" v-if="style==3">
			<view class="item" @click="goDetail(picList[0])">
				<image :src="picList[0].image"  mode="aspectFill"></image>
			</view>
			<view class="item">
				<view class="pic" @click="goDetail(picList[1])">
					<image :src="picList[1].image"  mode="aspectFill"></image>
				</view>
				<view class="pic" @click="goDetail(picList[2])">
					<image :src="picList[2].image"  mode="aspectFill"></image>
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
	export default {
		name: 'pictureCube',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
		},
		data() {
			return {
				picList: this.dataConfig.picStyle.picList,
				style: this.dataConfig.tabConfig.tabVal,
				prConfig: this.dataConfig.lrConfig.val,
				widthC: '',
				imageH: ''
			};
		},
		computed:{
			//最外层盒子的样式
			boxStyle(){
				return {
					borderRadius:this.dataConfig.bgStyle.val * 2 + 'rpx',
					background:`linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin:this.dataConfig.mbConfig.val * 2+'rpx' + ' ' + this.dataConfig.lrConfig.val * 2+'rpx' + ' ' + 0,
					padding:this.dataConfig.upConfig.val * 2+'rpx' + ' ' + '0' + ' ' + this.dataConfig.downConfig.val * 2+'rpx'
				}
			},
			// style == 5 height: 188rpx;
			//图片魔方排版
			gridColumns(){
				if([1,5].includes(this.dataConfig.tabConfig.tabVal)){
					return {
						gridTemplateColumns:'repeat(2, 1fr)'
					}
				}else if(this.dataConfig.tabConfig.tabVal == 0){
					return {
						gridTemplateColumns:'repeat(1, 1fr)'
					}
				}else if(this.dataConfig.tabConfig.tabVal == 2){
					return {
						gridTemplateColumns:'repeat(3, 1fr)'
					}
				}else if(this.dataConfig.tabConfig.tabVal == 4){
					return {
						gridTemplateColumns:'repeat(4, 1fr)'
					}
				}
			},
		},
		mounted() {
			if (this.picList.length) {
				let that = this;
				this.$nextTick((e) => {
					if ([0,1,5].includes(this.style)) {
						this.widthC = 375
					} else if (this.style == 2) {
						this.widthC = 250
					} else if (this.style == 4) {
						this.widthC = 188
					}
					uni.getImageInfo({
						src: that.setDomain(that.picList[0].image),
						success: (res) => {
							if (res && res.height > 0) {
								let height = res.height * ((that.widthC - that.prConfig * 2) / res
									.width)
								that.$set(that, 'imageH', height);
							} else {
								that.$set(that, 'imageH', (that.widthC - that.prConfig * 2) * 2);
							}
						},
						fail: function(error) {
							that.$set(that, 'imageH', (that.widthC - that.prConfig * 2) * 2);
						}
					})
				})
			}
		},
		methods: {
			//替换安全域名
			setDomain: function(url) {
				url = url ? url.toString() : '';
				//本地调试打开,生产请注销
				if (url.indexOf("https://") > -1) return url;
				else return url.replace('http://', 'https://');
			},
			goDetail(item) {
				this.$util.navigateTo(item.link);
			}
		}
	}
</script>

<style lang="scss">
	.grid_box{
		display: grid;
		grid-template-rows: auto;
		image{
			width: 100%;
		}
	}
	.pictureCube {
		.advertItem04 {
			width: 100%;

			.item {
				width: 50%;
				height: 376rpx;

				.pic {
					width: 100%;
					height: 188rpx;
				}

				image {
					width: 100%;
					height: 100%;
					display: block;
				}
			}
		}
	}
</style>