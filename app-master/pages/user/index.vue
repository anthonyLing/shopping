<template>
	<view :data-theme="theme">
		<tui-skeleton v-if="showSkeleton"></tui-skeleton>
		<view class="new-users copy-data tui-skeleton">
			<view class="mid" style="flex:1;overflow: hidden;">
				<scroll-view scroll-y="true">
					<view class="head borderPad">
						<view class="bg" :style="{'background-image': `url(${urlDomain}crmebimage/presets/user_bg.png)`}">
							<view class="user-card">
								<view class="user-info">
									<image class="avatar tui-skeleton-rect" :src='userInfo.avatar'
										v-if="userInfo.avatar" @click="goEdit()"></image>
									<image v-else class="avatar tui-skeleton-rect" :src="urlDomain+'crmebimage/presets/morenT.png'"
										mode="" @click="goEdit()">
									</image>
									<view class="info">
										<view class="name" v-if="!isLogin" @tap="openAuto">
											请点击登录
										</view>
										<view class="name" v-if="userInfo && uid" @click="goEdit()">
											{{userInfo && userInfo.nickname && uid ? userInfo.nickname : ''}}
											<view class="vip" v-if="userInfo.vipIcon">
												<image :src="userInfo.vipIcon" alt="">
													<!-- <view style="margin-left: 10rpx;" class="vip-txt">
														{{userInfo.vipName || ''}}
													</view> -->
											</view>
										</view>
										<view class="num mt10" v-if="userInfo && userInfo.phone && uid"
											@click="goEdit()">
											<view class="num-txt">{{userInfo.phone}}</view>
										</view>
									</view>
									<view class="app_set" @click="handleUserSetting()">
										<text class="iconfont icon-shezhi"></text>
									</view>
								</view>
								<view class="num-wrapper tui-skeleton-rect">
									<view class="num-item" @click="goMenuPage('/pages/merchant/user_integral/index')">
										<text class="num">{{userInfo.integral && uid ? userInfo.integral: 0}}</text>
										<view class="txt">积分</view>
									</view>
									<view class="num-item" @click="goMenuPage('/pages/users/user_coupon/index')">
										<text
											class="num">{{userInfo.couponCount && uid ? userInfo.couponCount : 0}}</text>
										<view class="txt">优惠券</view>
									</view>
									<view class="num-item"
										@click="goMenuPage('/pages/goods/user_goods_collection/index')">
										<text
											class="num">{{userInfo.collectCount && uid ? userInfo.collectCount : 0}}</text>
										<view class="txt">收藏</view>
									</view>
									<view class="num-item" @click="goMenuPage('/pages/goods/browsing_history/index')">
										<text class="num">{{userInfo.browseNum && uid ? userInfo.browseNum:0}}</text>
										<view class="txt">浏览记录</view>
									</view>
								</view>
							</view>
						</view>
						<view class="order-wrapper tui-skeleton-rect">
							<view class="order-hd flex">
								<view class="left">订单中心</view>
								<view class="right flex" @click="menusTap('/pages/goods/order_list/index')">查看全部
									<text class="iconfont icon-xiangyou"></text>
								</view>
							</view>
							<view class="order-bd">
								<block v-for="(item,index) in orderMenu" :key="index">
									<view class="order-item" @click="menusTap(item.url)">
										<view class="pic">
											<text class="iconfont pic_status" :class="item.img"></text>
											<text class="order-status-num" v-if="item.num > 0">{{ item.num }}</text>
										</view>
										<view class="txt tui-skeleton-rect">{{item.title}}</view>
									</view>
								</block>
							</view>
						</view>
					</view>
					<view class="contenBox borderPad" id="pageIndex">
						<!-- 轮播 -->
						<view class="slider-wrapper tui-skeleton-rect" @click.native="bindEdit('userBanner')"
							v-if="centerBanner != null && centerBanner.length > 0">
							<swiper v-if="centerBanner.length>0" indicator-dots="true" :autoplay="autoplay"
								:circular="circular" :interval="interval" :duration="duration"
								indicator-color="rgba(255,255,255,0.6)" indicator-active-color="#fff">
								<block v-for="(item,index) in centerBanner" :key="index">
									<swiper-item class="borRadius14">
										<image :src="item.pic" class="slide-image" @click="navito(item.url)"></image>
									</swiper-item>
								</block>
							</swiper>
						</view>
						<!-- 会员菜单 -->
						<view class="user-menus" style="margin-top: 20rpx;" @click.native="bindEdit('userMenus')">
							<view class="menu-title">我的服务</view>
							<view class="list-box">
								<block v-for="(item,index) in centerMenu" :key="index">
									<view class="item tui-skeleton-rect" @click="menusTap(item.url)"
										v-if="!(item.url =='/pages/service/index' || (item.url =='/pages/users/user_spread_user/index' && !userInfo.isPromoter))">
										<image :src="item.pic"></image>
										<text>{{item.name}}</text>
									</view>
								</block>
								<!-- #ifndef MP -->
								<view class="item" @click="kefuClick">
									<image :src="servicePic"></image>
									<text>联系客服</text>
								</view>
								<!-- #endif -->
								<!-- #ifdef MP -->
								<!--  v-if="chatConfig.telephone_service_switch" -->
								<button class="item" hover-class='none' @click="kefuClick"
									v-if="chatConfig.telephone_service_switch === 'true'">
									<image :src="servicePic"></image>
									<text>联系客服</text>
								</button>
								<button class="item" open-type='contact' hover-class='none' v-else>
									<image :src="servicePic"></image>
									<text>联系客服</text>
								</button>
								<!-- #endif -->
							</view>
						</view>
						<image :src="copyImage" alt="" class='support'>
					</view>
				</scroll-view>
			</view>
		</view>
		<view v-if="bottomNavigationIsCustom" class="footerBottom"></view>
		<pageFooter></pageFooter>
	</view>
</template>
<script>
	let sysHeight = uni.getSystemInfoSync().statusBarHeight + 'px';
	import Cache from '@/utils/cache';
	import {
		goPage
	} from '@/libs/iframe.js'
	import {
		BACK_URL,
		USER_INFO
	} from '@/config/cache';
	import {
		userCenterInfo,
		copyrightImageApi
	} from '@/api/user.js';
	import {
		orderNum
	} from '@/api/order.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		getCityList
	} from "@/utils";
	import {
		tokenIsExistApi
	} from '@/api/api.js';
	// #ifdef H5
	import Auth from '@/libs/wechat';
	// #endif
	import {
		getShare
	} from '@/api/public.js';
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import animationType from '@/utils/animationType.js'
	import {
		chatConfig
	} from '@/utils/consumerType.js'
	import tuiSkeleton from '@/components/base/tui-skeleton.vue';
	import pageFooter from "@/components/pageFooter/index.vue";
	const app = getApp();
	export default {
		computed: mapGetters(['isLogin', 'chatUrl', 'uid', 'globalData', 'bottomNavigationIsCustom']),
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				showSkeleton: true, //骨架屏显示隐藏
				orderMenu: [{
						img: 'icon-daifukuan',
						title: '待付款',
						url: '/pages/goods/order_list/index?status=0',
						num: 0
					},
					{
						img: 'icon-daifahuo',
						title: '待发货',
						url: '/pages/goods/order_list/index?status=1',
						num: 0
					},
					{
						img: 'icon-daihexiao',
						title: '待核销',
						url: '/pages/goods/order_list/index?status=3',
						num: 0
					},
					{
						img: 'icon-daishouhuo',
						title: '待收货',
						url: '/pages/goods/order_list/index?status=4',
						num: 0
					},
					{
						img: 'icon-daipingjia',
						title: '待评价',
						url: '/pages/goods/evaluation_list/index',
						num: 0
					},
					{
						img: 'icon-a-shouhoutuikuan',
						title: '售后/退款',
						url: '/pages/goods/user_return_list/index',
						num: 0
					},
				],
				centerBanner: [],
				userMenu: [],
				autoplay: true,
				circular: true,
				interval: 3000,
				duration: 500,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				orderStatusNum: {},
				centerMenu: [],
				wechatUrl: [],
				servicePic: '',
				sysHeight: sysHeight,
				// #ifdef MP
				pageHeight: '100%',
				// #endif
				// #ifdef H5 || APP-PLUS
				pageHeight: '',
				// #endif
				// #ifdef H5
				isWeixin: Auth.isWeixin(),
				//#endif
				configApi: {}, //分享类容配置
				theme: app.globalData.theme,
				bgColor: '#e93323',
				userInfo: {},
				copyImage: ''
			}
		},
		components: {
			tuiSkeleton,
			pageFooter
		},
		onLoad() {
			this.servicePic = this.urlDomain + 'crmebimage/presets/customer.png'
			this.getTokenIsExist();
			if (this.globalData.isIframe) {
				setTimeout(() => {
					let active;
					document.getElementById('pageIndex').children.forEach(dom => {
						dom.addEventListener('click', (e) => {
							e.stopPropagation();
							e.preventDefault();
							if (dom === active) return;
							dom.classList.add('borderShow');
							active && active.classList.remove('borderShow');
							active = dom;
						})
					})
				});
			}
			let that = this;
			// #ifdef H5 || APP-PLUS
			that.$set(that, 'pageHeight', this.globalData.windowHeight);
			// #endif
			that.$set(that, 'centerMenu', this.globalData.centerMenu);
			// #ifdef H5
			//that.shareApi();
			// #endif
			that.bgColor = setThemeColor();
			// #ifdef APP-PLUS
			setTimeout(() => {
				uni.setNavigationBarColor({
					frontColor: '#ffffff',
					backgroundColor: that.bgColor,
				});
			}, 500)
			// #endif
			// #ifdef MP
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor: that.bgColor,
			});
			// #endif
		},
		onShow: function() {
			if (this.isLogin) {
				this.getUserCenterInfo();
				this.getOrderData();
			}
			this.copyrightImage();
			this.showSkeleton = false
			let that = this;
			if (!that.$Cache.get('cityList')) getCityList();
			// #ifdef H5
			uni.getSystemInfo({
				success: function(res) {
					that.pageHeight = res.windowHeight + 'px'
				}
			});
			// #endif
			// #ifdef MP
			let query = uni.createSelectorQuery();
			let dom = query.select('.new-users');
			// #endif
		},
		methods: {
			//校验token是否有效,true为有效，false为无效
			getTokenIsExist() {
				tokenIsExistApi().then(res => {
					let tokenIsExist = res.data;
					if (!tokenIsExist) {
						this.$store.commit('UPDATE_LOGIN', '');
						this.$store.commit('UPDATE_USERINFO', {});
					}
				})
			},
			copyrightImage() {
				copyrightImageApi().then(res => {
					if (res.data) {
						this.copyImage = res.data;
					} else {
						this.copyImage = `${this.urlDomain}crmebimage/presets/support.png`;
					}
				}).catch(err => {
					return this.$util.Tips({
						title: err
					})
				});
			},
			bindEdit(name) {
				if (this.globalData.isIframe) {
					window.parent.postMessage({
							name: name
						},
						'*'
					);
					return;
				}
			},
			menusTap(url) {
				if (!this.isLogin) {
					this.openAuto();
				} else {
					goPage().then(res => {
						uni.navigateTo({
							animationType: animationType.type,
							animationDuration: animationType.duration,
							url: url
						})
					})
				}
			},
			//轮播图跳转
			navito(url) {
				this.$util.navigateTo(url);
			},
			kefuClick() {
				chatConfig(this.$Cache.getItem('platChatConfig'));
			},
			getOrderData() {
				let that = this;
				orderNum().then(res => {
					that.orderMenu.forEach((item, index) => {
						switch (item.title) {
							case '待付款':
								item.num = res.data.awaitPayCount
								break
							case '待发货':
								item.num = res.data.awaitShippedCount
								break
							case '待收货':
								item.num = res.data.receiptCount
								break
							case '待核销':
								item.num = res.data.verificationCount
								break
							case '待评价':
								item.num = res.data.awaitReplyCount
								break
							default:
								item.num = res.data.refundCount
						}
					})
					that.$set(that, 'orderMenu', that.orderMenu);
				})
			},
			// 绑定手机
			bindPhone() {
				uni.navigateTo({
					animationType: animationType.type,
					animationDuration: animationType.duration,
					url: '/pages/users/app_login/index'
				})
			},
			/**
			 * 
			 * 获取个人中心详情
			 */
			getUserCenterInfo: function() {
				let that = this;
				//if (this.centerMenu.length) return;
				userCenterInfo().then(res => {
					let data = res.data;
					this.userInfo = data;
					this.$store.commit("SETUID", data.id);
					that.$set(that, 'centerMenu', data.centerMenu);
					if (data.centerBanner) {
						that.centerBanner = data.centerBanner
					}
					this.$store.commit('UPDATE_USERINFO', {
						avatar: data.avatar,
						nickname: data.nickname,
						phone: data.phone
					});
					this.showSkeleton = false;
				}).catch(err => {
					this.showSkeleton = false;
				});
			},
			// 编辑页面
			goEdit() {
				if (this.isLogin == false) {
					this.openAuto();
				} else {
					uni.navigateTo({
						animationType: animationType.type,
						animationDuration: animationType.duration,
						url: '/pages/users/user_info/index'
					})
				}
			},
			goMenuPage(url) {
				if (this.isLogin) {
					uni.navigateTo({
						animationType: animationType.type,
						animationDuration: animationType.duration,
						url
					})
				} else {
					// #ifdef MP
					this.openAuto()
					// #endif
				}
			},
			// 打开授权
			openAuto() {
				Cache.set(BACK_URL, '')
				toLogin();
			},
			// 去设置页面
			handleUserSetting() {
				if (!this.isLogin) {
					return this.openAuto();
				}
				uni.navigateTo({
					url: '/pages/users/user_setting/index'
				})
			},
			shareApi: function() {
				getShare().then(res => {
					this.$set(this, 'configApi', res.data);
					// #ifdef H5
					this.setOpenShare(res.data);
					// #endif
				})
			},
			// 微信分享；
			setOpenShare: function(data) {
				let that = this;
				if (that.$wechat.isWeixin()) {
					let configAppMessage = {
						desc: data.synopsis,
						title: data.title,
						link: location.href,
						imgUrl: data.img
					};
					that.$wechat.wechatEvevt(["updateAppMessageShareData", "updateTimelineShareData"],
						configAppMessage);
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.num-txt {
		padding: 6rpx 20rpx;
		border-radius: 36rpx;
		background: rgba(255, 255, 255, 0.2);
	}

	page,
	body {
		height: 100%;
	}

	.ma .mp-header {
		@include main_bg_color(theme);
	}

	.bg {
		width: 100%;
		background-repeat: no-repeat;
		background-size: 100% 100%;
	}

	.support {
		width: 219rpx;
		height: 74rpx;
		margin: 20rpx auto;
		display: block;
	}

	.new-users {
		display: flex;
		flex-direction: column;
		height: 100%;

		.sys-head {
			position: relative;
			width: 100%;
			background: linear-gradient(90deg, $bg-star1 0%, $bg-end1 100%);

			.sys-title {
				z-index: 10;
				position: relative;
				height: 43px;
				text-align: center;
				line-height: 43px;
				font-size: 36rpx;
				color: #FFFFFF;
			}
		}

		.head {
			width: 100%;
			background-repeat: no-repeat;
			background-size: 100% 100%;
			@include index-gradient(theme);

			.user-card {
				width: 100%;
				margin: 0 auto;
				padding: 35rpx 0 30rpx 0;

				.user-info {
					z-index: 20;
					display: flex;
					align-items: center;
					color: #fff;

					.avatar {
						width: 120rpx;
						height: 120rpx;
						border-radius: 50%;
					}

					.app_set {
						color: #fff;
						width: 140rpx;
						height: 110rpx;
						text-align: right;
						line-height: 110rpx;

						.icon-shezhi {
							font-size: 36rpx;
						}
					}

					.info {
						flex: 1;
						display: flex;
						flex-direction: column;
						justify-content: space-between;
						margin-left: 20rpx;
						padding: 15rpx 0;

						.name {
							display: flex;
							align-items: center;
							color: #fff;
							font-size: 31rpx;

							.vip {
								display: flex;
								align-items: center;
								border-radius: 18px;
								font-size: 20rpx;
								margin-left: 14rpx;

								image {
									width: 27rpx;
									height: 27rpx;
								}
							}
						}

						.num {
							display: flex;
							align-items: center;
							font-size: 26rpx;
							color: rgba(255, 255, 255, 0.6);

							image {
								width: 22rpx;
								height: 23rpx;
								margin-left: 20rpx;
							}
						}
					}
				}

				.num-wrapper {
					z-index: 30;
					position: relative;
					display: flex;
					align-items: center;
					justify-content: space-between;
					margin-top: 30rpx;
					color: #fff;

					.num-item {
						width: 33.33%;
						text-align: center;

						.num {
							font-size: 42rpx;
							font-weight: bold;
						}

						.txt {
							margin-top: 10rpx;
							font-size: 26rpx;
							color: rgba(255, 255, 255, 0.6);
						}
					}
				}

				.sign {
					z-index: 200;
					position: absolute;
					right: -12rpx;
					top: 80rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					width: 120rpx;
					height: 60rpx;
					background: linear-gradient(90deg, rgba(255, 225, 87, 1) 0%, rgba(238, 193, 15, 1) 100%);
					border-radius: 29rpx 4rpx 4rpx 29rpx;
					color: #282828;
					font-size: 28rpx;
					font-weight: bold;
				}
			}

			.order-wrapper {
				background-color: #fff;
				border-radius: 14rpx;
				padding: 30rpx 16rpx;
				position: relative;
				z-index: 11;

				.order-hd {
					justify-content: space-between;
					font-size: 30rpx;
					color: #282828;
					margin-bottom: 40rpx;
					padding: 0 16rpx;

					.left {
						color: #282828;
						font-size: 30rpx;
						font-weight: 600;
					}

					.right {
						align-items: center;
						color: #666666;
						font-size: 26rpx;

						.icon-xiangyou {
							margin-left: 5rpx;
							font-size: 24rpx;
						}
					}
				}

				.order-bd {
					display: flex;
					justify-content: space-between;
					padding: 0;

					.order-item {
						display: flex;
						flex-direction: column;
						justify-content: center;
						align-items: center;

						.pic {
							position: relative;
							text-align: center;

							image {
								width: 48rpx;
								height: 48rpx;
							}
						}

						.txt {
							margin-top: 15rpx;
							font-size: 26rpx;
							color: #454545;
						}
					}
				}
			}
		}

		.slider-wrapper {
			margin: 20rpx 0;
			height: 138rpx;

			swiper,
			swiper-item {
				height: 100%;
			}

			image {
				width: 100%;
				height: 100%;
			}
		}

		.user-menus {
			background-color: #fff;
			border-radius: 14rpx;

			.menu-title {
				padding: 30rpx 30rpx 40rpx;
				font-size: 30rpx;
				color: #282828;
				font-weight: 600;
			}

			.list-box {
				display: flex;
				flex-wrap: wrap;
				padding: 0;
			}

			.item {
				position: relative;
				display: flex;
				align-items: center;
				justify-content: space-between;
				flex-direction: column;
				width: 25%;
				margin-bottom: 47rpx;
				font-size: 26rpx;
				color: #333333;

				image {
					width: 52rpx;
					height: 52rpx;
					margin-bottom: 18rpx;
				}


				&:last-child::before {
					display: none;
				}
			}

			button {
				font-size: 28rpx;
			}
		}

		.phone {
			color: #fff;
		}

		.pic_status {
			font-size: 43rpx;
			@include main_color(theme);
		}

		.order-status-num {
			min-width: 13rpx;
			background-color: #fff;
			@include main_color(theme);
			border-radius: 15px;
			position: absolute;
			right: -14rpx;
			top: -15rpx;
			font-size: 20rpx;
			padding: 0 8rpx;
			@include coupons_border_color(theme);
		}

	}

	.sub_btn {
		width: 690rpx;
		height: 86rpx;
		line-height: 86rpx;
		margin-top: 60rpx;
		background: $theme-color;
		border-radius: 43rpx;
		color: #fff;
		font-size: 28rpx;
		text-align: center;
	}
</style>