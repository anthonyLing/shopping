<template>
	<view v-if="noteDetail" class="main_content" :data-theme="theme" :class="!noteDetail.platReplySwitch?'bodyNo':''">
		<view v-if="fromTo!=='detail' && noteDetail.platReplySwitch" class="header">
			<text class="title">评论 {{noteDetail.replyNum?noteDetail.replyNum:0}}</text>
			<text class="iconfont icon-guanbi5" @click="close"></text>
		</view>
		<!-- 评论列表 -->
		<scroll-view class="bottom" scroll-y="true" @scrolltolower="onTouchmove" @scroll="followScroll" v-if="noteDetail.platReplySwitch">
			<view class="container">
				<view v-if="list.length > 0" id="reply">
					<view class="common_list" v-for="(item, index) in list" :key="index">
						<view class="commen_one">
							<image :src="item.avatar || urlDomain+'crmebimage/presets/morenT.png'" class="image"></image>
						</view>
						<view class="info_count">
							<view class="info">
								<view class="message" @click.stop="toReply(item,index)">
									<view v-if="item.nickname" class="name">{{item.nickname}}</view>
									<view class="desc acea-row row-middle">
										<view v-if="item.auditStatus==0" class="auditStatus">
											审核中
										</view>
										{{item.content}}
									</view>
									<view class="acea-row row-middle" style="margin-top: 16rpx;">
										<view class="time">{{item.createTime?$util.getDateDiff(item.createTime):''}}
										</view>
										<text @click.stop="onDel(item, index)" v-if="item.uid == uid"
											class="del">删除</text>
									</view>
								</view>
								<view class="like" @click.stop="starComment(item)">
									<view class="iconfont" :class="item.isLike ? 'icon-yidianzan' : 'icon-dianzan1'">
									</view>
									{{item.countStart?item.countStart:0}}
								</view>
							</view>
							<view v-if="item.replyList && item.replyList.length > 0" class="reply_count">
								<view v-for="(itemn,indexn) in item.replyList" :key="indexn" class="reply_list">
									<view class="item">
										<view class="item_count" @click.stop="toReply(itemn,index)">
											<image class="image" :src="itemn.avatar || urlDomain+'crmebimage/presets/morenT.png'">
											</image>
											<view v-if="itemn.nickname" class="name_two">{{itemn.nickname}}
											</view>
											<view class="desc_two">
												<text class="reply_user" v-if="Number(itemn.reviewUid) >0">回复
													@{{itemn.reviewUserNickname}} </text>{{itemn.content}}
											</view>
											<view class="acea-row row-middle" style="margin-top: 12rpx;">
												<view class="time_two">
													{{itemn.createTime?$util.getDateDiff(itemn.createTime):''}}</view>
												<text @click.stop="onDel(itemn, indexn, index)" v-if="itemn.uid == uid"
													class="del">删除</text>
											</view>

										</view>
										<view class="like_two" @click.stop="starComment(itemn)">
											<view class="iconfont"
												:class="itemn.isLike ? 'icon-yidianzan' : 'icon-dianzan1'">
											</view>
											{{itemn.countStart ? itemn.countStart : 0}}
										</view>
									</view>
								</view>
							</view>
						</view>

					</view>
					<view class="end"><text>到底了</text></view>
				</view>
				<view :hidden="!loading" class="acea-row row-center-wrapper loadingicon">
					<text class="iconfont icon-jiazai loading"></text>
				</view>
				<view v-if="list.length == 0 && !loading" class="empty-box">
					<image :src="urlDomain+'crmebimage/presets/noNei.png'"></image>
					<text>暂无评论，快去抢沙发吧~</text>
				</view>
			</view>
		</scroll-view>
		<!-- 内容详情中底部评论按钮 :disabled="noteDetail.replyStatus==2"-->
		<view class="release_bar acea-row fixed release_bar_detail" style="bottom: 0;"
			v-if="fromTo==='detail'&& !isShowComment">
			<view @click="parentPinglun" v-if="noteDetail.platReplySwitch" class="input_count"
				:class="{input_reply:content}">
				<text style="font-size: 13px; color: #999; margin-top: 7px; margin-left: 15px;">{{placeholder}}</text>
			</view>
			<view class="input_bar acea-row">
				<view class="item acea-row" @click.stop="likeToggle(noteDetail)">
					<text class="iconfont" :class="noteDetail.userIsLike ? 'icon-shoucang1' : 'icon-dianzan'"></text>
					<text>{{noteDetail.likeNum > 0 ? noteDetail.likeNum < 10000 ? noteDetail.likeNum : (noteDetail.likeNum / 10000).toFixed(2) : '点赞'}}</text>
				</view>
				<view v-if="noteDetail.platReplySwitch" class="item acea-row" @click="parentPinglun">
					<text class="iconfont icon-pinglun"></text>
					<text>{{noteDetail.replyNum > 0 ? noteDetail.replyNum : '评论'}}</text>
				</view>
			</view>
		</view>
		<!-- 评论弹窗评论触发处 -->
		<view v-if="fromTo!=='detail' && !isShowComment" @click="parentPinglun" class="release_bar bottoms">
			<image class="image" :src="userInfo.avatar || urlDomain+'crmebimage/presets/morenT.png'"></image>
			<view class="lang"
				:style="'height: 32px; margin-left: 15px; margin-top: 7px; background-color: #eee; border-radius: 50px; display: flex; flex-direction: row;'">
				<text style="font-size: 13px; color: #999; margin-top: 7px; margin-left: 15px;">{{placeholder}}</text>

			</view>
		</view>
		<!-- 真实评论弹窗 -->
		<view class="mask-popup" :class="isShowComment==true?'on':''">
			<view class="release_bar no-border" style="padding: 30rpx 24rpx;">
				<image class="image" :src="userInfo.avatar || urlDomain+'crmebimage/presets/morenT.png'"></image>
				<textarea :placeholder="placeholder" placeholder-style="color: #999999; font-size: 26rpx;"
					v-model="content" @blur="inputBindBlur" :cursor-spacing="cursorSpacing" confirm-hold
					:show-confirm-bar="false" class="input_count" :class="!isShowComment?'lang':''" :focus="autoFocus"
					:adjust-position="adjustPosition" auto-height />

				<button class="send" @click.stop="submitComment">发送</button>
			</view>
		</view>
		<view class='mask' catchtouchmove="true" :hidden='isShowComment==false' @tap="closeComment"></view>
	</view>
</template>

<script>
	import {
		mapGetters
	} from "vuex";
	import {
		discoverNoteLike
	} from '@/libs/follow.js';
	import {
		replyListApi,
		noteReplyAddApi,
		noteReplyLikeApi,
		replyDeleteApi
	} from '@/api/discover.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		Debounce
	} from '@/utils/validate.js'
	let app = getApp();
	export default {
		computed: mapGetters(['isLogin', 'userInfo', 'uid']),
		watch: {
			noteId: {
				handler: function(newV, oldV) {
					if (newV) {
						this.where.page = 1;
						this.loading = false;
						this.loaded = false;
						this.list = [];
						this.getList();

					}
				},
				deep: true
			},
			noteDetails: {
				handler: function(newV, oldV) {
					
					if (newV) {
						this.noteDetail = newV
					}
				},
				deep: true
			}
		},
		props: {
			/* 哪里引用，detail详情中引用 */
			fromTo: {
				type: String,
				default: ''
			},
			/* 内容id */
			noteId: {
				type: Number,
				default: 0
			},
			/* 逛逛详情 */
			noteDetails: {
				type: Object,
				default: function() {
					return {};
				},
			},
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				cursorSpacing: 20, //键盘距离输入框的距离
				adjustPosition: true, //默认
				autoFocus: false,
				noteDetail: this.noteDetails,
				theme: app.globalData.theme,
				content: '',
				isShowComment: false, //真实评论弹窗显示隐藏
				placeholder: "快来说点儿什么吧...",
				loadTitle: '加载更多',
				where: {
					page: 1,
					limit: 10,
					noteId: ''
				},
				list: [],
				loading: false,
				replyId: 0,
				focus: false,
				index: 0,
				isChild: false,
				bottomVal: 0
			}
		},
		mounted() {
			this.getList();
		},
		methods: {
			//删除自己的评论
			onDel(item, i, idx) {
				uni.showModal({
					title: '提示',
					content: '确认删除评论吗?',
					success: res => {
						if (res.confirm) {
							this.onSub(item, i, idx);
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
			},
			onSub(item, i ,idx) {
				uni.showLoading({
					title: '删除中...'
				});
				replyDeleteApi(item.id).then(res => {
					if(item.type ===1){
					  this.list.splice(i, 1);
					}else{
						this.list[idx].replyList.splice(i, 1)
					}
					uni.showToast({
						title: '删除成功',
						icon: 'none'
					})
					this.noteDetail.replyNum = res.data;
					this.$emit('getReplyNum', this.noteDetail.replyNum)
					uni.hideLoading();
				}).catch(err => {
					uni.hideLoading();
					uni.showToast({
						title: err,
						icon: 'none'
					})
				});
			},
			/*点赞评论*/
			starComment: Debounce(function(item) {
				let that = this;
				noteReplyLikeApi(item.id).then(res => {
					this.$set(item, 'isLike', !item.isLike);
					if (!item.isLike) {
						item.countStart--;
						item.countStart = item.countStart == 0 ? 0 : item.countStart
					} else {
						item.countStart++;
					}
				}).catch(err => {
					uni.showToast({
						title: err,
						icon: 'none'
					})
				});
			}),
			// 模拟触底刷新
			onTouchmove(e) {
				if (this.loadend || this.loading) return;
				this.getList();
			},
			/**
			 *  
			 */
			followScroll() {
				uni.$emit('scroll');
			},
			close() {
				this.$emit('close');
			},
			//点赞
			likeToggle: Debounce(function(item) {
				if (this.isLogin) {
					discoverNoteLike(item.id).then(() => {
						this.$set(item, 'userIsLike', !item.userIsLike);
						if (!item.userIsLike) {
							item.likeNum--;
							item.likeNum = item.likeNum == 0 ? 0 : item.likeNum
						} else {
							item.likeNum++;
						}
					});
				} else {
					toLogin();
				}
			}),
			//回复
			toReply(item, index) {
				if (item.auditStatus === 0) return this.$util.Tips({
					title: '审核中的评论不能进行回复'
				});
				this.placeholder = '回复：' + item.nickname
				this.replyId = item.id
				this.isChild = true
				this.index = index
				this.focus = true
				this.content = ""
			},
			clickTextarea() {
				this.isopen = false;
				if (uni.getSystemInfoSync().platform == 'ios') {
					this.autoFocus = false;
					setTimeout(() => {
						this.autoFocus = true;
					}, 200)
				}
			},
			linechange(event) {
				this.lineheight = event.detail.height
			},
			//真实评论弹窗 关闭
			closeComment() {

				this.autoFocus = false;
				this.placeholder = "快来说点儿什么吧...";
				this.content = ""
				this.isChild = false
				this.focus = false
				this.bottomVal = 0
				this.isShowComment = false;
			},
			oninput() {
				if (Number(this.noteDetail.replyStatus) > 1) {
					return this.$util.Tips({
						title: '该内容禁止评论'
					});
				} else {
					this.isShowComment = true
				}
			},
			parentPinglun() {
				if (!this.isLogin) {
					toLogin();
				} else {
					if (Number(this.noteDetail.replyStatus) > 1) {
						return this.$util.Tips({
							title: '该内容禁止评论'
						});
					} else {
						this.autoFocus = false;
						setTimeout(() => {
							this.autoFocus = true;
							this.isShowComment = true
						}, 50)
					}
				}
			},
			
            //失去焦点
			inputBindBlur() {
				//this.bottomVal = 0
				//this.isShowComment = false
				// input 失去焦点，键盘隐藏，设置 input 所在盒子的 bottom 值为0
				//this.$emit('changeBottomVal', 0)
			},
			//评论发送提交
			submitComment: Debounce(function() {
				noteReplyAddApi({
					content: this.content,
					noteId: this.noteId,
					replyId: this.replyId
				}).then(res => {
					this.isShowComment = false
					if (this.isChild) {
						if (this.list[this.index]['replyList']) {
							this.list[this.index]['replyList'].push(res.data)
						} else {
							this.list[this.index]['replyList'] = [res.data]
						}
					} else {
						this.list.unshift(res.data)
					}
					this.noteDetail.replyNum = res.data.noteReplyNum
					this.closeComment();
					this.$util.Tips({
						title: res.message
					});
				}).catch(err => {
					this.isShowComment = false
					uni.showToast({
						title: err,
						icon: 'none'
					})
				});
			}),
			//评论列表
			getList() {
				let that = this;
				if (that.loading || that.loaded) return;
				that.loading = true;
				that.where.noteId = this.noteId;

				replyListApi(that.where).then(res => {
						that.loading = false;
						that.all = res.data.all;
						that.loaded = res.data.list.length < that.where.limit;
						that.loadTitle = that.loadend ? '没有了' : '加载更多';
						that.list.push.apply(that.list, res.data.list);
						that.where.page = that.where.page + 1;
					},
					error => {
						that.loading = false;
						that.$util.Tips({
							title: error.msg
						})
					}
				);
			},
		}
	}
</script>

<style lang="scss" scoped>
	.del{
		font-size: 24rpx;
		color: #4A8AC9;
		display: inline-block;
	}
	.no-border {
		position: static !important;
		// border: none !important;
		background-color: #fff;
	}

	.release_box {
		padding: 30rpx 0;
		// padding-bottom: constant(safe-area-inset-bottom); // 兼容 IOS<11.2
		// padding-bottom: env(safe-area-inset-bottom); // 兼容 IOS>11.2
	}

	.lang {
		width: 616rpx !important;
	}

	.bodyNo {
		padding: 0 !important;
	}

	.main_content {
		padding: 30rpx 24rpx;
	}

	.header {
		position: relative;
		text-align: center;
		margin-bottom: 60rpx;

		.title {
			color: #282828;
			font-size: 36rpx;
			font-weight: bold;
		}

		.iconfont {
			color: #8A8A8A;
			font-size: 36rpx;
			position: absolute;
			top: 4rpx;
			right: 0;
		}
	}

	.fixed {
		// bottom: calc(40rpx+ constant(safe-area-inset-bottom)) !important; ///兼容 IOS<11.2/
		// bottom: calc(40rpx + env(safe-area-inset-bottom));
		position: fixed !important;
		//bottom: 0;
		// bottom: constant(safe-area-inset-bottom); // 兼容 IOS<11.2
		// bottom: env(safe-area-inset-bottom); // 兼容 IOS>11.2
		background-color: #fff;
	}

	.auditStatus {
		font-size: 18rpx;
		border-radius: 2px;
		opacity: 1;
		border: 1px solid #FE960F;
		color: #FE960F;
		padding: 4rpx;
		margin-right: 8rpx;
	}

	.textarea {
		bottom: constant(safe-area-inset-bottom); // 兼容 IOS<11.2
		bottom: env(safe-area-inset-bottom); // 兼容 IOS>11.2 
	}

	.bottom {
		bottom: constant(safe-area-inset-bottom); // 兼容 IOS<11.2
		bottom: env(safe-area-inset-bottom); // 兼容 IOS>11.2 
	}

	.release_bar_detail {
		height: calc(90rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(90rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
		padding-bottom: constant(safe-area-inset-bottom); ///兼容 IOS<11.2/
		padding-bottom: env(safe-area-inset-bottom); ///兼容 IOS>11.2/
		background-color: #fff;
	}

	.release_bar {
		background-color: #fff;
		flex-shrink: 0;
		width: 100%;
		position: absolute; // input 所在盒子设置绝对定位
		left: 0;
		z-index: 999;
		display: flex;
		bottom: constant(safe-area-inset-bottom); ///兼容 IOS<11.2/
		bottom: env(safe-area-inset-bottom); ///兼容 IOS>11.2/
		align-items: center;
		justify-content: space-between;
		padding: 15rpx 24rpx;
		border-top: 1px solid #F5F5F5;

		.avatar,
		image,
		uni-image {
			width: 54rpx;
			height: 54rpx;
			border-radius: 100%;
		}

		.input_count {
			width: 428rpx;
			background: #F7F7F7;
			border-radius: 31rpx;
			padding: 12rpx 30rpx;
		}

		textarea {
			width: 100%;
		}

		.send {
			font-size: 26rpx;
			color: #ffffff;
			width: 120rpx;
			height: 62rpx;
			line-height: 62rpx;
			text-align: center;
			@include linear-gradient(theme);
			border-radius: 30rpx;
			text-align: center;
		}

		.input_bar {
			align-items: center;
			color: #282828;
			font-size: 26rpx;

			.iconfont {
				font-size: 40rpx;
				margin-right: 6rpx;
			}

			.icon-shoucang1 {
				@include main_color(theme);
			}

			.item {
				align-items: center;

				&:first-child {
					margin-right: 25rpx;
				}
			}
		}
	}

	.container {
		background-color: #fff;
		position: relative;
		height: 986rpx;
	}

	.end {
		margin-top: 50rpx;
		text-align: center;

		text {
			color: #999999;
			font-size: 22rpx;
			position: relative;

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

	.common_list {
		position: relative;
		padding-left: 94rpx;

		.commen_one {
			position: absolute;
			top: 0;
			left: 0;

			.image,
			uni-image {
				width: 74rpx;
				height: 74rpx;
				border-radius: 100%;
			}
		}

		.info_count {
			margin-bottom: 30rpx;
		}

		.info {
			position: relative;
			padding-right: 90rpx;

		}

		.name,
		.name_two {
			color: #999999;
			font-size: 26rpx;
		}

		.desc,
		.desc_two {
			color: #282828;
			font-size: 28rpx;
			margin-top: 10rpx;
		}

		.desc_two {
			font-size: 26rpx;

			.reply_user {
				font-size: 24rpx;
				color: #4A8AC9;
				display: inline-block;
				margin: 10rpx 6rpx 0 6rpx;
			}
		}

		.time {
			color: #BBBBBB;
			font-size: 22rpx;
			margin-right: 20rpx;
		}

		.icon-cha2 {
			font-size: 26rpx;
			margin-top: 4rpx;
		}

		.time_two {
			color: #BBBBBB;
			font-size: 22rpx;
			margin-right: 20rpx;
		}

		.like,
		.like_two {
			color: #999999;
			font-size: 26rpx;
			text-align: center;
			position: absolute;
			top: 0;
			right: 0;
			width: 75rpx;

			.icon-yidianzan {
				@include main_color(theme);
			}
		}

		.reply_list {
			margin-top: 24rpx;

			.item {
				padding-right: 140rpx;
				position: relative;
			}

			.item_count {
				position: relative;
				padding-left: 56rpx;

				.image,
				un-image {
					width: 36rpx;
					height: 36rpx;
					border-radius: 100%;
					position: absolute;
					top: 0;
					left: 0;
				}
			}
		}
	}

</style>