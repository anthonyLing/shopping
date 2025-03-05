<template>
	<view :data-theme="theme">
		<form @submit="subRefund" report-submit='true'>
			<view class='apply-return'>
				<view class='goodsStyle borRadius14'>
					<view class="acea-row row-between mb30">
						<view class='pictrue'>
							<image :src='orderInfo.image'></image>
						</view>
						<view class='text'>
							<view class='name line2'>{{orderInfo.productName}}</view>
							<view class='money acea-row'>
								<view class="mr10">￥{{orderInfo.price}} </view>
								<view class='num'> x {{orderInfo.payNum}}</view>
							</view>
						</view>
					</view>
					<view class="applyRefundNum">已申请退款数量：{{parseFloat(orderInfo.refundNum) + parseFloat(orderInfo.applyRefundNum)}}</view>
				</view>
				<view class='list borRadius14'>
					<view class='item acea-row row-between-wrapper'>
						<view>退货件数</view>
						<view class="picker">
							<picker @change="bindNumChange" :value="numIndex" :range="numArray">
								<view class="picker-box">
									{{numArray[numIndex]}}
									<text class="iconfont icon-jiantou"></text>
								</view>
							</picker>
						</view>
					</view>
					<view class='item acea-row row-between-wrapper'>
						<view>预计退款金额</view>
						<view class='num'>￥{{refundPrice}}</view>
					</view>
					<view class='item acea-row row-between-wrapper' @tap="toggleTab('region')">
						<view>退款原因</view>
						<picker class='num' @change="bindPickerChange" :value="index" :range="RefundArray">
							<view class="picker acea-row row-between-wrapper">
								<view class='reason'>{{RefundArray[index]}}</view>
								<text class='iconfont icon-jiantou'></text>
							</view>
						</picker>
					</view>
					<view class='item textarea acea-row row-between'>
						<view>备注说明</view>
						<textarea placeholder='填写备注信息，100字以内' class='num' name="refund_reason_wap_explain"
							placeholder-class='填写备注信息，100字以内'></textarea>
					</view>
					<view class='item acea-row row-between' style="border: none;">
						<view class='title acea-row row-between-wrapper'>
							<view>上传凭证</view>
							<view class='tip'>( 最多可上传3张 )</view>
						</view>
						<view class='upload acea-row row-middle'>
							<view class='pictrue' v-for="(item,index) in refund_reason_wap_imgPath" :key="index">
								<image :src='item'></image>
								<view class='iconfont icon-guanbi1 font-color' @tap='DelPic(index)'></view>
							</view>
							<view class='pictrue acea-row row-center-wrapper row-column' @tap='uploadpic'
								v-if="refund_reason_wap_imgPath.length < 3">
								<text class='iconfont icon-icon25201'></text>
								<view>上传凭证</view>
							</view>
						</view>
					</view>
					<button class='returnBnt bg_color' form-type="submit">申请退款</button>
				</view>
			</view>
		</form>
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
		ordeRefundReason,
		orderRefundVerify
	} from '@/api/order.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		Debounce
	} from '@/utils/validate.js'
	let app = getApp();
	export default {
		data() {
			return {
				refund_reason_wap_img: [],
				refund_reason_wap_imgPath: [],
				orderInfo: {},
				RefundArray: [],
				index: 0,
				orderId: 0,
				theme: app.globalData.theme,
				numArray: [] ,//选择退款个数
				refundPrice: 0 ,//退款金额
				numIndex: 0
			};
		},
		computed: mapGetters(['isLogin']),
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						this.getRefundReason();
					}
				},
				deep: true
			}
		},
		onLoad: function(options) {
			this.$set(this, 'orderInfo', JSON.parse(this.$Cache.get('productInfo')));
			for (let i=1;i<=parseFloat(this.orderInfo.payNum)-parseFloat(this.orderInfo.applyRefundNum)-parseFloat(this.orderInfo.refundNum);i++){
				this.numArray.unshift(i)
			}
			let price = this.$util.$h.Div(this.orderInfo.payPrice, this.orderInfo.payNum);
			this.refundPrice = this.$util.$h.Mul(price, this.numArray[0]).toFixed(2);
			if (!options.orderNo) return this.$util.Tips({
				title: '缺少订单id,无法退款'
			}, {
				tab: 3,
				url: 1
			});
			this.orderNo = options.orderNo;
			this.orderId = options.orderId;
			if (this.isLogin) {
				this.getRefundReason();
			} else {
				toLogin();
			}
		},
		methods: {
			bindNumChange(e){
				this.numIndex = e.target.value;
				let price = this.$util.$h.Div(this.orderInfo.payPrice, this.orderInfo.payNum);
				this.refundPrice = this.$util.$h.Mul(price, this.numArray[e.target.value]).toFixed(2);
			},
			/**
			 * 获取退款理由
			 */
			getRefundReason: function() {
				let that = this;
				ordeRefundReason().then(res => {
					that.$set(that, 'RefundArray', res.data);
				})
			},

			/**
			 * 删除图片
			 *
			 */
			DelPic: function(e) {
				let index = e,
					that = this;
				that.refund_reason_wap_imgPath.splice(index, 1);
			},
			/**
			 * 上传文件
			 *
			 */
			uploadpic: function() {
				let that = this;
				that.$util.uploadImageOne({
					url: 'upload/image',
					name: 'multipart',
					model: "product",
					pid: 1
				}, function(res) {
					that.refund_reason_wap_imgPath.push(res);
				});
			},

			/**
			 * 申请退货
			 */
			subRefund: Debounce(function(e) {
				let that = this,
					value = e.detail.value;
				//收集form表单
				// if (!value.refund_reason_wap_explain) return this.$util.Tips({title:'请输入退款原因'});
				orderRefundVerify({
					num: that.numArray[that.numIndex],
					text: that.RefundArray[that.index] || '',
					explain: value.refund_reason_wap_explain,
					reasonImage: that.refund_reason_wap_imgPath.join(','),
					orderDetailId: that.orderId,
					orderNo: that.orderNo,
				}).then(res => {
					return this.$util.Tips({
						title: '申请成功',
						icon: 'success'
					}, {
						tab: 5,
						url: '/pages/goods/user_return_list/index?isT=1'
					});
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				})
			}),
			bindPickerChange: function(e) {
				this.$set(this, 'index', e.detail.value);
			}
		}
	}
</script>

<style scoped lang="scss">
	.applyRefundNum{
		font-size: 20rpx;
		color: #666;
	}
	.goodsStyle{
		.text{
			.name{
				height: 78rpx;
				line-height: 40rpx;
			}
		}

	}
	.icon-jiantou{
		margin-left: 10rpx;
		font-size: 28rpx;
		color: #BBBBBB;
	}
	.apply-return {
		padding: 20rpx 30rpx 70rpx 30rpx;
	}

	.apply-return .list {
		background-color: #fff;
		margin-top: 18rpx;
		padding: 0 24rpx 70rpx 24rpx;
	}

	.apply-return .list .item {
		min-height: 90rpx;
		border-bottom: 1rpx solid #eee;
		font-size: 30rpx;
		color: #333;
	}

	.apply-return .list .item .num {
		color: #282828;
		width: 427rpx;
		text-align: right;
	}

	.apply-return .list .item .num .picker .reason {
		width: 385rpx;
	}

	.apply-return .list .item .num .picker .iconfont {
		color: #666;
		font-size: 30rpx;
		margin-top: 2rpx;
	}

	.apply-return .list .item.textarea {
		padding: 24rpx 0;
	}

	.apply-return .list .item textarea {
		height: 100rpx;
		font-size: 30rpx;
	}

	.apply-return .list .item .placeholder {
		color: #bbb;
	}

	.apply-return .list .item .title {
		height: 95rpx;
		width: 100%;
	}

	.apply-return .list .item .title .tip {
		font-size: 30rpx;
		color: #bbb;
	}

	.apply-return .list .item .upload {
		padding-bottom: 36rpx;
	}

	.apply-return .list .item .upload .pictrue {
		border-radius: 14rpx;
		margin: 22rpx 23rpx 0 0;
		width: 156rpx;
		height: 156rpx;
		position: relative;
		font-size: 24rpx;
		color: #bbb;
	}

	.apply-return .list .item .upload .pictrue:nth-of-type(4n) {
		margin-right: 0;
	}

	.apply-return .list .item .upload .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
	}

	.apply-return .list .item .upload .pictrue .icon-guanbi1 {
		position: absolute;
		font-size: 45rpx;
		top: -10rpx;
		right: -10rpx;
	}

	.apply-return .list .item .upload .pictrue .icon-icon25201 {
		color: #bfbfbf;
		font-size: 50rpx;
	}

	.apply-return .list .item .upload .pictrue:nth-last-child(1) {
		border: 1rpx solid #ddd;
		box-sizing: border-box;
	}

	.apply-return .returnBnt {
		font-size: 32rpx;
		color: #fff;
		width: 100%;
		height: 86rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 86rpx;
		margin: 43rpx auto;
	}

	.bg_color {
		@include main_bg_color(theme);
	}
</style>
