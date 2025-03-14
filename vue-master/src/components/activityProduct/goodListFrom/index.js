// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import Element from 'element-ui';
import '@/styles/element-variables.scss';
import uploadFromComponent from './index.vue';
import Vue from 'vue';
import Cookies from 'js-cookie';
Vue.use(Element, {
  size: Cookies.get('size') || 'medium', // set element-ui default size
});
const goodListFrom = {};
goodListFrom.install = function (Vue, options) {
  const ToastConstructor = Vue.extend(uploadFromComponent);
  // 生成一个该子类的实例
  const instance = new ToastConstructor();
  instance.$mount(document.createElement('div'));
  document.body.appendChild(instance.$el);
  Vue.prototype.$modalActivityProduct = function (callback, handleNum, row, activityId) {
    instance.visible = true;
    instance.callback = callback;
    instance.handleNum = handleNum;
    instance.checked = row;
    instance.activityId = activityId;
  };
};
export default goodListFrom;
