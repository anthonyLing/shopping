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
	preOrderApi
} from '@/api/order.js';
import util from '@/utils/util'
import animationType from '@/utils/animationType.js'

/**
 * 去订单详情
 */
export function goOrderDetail(orderNo) {
	return new Promise(resolve => {
		if (!orderNo) return that.$util.Tips({
			title: '缺少订单号无法查看订单详情'
		});
		// #ifdef MP
		uni.navigateTo({
			url: '/pages/goods/order_details/index?orderNo=' + orderNo
		})
		// #endif  
		// #ifndef MP
		uni.navigateTo({
			animationType: animationType.type,
			animationDuration: animationType.duration,
			url: '/pages/goods/order_details/index?orderNo=' + orderNo
		})
		// #endif
	});
}

/**
 * 去商品详情
 */
export function goShopDetail(id) {
	return new Promise(resolve => {
		// #ifdef MP
		uni.navigateTo({
			url: `/pages/goods/goods_details/index?id=${id}`
		})
		// #endif
		// #ifndef MP
		uni.navigateTo({
			animationType: animationType.type,
			animationDuration: animationType.duration,
			url: `/pages/goods/goods_details/index?id=${id}`
		})
		// #endif
	});
}

/**
 * 活动商品、普通商品、购物车、再次购买预下单
 */
export function getPreOrder(preOrderType, orderDetails) {
	return new Promise((resolve, reject) => {
		preOrderApi({
			"preOrderType": preOrderType,
			"orderDetails": orderDetails
		}).then(res => {
			uni.navigateTo({
				url: '/pages/goods/order_confirm/index?orderNo=' + res.data.orderNo
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		})
	});
}
