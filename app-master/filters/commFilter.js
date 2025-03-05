// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import store from "../store";

// 公共过滤器
export function filterEmpty(val) {
	let _result = '-'
	if (!val) {
		return _result
	}
	_result = val
	return _result
}
/**
 * 商户分类
 */
export function merCategoryFilter(status) {
	if (!status) {
		return ''
	}
	let arrayList = store.getters.merchantClassify;
	let array = arrayList.filter(item => status === item.id)
	if (array.length) {
		return array[0].name
	} else {
		return ''
	}
}

/**
 * 商铺类型
 */
export function merchantTypeFilter(status) {
	if (!status) {
		return ''
	}
	let arrayList = store.getters.merchantType;
	let array = arrayList.filter(item => status === item.id)
	if (array.length) {
		return array[0].name
	} else {
		return ''
	}
}

/**
 * 商户创建类型
 */
export function merCreateTypeFilter(status) {
	const statusMap = {
		'admin': '管理员创建',
		'apply': '商户入驻申请'
	}
	return statusMap[status]
}

/**
 * 商户类别
 */
export function selfTypeFilter(status) {
	const statusMap = {
		true: '自营',
		false: '非自营'
	}
	return statusMap[status]
}

/**
 * 日期去掉时间
 */
export function dateFormat(value) {
	if (!value) {
		return '';
	}
	return value.split(' ')[0];
}

/**
 * 退款状态
 */
export function refundStatusFilter(status) {
	const statusMap = {
		0: '待审核',
		1: '审核未通过',
		2: '退款中',
		3: '退款成功'
	}
	return statusMap[status]
}

/**
 * 订单状态
 */
export function orderStatusFilter(status) {
	const statusMap = {
		0: '待付款',
		1: '待发货',
		2: '部分发货',
		3: '待核销',
		4: '待收货',
		5: '已收货',
		6: '已完成',
		9: '已取消'
	}
	return statusMap[status]
}

/**
 * 支付方式
 */
export function payTypeFilter(status) {
	const statusMap = {
		'weixin': '微信',
		'alipay': '支付宝',
		'yue': '余额'
	}
	return statusMap[status]
}

/**
 * 秒杀时间段状态
 */
export function timeStatusFilter(status) {
	const statusMap = {
		0: '已结束',
		1: '抢购中',
		2: '即将开始',
		3: '明日预告'
	}
	return statusMap[status]
}

/**
 * 订单商品类型
 */
export function orderTypeFilter(status) {
	const statusMap = {
		1: '视频号',
		2: '秒杀',
	}
	return statusMap[status]
}

/**
 * 订单商品类型
 */
export function orderTypeValFilter(status) {
	const statusMap = {
		0: 'normal',
		1: 'seckill',
		4: 'video'
	}
	return statusMap[status]
}

/**
 * 逛逛评论审核状态
 */
export function discoverReplyAuditStatusFilter(status) {
	const statusMap = {
		0: '待审核',
		1: '审核通过',
		2: '审核失败',
	}
	return statusMap[status]
}

/**
 * 平台优惠券类型
 */
export function couponTypeFilter(status) {
	const statusMap = {
		1: '店铺',
		2: '商品',
		3: '通用',
		4: '品类',
		5: '品牌',
		6: '跨店'
	}
	return statusMap[status]
}