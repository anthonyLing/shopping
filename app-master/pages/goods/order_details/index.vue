<template>
	<view :data-theme="theme">
		<view class='order-details'>
			<!-- 给header上与data上加on为退款订单-->
			<view class='header bg_color'>
				<view class='picTxt acea-row row-middle'>
					<!-- 					<view class='pictrue'>
						<image :src="statusPic"></image>
					</view> -->
					<view class='data'>
						<view class='state'>{{orderInfo.status | orderStatusFilter}}</view>
						<view v-if="orderInfo.refundReasonTime !== null">{{orderInfo.refundReasonTime}}</view>
						<view v-if="orderInfo.createTime">{{orderInfo.createTime}}</view>
					</view>
				</view>
			</view>

			<view class="borderPad" style="margin: -100rpx auto 0 auto;">
				<view v-if="orderInfo.status!==9 && shippingType ===1" class='nav'>
					<view class='navCon acea-row row-between-wrapper'>
						<view :class="orderInfo.status == 0 ?'on':''">待付款</view>
						<view :class="orderInfo.status == 1 ? 'on':''">待发货</view>
						<view :class="orderInfo.status == 4 ? 'on':''">待收货</view>
						<view :class="orderInfo.status == 5 ? 'on':''">已收货</view>
						<view :class="orderInfo.status == 6 ? 'on':''">已完成</view>
					</view>
					<!-- 订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消） -->
					<view class='progress acea-row row-between-wrapper'>
						<view class='iconfont'
							:class='(orderInfo.status == 0 ? "icon-webicon318":"icon-yuandianxiao") + " " + (orderInfo.status >= 0 ? "font_color":"")'>
						</view>
						<view class='line' :class='orderInfo.status > 0 ? "bg_color":""'></view>
						<view class='iconfont'
							:class='(orderInfo.status == 1 ? "icon-webicon318":"icon-yuandianxiao") + " " +(orderInfo.status >= 1 ? "font_color":"")'>
						</view>
						<view class='line' :class='orderInfo.status > 2  ? "bg_color":""'></view>
						<view class='iconfont'
							:class='(orderInfo.status == 4 ? "icon-webicon318":"icon-yuandianxiao") + " " + (orderInfo.status >= 4 ? "font_color":"")'>
						</view>
						<view class='line' :class='orderInfo.status > 4 ? "bg_color":""'></view>
						<view class='iconfont'
							:class='(orderInfo.status == 5 ? "icon-webicon318":"icon-yuandianxiao") + " " + (orderInfo.status >= 5 ? "font_color":"")'>
						</view>
						<view class='line' :class='orderInfo.status > 5 ? "bg_color":""'></view>
						<view class='iconfont'
							:class='(orderInfo.status == 6 ? "icon-webicon318":"icon-yuandianxiao") + " " + (orderInfo.status >= 6 ? "font_color":"")'>
						</view>
					</view>
				</view>
				<view v-if="shippingType ===2" class='nav'>
					<view class='navCon acea-row row-between-wrapper'>
						<view :class="orderInfo.status == 0 ?'on':''">待付款</view>
						<view :class="orderInfo.status == 3 ? 'on':''">待核销</view>
						<view :class="orderInfo.status == 6 ? 'on':''">已收货</view>
					</view>
					<!-- 订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消） -->
					<view class='progress acea-row row-between-wrapper'>
						<view class='iconfont'
							:class='(orderInfo.status == 0 ? "icon-webicon318":"icon-yuandianxiao") + " " + (orderInfo.status >= 0 ? "font_color":"")'>
						</view>
						<view class='line' :class='orderInfo.status > 0 ? "bg_color":""'></view>
						<view class='iconfont'
							:class='(orderInfo.status == 3 ? "icon-webicon318":"icon-yuandianxiao") + " " +(orderInfo.status >= 3 ? "font_color":"")'>
						</view>
						<view class='line' :class='orderInfo.status > 4  ? "bg_color":""'></view>
						<view class='iconfont'
							:class='(orderInfo.status == 5 ? "icon-webicon318":"icon-yuandianxiao") + " " + (orderInfo.status >= 5 ? "font_color":"")'>
						</view>
					</view>
				</view>
				<!-- 收货地址只显示一次 -->
				<view v-if="isUserAddress" class='wrapper borRadius14 address'>
					<view class='name'>{{userAddress[0].realName}}<text
							class='phone'>{{userAddress[0].userPhone}}</text></view>
					<view>{{userAddress[0].userAddress}}</view>
				</view>
				<block v-for="(item, index) in orderInfo.merchantOrderList" :key="item.merId">
					<!-- <view class='wrapper borRadius14 address' v-if="item.shippingType === 1">
						<view class='name'>{{item.realName}}<text class='phone'>{{item.userPhone}}</text></view>
						<view>{{item.userAddress}}</view>
					</view> -->
					<view v-if="item.shippingType == 2" class="writeOff borRadius14">
						<view class="title">核销信息</view>
						<view class="grayBg">
							<view class="pictrue">
								<!-- <div class="qrcode" ref="qrcode"></div> -->
								<!-- <canvas canvas-id="qrcode" :style="{width: `${qrcodeSize}100%`, height: `${qrcodeSize}100%`}"/> -->
								<image :src="codeImg"></image>
							</view>
						</view>
						<view class="gear">
							<image src="../static/images/writeOff.jpg"></image>
						</view>
						<view class="num">{{item.verifyCode}}</view>
						<view class="rules" v-if='item.systemStore'>
							<view class="item">
								<view class="rulesTitle acea-row row-middle">
									<text class="iconfont icon-shijian"></text>核销时间
								</view>
								<view class="info">
									每日：<text class="time">{{item.systemStore.dayTime.replace(',','-')}}</text>
								</view>
							</view>
							<view class="item">
								<view class="rulesTitle acea-row row-middle">
									<text class="iconfont icon-shuoming1"></text>使用说明
								</view>
								<view class="info">可将二维码出示给店员扫描或提供数字核销码</view>
							</view>
						</view>
					</view>
					<view v-if="item.shippingType == 2" class="map borRadius14">
						<view class='title item acea-row row-between-wrapper'>
							<view>自提地址信息</view>
							<view class="place cart-color acea-row row-center-wrapper" @tap="showMaoLocation(item)">
								<text class="iconfont icon-weizhi"></text>查看位置
							</view>
						</view>
						<view class="address">
							<view class='name' @tap="makePhone(item.merPhone)">{{item.merName}}<text
									class='phone'>{{item.merPhone}}</text><text
									class="iconfont icon-tonghua font-color"></text></view>
							<view>{{item.merAddressDetail}}</view>
						</view>
					</view>

					<view class="borRadius14">
						<orderGoods :orderInfo="item" :orderNo="orderNo" :cartInfo="item.orderInfoList" :jump="true"
							:orderData="orderInfo"></orderGoods>
					</view>
				</block>
			</view>
			<!-- 订单信息 "-->
			<view class="borderPad">
				<view class='wrapper borRadius14'>
					<view class='item acea-row row-between'>
						<view>订单编号：</view>
						<view class='conter acea-row row-middle row-right' style="width: auto;"><text
								class="text-overflow">{{orderInfo.orderNo}}</text>
							<!-- #ifndef H5 -->
							<text class='copy' @tap='copy'>复制</text>
							<!-- #endif -->
							<!-- #ifdef H5 -->
							<text class='copy copy-data' :data-clipboard-text="orderInfo.orderNo">复制</text>
							<!-- #endif -->
						</view>
					</view>
					<view class='item acea-row row-between'>
						<view>下单时间：</view>
						<view class='conter'>{{(orderInfo.createTime || 0)}}</view>
					</view>
					<view v-if="orderInfo.paid" class='item acea-row row-between'>
						<view>支付状态：</view>
						<view class='conter' v-if="orderInfo.paid">已支付</view>
						<view class='conter' v-else>未支付</view>
					</view>
					<view v-if="orderInfo.payType" class='item acea-row row-between'>
						<view>支付方式：</view>
						<view class='conter'>{{orderInfo.payType | payTypeFilter}}</view>
					</view>
					<view v-for="(item, index) in orderInfo.merchantOrderList" class='item flex justify-between'>
						<view>买家留言：</view>
						<view class="flex align-center userRemark">
							<view class='cor-86'>{{item.userRemark || '-'}}</view>
						</view>
						<view v-show="!isShow" @click="isShow=!isShow">
							<view class='mark_show'>{{item.userRemark}}</view>
						</view>
					</view>
				</view>
				<view v-if="orderInfo.status>0">
					<view class='wrapper borRadius14' v-if='orderInfo.deliveryType=="express"'>
						<view class='item acea-row row-between'>
							<view>配送方式：</view>
							<view class='conter'>发货</view>
						</view>
						<view class='item acea-row row-between'>
							<view>快递公司：</view>
							<view class='conter'>{{orderInfo.deliveryName || ''}}</view>
						</view>
						<view class='item acea-row row-between'>
							<view>快递号：</view>
							<view class='conter'>{{orderInfo.deliveryId || ''}}</view>
						</view>
					</view>
					<view class='wrapper borRadius14' v-else-if='orderInfo.deliveryType=="send"'>
						<view class='item acea-row row-between'>
							<view>配送方式：</view>
							<view class='conter'>送货</view>
						</view>
						<view class='item acea-row row-between'>
							<view>配送人姓名：</view>
							<view class='conter'>{{orderInfo.deliveryName || ''}}</view>
						</view>
						<view class='item acea-row row-between'>
							<view>联系电话：</view>
							<view class='conter acea-row row-middle row-right'>{{orderInfo.deliveryId || ''}}<text
									class='copy' @tap='goTel'>拨打</text></view>
						</view>
					</view>
					<view class='wrapper borRadius14' v-else-if='orderInfo.deliveryType=="fictitious"'>
						<view class='item acea-row row-between'>
							<view>虚拟发货：</view>
							<view class='conter'>已发货，请注意查收</view>
						</view>
					</view>
				</view>
				<view class='wrapper borRadius14'>
					<view class='item acea-row row-between'>
						<view>商品总价：</view>
						<view class='conter'>￥{{orderInfo.proTotalPrice || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if="orderInfo.payPostage > 0">
						<view>运费：</view>
						<view class='conter'>￥{{orderInfo.payPostage || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if='orderInfo.merCouponPrice > 0'>
						<view>店铺优惠：</view>
						<view class='conter'>-￥{{orderInfo.merCouponPrice || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if='orderInfo.platCouponPrice > 0'>
						<view>平台优惠：</view>
						<view class='conter'>-￥{{orderInfo.platCouponPrice || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if="orderInfo.integralPrice > 0">
						<view>积分抵扣：</view>
						<view class='conter'>-￥{{orderInfo.integralPrice || 0}}</view>
					</view>
					<view class='actualPay acea-row row-right'>实付款：<text
							class='money'>￥{{orderInfo.payPrice || 0}}</text>
					</view>
				</view>
				<view style='height:120rpx;'></view>
				<!-- 订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消） -->
				<view v-show="orderInfo.status!=3 && orderInfo.status!=1" class='footer acea-row row-right row-middle'>
					<view class="qs-btn" v-if="orderInfo.status==0" @click.stop="cancelOrder">取消订单</view>
					<view class='bnt bg_color' v-if="orderInfo.combinationId > 0" @tap='goJoinPink'>查看拼团</view>
					<navigator class='bnt cancel'
						v-if="Number(orderInfo.status) >1 &&Number(orderInfo.status) < 9 && orderInfo.status!=3&&shippingType!==2"
						hover-class='none' :url="'/pages/goods/order_logistics/index?orderNo='+orderInfo.orderNo">查看物流
					</navigator>
					<view class='bnt bg_color' v-if="orderInfo.status==4" @tap='confirmOrder'>确认收货</view>
					<view class='bnt cancel' v-if="orderInfo.status==6 || orderInfo.status==9" @tap='delOrder'>删除订单
					</view>
					<view class='bnt bg_color' v-if="orderInfo.status==0" @tap='goPay(orderInfo)'>立即付款</view>
					<view class='bnt bg_color' v-if="(Number(orderInfo.status)===4 || Number(orderInfo.status)===9) && type === 0" @tap='goOrderConfirm'>
						再次购买</view>

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
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import {
		getOrderDetail,
		orderTake,
		orderDel,
		orderCancel,
		strToBase64Api,
		orderStatusImgApi
	} from '@/api/order.js';
	import {
		cartBatchAddApi
	} from '@/api/product.js';
	import orderGoods from "../components/orderGoods";
	import ClipboardJS from "@/plugin/clipboard/clipboard.js";
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js'
	// #endif
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import {
		Debounce
	} from '@/utils/validate.js'
	const app = getApp();
	export default {
		components: {
			orderGoods,
			easyLoadimage
		},
		data() {
			return {
				codeImg: '',
				qrcodeSize: 100,
				orderNo: '',
				cartInfo: [], //购物车产品
				orderInfo: {
					systemStore: {},
					pstatus: {}
				}, //订单详情
				status: {}, //订单底部按钮状态
				totalPrice: '0',
				id: 0, //订单id
				uniId: '',
				utils: this.$util,
				againStatus: 0,
				type: 0, //订单类型 0-普通订单，1-视频号订单,2-秒杀订单
				isShow: true,
				theme: app.globalData.theme,
				bgColor: '#e93323',
				userAddress: [],
				shippingType: 1,
				statusPic: '',
				isUserAddress: false //是否只显示一次收货地址
			};
		},
		computed: mapGetters(['isLogin', 'chatUrl', 'userInfo']),
		onLoad: function(options) {
			if (!options.orderNo) return this.$util.Tips({
				title: '缺少参数'
			}, {
				tab: 3,
				url: 1
			});
			this.$set(this, 'orderNo', options.orderNo);
			this.bgColor = setThemeColor();
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor: this.bgColor,
			});
		},
		onShow() {
			if (this.isLogin) {
				this.getOrderInfo();
			} else {
				toLogin();
			}
		},
		onHide: function() {
			this.isClose = true;
		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		},
		onReady: function() {
			// #ifdef H5
			this.$nextTick(function() {
				const clipboard = new ClipboardJS(".copy-data");
				clipboard.on("success", () => {
					this.$util.Tips({
						title: '复制成功'
					});
				});
			});
			// #endif

		},
		methods: {
			/**
			 * 打开支付组件
			 *
			 */
			goPay: Debounce(function(item) {
				uni.showLoading({
					title: this.$t(`message.tips.loding`)
				});

				uni.navigateTo({
					url: `/pages/goods/order_payment/index?orderNo=${item.orderNo}&payPrice=${item.payPrice}`
				});
			}),
			/**
			 * 退款申请
			 */
			goRefund() {
				uni.navigateTo({
					url: '/pages/goods/goods_return/index?orderId=' + this.orderNo
				})
				if (this.orderInfo.status !== 0 || this.orderInfo.status !== 9) {
					this.$Cache.set('productInfo', this.orderInfo.merchantOrderList[0].orderInfoList)
				}
			},
			/**
			 * 拨打电话
			 */
			makePhone: function(e) {
				uni.makePhoneCall({
					phoneNumber: e
				})
			},
			/**
			 * 打开地图
			 *
			 */
			showMaoLocation: function(e) {
				if (!e.merLatitude || !e.merLongitude) return this.$util.Tips({
					title: '缺少经纬度信息无法查看地图！'
				});
				//#ifdef H5
				if (this.$wechat.isWeixin() === true) {
					this.$wechat.seeLocation({
						latitude: parseFloat(e.merLatitude),
						longitude: parseFloat(e.merLongitude),
						name: e.merName,
						address: e.merAddressDetail,
					}).then(res => {
						console.log('success');
					})
				} else {
					//#endif
					uni.openLocation({
						latitude: parseFloat(e.merLatitude),
						longitude: parseFloat(e.merLongitude),
						scale: 8,
						name: e.merName,
						address: e.merAddressDetail,
						success: function() {

						},
					});
					// #ifdef H5
				}
				//#endif

			},
			/**
			 * 登录授权回调
			 *
			 */
			onLoadFun: function() {
				this.getOrderInfo();
			},
			orderStatusImg() {
				let that = this;
				orderStatusImgApi().then(res => {
					res.data.map(item => {
						if (item.title === 'await_pay' && this.orderInfo.status === 0) {
							this.statusPic = item.pic
						} else if (item.title === 'await_shipped' && this.orderInfo.status === 1) {
							this.statusPic = item.pic
						} else if (item.title === 'receiving' && this.orderInfo.status === 3) {
							this.statusPic = item.pic
						} else if (item.title === 'spike' && this.orderInfo.status === 2) {
							this.statusPic = item.pic
						} else if (item.title === 'complete' && this.orderInfo.status === 4) {
							this.statusPic = item.pic
						}
					})
				}).catch(err => {
					that.$util.Tips({
						title: err
					});
				});
			},
			/**
			 * 获取订单详细信息
			 *
			 */
			getOrderInfo: function() {
				let that = this;
				uni.showLoading({
					title: "正在加载中"
				});
				getOrderDetail(that.orderNo).then(res => {
					uni.hideLoading();
					let data = res.data;
					that.$set(that, 'orderInfo', data);
					that.$set(that, 'id', data.id);
					that.$set(that, 'type', data.type);
					that.orderStatusImg();
					that.userAddress = data.merchantOrderList.filter(item => {
						return item.shippingType === 1
					})
					this.isUserAddress = that.userAddress.length > 0 ? true : false;
					if (data.status !== 0 || data.status !== 9) {
						if (data.merchantOrderList[0].shippingType == 2) that.markCode(data.merchantOrderList[
							0].verifyCode);
					}
					that.$set(that, 'shippingType', data.merchantOrderList[0].shippingType);
					that.$set(that, 'cartInfo', data.merchantOrderList[0].orderInfoList);
					if (that.orderInfo.refundStatus > 0) {
						uni.setNavigationBarColor({
							frontColor: '#fff',
							backgroundColor: '#666666'
						})
					}
					// if (data.combinationId > 0 || data.bargainId > 0 || data.seckillId > 0) {
					// 	this.againStatus = 1;
					// }
				}).catch(err => {
					that.$util.Tips({
						title: err
					}, {
						tab: 4,
						url: '/pages/user/index'
					});
				});
			},
			/**
			 *
			 * 生成二维码
			 */
			markCode(text) {
				strToBase64Api({
					height: '145',
					text: text,
					width: '145'
				}).then(res => {
					this.codeImg = res.data.code
				});
			},
			/**
			 *
			 * 剪切订单号
			 */
			// #ifndef H5
			copy: function() {
				let that = this;
				uni.setClipboardData({
					data: this.orderInfo.orderNo
				});
			},
			// #endif
			/**
			 * 打电话
			 */
			goTel: function() {
				uni.makePhoneCall({
					phoneNumber: this.orderInfo.deliveryId
				})
			},
			/**
			 * 设置底部按钮
			 *
			 */
			getOrderStatus: function() {
				let orderInfo = this.orderInfo || {},
					_status = orderInfo.pstatus || {
						type: 0
					},
					status = {};
				let type = parseInt(_status.type),
					delivery_type = orderInfo.deliveryType,
					seckill_id = orderInfo.seckillId ? parseInt(orderInfo.seckillId) : 0,
					bargain_id = orderInfo.bargainId ? parseInt(orderInfo.bargainId) : 0,
					combination_id = orderInfo.combinationId ? parseInt(orderInfo.combinationId) : 0;
				status = {
					type: type == 9 ? -9 : type,
					class_status: 0
				};
				if (type == 1 && combination_id > 0) status.class_status = 1; //查看拼团
				if (type == 2 && delivery_type == 'express') status.class_status = 2; //查看物流
				if (type == 2) status.class_status = 3; //确认收货
				if (type == 4 || type == 0) status.class_status = 4; //删除订单
				if (!seckill_id && !bargain_id && !combination_id && (type == 3 || type == 4)) status.class_status =
					5; //再次购买
				this.$set(this, 'status', status);
			},
			/**
			 * 去拼团详情
			 *
			 */
			goJoinPink: function() {
				uni.navigateTo({
					url: '/pages/activity/goods_combination_status/index?id=' + this.orderInfo.pinkId,
				});
			},
			/**
			 * 再此购买
			 *
			 */
			goOrderConfirm: Debounce(function() {
				uni.showLoading({
					title: this.$t(`message.tips.loding`)
				});
				let cartListRequest = []
				let that = this;
				this.cartInfo.map(item => {
					cartListRequest.push({
						productId: parseFloat(item.productId),
						productAttrUnique: item.attrValueId,
						cartNum: parseFloat(item.payNum)
					})
				})
				cartBatchAddApi(cartListRequest).then(function(res) {
					uni.switchTab({
						url: '/pages/order_addcart/order_addcart'
					})
				}).catch(res => {
					return this.$util.Tips({
						title: res
					});
				});
			}),
			confirmOrder: Debounce(function() {
				let that = this;
				uni.showModal({
					title: '确认收货',
					content: '为保障权益，请收到货确认无误后，再确认收货',
					success: function(res) {
						if (res.confirm) {
							orderTake(that.orderNo).then(res => {
								return that.$util.Tips({
									title: '操作成功',
									icon: 'success'
								}, function() {
									that.getOrderInfo();
								});
							}).catch(err => {
								return that.$util.Tips({
									title: err
								});
							})
						}
					}
				})
			}),
			/**
			 *
			 * 删除订单
			 */
			delOrder: Debounce(function() {
				uni.showModal({
					content: '确定删除该订单',
					cancelText: "取消",
					confirmText: "确定",
					showCancel: true,
					confirmColor: '#f55850',
					success: (res) => {
						if (res.confirm) {
							let that = this;
							orderDel(this.orderNo).then(res => {
								return that.$util.Tips({
									title: '删除成功',
									icon: 'success'
								}, {
									tab: 4,
									url: '/pages/user/index'
								});
							}).catch(err => {
								return that.$util.Tips({
									title: err
								});
							});
						} else {

						}
					}
				})
			}),
			cancelOrder: Debounce(function() {
				let self = this
				uni.showModal({
					title: '提示',
					content: '确认取消该订单?',
					success: function(res) {
						if (res.confirm) {
							orderCancel(self.orderNo)
								.then((data) => {
									self.$util.Tips({
										title: '取消成功'
									}, {
										tab: 4,
										url: '/pages/user/index'
									})
								}).catch((err) => {
									self.$util.Tips({
										title: err
									})
									self.getDetail();
								});
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
			})
		}
	}
</script>

<style scoped lang="scss">
	.refundPrice {
		font-size: 35rpx;
		font-weight: bold;
	}

	.qs-btn {
		width: auto;
		height: 60rpx;
		text-align: center;
		line-height: 60rpx;
		border-radius: 50rpx;
		color: #fff;
		font-size: 27rpx;
		padding: 0 3%;
		color: #aaa;
		border: 1px solid #ddd;
		margin-right: 20rpx;
	}

	.text-overflow {
		width: 392rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.shuoming {
		width: 32rpx;
		height: 32rpx;
	}

	.mp-header {
		width: 100%;
		@include main_bg_color(theme);
	}

	.goodCall {
		@include main_color(theme);
		text-align: center;
		width: 100%;
		height: 86rpx;
		padding: 0 30rpx;
		border-bottom: 1rpx solid #eee;
		font-size: 30rpx;
		line-height: 86rpx;
		background: #fff;

		.icon-kefu {
			font-size: 36rpx;
			margin-right: 15rpx;
		}

		/* #ifdef MP */
		button {
			display: flex;
			align-items: center;
			justify-content: center;
			height: 86rpx;
			font-size: 30rpx;
			@include main_color(theme);
		}

		/* #endif */
	}

	.justify-between {
		justify-content: space-between;
	}

	.align-center {
		align-items: center;
	}

	.order-details .header {
		height: 250rpx;
		padding: 0 30rpx;
	}

	.bg_color {
		@include main_bg_color(theme);
	}

	.order-details .header.on {
		background-color: #666 !important;
	}

	.order-details .header .pictrue {
		width: 110rpx;
		height: 110rpx;
		margin-right: 27rpx;
	}

	.order-details .header .pictrue image {
		width: 100%;
		height: 100%;
	}

	.order-details .header .data {
		color: rgba(255, 255, 255, 0.8);
		font-size: 24rpx;
	}

	.order-details .header .data.on {
		margin-left: 0;
	}

	.order-details .header .data .state {
		font-size: 30rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 7rpx;
	}

	.order-details .header .data .time {
		margin-left: 20rpx;
	}

	.picTxt {
		height: 150rpx;
	}

	.order-details .nav {
		background-color: #fff;
		font-size: 26rpx;
		color: #282828;
		padding: 27rpx 0;
		width: 100%;
		border-radius: 14rpx;
	}

	.order-details .nav .navCon {
		padding: 0 40rpx;
	}

	.order-details .nav .on {
		@include main_color(theme);
	}

	.font_color {
		@include main_color(theme);
	}

	.order-details .nav .progress {
		padding: 0 65rpx;
		margin-top: 10rpx;
	}

	.order-details .nav .progress .line {
		width: 100rpx;
		height: 2rpx;
		background-color: #939390;
	}

	.order-details .nav .progress .iconfont {
		font-size: 25rpx;
		color: #939390;
		margin-top: -2rpx;
	}

	.order-details .address {
		font-size: 26rpx;
		color: #868686;
		background-color: #fff;
		padding: 30rpx 0;

		.phone {
			margin-left: 20rpx;
		}
	}

	.order-details .address .name {
		font-size: 30rpx;
		color: #333;
		margin-bottom: 15rpx;
	}

	.order-details .line {
		width: 100%;
		height: 3rpx;
	}

	.order-details .line image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-details .wrapper {
		background-color: #fff;
		margin-top: 12rpx;
		padding: 30rpx 24rpx;
	}

	.order-details .wrapper .item {
		font-size: 28rpx;
		color: #282828;
	}

	.order-details .wrapper .item~.item {
		margin-top: 20rpx;
	}

	.order-details .wrapper .item .conter {
		color: #868686;
		width: 460rpx;
		text-align: right;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.mark_show {
		color: #868686;
		width: 470rpx;
		text-align: right;
	}

	.order-details .wrapper .item .conter .copy {
		font-size: 20rpx;
		color: #333;
		border-radius: 20rpx;
		border: 1rpx solid #666;
		padding: 3rpx 15rpx;
		margin-left: 24rpx;
	}

	.order-details .wrapper .actualPay {
		border-top: 1rpx solid #eee;
		margin-top: 30rpx;
		padding-top: 30rpx;
	}

	.order-details .wrapper .actualPay .money {
		font-weight: bold;
		font-size: 30rpx;
		@include price_color(theme);
	}

	.order-details .footer {
		width: 100%;
		height: 100rpx;
		position: fixed;
		bottom: 0;
		left: 0;
		background-color: #fff;
		padding-right: 30rpx;
		box-sizing: border-box;
	}

	.order-details .footer .bnt {
		width: 158rpx;
		height: 54rpx;
		text-align: center;
		line-height: 54rpx;
		border-radius: 50rpx;
		color: #fff;
		font-size: 27rpx;
	}

	.order-details .footer .bnt.cancel {
		color: #aaa;
		border: 1rpx solid #ddd;
	}

	.order-details .footer .bnt~.bnt {
		margin-left: 18rpx;
	}

	.order-details .writeOff {
		background-color: #fff;
		margin-top: 15rpx;
		padding-bottom: 50rpx;
	}

	.order-details .writeOff .title {
		font-size: 30rpx;
		color: #282828;
		height: 87rpx;
		border-bottom: 1px solid #f0f0f0;
		padding: 0 24rpx;
		line-height: 87rpx;
	}

	.order-details .writeOff .grayBg {
		background-color: #f2f5f7;
		width: 590rpx;
		height: 384rpx;
		border-radius: 20rpx 20rpx 0 0;
		margin: 50rpx auto 0 auto;
		padding-top: 55rpx;
	}

	.order-details .writeOff .grayBg .pictrue {
		width: 290rpx;
		height: 290rpx;
		margin: 0 auto;
	}

	.order-details .writeOff .grayBg .pictrue image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-details .writeOff .gear {
		width: 590rpx;
		height: 30rpx;
		margin: 0 auto;
	}

	.order-details .writeOff .gear image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-details .writeOff .num {
		background-color: #f0c34c;
		width: 590rpx;
		height: 84rpx;
		color: #282828;
		font-size: 48rpx;
		margin: 0 auto;
		border-radius: 0 0 20rpx 20rpx;
		text-align: center;
		padding-top: 4rpx;
	}

	.order-details .writeOff .rules {
		margin: 46rpx 30rpx 0 30rpx;
		border-top: 1px solid #f0f0f0;
		padding-top: 10rpx;
	}

	.order-details .writeOff .rules .item {
		margin-top: 20rpx;
	}

	.order-details .writeOff .rules .item .rulesTitle {
		font-size: 28rpx;
		color: #282828;
	}

	.order-details .writeOff .rules .item .rulesTitle .iconfont {
		font-size: 30rpx;
		color: #333;
		margin-right: 8rpx;
		margin-top: 5rpx;
	}

	.order-details .writeOff .rules .item .info {
		font-size: 28rpx;
		color: #999;
		margin-top: 7rpx;
	}

	.order-details .writeOff .rules .item .info .time {
		margin-left: 20rpx;
	}

	.order-details .map {
		font-size: 30rpx;
		color: #282828;
		margin-top: 15rpx;
		background-color: #fff;
		padding: 0 24rpx;

		.title {
			line-height: 86rpx;
			border-bottom: 1px solid #f0f0f0;
		}
	}

	.order-details .map .place {
		font-size: 26rpx;
		width: 176rpx;
		height: 50rpx;
		border-radius: 25rpx;
		line-height: 50rpx;
		text-align: center;
	}

	.order-details .map .place .iconfont {
		font-size: 27rpx;
		height: 27rpx;
		line-height: 27rpx;
		margin: 2rpx 3rpx 0 0;
	}

	.order-details .address .name .iconfont {
		font-size: 34rpx;
		margin-left: 10rpx;
	}

	.refund {
		padding: 0 !important;
		margin-top: 15rpx;
		background-color: #fff;

		.title {
			display: flex;
			align-items: center;
			font-size: 30rpx;
			color: #333;
			height: 86rpx;
			border-bottom: 1px solid #f5f5f5;
			font-weight: 400;
			padding: 0 24rpx;

			image {
				width: 32rpx;
				height: 32rpx;
				margin-right: 10rpx;
			}
		}

		.con {
			font-size: 26rpx;
			color: #666666;
			padding: 30rpx 24rpx;
		}
	}

	.userRemark {
		width: 76%;
		display: inline-block;
	}

	.cor-86 {
		color: #868686;
		text-align: right;
	}
</style>
