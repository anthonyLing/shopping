// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import Auth from '../../libs/wechat';
import {
	getPayConfigApi
} from '../../api/order.js';
import {
	getUserInfo,
	getLogout
} from "../../api/user.js";
import {
	getMerTypeListApi,
	getMerCategoryListApi
} from '@/api/merchant.js';
import {
	tokenIsExistApi
} from '../../api/api.js';
import {
	LOGIN_STATUS,
	UID,
	PLATFORM,
	GLOBAL_DATA
} from '../../config/cache';
import Cache from '../../utils/cache';
import {
	USER_INFO
} from '../../config/cache';
import util from '../../utils/util';
let cartArr = [{
		name: "微信支付",
		icon: "icon-weixinzhifu1222",
		value: 'weixin',
		title: '微信快捷支付',
		payStatus: 1,
	},
	{
		name: "余额支付",
		icon: "icon-yuezhifu",
		value: 'yue',
		title: '可用余额:',
		payStatus: 1,
		userBalance: ''
	},
	// #ifndef MP
	{
		name: "支付宝支付",
		icon: "icon-zhifubao",
		value: 'alipay',
		title: '支付宝快捷支付',
		payStatus: 1,
	}
	// #endif
];
const state = {
	token: Cache.get(LOGIN_STATUS) || false,
	backgroundColor: "#fff",
	userInfo: Cache.get(USER_INFO) ? JSON.parse(Cache.get(USER_INFO)) : null,
	uid: Cache.get(UID) || null,
	homeActive: false,
	chatUrl: Cache.get('chatUrl') || '',
	systemPlatform: Cache.get(PLATFORM) ? Cache.get(PLATFORM) : '',
	productType: Cache.get('productType') || '',
	globalData: Cache.get(GLOBAL_DATA) || {},
	merchantClassify: Cache.get('merchantClassify') ? JSON.parse(Cache.get('merchantClassify')) : [],
	/** 商户分类 **/
	merchantType: Cache.get('merchantType') ? JSON.parse(Cache.get('merchantType')) : [],
	/** 商户类型 **/
	merchantAPPInfo: Cache.get('merchantAPPInfo') ? JSON.parse(Cache.get('merchantAPPInfo')) : {},
	merSttledData: Cache.get('merSttledData') ? JSON.parse(Cache.get('merSttledData')) : {},
	bottomNavigationIsCustom: false, //是否使用自定义导航
	merTokenIsExist: Cache.get('merTokenIsExist') || false,
	discoverTopic: []
};

const mutations = {
	LOGIN(state, opt) {
		state.token = opt.token;
		Cache.set(LOGIN_STATUS, opt.token);
	},
	SETUID(state, val) {
		state.uid = val;
		Cache.set(UID, val);
	},
	UPDATE_LOGIN(state, token) {
		state.token = token;
	},
	LOGOUT(state) {
		state.token = undefined;
		state.uid = undefined
		Cache.clear(LOGIN_STATUS);
		Cache.clear(UID);
		Cache.clear(USER_INFO);
	},
	BACKGROUND_COLOR(state, color) {
		state.color = color;
		document.body.style.backgroundColor = color;
	},
	UPDATE_USERINFO(state, userInfo) {
		state.userInfo = userInfo;
		Cache.set(USER_INFO, userInfo);
	},
	OPEN_HOME(state) {
		state.homeActive = true;
	},
	CLOSE_HOME(state) {
		state.homeActive = false;
	},
	SET_CHATURL(state, chatUrl) {
		state.chatUrl = chatUrl;
	},
	SYSTEM_PLATFORM(state, systemPlatform) {
		state.systemPlatform = systemPlatform;
		Cache.set(PLATFORM, systemPlatform);
	},
	//更新useInfo数据
	changInfo(state, payload) {
		state.userInfo[payload.amount1] = payload.amount2;
		Cache.set(USER_INFO, state.userInfo);
	},
	//商品类型，用于区分视频号商品与一般商品
	PRODUCT_TYPE(state, productType) {
		state.productType = productType;
		Cache.set('productType', productType);
	},
	GLOBAL_DATA(state, key) {
		Cache.set(GLOBAL_DATA, key);
		state.globalData = key;
	},
	/** 商户全部分类  **/
	SET_MerchantClassify: (state, merchantClassify) => {
		state.merchantClassify = changeNodes(merchantClassify)
		Cache.set('merchantClassify', JSON.stringify(changeNodes(merchantClassify)));
	},
	/** 商户全部类型 **/
	SET_MerchantType: (state, merchantType) => {
		state.merchantType = changeNodes(merchantType)
		Cache.set('merchantType', JSON.stringify(changeNodes(merchantType)));
	},
	/** 商户信息 **/
	MERCHANTJINFO: (state, merchantJInfo) => {
		state.merchantAPPInfo = merchantJInfo
		Cache.set('merchantAPPInfo', merchantJInfo);
	},
	/** 入驻申请信息 **/
	MERSTTLEDDATA: (state, merSttledData) => {
		state.merSttledData = merSttledData
		Cache.set('merSttledData', merSttledData);
	},
	/** 是否使用自定义导航 **/
	BottomNavigationIsCustom: (state, bottomNavigationIsCustom) => {
		state.bottomNavigationIsCustom = bottomNavigationIsCustom
	},
	/** 校验token是否有效 **/
	TokenIsExist: (state, merTokenIsExist) => {
		state.merTokenIsExist = merTokenIsExist
		Cache.set('merTokenIsExist', merTokenIsExist);
	},
	/** 选中的话题列表 **/
	DiscoverTopic: (state, discoverTopic) => {
		state.discoverTopic = discoverTopic
		//Cache.set('merTokenIsExist', merTokenIsExist);
	},
};

/** tree去除 childList=[] 的结构**/
const changeNodes = function(data) {
	if (data.length > 0) {
		for (var i = 0; i < data.length; i++) {
			if (!data[i].childList || data[i].childList.length < 1) {
				data[i].childList = undefined;
			} else {
				changeNodes(data[i].childList);
			}
		}
	}
	return data
};

const actions = {
	/**
	 * 校验token是否有效,true为有效，false为无效
	 */
	GetTokenIsExist({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			tokenIsExistApi().then(res => {
				commit('TokenIsExist', res.data)
				reslove(res.data);
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	/**
	 * 用户信息
	 */
	USERINFO({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getUserInfo().then(res => {
				commit("UPDATE_USERINFO", res.data);
				commit("SETUID", res.data.id);
				reslove(res.data);
			});
		}).catch(() => {

		});
	},

	/**
	 * 退出登录
	 */
	GETLOGOUT({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getLogout().then(res => {
				commit("LOGOUT");
				uni.reLaunch({
					url: '/pages/index/index'
				});
			});
		}).catch(() => {

		});
	},
	MerCategoryList({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getMerCategoryListApi().then(res => {
				commit('SET_MerchantClassify', res.data)
				reslove(res.data);
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	MerTypeList({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getMerTypeListApi().then(res => {
				commit('SET_MerchantType', res.data)
				reslove(res.data);
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	getPayConfig({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getPayConfigApi().then(res => {
				let data = res.data;
				cartArr[0].payStatus = data.payWechatOpen ? 1 : 0;
				cartArr[1].payStatus = data.yuePayStatus ? 1 : 0;
				cartArr[1].userBalance = data.userBalance ? data.userBalance : 0;
				// #ifdef H5
				if (Auth.isWeixin()) {
					cartArr[2].payStatus = 0;
				} else {
					cartArr[2].payStatus = data.aliPayStatus ? 1 : 0;
				}
				// #endif
				// #ifdef APP-PLUS
				cartArr[2].payStatus = data.aliPayStatus ? 1 : 0;
				// #endif
				let cartArrs = cartArr.filter(e => e.payStatus === 1);
				reslove(cartArrs);
			})
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	}
}
export default {
	state,
	mutations,
	actions
};
