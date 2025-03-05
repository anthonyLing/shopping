// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import VueI18n from 'vue-i18n'
import Vue from 'vue'
import zh from './common/zh.js'
import en from './common/en.js'
import fr from './common/fr.js'
import th from './common/th.js'
import lao from './common/lao.js'

Vue.use(VueI18n)
export default new VueI18n({
	locale:'zh',
	messages:{
		'en': en,
		'zh': zh,
		'fr': fr,
		'th': th,
		'lao': lao
	}
})
