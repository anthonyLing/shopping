<template>
	<view :data-theme="theme">
		<!-- #ifndef APP-PLUS -->
		<view class='cart_nav'>
			<nav-bar :navTitle='navTitle' @getNavH='getNavH'></nav-bar>
		</view>

		<!-- #endif -->
		<view class='order-submission' :style="'margin-top:'+(marTop)+'rpx;'">
			<view class="allAddress">
				<view class='address acea-row row-between-wrapper' @tap='onAddress' v-if='shippingType == 0'>
					<view class='addressCon' v-if="addressInfo.realName">
						<view class='name'>{{addressInfo.realName}}
							<text class='phone'>{{addressInfo.phone}}</text>
						</view>
						<view class="acea-row">
							<text class='default font_color' v-if="addressInfo.isDefault">[默认]</text>
							<text
								class="line2">{{addressInfo.province}}{{addressInfo.city}}{{addressInfo.district}}{{addressInfo.detail}}</text>
						</view>
					</view>
					<view class='addressCon' v-else>
						<view class='setaddress'>设置收货地址</view>
					</view>
					<view class='iconfont icon-jiantou'></view>
				</view>
				<view class='line'>
					<image src='../static/images/line.png'></image>
				</view>
			</view>
			<view class="borderPad">
				<view v-for="(item, index) in merchantOrderVoList" :key="index" class='wrapper borRadius14'>
					<orderGoods :cartInfo="item.orderInfoList" :orderInfo="item" :orderProNum="orderProNum">
					</orderGoods>
					<view class="boxs">
						<view class='item acea-row row-between-wrapper'>
							<view>配送方式</view>
							<view v-if="item.takeTheirSwitch" class='discount acea-row row-middle'>
								<text @tap="openShowBox(item,index)">{{item.shippingType === 1 ? '快递配送' : '到店核销'}}</text>
								
								<text @tap="openShowBox(item,index)" class='iconfont icon-jiantou'></text>
							</view>
							<view v-else class='discount'>
								{{item.shippingType === 1 ? '快递配送' : '到店核销'}}

							</view>
						</view>
						<view v-if="item.shippingType === 2" class="store-address acea-row">
							<view>
								<view class="name phone">{{item.phone}}</view>
								<view class="name line2">{{item.addressDetail}}</view>
							</view>
							<view class="map" @click="goMap(item)">
								<text class="iconfont icon-chakanditu"></text>
								<view class="map_text">查看地图</view>
							</view>
						</view>
					</view>
					<view v-show="item.shippingType === 1" class='item acea-row row-between-wrapper'>
						<view>快递费用</view>
						<view v-if='!item.freightFee || item.freightFee == 0' class="noCoupon">免运费</view>
						<view v-else class='money'>￥{{item.freightFee}}</view>
					</view>
					<view v-if="type===0" class='item acea-row row-between-wrapper'>
						<view>店铺优惠</view>
						<view v-if="item.merCouponUserList && item.merCouponUserList.length"
							@tap='couponTap(item.merCouponUserList,item.merId, index)'>
							<view class='discount acea-row row-between-wrapper'>
								<text class="couponTitle line1">{{item.couponFee==0?`有${item.merCouponUserList.length}张优惠券可选`:`-￥${item.couponFee}`}}</text>
								<text class='iconfont icon-jiantou'></text>
							</view>
						</view>
						<view v-else class="noCoupon">暂无优惠券</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if="textareaStatus" style="height: auto;">
						<view>备注信息</view>
						<input placeholder-class='placeholder' value="" name="mark" placeholder='选填备注信息'
							v-model="item.remark" @input='bindHideKeyboard(item.remark,index)'
							style="width: 484rpx;text-align: right;"></input>
					</view>
				</view>
				<view class='wrapper borRadius14'>
					<!-- <view class='item acea-row row-between-wrapper'
						v-if="!orderInfoVo.bargainId && !orderInfoVo.combinationId && !orderInfoVo.seckillId && productType==='normal'"> -->

				</view>
				<view class='moneyList borRadius14'>
					<view class='item acea-row row-between-wrapper'>
						<view>商品总价：</view>
						<view class='money'>￥{{orderInfoVo.proTotalFee || 0}}</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if="parseInt(orderInfoVo.freightFee) > 0">
						<view>运费：</view>
						<view class='money'>+￥{{orderInfoVo.freightFee}}</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if="parseInt(merCouponFee) > 0 && type===0">
						<view>店铺优惠：</view>
						<view class='money'>-￥{{merCouponFee}}</view>
					</view>
					<view v-if="type===0" class='item acea-row row-between-wrapper'>
						<view>平台优惠</view>
						<view v-if="orderInfoVo.platCouponUserList && orderInfoVo.platCouponUserList.length"
							class='discount acea-row row-between-wrapper' @tap='couponTap(orderInfoVo.platCouponUserList,0)'>
							<text class="couponTitle line1">{{platCouponFee==0?`有${orderInfoVo.platCouponUserList.length}张优惠券可选`:`-￥${platCouponFee}`}}</text>
							<text class='iconfont icon-jiantou'></text>
						</view>
						<view v-else class="noCoupon">暂无优惠券</view>
					</view>
					<view class='item acea-row row-between-wrapper'
						v-if="orderInfoVo.integralDeductionSwitch && productType==='normal'">
						<view>积分抵扣</view>
						<view class='discount acea-row row-middle'>
							<view class="mr14"> {{isUseIntegral ? "使用积分":"当前积分"}}
								<text
									class='num font_color'>{{ isUseIntegral ? orderInfoVo.surplusIntegral : orderInfoVo.userIntegral}}</text>
							</view>
							<checkbox-group @change="ChangeIntegral">
								<checkbox :checked='isUseIntegral ? true : false'
									:disabled="orderInfoVo.userIntegral==0 && !isUseIntegral" />
							</checkbox-group>
						</view>
					</view>

					<view class='item acea-row row-between-wrapper' v-if="Number(orderInfoVo.deductionPrice) > 0">
						<view>抵扣金额：</view>
						<view class='money'>-￥{{orderInfoVo.deductionPrice}}</view>
					</view>

				</view>
				<view style='height:120rpx;'></view>
			</view>
			<view class='footer acea-row row-between-wrapper'>
				<view>合计:
					<text class='price_color'>￥{{orderInfoVo.payFee || 0}}</text>
				</view>
				<view class='settlement' style='z-index:100' @tap="SubOrder">立即下单</view>
			</view>
		</view>
		<couponListWindow :coupon='coupon' @ChangCouponsClone="ChangCouponsClone" :openType='openType'
			@ChangCoupons="ChangCoupons" :orderShow="orderShow" :surplusFee="surplusFee"></couponListWindow>

		<view v-if="isShowBox">
			<checkDelivery :isShowBox="isShowBox" :activeObj="activeObj" @close="boxClose"
				@confirmBtn="getShippingType">
			</checkDelivery>
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
		getCouponsOrderPrice,
		orderCreate,
		postOrderComputed,
		loadPreOrderApi
	} from '@/api/order.js';
	import {
		getAddressDetail,
		getAddressDefault
	} from '@/api/user.js';
	// import {
	// 	openPaySubscribe
	// } from '@/utils/SubscribeMessage.js';
	import {
		takeTheirApi
	} from '@/api/merchant.js';
	import {
		CACHE_LONGITUDE,
		CACHE_LATITUDE
	} from '@/config/cache.js';
	import couponListWindow from '../components/couponListWindow';
	import orderGoods from '../components/orderGoods'
	import navBar from '@/components/navBar';
	import checkDelivery from '../components/checkDelivery/index.vue';
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
		components: {
			navBar,
			couponListWindow,
			orderGoods,
			checkDelivery
		},
		computed: {
			...mapGetters(['productType', 'isLogin'])
		},
		data() {
			return {
				marTop: 0,
				navTitle: '提交订单',
				homeTop: 20,
				orderShow: 'orderShow', //下单页面使用优惠券组件不展示tab切换页
				textareaStatus: true,
				openType: 1, //优惠券打开方式 1=使用
				couponShow: false,
				coupon: {
					coupon: false,
					list: [], //商户优惠券
					statusTile: '立即使用',
					couponMoney: 0
				}, //优惠券组件
				addressInfo: {}, //地址信息
				addressId: 0, //地址id
				orderMerchantRequestList: [], //商户属性集合
				cartId: '', //购物车id
				userInfo: {}, //用户信息
				mark: '', //备注信息
				couponFee: '请选择', //优惠券
				coupon_price: 0, //优惠券抵扣金额
				isUseIntegral: false, //是否使用积分
				integral_price: 0, //积分抵扣金额
				integral: 0,
				ChangePrice: 0, //使用积分抵扣变动后的金额
				formIds: [], //收集formid
				status: 0,
				is_address: false,
				toPay: false, //修复进入支付时页面隐藏从新刷新页面
				shippingType: 0,
				storePostage: 0,
				contacts: '',
				contactsTel: '',
				mydata: {},
				merchantOrderVoList: [],
				priceGroup: {},
				animated: false,
				totalPrice: 0,
				integralRatio: "0",
				orderKey: "",
				// usableCoupon: {},
				offlinePostage: "",
				news: true,
				again: false,
				addAgain: false,
				bargain: false, //是否是砍价
				combination: false, //是否是拼团
				secKill: false, //是否是秒杀
				orderInfoVo: {},
				addressList: [], //地址列表数据
				orderProNum: 0,
				orderNo: '', //预下单订单号
				theme: app.globalData.theme,
				addressChangeId: 0,
				isShowBox: false,
				activeObj: {},
				activeIndex: '', // 选中店铺索引
				type: 0, //0普通订单，1视频号订单，2秒杀订单
				merId: 0, //商户id，用于判断商户优惠券还是平台优惠券，平台优惠券商户id为0
				platUserCouponObj: {}, //平台优惠券对象，用于缓存数据使用
				platUserCouponId: 0, //平台优惠券id
				platCouponFee: '', //平台优惠券金额
				merCouponFee: '', //店铺优惠券总金额
				surplusFee: 0, //商品总金额-商户优惠券金额=平台端可使用优惠券的门槛
				merUserCouponId: 0, //店铺使用优惠券的id
				tempCouponObj: {} //临时优惠券数据
			};
		},
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						this.getloadPreOrder();
					}
				},
				deep: true
			}
		},
		onLoad(options) {
			this.orderNo = options.orderNo || 0;
			this.addressChangeId = parseInt(options.addressId) || 0;
			this.is_address = options.is_address ? true : false;
			if (this.isLogin) {
				this.getloadPreOrder();
			} else {
				toLogin();
			}
		},
		/**
		 * 生命周期函数--监听页面显示
		 */
		onShow: function() {
			let _this = this
			this.textareaStatus = true;
		},
		methods: {
			getShippingType(item) {
				this.orderMerchantRequestList[this.activeIndex].shippingType = item.shippingType;
				this.$set(this.merchantOrderVoList[this.activeIndex], 'shippingType', item.shippingType);
				if (item.shippingType === 2) this.getTakeTheir(item.merId);
				this.computedPrice();
				this.isShowBox = false;
			},
			getTakeTheir(id) {
				takeTheirApi(id).then(res => {
					this.$set(this.merchantOrderVoList[this.activeIndex], 'addressDetail', res.data.addressDetail);
					this.$set(this.merchantOrderVoList[this.activeIndex], 'phone', res.data.phone);
					this.$set(this.merchantOrderVoList[this.activeIndex], 'latitude', res.data.latitude);
					this.$set(this.merchantOrderVoList[this.activeIndex], 'longitude', res.data.longitude);
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				})
			},
			//查看内置地图
			goMap(item) {
				let that = this;
				//#ifdef H5
				if (that.$wechat.isWeixin() === true) {
					that.$wechat.seeLocation({
						latitude: parseFloat(this.merchantOrderVoList[this.activeIndex].latitude),
						longitude: parseFloat(this.merchantOrderVoList[this.activeIndex].longitude),
						name: item.merName,
						address: item.addressDetail ? item.addressDetail : '',
					}).then(res => {
						console.log('success');
					})
				} else {
					//#endif
					uni.openLocation({
						latitude: parseFloat(this.merchantOrderVoList[this.activeIndex].latitude),
						longitude: parseFloat(this.merchantOrderVoList[this.activeIndex].longitude),
						scale: 8,
						name: item.merName,
						address: item.addressDetail ? item.addressDetail : '',
						success: function(res) {
							that.go_map = true
						},
					});
					// #ifdef H5
				}
				//#endif
			},
			// 打开配送方式弹窗
			openShowBox(item, index) {
				this.activeObj = item
				this.activeIndex = index
				this.isShowBox = true
			},
			boxClose() {
				this.isShowBox = false
			},
			getNavH(marTop) {
				this.marTop = marTop;
			},
			// 订单详情
			getloadPreOrder: function() {
				loadPreOrderApi(this.orderNo).then(res => {
					let orderInfoVo = res.data;
					this.orderInfoVo = orderInfoVo;
					this.merchantOrderVoList = orderInfoVo.merchantInfoList; //商户端数据
					this.platCouponFee = orderInfoVo.platCouponFee; //平台优惠券总金额
					this.platUserCouponId = orderInfoVo.platUserCouponId;
					this.merCouponFee = orderInfoVo.merCouponFee; //店铺优惠券总金额
					orderInfoVo.merchantInfoList.map(item => {
						this.orderMerchantRequestList.push({
							shippingType: item.shippingType,
							merId: item.merId,
							remark: '',
							userCouponId: item.userCouponId
						})
					});
					this.type = orderInfoVo.type; //订单类型
					this.orderProNum = orderInfoVo.orderProNum;
					if (orderInfoVo.addressId && this.addressChangeId === 0) {
						this.addressId = orderInfoVo.addressId;
					} else {
						this.addressId = this.addressChangeId;
						if (orderInfoVo.addressId != this.addressChangeId && this.addressChangeId > 0)
							this.computedPrice();
					}
					this.getaddressInfo();
				}).catch(err => {
					uni.navigateTo({
						url: '/pages/goods/order_list/index'
					});
				})
			},
			// 计算订单价格
			computedPrice: function() {
				uni.showLoading({
					title: this.$t(`message.tips.loding`)
				});
				let shippingType = this.shippingType;
				postOrderComputed({
					addressId: this.addressId,
					isUseIntegral: this.isUseIntegral,
					orderMerchantRequestList: this.orderMerchantRequestList,
					preOrderNo: this.orderNo,
					platUserCouponId: this.platUserCouponId
				}).then(res => {
					let data = res.data;
					//usedIntegral 使用的积分，surplusIntegral 剩余积分
					data.merOrderResponseList.map((item, i) => {
						this.merchantOrderVoList[i].freightFee = item.freightFee
						this.merchantOrderVoList[i].couponFee = item.couponFee
					});
					this.orderInfoVo.platCouponUserList = data.platCouponUserList; //平台优惠券数据
					this.merCouponFee = data.merCouponFee; //店铺优惠券总金额
					this.orderInfoVo.couponFee = data.couponFee; //优惠券优惠金额
					this.orderInfoVo.userIntegral = data.surplusIntegral; //使用的积分
					this.orderInfoVo.deductionPrice = data.deductionPrice;
					this.orderInfoVo.freightFee = data.freightFee;
					this.orderInfoVo.payFee = data.payFee;
					this.orderInfoVo.proTotalFee = data.proTotalFee;
					this.orderInfoVo.surplusIntegral = data.usedIntegral; //剩余积分
					this.platCouponFee = data.platCouponFee; //平台优惠金额
					//选中商户优惠券的值
					this.merchantOrderVoList[this.activeIndex].merCouponUserList = data.merOrderResponseList[
						this.activeIndex].merCouponUserList; //商户数据
					this.merUserCouponId = data.merOrderResponseList[this.activeIndex]
						.userCouponId //店铺使用优惠券的id
					uni.hideLoading();
				}).catch(err => {
					uni.hideLoading();
					return this.$util.Tips({
						title: err
					});
				});
			},
			bindPickerChange: function(e) {
				let value = e.detail.value;
				this.shippingType = value;
				this.computedPrice();
			},
			/**
			 * 使用积分抵扣
			 */
			ChangeIntegral: function() {
				this.isUseIntegral = !this.isUseIntegral;
				this.computedPrice();
			},
			bindHideKeyboard: function(e, i) {
				this.orderMerchantRequestList[i].remark = e;
			},
			/*
			 * 获取默认收货地址或者获取某条地址信息
			 */
			getaddressInfo: function() {
				if (this.addressId) {
					getAddressDetail(this.addressId).then(res => {
						if (res.data) {
							res.data.isDefault = res.data.isDefault;
							this.addressInfo = res.data || {};
						}
					})
				}
			},

			//关闭优惠券弹窗
			ChangCouponsClone: function(coupon) {
				this.coupon.list = [];
				this.coupon.coupon = false;
			},
			//点击优惠券弹窗确定后
			ChangCoupons: function(item) {
				this.coupon.list = [];
				if (item.merId === 0) {
					this.platUserCouponId = item.isChecked ? item.id : 0;
				} else {
					this.$set(this.orderMerchantRequestList[this.activeIndex], 'userCouponId', item.isChecked ? item
						.id : 0);

				}
				this.coupon.coupon = false;
				this.computedPrice();
			},
			/**
			 * 选择优惠券
			 * @param {Object} item优惠券对象
			 * @param {Number} merId商户id
			 * @param {Number} index索引
			 */
			couponTap: function(item, merId, index) {
				this.$set(this.coupon, 'list', item);
				this.$set(this.coupon, 'couponMoney', item);
				this.coupon.coupon = true;
				this.merId = merId; //商户id
				if (merId !== 0) this.activeIndex = index;
				this.coupon.loading = false;
			},
			/**
			 * 获取当前金额可用优惠券
			 *
			 */
			getCouponList: function(item, merId) {
				this.$set(this.coupon, 'list', item);
				this.openType = 1;
				uni.hideLoading();
			},
			onAddress: function() {
				uni.redirectTo({
					url: '/pages/address/user_address_list/index?orderNo=' + this.orderNo
				});
			},
			realName: function(e) {
				this.contacts = e.detail.value;
			},
			phone: function(e) {
				this.contactsTel = e.detail.value;
			},
			payment: function(data) {
				let that = this;
				// #ifdef MP
				uni.checkBeforeAddOrder({
					success(res) {
						console.log("下单前置检查 成功：", JSON.stringify(res));
						const traceId = res.data.traceId;
						data.traceId = traceId;
						that.onCreate(data);
					},
					fail(res) {
						console.log("下单前置检查 失败：", JSON.stringify(res));
					}
				});
				// #endif
				// #ifndef MP
				that.onCreate(data);
				// #endif
			},
			onCreate(data) {
				orderCreate(data).then(res => {
					uni.redirectTo({
						url: `/pages/goods/order_payment/index?orderNo=${res.data.orderNo}&payPrice=${res.data.payPrice}`
					});
					uni.hideLoading();
				}).catch(err => {
					uni.hideLoading();
					return this.$util.Tips({
						title: err
					});
				});
			},
			SubOrder: Debounce(function(e) {
				let that = this,
					data = {};
				let flag = false;
				that.orderMerchantRequestList.map(function(v) {
					if (v.shippingType === 1 && !that.addressId) {
						flag = true;
					}
				});
				if (flag) {
					that.$util.Tips({
						title: '请选择收货地址'
					});
					return;
				}
				data = {
					addressId: that.addressId,
					orderMerchantRequestList: that.orderMerchantRequestList,
					isUseIntegral: that.isUseIntegral,
					preOrderNo: that.orderNo,
					platUserCouponId: this.platUserCouponId

				};
				uni.showLoading({
					title: '订单提交中'
				});
				that.payment(data);
				//// #ifdef MP
				// openPaySubscribe().then(() => {
				// 	that.payment(data);
				// });
				// // #endif
				// // #ifndef MP
				// that.payment(data);
				// // #endif
			})
		}
	}
</script>

<style lang="scss" scoped>
	.noCoupon {
		color: #999999;

	}

	.icon-jiantou {
		font-size: 26rpx;
		color: #515151;
		margin-left: 14rpx;
	}

	.mr14 {
		margin-right: 14rpx;
	}

	.couponTitle {
		width: 430rpx;
		display: inline-block;
		text-align: right;
		@include main_color(theme);
	}

	.store-address {
		padding: 20rpx;
		margin: 0 24rpx;
		background: #F6F6F6;
		border-radius: 8rpx;
		font-size: 24rpx;
		/*垂直居中*/
		-webkit-box-align: center;
		/*旧版本*/
		-moz-box-align: center;
		/*旧版本*/
		-ms-flex-align: center;
		/*混合版本*/
		-webkit-align-items: center;
		/*新版本*/
		align-items: center;

		/*新版本*/
		.phone {
			margin-bottom: 10rpx;
			color: #282828;
			font-weight: bold;
		}

		.name {
			padding-right: 20rpx;
		}

		.info {
			flex: 1;
		}

		.line2 {
			width: 456rpx !important;
		}

		.map {
			text-align: center;
			padding-left: 36rpx;
			position: relative;
			@include main_color(theme);

			&::before {
				content: '';
				display: inline-block;
				width: 2rpx;
				height: 42rpx;
				background-color: #DDDDDD;
				position: absolute;
				left: 0;
				top: 18rpx;
			}

			.iconfont {
				color: var(--view-theme);
			}

			.map_text {
				color: var(--view-theme);
			}
		}
	}

	.font_color {
		@include main_color(theme);
	}

	.price_color {
		@include price_color(theme);
	}

	.line2 {
		width: 504rpx;
	}

	.textR {
		text-align: right;
	}

	.order-submission .line {
		width: 100%;
		height: 3rpx;
	}

	.order-submission .line image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-submission .address {
		padding: 40rpx 24rpx;
		background-color: #fff;
		box-sizing: border-box;
	}

	.order-submission .address .addressCon {
		width: 596rpx;
		font-size: 26rpx;
		color: #666;
	}

	.order-submission .address .addressCon .name {
		font-size: 30rpx;
		color: #282828;
		font-weight: bold;
		margin-bottom: 10rpx;
	}

	.order-submission .address .addressCon .name .phone {
		margin-left: 50rpx;
	}

	.order-submission .address .addressCon .default {
		margin-right: 12rpx;
	}

	.order-submission .address .addressCon .setaddress {
		color: #333;
		font-size: 28rpx;
	}

	.order-submission .address .iconfont {
		color: #707070;
	}

	.order-submission .allAddress {
		width: 100%;
		@include index-gradient(theme);
		padding: 30rpx 24rpx 0 24rpx;
	}

	.order-submission .allAddress .address {
		max-height: 180rpx;
		margin: -2rpx auto 0 auto;
		border-radius: 14rpx 14rpx 0 0;
	}

	.order-submission .allAddress .line {
		width: 100%;
		margin: 0 auto;
	}

	.order-submission .wrapper .item .discount .placeholder {
		color: #ccc;
	}

	.order-submission .wrapper {
		background-color: #fff;
		margin-top: 15rpx;
	}

	.order-submission .wrapper .item {
		padding: 28rpx 24rpx;
		font-size: 30rpx;
		color: #333333;
	}

	.order-submission .wrapper .item .discount {
		font-size: 30rpx;
		color: #333;
	}

	.order-submission .wrapper .item .discount .iconfont {
		color: #515151;
	}

	.order-submission .wrapper .item .discount .num {
		font-size: 32rpx;
		margin-right: 20rpx;
	}

	.order-submission .wrapper .item .shipping {
		font-size: 30rpx;
		color: #999;
		position: relative;
		padding-right: 58rpx;
	}

	.order-submission .wrapper .item .shipping .iconfont {
		font-size: 35rpx;
		color: #707070;
		position: absolute;
		right: 0;
		top: 50%;
		transform: translateY(-50%);
		margin-left: 30rpx;
	}

	.order-submission .wrapper .item textarea {
		background-color: #f9f9f9;
		width: auto !important;
		height: 140rpx;
		border-radius: 14rpx;
		margin-top: 30rpx;
		padding: 15rpx;
		box-sizing: border-box;
		font-weight: 400;
	}

	.order-submission .wrapper .item .placeholder {
		color: #ccc;
	}

	.order-submission .wrapper .item .list {
		margin-top: 35rpx;
	}

	.order-submission .wrapper .item .list .payItem {
		border: 1px solid #eee;
		border-radius: 14rpx;
		height: 86rpx;
		width: 100%;
		box-sizing: border-box;
		margin-top: 20rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.order-submission .wrapper .item .list .payItem.on {
		// border-color: #fc5445;
		@include coupons_border_color(theme);
		color: $theme-color;
	}

	.order-submission .wrapper .item .list .payItem .name {
		width: 50%;
		text-align: center;
		border-right: 1px solid #eee;
	}

	.order-submission .wrapper .item .list .payItem .name .iconfont {
		width: 44rpx;
		height: 44rpx;
		border-radius: 50%;
		text-align: center;
		line-height: 44rpx;
		background-color: #fe960f;
		color: #fff;
		font-size: 30rpx;
		margin-right: 15rpx;
	}

	.order-submission .wrapper .item .list .payItem .name .iconfont.icon-weixin2 {
		background-color: #41b035;
	}

	.order-submission .wrapper .item .list .payItem .name .iconfont.icon-zhifubao {
		background-color: #00AAEA;
	}

	.order-submission .wrapper .item .list .payItem .tip {
		width: 49%;
		text-align: center;
		font-size: 26rpx;
		color: #aaa;
	}

	.order-submission .moneyList {
		margin-top: 15rpx;
		background-color: #fff;
		padding: 0 30rpx;
		margin-bottom: calc(constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		margin-bottom: calc(env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}

	.order-submission .moneyList .item {
		font-size: 30rpx;
		color: #282828;
		height: 96rpx;
	}

	.order-submission .moneyList .item~.item {
		// margin-top: 20rpx;
	}

	.order-submission .moneyList .item .money {
		color: #666666;
	}

	.order-submission .footer {
		width: 100%;
		height: 100rpx;
		background-color: #fff;
		padding: 0 30rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
		position: fixed;
		bottom: 0;
		left: 0;
		height: calc(100rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(100rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}

	.order-submission .footer .settlement {
		font-size: 30rpx;
		color: #fff;
		width: 240rpx;
		height: 70rpx;
		@include main_bg_color(theme);
		border-radius: 50rpx;
		text-align: center;
		line-height: 70rpx;
	}

	.footer .transparent {
		opacity: 0
	}

	/deep/ checkbox .uni-checkbox-input.uni-checkbox-input-checked {
		@include main_bg_color(theme);
		border: none !important;
		color: #fff !important
	}

	/deep/ checkbox .wx-checkbox-input.wx-checkbox-input-checked {
		@include main_bg_color(theme);
		border: none !important;
		color: #fff !important;
		margin-right: 0 !important;
	}
</style>