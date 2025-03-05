// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import request from '@/utils/request';

/**
 * @description 全部物流公司
 */
export function expressAllApi(params) {
  return request({
    url: 'admin/merchant/express/all',
    method: 'get',
    params,
  });
}

/**
 * @description 物流运费模板列表
 */
export function shippingTemplatesList(data) {
  return request({
    url: '/admin/merchant/shipping/templates/list',
    method: 'get',
    params: { ...data },
  });
}

/**
 * @description 物流运费模板详情
 */
export function templateDetailApi(data) {
  return request({
    url: `/admin/merchant/shipping/templates/info/${data.id}`,
    method: 'get',
  });
}

/**
 * @description 物流运费模板新增
 */
export function shippingSave(data) {
  return request({
    url: 'admin/merchant/shipping/templates/save',
    method: 'post',
    data,
  });
}

/**
 * @description 物流运费模板更新
 */
export function shippingUpdate(data) {
  return request({
    url: 'admin/merchant/shipping/templates/update',
    method: 'post',
    data,
  });
}

/**
 * @description 物流运费模板删除
 */
export function shippingDetete(data) {
  return request({
    url: `admin/merchant/shipping/templates/delete/${data.id}`,
    method: 'POST',
  });
}

/**
 * @description 城市列表
 */
export function cityListTree() {
  return request({
    url: '/admin/merchant/city/region/city/tree',
    method: 'get',
  });
}

/**
 * @description 查询物流公司面单模板
 */
export function exportTempApi(params) {
  return request({
    url: '/admin/merchant/express/template',
    method: 'get',
    params,
  });
}
