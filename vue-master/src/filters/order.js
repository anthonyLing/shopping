// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

//订单过滤器

/**
 * @description 支付状态
 */
export function paidFilter(status) {
  const statusMap = {
    true: '已支付',
    false: '未支付',
  };
  return statusMap[status];
}

/**
 * @description 订单状态
 * 2,已收货，待评价
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
    9: '已取消',
  };
  return statusMap[status];
}

/**
 * @description 订单中的退款状态
 *
 */
export function orderRefundStatusFilter(status) {
  const statusMap = {
    0: '未退款',
    1: '申请退款中',
    2: '部分退款',
    3: '已退款',
  };
  return statusMap[status];
}

/**
 * @description 退款状态
 * 2,已收货，待评价
 */
export function refundStatusFilter(status) {
  const statusMap = {
    0: '待审核',
    1: '拒绝退款',
    2: '退款中',
    3: '已退款',
  };
  return statusMap[status];
}

/**
 * @description 支付方式
 */
export function payTypeFilter(status) {
  const statusMap = {
    weixin: '微信',
    alipay: '支付宝',
    yue: '余额',
  };
  return statusMap[status];
}
