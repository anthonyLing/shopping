<template>
	<view class="discover-details">
		<discover-details v-if="noteRecommendList.length>0 && !loading" :noteRecommendList="noteRecommendList" type="detail"
			@onChangeReplyStatus="onChangeReplyStatus"></discover-details>
		<view class='loadingicon acea-row row-center-wrapper' :hidden='loading==false'>
			<text class='loading iconfont icon-jiazai'></text>
		</view>
		<view class="details borderPad">
			<view v-if="noteDetail.platReplySwitch" class="commen_count">
				评论{{noteDetail.replyNum == 0 ? '' : noteDetail.replyNum}}</view>
			<discoverComment v-if="noteDetail" :noteId="noteId" :noteDetails="noteDetail" fromTo="detail">
			</discoverComment>
		</view>
		<!-- 评论 -->
		<uni-popup type="bottom" ref="comment">
			<discoverComment v-if="showComment" @getReplyNum="getReplyNum" :noteId="noteDetails.id"
				:noteDetails="noteDetails" @close="close">
			</discoverComment>
		</uni-popup>
	</view>
</template>

<script>
	import discoverDetails from '@/components/discoverDetails/index.vue'
	import discoverComment from '@/components/discoverComment/index.vue'
	import {
		noteDetailApi
	} from '@/api/discover.js';
	export default {
		components: {
			discoverDetails,
			discoverComment
		},
		data() {
			return {
				noteDetail: {}, //内容详情
				content: '',
				bottomVal: 0,
				placeholder: "快来说点儿什么吧...",
				loading: false,
				isShowComment: false, //真实评论弹窗显示隐藏
				noteId: 0,
				noteRecommendList: [],
				showComment: false, //评论弹窗
			}
		},
		onLoad: function(options) {
			this.noteId = Number(options.noteId);
			this.getNoteDetail(options.noteId);
		},
		/**
		 * 用户点击右上角分享
		 */
		// #ifdef MP
		onShareAppMessage: function(res) {
			let data = res.target.dataset;
			return {
				title: data.title || '',
				imageUrl: data.cover || '',
				path: 'pages/discover/discover_details/index?noteId=' + data.id + '&spread=' + data.uid,
			}
		},
		// #endif
		methods: {
			close() {
				uni.showTabBar();
				this.$refs.comment.close();
				this.showComment = false;
			},
			getReplyNum(n) {
				this.noteRecommendListNew[this.noteIndex].replyNum = n;
			},
			getComment(item) {
				this.noteDetails = item;
				uni.hideTabBar();
				this.$refs.comment.open('bottom');
				this.showComment = true
			},
			//关闭评论回调
			onChangeReplyStatus(replyStatus) {
				if (replyStatus === 1) {
					this.$set(this.noteDetail, 'replyStatus', 2)
					this.$util.Tips({
						title: '禁止成功'
					});
				} else {
					this.$set(this.noteDetail, 'replyStatus', 1)
					this.$util.Tips({
						title: '开启成功'
					});
				}
			},
			// 内容详情
			getNoteDetail(noteId) {
				noteDetailApi(noteId).then(res => {
					this.loading = true;
					this.noteDetail = res.data;
					this.noteRecommendList.push(res.data);
					this.loading = false;
				}).catch(err => {
					this.loading = false;
					uni.showToast({
						title: err,
						icon: 'none'
					})
				});
			},
		}
	}
</script>

<style scoped lang="scss">
	.details {
		/deep/.container {
			height: auto !important;
		}
	}

	.commen_count {
		font-size: 26rpx;
		color: #282828;
		margin: 46rpx 0 35rpx 0;
	}

	.discover-details {
		padding-bottom: calc(80rpx+ constant(safe-area-inset-bottom)) !important; ///兼容 IOS<11.2/
		padding-bottom: calc(80rpx + env(safe-area-inset-bottom));
		background-color: #fff;

		/deep/.main_content {
			padding: 30rpx 0 !important;
		}
	}

	.details {
		padding-bottom: calc(40rpx+ constant(safe-area-inset-bottom)) !important; ///兼容 IOS<11.2/
		padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
	}

	.lang {
		width: 560rpx !important;
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

	.details {
		background-color: #fff;
	}
</style>