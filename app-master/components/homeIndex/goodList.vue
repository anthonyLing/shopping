<template>
	<!-- 商品列表 -->
	<view>
		<view v-if="tempArr.length" :style="[boxStyle]">
			<!-- 单列 -->
			<block v-if="itemStyle == 0">
				<view class="listA" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue">
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val">
							</easy-loadimage>
						</view>
						<view class="text-info text-add">
							<view>
								<view class="title line2" :style="[titleColor]">
									<view class="mr10 mer_badge" :style="[labelColor]" v-if="merTypeShow">自营</view>
									<span v-if="titleShow">{{ item.name }}</span>
								</view>
							</view>
							<view class="price acea-row row-middle" :style="[priceColor]">
								<view v-if="priceShow">
									<text>￥</text>
									{{ item.price }}
								</view>
							</view>
							<view class="old-price" :style="[soldColor]" v-if="soldShow">已售
								{{ (Math.floor(item.sales) + Math.floor(item.ficti)) || 0 }} {{item.unitName}}
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 两列 -->
			<block v-if="itemStyle == 1">
				<view class="listC" :style="[gridGap]">
					<view class="item" :style="[contentStyle]" v-for="(item, index) in tempArr" :key="index"
						@click="goDetail(item)">
						<view class="pictrue">
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val">
							</easy-loadimage>
						</view>
						<view class="text-info">
							<view class="title line1" :style="[titleColor]">
								<view class="mr10 mer_badge" v-if="merTypeShow" :style="[labelColor]">自营</view>
								<span v-if="titleShow">{{ item.name }}</span>
							</view>
							<view class="acea-row row-middle price" :style="[priceColor]">
								<view v-if="priceShow">
									<text>￥</text>
									{{ item.price }}
								</view>
							</view>
							<view class="old-price" :style="[soldColor]" v-if="soldShow">已售
								{{ (Math.floor(item.sales) + Math.floor(item.ficti)) || 0 }} {{item.unitName}}
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 三列 -->
			<block v-if="itemStyle == 2">
				<view class="listB" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue" :style="[contentStyle]">
							<easy-loadimage :image-src="item.image">
							</easy-loadimage>
						</view>
						<view class="text-info">
							<view class="title line2" :style="[titleColor]">
								<view class="mr10 mer_badge" v-if="merTypeShow" :style="[labelColor]">自营</view>
								<span v-if="titleShow">{{ item.name }}</span>
							</view>
							<view class="price" :style="[priceColor]">
								<view v-if="priceShow">
									<text>￥</text>
									{{ item.price }}
								</view>
							</view>
							<view class="old-price" v-if="soldShow" :style="[soldColor]">
								已售 {{ Math.floor(item.sales) + Math.floor(item.ficti) || 0 }} {{ item.unitName }}
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 大图 -->
			<block v-if="itemStyle == 3 && tempArr.length">
				<view class="listBig" :style="[gridGap]">
					<view class="itemBig" v-for="(item,index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="img-box">
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val">
							</easy-loadimage>
						</view>
						<view class="name line2" :style="[titleColor]">
							<view class="mr10 mer_badge" v-if="merTypeShow" :style="[labelColor]">自营</view>
							<span v-if="titleShow">{{item.name}}</span>
						</view>
						<slot name="center"></slot>
						<view class="acea-row row-middle price" :style="[priceColor]">
							<span v-if="priceShow">￥<span class="num">{{item.price}}</span></span>
						</view>
						<view class="old-price ml10" :style="[soldColor]" v-if="soldShow">已售
							{{ (Math.floor(item.sales) + Math.floor(item.ficti)) || 0 }} {{item.unitName}}
						</view>
					</view>
				</view>
			</block>
			<view class='loadingicon acea-row row-center-wrapper' :hidden='loading==false'>
				<text class='loading iconfont icon-jiazai'></text>
			</view>
			<!-- <view class="mores-txt" v-if="goodScroll">
				<text>我是有底线的</text>
			</view> -->
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
		productList
	} from '@/api/product.js';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	export default {
		name: 'goodList',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
		},
		components: {
			easyLoadimage
		},
		data() {
			return {
				tempArr: [],
				numConfig: this.dataConfig.numConfig.val, //展示多少条
				itemStyle: this.dataConfig.itemStyle.tabVal, //商品列表展示方式  单列 两列 三列
				type: this.dataConfig.tabConfig.tabVal || 0, //商品类型 0指定商品，1指定品牌，2指定分类，3指定商户
				selectId: this.dataConfig.selectConfig ? this.dataConfig.selectConfig.activeValue : [], //分类
				brandId: this.dataConfig.activeValueBrand ? this.dataConfig.activeValueBrand.activeValue : [], //品牌
				productIds: this.dataConfig.goodsList.ids || [],
				merId: this.dataConfig.activeValueMer ? this.dataConfig.activeValueMer.activeValue : [], //商户
				params: { //精品推荐分页
					page: 1,
					limit: 10,
					cid: '',
					brandId: '',
					merId: '',
					priceOrder: '',
					salesOrder: ''
				},
				goodScroll: false,
				loading: false,
			};
		},
		computed: {
			//商品名称颜色
			titleColor() {
				return {
					'color': this.dataConfig.titleColor.color[0].item,
				}
			},
			//最外层盒子的样式
			boxStyle() {
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
					padding: this.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + 0 + ' ' + this.dataConfig.downConfig.val *
						2 + 'rpx'
				}
			},
			//图片展示样式
			gridGap() {
				return {
					'grid-gap': this.dataConfig.contentConfig.val * 2 + 'rpx'
				}
			},
			//文章图片的圆角和高度
			imgStyle() {
				return {
					'border-radius': this.dataConfig.contentStyle.val * 2 + 'rpx',
				}
			},
			//标签
			labelColor() {
				return {
					backgroundColor: this.dataConfig.labelColor.color[0].item,
					color: this.dataConfig.labelFontColor.color[0].item
				}
			},
			//价格颜色
			priceColor() {
				return {
					'color': this.dataConfig.priceColor.color[0].item,
				}
			},
			//已售数量
			soldColor() {
				return {
					'color': this.dataConfig.soldColor.color[0].item,
				}
			},
			//店铺类型
			merTypeShow() {
				if (this.dataConfig.typeConfig.activeValue.includes(0)) {
					return true;
				} else {
					return false;
				}
			},
			//商品名称
			titleShow() {
				if (this.dataConfig.typeConfig.activeValue.includes(1)) {
					return true;
				} else {
					return false;
				}
			},
			//价格
			priceShow() {
				if (this.dataConfig.typeConfig.activeValue.includes(2)) {
					return true;
				} else {
					return false;
				}
			},
			//销量
			soldShow() {
				if (this.dataConfig.typeConfig.activeValue.includes(3)) {
					return true;
				} else {
					return false;
				}
			},
			//排序，0综合，1销量，2价格
			goodsSort() {
				return this.dataConfig.goodsSort.tabVal
			},
			//内容圆角
			contentStyle() {
				return {
					'border-radius': this.dataConfig.contentStyle.val ? this.dataConfig.contentStyle.val + 'px' : '0'
				};
			},
		},
		mounted() {
			this.params.page = 1;
			this.goodScroll = false;
			this.tempArr = [];
			//类型为0时，直接加载选中的商品，不为0时根据条件加载商品列表
			if (this.type > 0) {
				this.productslist();
			} else {
				this.getProList();
			}
		},
		//uniapp小程序用deep重写组件样式不生效
		options: {
			styleIsolation: 'shared'
		},
		methods: {
			getProList() {
				this.tempArr = this.dataConfig.goodsList.list;
			},
			productslist() {
				if (this.goodScroll) return;
				this.loading = true
				this.params.limit = this.numConfig;
				this.params.cid = this.selectId.join(',');
				this.params.brandId = this.brandId.join(',');
				this.params.merId = this.merId.join(',');
				if (this.goodsSort === 0) {
					this.params.priceOrder = '';
					this.params.salesOrder = '';
				} else if (this.goodsSort === 1) {
					this.params.priceOrder = '';
					this.params.salesOrder = 'desc';
				} else {
					this.params.priceOrder = 'desc';
					this.params.salesOrder = '';
				}

				productList(this.params).then(res => {
					this.$set(this.params, 'page', this.params.page + 1);
					this.goodScroll = this.params.page > res.data.totalPage;
					this.tempArr = this.tempArr.concat(res.data.list || []);
					this.loading = false
				});
			},
			goDetail(item) {
				this.$emit('detail', item);
			}
		}
	};
</script>

<style lang="scss" scoped>
	.mores-txt {
		text-align: center;
	}

	.text-add {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.listBig {
		display: grid;
		grid-template-rows: auto;
		grid-template-columns: repeat(1, 1fr);

		.itemBig {
			width: 100%;

			.img-box {
				width: 100%;
				height: 710rpx;
				position: relative;
			}

			.name {
				font-size: 28rpx;
				font-weight: bold;
				margin-top: 16rpx;
				padding: 0 8px;
			}

			.price {
				font-weight: bold;
				font-size: 12px;
				margin-top: 10rpx;
				padding: 0 8px;

				.num {
					font-size: 32rpx;
					margin-right: 10rpx;
				}

				.old-price {
					color: #aaa;
					font-weight: normal;
				}
			}
		}
	}

	.listA {
		display: grid;
		grid-template-columns: repeat(1, 1fr);
		grid-template-rows: auto;
		width: 100%;

		.item {
			display: flex;
			width: 100%;

			.pictrue {
				width: 220rpx;
				height: 220rpx;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				margin-left: 20rpx;
				flex: 1
			}
		}
	}

	.listB {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		grid-template-rows: auto;
		width: 100%;

		.item {
			.pictrue {
				width: 100%;
				height: 220rpx;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				padding-top: 14rpx;
			}
		}
	}

	.listC {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		grid-template-rows: auto;
		width: 100%;

		/deep/.origin-img,
		/deep/.easy-loadimage {
			border-bottom-left-radius: 0 !important;
			border-bottom-right-radius: 0 !important;
		}

		.item {
			background-color: #fff;

			.pictrue {
				width: 100%;
				height: 345rpx;
				overflow: hidden;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				padding: 14rpx 0 14rpx 14rpx;

				.title {
					width: 300rpx;
					height: 40rpx;
				}
			}
		}
	}

	.text-info {
		.title {
			width: 100%;
			height: 80rpx;
			line-height: 40rpx;
			color: #333;
		}

		.old-price {
			font-weight: normal;
			font-size: 24rpx;
			color: #999;
		}

		.price {
			font-size: 36rpx;
			font-weight: 550;

			text {
				padding-bottom: 4rpx;
				font-size: 26rpx;
				font-weight: normal;
			}
		}
	}

	.mer_badge {
		padding: 0 4rpx;
		background-color: theme;
		color: #fff;
		font-size: 20rpx;
		display: inline-block;
		border-radius: 4rpx;
		line-height: 28rpx;
		height: 28rpx;
	}
</style>