<template>
	<view :data-theme="theme">
		<view class="seckill_header">
			<!-- #ifdef MP || APP-PLUS-->
			<view class='cart_nav'>
				<nav-bar @getNavH='getNavH' :backgroundColor="backgroundColor">
					<view slot='default' class="">
						<image class="seckill_title" src="../static/images/seckill_title.png" mode=""></image>
					</view>
				</nav-bar>
			</view>
			<!--  #endif -->
			<view class="scroll_box" :style='{width: mainWidth+"px", paddingTop: (marTop + 30) + "rpx"}'>
				<scroll-view scroll-x="true" scroll-with-animation style="white-space: nowrap; vertical-align: middle;"
					show-scrollbar="false" :scroll-left="tabLeft">
					<!-- #ifdef APP-PLUS -->
					<!-- <view class="" :style="'width:100%;' + 'height:'+sysHeight"></view> -->
					<!-- #endif -->
					<view class="scroll_box_item">
						<view class="scroll_item" v-for="(item,index) in timeLine" :key="index"
							@click="checkSeckill(index,item)">
							<view class="acea-row row-column row-center row-middle" :style='"width:"+isWidth+"px;"'
								:id='"sort"+index'>
								<text class="header_time f-w-700"
									:class="active == index ? 'active' : ''">{{item.startTime}}</text>
								<text class="header_day f-w-700"
									:class="active == index ? 'active_day' : ''">{{item.status | timeStatusFilter}}</text>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>
		<view class="seckill_box" :style='{ marginTop: listMarTop + "rpx"}'>
			<view class="seckill_desc acea-row row-between row-middle">
				<view class="acea-row row-column">
					<text class="seckill_name"></text>
				</view>
				<view v-if="status>0" class="acea-row datatimeBox">
					<text class="f-s-24 text-right">{{desc}}</text>
					<view class="count_down">
						<countDown :is-day="true" :tip-text="' '" :day-text="' '" :hour-text="' : '"
							:minute-text="' : '" :second-text="' '" :datatime="datatime" :isCol="false"
							:bgColor="bgColor"></countDown>
					</view>
				</view>
			</view>
			<view class="seckill_list">
				<view class="seckill_item acea-row" v-for="(item,index) in seckillList " :key="index"
					@click="goDetail(item.id)">
					<view class="picture">
						<easy-loadimage mode="widthFix" :image-src="item.image" :border-src="item.image">
						</easy-loadimage>
					</view>
					<view class="acea-row row-column row-between" :class="status == 1?'':''">
						<view class="pro_name line1 f-w-700"><text v-if="item.isSelf"
								class="font-bg-red bt-color mr10 self_min merType">自营</text>{{item.name}}</view>
						<view v-if="status == 1" class="seckill_zhekou acea-row row-between">
							<view class="zhekou_price futura f-s-22 frice_hua">￥{{item.price}}</view>
							<view class="acea-row row-column row-center row-middle pb-30">
								<text class="f-s-24 reduce_color title">直降</text>
								<text
									class="f-s-24 reduce_color">¥{{(Number(item.price)-Number(item.seckillPrice)).toFixed(2)}}</text>
							</view>
							<view class="zhekou_price f-s-20 futura text-right">即将恢复</view>
						</view>
						<view v-else class="acea-row row-between">
							<text
								class="f-s-24 reduce_color mts">商品直降¥{{(Number(item.price)-Number(item.seckillPrice)).toFixed(2)}}</text>
						</view>
						<!--  抢购中按钮-->
						<view class="seckill_btn seckill_btn_bg1 acea-row row-middle" v-if="status == 1">
							<view class="seckill_btn_left f-w-700">
								<text class="friceIcon">￥</text>{{item.seckillPrice}}
							</view>
							<view class="middle"></view>
							<view class="acea-row row-column seckill_btn_right">
								<text class="f-s-26 f-w-700 text-white text-center">立即抢购</text>
								<view class="acea-row row-middle mt12">
									<text class="f-s-17 text-white f-w-300">已抢{{item.payRange}}</text>
									<view class="w80 ml8">
										<view class="activityProgress skeleton-rect">
											<view class='bg-reds' :style="'width:'+item.payRange"></view>
										</view>
									</view>
								</view>
							</view>
						</view>
						<!--  未开始按钮-->
						<view class="seckill_btn seckill_btn_bg2 acea-row row-middle" v-if="status == 2 || status ==3">
							<view class="seckill_btn_left f-w-700">
								<text class="friceIcon">￥</text>{{item.seckillPrice}}
							</view>
							<view class="middle"></view>
							<view class="acea-row row-column">
								<text class="f-s-28 f-w-700 btn_text text-center">敬请期待</text>
							</view>
						</view>
						<view class="seckill_btn seckill_btn_bg3 acea-row row-middle" v-if="status == 0">
							<view class="seckill_btn_left f-w-700">
								<text class="friceIcon">￥</text>{{item.seckillPrice}}
							</view>
							<view class="middle"></view>
							<view class="acea-row row-center row-middle">
								<text class="f-s-28 f-w-700 btn_text text-center">已结束</text>
							</view>
						</view>
					</view>
				</view>
				<view class='loadingicon acea-row row-center-wrapper'>
					<text class='loading iconfont icon-jiazai'
						:hidden='loading==false'></text>{{seckillList.length>0?loadTitle:''}}
				</view>
				<view class="empty-boxs" v-if="!seckillList.length && !loading">
					<emptyPage title="暂无秒杀商品~" mTop="31%">
						<div slot='emptysrc'>
							<image :src="urlDomain+'crmebimage/presets/noShopper.png'"></image>
						</div>
					</emptyPage>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getSeckillHeaderApi,
		getSeckillListApi
	} from '../../../api/activity.js';
	import animationType from '@/utils/animationType.js'
	import countDown from "@/components/countDown";
	import easyLoadimage from '@/components/base/easy-loadimage.vue'
	import navBar from '@/components/navBar';
	import emptyPage from '@/components/emptyPage.vue'
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import {
		goShopDetail
	} from '@/libs/order.js'
	let app = getApp();
	let sysHeight = uni.getSystemInfoSync().statusBarHeight + 'px';
	export default {
		components: {
			countDown,
			easyLoadimage,
			navBar,
			emptyPage
		},
		created() {
			var that = this
			// 获取设备宽度
			uni.getSystemInfo({
				success(e) {
					that.mainWidth = e.windowWidth
					that.isWidth = (e.windowWidth - 65) / 4
				}
			})
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				listMarTop: 0,
				backgroundColor: false,
				showSkeleton: true, //骨架屏显示隐藏
				isNodes: 0, //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
				circular: true,
				autoplay: true,
				interval: 500,
				topImage: '',
				seckillList: [],
				timeList: [],
				active: 0,
				scrollLeft: 0,
				interval: 0,
				status: 1,
				countDownHour: "00",
				countDownMinute: "00",
				countDownSecond: "00",
				page: 1,
				limit: 10,
				loading: true,
				loadend: false,
				timeLine: [],
				returnShow: true,
				navH: '',
				theme: app.globalData.theme,
				isLeft: 0,
				mainWidth: 0,
				tabLeft: 0,
				isWidth: 0,
				info: {},
				desc: '',
				datatime: 0,
				bgColor: {
					'bgColor': '',
					'Color': '#fff',
					'width': '36rpx',
					'timeTxtwidth': '16rpx',
					'isDay': true
				},
				marTop: 0,
				activeTime: {},
				loadTitle: '',
				sysHeight: sysHeight
			}
		},
		onLoad() {
			this.bgColor.bgColor = setThemeColor();
			setTimeout(() => {
				this.isNodes++;
			}, 500);
			var pages = getCurrentPages();
			this.returnShow = pages.length === 1 ? false : true;
			// #ifdef H5
			this.navH = app.globalData.navHeight - 18;
			// #endif
			this.getSeckillConfig();
		},
		methods: {
			/*
			 *去商品详情页 
			 */
			goDetail(id) {
				// #ifdef MP
				uni.navigateTo({
					url: `/pages/goods/goods_details/index?id=${id}&type=seckill&status=${this.status}&datatime=${this.datatime}`
				})
				// #endif
				// #ifndef MP
				uni.navigateTo({
					animationType: animationType.type,
					animationDuration: animationType.duration,
					url: `/pages/goods/goods_details/index?id=${id}&type=seckill&status=${this.status}&datatime=${this.datatime}`
				})
				// #endif
			},
			getNavH(marTop) {
				this.marTop = marTop;
				this.$nextTick(function() {
					const query = uni.createSelectorQuery().in(this);
					query.select('.scroll_box_item').boundingClientRect(data => {
						// #ifdef MP
						this.listMarTop = this.marTop + data.height * 2 + 38 + 38
						// #endif
						// #ifdef APP-PLUS
						this.listMarTop = this.marTop + data.height * 2 + 30 + 30
						// #endif
					}).exec();
				});
			},
			checkSeckill(index, item) {
				let that = this;
				that.active = index;
				that.isLeft = -(index * that.isWidth) //设置下划线位置	
				that.tabLeft = (index - 2) * this.isWidth //设置下划线位置
				this.status = item.status;
				if (item.status == 1) {
					this.desc = '距本场结束';
					this.datatime = item.endTimeStamp / 1000;
				} else if (item.status == 2 || item.status == 3) {
					this.desc = '距本场开始';
					this.datatime = item.startTimeStamp / 1000;
				} else if (item.status == 0) {
					this.desc = '已结束';
				}
				that.loading = false;
				that.loadend = false;
				this.page = 1;
				this.seckillList = [];
				this.activeTime = item;
				that.getSeckillList(item);
			},
			goBack: function() {
				uni.navigateBack();
			},
			getSeckillConfig: function() {
				getSeckillHeaderApi().then(res => {
					this.timeLine = res.data;
					//找出第一个进行中的下标
					this.active = this.timeLine.length ? res.data.findIndex(item => item.status === 1 || item
						.status === 2) : 0;
					this.$nextTick(() => {
						this.tabLeft = (this.active - 2) * this.isWidth //选中的位置放在第一位
					})

					if (res.data[this.active].status == 1) {
						this.desc = '距本场结束';
						this.datatime = res.data[this.active].endTimeStamp / 1000;
						this.status = 1;
					} else if (res.data[this.active].status == 2) {
						this.desc = '距本场开始';
						this.status = 2;
						this.datatime = res.data[this.active].startTimeStamp / 1000;
					} else if (res.data[this.active].status == 0) {
						this.desc = '已结束';
						this.status = 0;
					}
					this.loading = false;
					this.loadend = false;
					this.page = 1;
					this.seckillList = [];
					let item = this.timeLine[this.active];
					this.activeTime = item;
					this.getSeckillList(this.activeTime);

				});
			},
			getSeckillList: function(item) {
				var that = this;
				var data = {
					page: that.page,
					limit: that.limit,
					date: item.date,
					startTime: item.startTime,
					endTime: item.endTime
				};
				if (that.loadend) return;
				if (that.loading) return;
				that.loadTitle = '';
				that.loading = true
				getSeckillListApi(data).then(res => {
					this.$set(this, 'page', this.page + 1);
					this.seckillList = this.seckillList.concat(res.data.list || []);
					this.loadend = this.page > res.data.totalPage;
					that.loadTitle = this.loadend ? '已全部加载' : '加载更多';
					that.loading = false;
					// #ifdef H5
					that.setShare();
					// #endif
					that.showSkeleton = false
				}).catch(err => {
					that.loading = false
				});
			},
			settimeList: function(item, index) {
				if (index !== this.active) {
					var that = this;
					this.active = index
					if (that.interval) {
						clearInterval(that.interval);
						that.interval = null
					}
					that.interval = 0,
						that.countDownHour = "00";
					that.countDownMinute = "00";
					that.countDownSecond = "00";
					that.status = that.timeLine[that.active].status;
					that.loading = false;
					that.loadend = false;
					that.page = 1;
					that.seckillList = [];
					that.activeTime = item;
					that.getSeckillList(that.activeTime);
				}
			},
			goDetails(item) {
				uni.navigateTo({
					animationType: animationType.type,
					animationDuration: animationType.duration,
					url: '/pages/activity/goods_seckill_details/index?id=' + item.id
				})
			},
			setShare: function() {
				this.$wechat.isWeixin() &&
					this.$wechat.wechatEvevt([
						"updateAppMessageShareData",
						"updateTimelineShareData",
						"onMenuShareAppMessage",
						"onMenuShareTimeline"
					], {
						desc: this.seckillList[0].title,
						title: this.seckillList[0].title,
						link: location.href,
						imgUrl: this.seckillList[0].image
					}).then(res => {}).catch(err => {
						console.log(err);
					});
			},
		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		},
		/**
		 * 页面上拉触底事件的处理函数
		 */
		onReachBottom: function() {
			this.getSeckillList(this.activeTime);
		}
	}
</script>

<style lang="scss" scoped>
	.mts {
		margin-top: -56rpx;
	}

	.middle {
		width: 24rpx;
		height: 82rpx;
		background-image: url('../static/images/middle.png');
		background-size: cover;
	}

	.datatimeBox {
		height: 36rpx;
		line-height: 36rpx;
	}

	.friceIcon {
		font-size: 28rpx;
	}

	.f-w-700 {
		font-family: PingFang SC-中黑体, PingFang SC;
		font-weight: 700;
	}

	.futura {
		margin-top: -6rpx;
	}

	.f-s-17 {
		font-size: 17rpx;
	}

	.seckill_header {
		background-image: url('../static/images/seckill_bg.jpg');
		background-size: cover;
		top: 0;
		left: 0;
		z-index: 99999;
		/* #ifdef H5 */
		height: 210rpx;
		/* #endif */
		/* #ifndef H5*/
		height: 380rpx;
		/* #endif */
		position: fixed;
	}

	.head-menu {
		display: flex;
		align-items: center;
		height: 54rpx;
		width: 140rpx;
		background: transparent;
		border: 1px solid rgba(151, 151, 151, 0.2);
		border-radius: 27rpx;
		position: relative;

		&:after {
			content: '';
			position: absolute;
			width: 1px;
			height: 26rpx;
			background-color: #EAEAEA;
			left: 50%;
			top: 50%;
			transform: translate(-50%, -50%);
		}

		.iconfont {
			flex: 1;
			text-align: center;
			color: #fff;
			box-sizing: border-box;
		}
	}

	.seckill_title {
		width: 144rpx;
		height: 36rpx;
	}

	.scroll_box_item {
		width: 100%;
		height: 92rpx;
	}

	.scroll_box {
		height: 104rpx;
		//z-index: 999999;
		//position: fixed;
		//margin-bottom: 38rpx;
		//padding: 48rpx 0 !important;
	}

	.scroll_item {
		display: inline-block;
		height: 92rpx;
	}

	.header_time {
		font-size: 36rpx;
		color: rgba(255, 255, 255, .4);
		display: inline-block;
		margin-bottom: 10rpx;
		transition: all .3s ease;
		font-family: PingFang SC-中黑体, PingFang SC;
	}

	.active {
		color: #fff;
	}

	.active_day {
		width: 108rpx;
		height: 30rpx;
		line-height: 30rpx;
		text-align: center;
		font-size: 22rpx;
		color: #fff !important;
		@include linear-gradient(theme);
		background-size: contain;
		background-repeat: no-repeat;
	}

	.header_day {
		font-size: 24rpx;
		color: rgba(255, 255, 255, .4);
		font-family: PingFang SC-中黑体, PingFang SC;
	}

	.seckill_box {
		width: 100%;
		background-color: #fff;
		left: 0;
		position: relative;
		/* #ifdef H5 */
		margin-top: 172rpx !important;
		/* #endif */
	}

	.seckill_desc {
		background: linear-gradient(360deg, #FFFFFF 0%, #F4F4F4 100%);
		border-radius: 24rpx 24rpx 0px 0px;
		padding: 0 24rpx;
		width: 100%;
		height: 117rpx;
		position: fixed;
		z-index: 99999;
	}

	.seckill_name {
		width: 222rpx;
		height: 57rpx;
		background-image: url('../static/images/miaos.png');
		background-size: contain;
		background-repeat: no-repeat;
		padding-bottom: 10rpx;
	}

	.seckill_discount {
		font-size: 32rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #AC6926;
	}

	.text-right {
		text-align: right;
	}

	.count_down {}

	/deep/ .is_day {
		font-size: 24rpx;
		font-weight: 500;
		color: #1B1B1B;
	}

	.seckill_list {
		background-color: #fff;
		padding: 117rpx 24rpx 24rpx 24rpx;
		overflow-y: scroll;
	}

	.seckill_item {
		height: 300rpx;
		background: #FFFFFF;
		border-radius: 14rpx;
		border: 1px solid #E6E6E6;
		margin-bottom: 20rpx;
		padding: 20rpx;
		box-sizing: border-box;

		.picture,
		/deep/.easy-loadimage image {
			width: 254rpx;
			height: 254rpx;
			display: block;
			border-radius: 8rpx;
			margin-right: 16rpx;
		}

		.pro_name {
			width: 386rpx;
			// height: 72rpx;
			font-size: 28rpx;
			font-family: PingFang SC-中黑体, PingFang SC;
			color: #333333;
			// line-height: 36rpx;
		}
	}

	.seckill_zhekou {
		width: 100%;
		height: 76rpx;
		background-image: url('../static/images/seckill_discount.png');
		background-position: bottom;
		background-repeat: no-repeat;
		background-size: contain;

		.title {
			margin-top: -20rpx;
		}
	}

	.seckill_btn {
		background-size: contain;
		background-repeat: no-repeat;
		width: 388rpx;
		height: 82rpx;
		// padding: 0 16rpx 0;
		border-radius: 8rpx;
	}

	.seckill_btn_left {
		width: 190rpx;
		height: 82rpx;
		line-height: 82rpx;
		text-align: center;
		color: #FFD39D;
		font-size: 36rpx;
		font-family: PingFang SC-中黑体, PingFang SC;
		background: linear-gradient(300deg, #22170F 0%, #4B3A30 100%);
		opacity: 1;
		background-image: url('../static/images/seckill_btn_bg2.png');
		background-size: 100% 100%;
		background-repeat: no-repeat;
	}

	.seckill_btn_right {
		width: 174rpx;
		text-align: center;
		align-items: center;
	}

	.seckill_btn_bg1 {
		@include main_bg_color(theme);
	}

	.seckill_btn_bg2,
	.seckill_btn_bg3 {
		background-color: #BBBBBB;
	}

	.btn_text {
		width: 150rpx;
		color: #fff;
	}

	.zhekou_price {
		color: #888888;
		width: 122rpx;

	}

	.frice_hua {
		text-decoration: line-through;
	}

	.text-center {
		text-align: center;
	}

	.other_btn {
		width: 200rpx;
		height: 60rpx;
		font-size: 28rpx;
		font-weight: 500;
		color: #000;
		border-radius: 30rpx;
		border: 1px solid #000;
		margin: 30rpx 0;
	}

	.icon-shangpinye_gengduo {
		font-size: 24rpx;
		padding-left: 4rpx;
	}

	.pl-6 {
		padding-left: 6rpx;
	}

	.w80 {
		width: 60rpx;
	}

	.mt12 {
		margin-top: 8rpx;
	}

	.ml8 {
		margin-left: 8rpx;
	}

	.f-s-16 {
		font-size: 16rpx;
	}

	.pb-30 {
		padding-bottom: 30rpx;
	}

	.reduce_color {
		color: #AC6926;
	}

	.f-w-300 {
		font-weight: 300;
	}

	.f-s-20 {
		font-size: 20rpx;
	}

	.f-s-22 {
		font-size: 22rpx;
	}
</style>
