// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import request from "@/utils/request.js";

/**
 * 获取首页秒杀信息
 * 
*/
export function getSeckillIndexApi(){
  return request.get('index/seckill/info',{},{ noAuth : true});
}

/**
 * 秒杀时段信息
 * 
*/
export function getSeckillHeaderApi(){
  return request.get('Seckill/activity/time/info',{},{ noAuth : true});
}

/**
 * 秒杀商品列表
 * 
*/
export function getSeckillListApi(data){
  return request.get('Seckill/product/list',data,{ noAuth : true});
}

/**
 * 优惠券列表
 * 
*/
export function getCouponLstApi(data){
  return request.get('coupon/voucher/collection/center',data,{ noAuth : true});
}

/**
 * 优惠券列表
 * 
*/
export function couponReceiveApi(id){
  return request.post(`coupon/receive/${id}`);
}