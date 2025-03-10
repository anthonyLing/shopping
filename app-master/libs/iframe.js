// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import $store from "@/store";
/**
 * 点击事件，判断是否是isIframe页面
*/ 
export function goPage() {
	return new Promise(resolve => {
		if ($store.state.app.globalData.isIframe == false) {
			resolve(true);
		}else{
			return false
		}
	});
}
