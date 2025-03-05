<template>
  <div class="divBox">
    <!--头部-->
    <base-info ref="baseInfo" v-if="checkPermi(['admin:statistics:home:index'])" />
    <!--小方块-->
    <grid-menu class="mb20" />
    <!-- 经营数据、用户渠道 -->
    <user-overview></user-overview>
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import baseInfo from './components/baseInfo';
import gridMenu from './components/gridMenu';
import userOverview from './components/userOverview';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
//import { authCertQuery } from '@/api/authInformation';
import { configGetUniq } from '@/api/systemConfig';
import Cache from '@/plugins/cache';
export default {
  name: 'Dashboard',
  components: { baseInfo, gridMenu, userOverview },
  data() {
    return {
      authStatus: null,
      authHost: '',
      authQueryStatus: false,
      notInformation: null,
    };
  },
  mounted() {
    if (window.location.host.indexOf('localhost') == -1) {
      this.authStatus = Cache.local.has('auth-information') ? Cache.local.getJSON('auth-information') : null;
      this.notInformation = Cache.local.has('not-information') ? Cache.local.getJSON('not-information') : null;
      if (!this.authStatus) {
        this.getUniq();
      }
    }
  },
  methods: {
    checkPermi,
    authInformationQuery() {
      authCertQuery({ host: this.authHost }).then((res) => {
        let oneDay = 24 * 3600 * 1000;
        if (res.data.status == -1 || res.data.status == 2) {
          Cache.local.setItem({ name: 'not-information', value: true, expires: oneDay });
          if (!notInformation) {
            this.$modal
              .confirm(res.data.msg)
              .then(() => {
                this.$router.push({ path: '/operation/maintain/authCRMEB' });
              })
              .catch(() => {});
          }
        } else if (res.data.status == 1) {
          this.$modal.msgSuccess(res.data.msg);
          Cache.local.setItem({ name: 'auth-information', value: true, expires: oneDay * 7 });
        } else if (res.data.status == 3 || res.data.status == 0) {
          this.$modal.msg(res.data.msg);
        }
      });
    },
    getUniq() {
      configGetUniq({ key: 'authHost' }).then((res) => {
        this.authHost = res;
        if (res !== '') {
          //this.authInformationQuery();
        } else {
          this.$modal.confirm('您尚未提交授权申请').then(() => {
            this.$router.push({ path: '/operation/maintain/authCRMEB' });
          });
        }
      });
    },
  },
};
</script>
