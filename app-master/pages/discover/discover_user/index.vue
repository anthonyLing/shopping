<template>
	<view class="container" :data-theme="theme">
		<view class="header">
			<view class="user_wrapper acea-row">
				<image v-if="userInfo.userLevelIcon" class="level_icon" :src="userInfo.userLevelIcon" alt="">
				<view class="acea-row row-middle">
					<image :src="userInfo.avatar || urlDomain+'crmebimage/presets/morenT.png'" class="picture"></image>
						<view class="user_text">
							<view class="name acea-row">
								<text>{{userInfo.isLogoff ? "用户已注销": (userInfo.nickname || '')}}</text>
							</view>
							<view v-if="userInfo.id" class="user_id">ID: {{userInfo.id || ''}}</view>
						</view>
				</view>
				<view v-if="id && id!=uid" @click.stop="followAuthor">
					<button v-if="!userInfo.isConcerned" class="follow_btn focus">
						<text class="iconfont icon-jiahao2"></text>关注
					</button>
					<button v-else class="follow_btn focused">已关注</button>
				</view>
				<view v-if="userInfo.id==uid">
					<navigator hover-class="none" url="/pages/discover/discover_release/index"
						class="follow_btn focus bg-color">
						去发布
					</navigator>
				</view>
			</view>
			<view class="acea-row plant_info" @click="onEdit">
				<view v-if="!userInfo.signature && userInfo.id==uid">点击可编辑个性签名</view>
				<view class="acea-row" v-if="userInfo.signature" style="width: 93%;">
					<view v-if="userInfo.isMore || userInfo.signature.length<=21">
						{{userInfo.signature}}
						<text v-if="userInfo.isMore" class="more" @click.stop="moreTap">收起</text>
					</view>
					<view v-else>
						<text class="info">{{userInfo.signature.slice(0,21)}}...</text>
						<text class="more" @click.stop="moreTap"> 展开</text>
					</view>
				</view>
				<text v-show="userInfo.id==uid" class="ml10 iconfont icon-fabuzhongcao"></text>
			</view>

			<view class="plant_info" style="padding-bottom: 30rpx;">
				<view class="count_wrapper acea-row">
					<navigator :url="!id ? '/pages/discover/discover_follow/index?type=follow' : ''" class="item"
						hover-class="none">
						<text class="mr10">{{userInfo.concernedNum}}</text> 关注
					</navigator>
					<navigator :url="!id ? '/pages/discover/discover_follow/index?type=fans' : ''" class="item"
						hover-class="none">
						<text class="mr10">{{userInfo.fansNum}}</text> 粉丝
					</navigator>
					<view class="item">
						<text class="mr10">{{userInfo.likeNum}}</text> 获赞
					</view>
				</view>
			</view>
			<view v-if="!id && isShow" class="tab_count">
				<text @click.stop="changeTab(0)" :class="tab==0 ? 'on' : ''">作品</text>
				<text @click.stop="changeTab(1)" :class="tab==1 ? 'on' : ''">赞过</text>
			</view>
		</view>
		<view class="main">
			<view class="tab-conts">
				<view v-if="list.length > 0" class="goods-wrap">
					<view class="goods">
						<WaterfallsFlow v-if="list.length" :wfList="list" :fromType="1" fromTo="home">
						</WaterfallsFlow>
					</view>
				</view>
				<view class="empty-boxs noContent" v-if="list.length == 0 && !loading">
					<emptyPage title="暂无更多内容~" mTop="13%">
						<div slot='emptysrc'>
							<image :src="urlDomain+'crmebimage/presets/noNei.png'"></image>
						</div>
					</emptyPage>
				</view>
				<view class='loadingicon acea-row row-center-wrapper'>
					<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>
					<view class="end" :hidden="loading || list.length == 0"><text
							:class="loaded ? 'loaded' : ''">{{loadTitle}}</text>
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
	import WaterfallsFlow from '@/components/WaterfallsFlow/WaterfallsFlow.vue'
	import WaterfallsFlowItem from '@/components/discoverFlowItem/discoverFlowItem.vue'
	import emptyPage from '@/components/emptyPage.vue';
	import {
		userHomeApi,
		myHomeApi,
		authorNoteApi,
		myNoteApi,
		myLikeListApi,
		editSignatureApi
	} from '@/api/discover.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		discoverFollowAuthor
	} from '@/libs/follow.js';
	const app = getApp();
	export default {
		components: {
			WaterfallsFlow,
			WaterfallsFlowItem,
			emptyPage
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				theme: app.globalData.theme,
				list: [], // 内容列表
				loadTitle: '加载更多',
				loading: false,
				loaded: false,
				isShowAuth: false, //是否隐藏授权
				isAuto: false, //没有授权的不会自动授权
				userInfo: {},
				where: {
					page: 1,
					limit: 10
				},
				tab: 0,
				id: '', //作者id
				isShow: false
			}
		},
		created() {},
		computed: {
			...mapGetters(['isLogin', 'uid']),
		},
		watch: {},
		onLoad: function(options) {
			this.id = options.id ? options.id : '';
		},
		onShow() {
			if (this.id) {
				this.isShow = false
				this.getUserHome(this.id);
				this.authorNoteList();
			} else {
				this.isShow = true
				this.getMyHome();
				this.myNoteList();
			}
		},
		onReady() {},
		mounted: function() {},
		methods: {
			moreTap() {
				this.$set(this.userInfo, 'isMore', !this.userInfo.isMore)
			},
			//编辑个性签名
			onEdit() {
				if(this.userInfo.id==this.uid){
					uni.showModal({
						title: '个性签名',
						content: this.userInfo.signature,
						placeholderText: this.userInfo.signature,
						editable: true,
						success: res => {
							if (res.confirm) {
								editSignatureApi({
									signature: res.content
								}).then(ress => {
									this.userInfo.signature = res.content
								}).catch(err => {
									uni.showToast({
										title: err,
										icon: 'none'
									})
								});
							} else if (res.cancel) {
								console.log('用户点击取消');
							}
						}
					});
				}
				
			},
			//回退页面之后重新加载页面
			reFresh() {
				this.loading = false;
				this.loaded = false;
				this.where = {
					page: 1,
					limit: 10
				}
				this.list = []
				if (this.id) {
					this.getUserHome(this.id);
					this.authorNoteList();
				} else {
					this.getMyHome();
					this.myNoteList();
				}
			},
			//获取我的用户信息
			getMyHome(id) {
				myHomeApi(id).then(res => {
					this.userInfo = res.data;
					this.isMore = false;
				}).catch(err => {
					uni.showToast({
						title: err,
						icon: 'none'
					})
				});
			},
			//获取用户信息
			getUserHome(id) {
				userHomeApi(id).then(res => {
					this.userInfo = res.data
				}).catch(err => {
					uni.showToast({
						title: err,
						icon: 'none'
					})
				});
			},
			// 关注作者
			followAuthor: function() {
				if (this.isLogin) {
					if(this.userInfo.isLogoff) return;
					discoverFollowAuthor(this.id).then(() => {
						this.$set(this.userInfo, 'isConcerned', !this.userInfo.isConcerned);
					});
				} else {
					toLogin();
				}
			},
			changeTab(tab) {
				this.tab = tab
				this.where.page = 1
				this.loaded = this.loading = false
				this.list = []
				if (tab === 0) {
					this.myNoteList();
				} else {
					this.myLikeList();
				}
			},
			// 获取我的点赞作品
			myLikeList: function() {
				let that = this;
				if (that.loaded || that.loading) return;
				that.loading = true;
				that.loadTitle = '';
				myLikeListApi(that.where).then(res => {
					let list = res.data.list;
					let goods = that.$util.SplitArray(list, that.list);
					that.loaded = list.length < that.where.limit;
					that.loading = false;
					that.loadTitle = that.loaded ? '到底了' : '加载更多';
					that.$set(that, 'list', goods);
					that.$set(that.where, 'page', that.where.page + 1);
				}).catch(err => {
					that.loading = false;
					uni.showToast({
						title: err,
						icon: 'none'
					})
				})
			},
			// 获取我的的作品 
			myNoteList: function() {
				let that = this;
				if (that.loaded || that.loading) return;
				that.loading = true;
				that.loadTitle = '';
				myNoteApi(that.where).then(res => {
					let list = res.data.list;
					let goods = that.$util.SplitArray(list, that.list);
					that.loaded = list.length < that.where.limit;
					that.loading = false;
					that.loadTitle = that.loaded ? '到底了' : '加载更多';
					that.$set(that, 'list', goods);
					that.$set(that.where, 'page', that.where.page + 1);
				}).catch(err => {
					that.loading = false;
					uni.showToast({
						title: err,
						icon: 'none'
					})
				})
			},
			// 作者的作品
			authorNoteList: function() {
				let that = this;
				if (that.loaded || that.loading) return;
				that.loading = true;
				that.loadTitle = '';
				this.where.authorId = this.id
				authorNoteApi(this.where.authorId, that.where).then(res => {
					let list = res.data.list;
					let goods = that.$util.SplitArray(list, that.list);
					that.loaded = list.length < that.where.limit;
					that.loading = false;
					that.loadTitle = that.loaded ? '到底了' : '加载更多';
					that.$set(that, 'list', goods);
					that.$set(that.where, 'page', that.where.page + 1);
				}).catch(err => {
					that.loading = false;
					uni.showToast({
						title: err,
						icon: 'none'
					})
				})
			}
		},
		onReachBottom() {
			if (this.id) {
				this.authorNoteList();
			} else {
				if (this.tab === 0) {
					this.myNoteList();
				} else {
					this.myLikeList();
				}
			}
		},
		onPullDownRefresh() {},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		}
	}
</script>

<style lang="scss" scoped>
	.goods-wrap{
		padding-top: 24rpx;
	}
	.more {
		font-weight: 600;
		margin-left: 8rpx;
	}

	.container {
		background: #ffffff;
		min-height: 100vh;
	}

	.header {
		width: 750rpx;
		padding: 60rpx 0 0;
		box-sizing: border-box;
		background: linear-gradient(360deg, #72798E 0%, #384857 99%);
		opacity: 1;

		.tab_count {
			text-align: center;
			background: #ffffff;
			padding: 15px 0 15rpx 0;
			border-radius: 12px 12px 0 0;

			text {
				font-size: 28rpx;
				color: #999999;
				margin: 0 30rpx;
				position: relative;
				padding-bottom: 14rpx;

				&.on {
					font-size: 32rpx;
					color: #282828;
					font-weight: bold;

					&::after {
						content: "";
						width: 40rpx;
						height: 5rpx;
						@include main_bg_color(theme);
						position: absolute;
						bottom: 0;
						left: 10rpx;
					}

					.underline {
						opacity: 1;
					}
				}
			}
		}

		.user_wrapper {
			align-items: center;
			justify-content: space-between;
			padding: 0 34rpx;
			position: relative;

			.level_icon {
				position: absolute;
				width: 38rpx;
				height: 38rpx;
				margin: 4rpx 0 0 6rpx;
				border: none;
				z-index: 11;
				bottom: 2rpx;
				left: 124rpx;
			}

			.image,
			uni-image,
			image {
				width: 140rpx;
				height: 140rpx;
				border-radius: 100%;
				border: 4rpx solid #ffffff;
			}

			.user_text {
				margin-left: 30rpx;
				color: #FFFFFF;

				.name {
					font-size: 34rpx;
					font-weight: bold;
					align-items: center;
				}

				.user_id {
					margin-top: 16rpx;
					font-size: 24rpx;
				}
			}
		}

		.plant_info {
			padding: 0 34rpx;
			margin-top: 30rpx;
			align-items: center;
			justify-content: space-between;
			color: #ffffff;
			font-size: 24rpx;

			.count_wrapper {
				color: #FFFFFF;
				font-size: 24rpx;

				.item {
					text-align: center;
					margin-right: 60rpx;

					&:last-child {
						margin-right: 0;
					}

					text {
						font-size: 30rpx;
						margin-top: 10rpx;
					}
				}
			}
		}

		.iconfont {
			font-size: 24rpx;
			margin-right: 12rpx;
		}

		.follow_btn {
			color: #ffffff;
			font-size: 26rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			width: 146rpx;
			height: 62rpx;
			@include linear-gradient(theme);
			border-radius: 33rpx;

			.icon-fabu {
				font-size: 24rpx;
			}

			&.focused {
				background: transparent;
				color: #FFFFFF;
				border: 1px solid #FFFFFF;
			}
		}
	}

	.main {
		background: #ffffff;
		position: relative;
	}

	.goods {
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
		width: 750rpx;
		padding: 0 24rpx;
	}

	.empty {
		margin: 130rpx 0 150rpx;
		text-align: center;

		image,
		uni-image {
			display: inline-block;
			width: 414rpx;
			height: 305rpx;
		}

		text {
			display: block;
			color: #999999;
			font-size: 26rpx;
		}
	}

	.end {
		margin-top: 50rpx 0;
		text-align: center;

		text {
			color: #999999;
			font-size: 22rpx;
			position: relative;

			&.loaded {

				&::before,
				&::after {
					content: "";
					display: inline-block;
					width: 22rpx;
					height: 1rpx;
					background: #999999;
					position: absolute;
					top: 18rpx;
					opacity: .5;
				}

				&::before {
					left: -30rpx;
				}

				&::after {
					right: -30rpx;
				}
			}
		}
	}
</style>