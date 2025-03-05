<template>
	<!-- tab选项卡 -->
	<view class="index-product-wrapper" :style="[mbConfig]">
		<view class="nav-bd longTab" :style="[tabBgColor]">
			<scroll-view scroll-x="true" style="white-space: nowrap; display: flex" :scroll-left="tabLeft">
				<view class="longItem"
					:style="'width:'+isWidth+'px;color:' + (index == ProductNavindex ? checkColor : fontColor)+';--color:'+checkColor"
					:data-index="index" :class="index===ProductNavindex?'click':''" v-for="(item,index) in navList"
					:key="index" :id="'id'+index" @click="ProductNavTab(item, index)">{{ item.val }}
				</view>
			</scroll-view>
		</view>
		<view :style="[boxStyle]">
			<!-- 单列 -->
			<block v-if="itemStyle == 0">
				<view class="listA" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue">
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
						<view class="text-info text-add">
							<view>
								<view class="title line2">
									<view class="mr10 mer_badge" :style="[labelColor]" v-if="merTypeShow">自营</view>
									<span v-if="titleShow" :style="[titleColor]">{{ item.name }}</span>
								</view>
							</view>
							<view class="price acea-row row-middle" :style="[priceColor]">
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
			<!-- 两列 -->
			<block v-if="itemStyle == 1">
				<view class="listC" :style="[gridGap]">
					<view class="item" :style="[contentStyle]" v-for="(item, index) in tempArr" :key="index"
						@click="goDetail(item)">
						<view class="pictrue">
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
						<view class="text-info">
							<view class="title line1">
								<view class="mr10 mer_badge" v-if="merTypeShow" :style="[labelColor]">自营</view>
								<span v-if="titleShow" :style="[titleColor]">{{ item.name }}</span>
							</view>
							<view class="row-middle price" :style="[priceColor]">
								<view v-if="priceShow">
									<text>￥</text>
									{{ item.price }}
								</view>
								<view class="old-price ml10" v-if="soldShow" :style="[soldColor]">
									已售 {{ Math.floor(item.sales) + Math.floor(item.ficti) || 0 }} {{ item.unitName }}
								</view>
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 三列 -->
			<block v-if="itemStyle == 2">
				<view class="listB" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue">
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
						<view class="text-info">
							<view class="title line2">
								<view class="mr10 mer_badge" v-if="merTypeShow" :style="[labelColor]">自营</view>
								<span :style="[titleColor]">{{ item.name }}</span>
							</view>
							<view class="price" :style="[priceColor]">
								<view v-if="priceShow">
									<text>￥</text>
									{{ item.price }}
								</view>
							</view>
							<view class="old-price ml10" v-if="soldShow" :style="[soldColor]">
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
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
						<view class="name line2">
							<view class="mr10 mer_badge" v-if="merTypeShow" :style="[labelColor]">自营</view>
							<span :style="[titleColor]" v-if="titleShow">{{item.name}}</span>
						</view>
						<slot name="center"></slot>
						<view class="row-middle price" :style="[priceColor]">
							<span v-if="priceShow">￥<span class="num">{{item.price}}</span></span>
							<view class="old-price ml10" v-if="soldShow" :style="[soldColor]">
								已售 {{ Math.floor(item.sales) + Math.floor(item.ficti) || 0 }} {{ item.unitName }}
							</view>
						</view>
					</view>
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
		productList
	} from '@/api/product.js';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	export default {
		name: 'homeTab',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				tempArr: [],
				iSshowH: false,
				ProductNavindex: 0,
				itemStyle: 0, //样式类型
				themeColor: '#f00',
				titleConfig: 1, //标题位置
				infoColor: '#999',
				goodType: 3,
				loadend: false,
				loading: false,
				page: 1,
				goodsSort: 0, //排序，0综合，1销量，2价格
				isWidth: 0, //每个导航栏占位
				tabLeft: 0,
				limit: 0 //分页条数
			};
		},
		components: {
			easyLoadimage
		},
		created() {
			let that = this
			uni.getSystemInfo({
				success(e) {
					that.isWidth = (e.windowWidth) / 5;
				}
			})
		},
		computed: {
			//标签文字颜色
			fontColor() {
				return this.dataConfig.fontColor.color[0].item
			},
			//选中颜色
			checkColor() {
				return this.dataConfig.checkColor.color[0].item
			},
			//选项卡背景颜色
			tabBgColor() {
				return {
					background: `linear-gradient(${this.dataConfig.tabBgColor.color[0].item}, ${this.dataConfig.tabBgColor.color[1].item})`,
				};
			},
			//页面间距
			mbConfig() {
				return {
					marginTop: this.dataConfig.mbConfig.val ? this.dataConfig.mbConfig.val + 'px' : 0
				}
			},
			//分类列表
			navList() {
				return this.dataConfig.tabItemConfig.list;
			},
			//最外层盒子的样式
			boxStyle() {
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.topConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
					padding: this.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + 0 + ' ' + this.dataConfig.downConfig.val *
						2 + 'rpx'
				}
			},
			//商品间距
			gridGap() {
				return {
					'grid-gap': this.dataConfig.contentConfig.val * 2 + 'rpx'
				}
			},
			//图片的圆角和高度
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
			//商品名称颜色
			titleColor() {
				return {
					'color': this.dataConfig.titleColor.color[0].item,
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
			//内容圆角
			contentStyle() {
				return {
					'border-radius': this.dataConfig.contentStyle.val ? this.dataConfig.contentStyle.val + 'px' : '0'
				};
			},
		},
		mounted() {
			//默认加载第一项的商品数据
			if (this.navList) {
				this.itemStyle = this.navList[0].activeList ? this.navList[0].activeList.styleType : 0;
				if (this.navList[0].activeList && this.navList[0].activeList.activeProTabIndex == 0) {
					this.tempArr = this.navList[0].activeList.goods;
				} else {
					this.limit = this.navList[0].activeList ? this.navList[0].activeList.num : 3;
					if (this.navList[0].activeList){
						this.getGroomList(this.navList[0].activeList.activeProTabIndex, this
							.navList[0].activeList
							.activeValue);
					}
				}
			}
		},
		//uniapp小程序用deep重写组件样式不生效 
		options: {
			styleIsolation: 'shared'
		},
		methods: {
			// 选项卡切换点击事件；商品类型选择除第一个指定商品，加载商品从平台端获取数据，其余选项均请求接口加载
			changeTab(item, index) {
				this.tempArr = [];
				if (item.activeList.activeProTabIndex == 0) {
					this.tempArr = item.activeList.goods;
				} else {
					this.page = 1;
					this.loadend = false;
					this.getGroomList(item.activeList.activeProTabIndex, item.activeList.activeValue);
				}
			},
			// 商品列表
			getGroomList(typeIndex, val) {
				let that = this;
				let priceOrder = '';
				let salesOrder = '';
				if (that.loadend) return false;
				if (that.loading) return false;
				if (this.goodsSort === 0) {
					priceOrder = '';
					salesOrder = '';
				} else if (this.goodsSort === 1) {
					priceOrder = '';
					salesOrder = 'desc';
				} else {
					priceOrder = 'desc';
					salesOrder = '';
				}
				productList({
						page: that.page,
						limit: this.limit,
						brandId: typeIndex == 1 ? val : '',
						cid: typeIndex == 2 ? val : '',
						merId: typeIndex == 3 ? val : '',
						priceOrder: priceOrder,
						salesOrder: salesOrder
					}).then((res) => {
						let list = res.data.list;
						let tempArr = that.$util.SplitArray(list, that.tempArr);
						let loadend = list.length < that.limit;
						that.loadend = loadend;
						that.loading = false;
						that.productList = productList;
						that.page = that.page + 1;
					})
					.catch(res => {
						that.loading = false;
					});
			},
			// 选项卡切换
			ProductNavTab(item, index) {
				this.ProductNavindex = index;
				this.itemStyle = this.navList[index].activeList.styleType;
				this.goodsSort = item.activeList.goodsSort;
				this.$nextTick(() => {
					let id = 'id' + index;
					this.tabLeft = (index - 2) * this.isWidth //设置下划线位置
				})
				this.limit = item.activeList.num;
				this.changeTab(item, index);
			},
			goDetail(item) {
				this.$emit('detail', item);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.longTab {
		.longItem {
			height: 70rpx;
			display: inline-block;
			line-height: 70rpx;
			text-align: center;
			font-size: 28rpx;
			color: #333333;
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;

			&.click {
				font-weight: bold;
				font-size: 30rpx;
				position: relative;

				&::after {
					content: '';
					width: 40rpx;
					height: 4rpx;
					background: var(--color);
					position: absolute;
					bottom: 0;
					left: 50%;
					transform: translateX(-50%);
				}
			}
		}
	}

	.index-product-wrapper {

		&.on {
			min-height: 1500rpx;
		}

		.nav-bd {
			height: 70rpx;
			line-height: 70rpx;
			padding-left: 20rpx;
			background: #fff;

			.item {
				display: inline-block;
				font-size: 28rpx;
				color: #333;
				font-weight: 400;
				padding-right: 48rpx;

				&.on {
					border-radius: 0;
				}
			}
		}
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
		padding: 0 20rpx;

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
		padding: 0 20rpx;

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
				margin-left: 14rpx;
				flex: 1
			}
		}
	}

	.listB {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		grid-template-rows: auto;
		width: 100%;
		padding: 0 20rpx;

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
				padding: 10rpx;
			}
		}
	}

	.listC {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		grid-template-rows: auto;
		width: 100%;
		padding: 0 20rpx;

		/deep/.origin-img,
		/deep/.easy-loadimage {
			border-bottom-left-radius: 0 !important;
			border-bottom-right-radius: 0 !important;
		}

		.item {
			background-color: #fff;
			overflow: hidden;

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