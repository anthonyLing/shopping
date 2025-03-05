<template>
  <div class="sidebar-logo-container" :class="{ collapse: collapse }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="logoSmall" :src="logoSmall" class="sidebar-logo-small" />
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo-big" />
      </router-link>
    </transition>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import * as systemConfigApi from '@/api/systemConfig.js';
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {
      title: 'Vue Element Admin',
      logo: '',
      logoSmall: '',
    };
  },
  mounted() {
    this.getLogo();
    this.getSquareLogo();
  },
  methods: {
    getLogo() {
      systemConfigApi.configGetUniq({ key: 'site_logo_lefttop' }).then((data) => {
        this.logo = data;
      });
    },
    getSquareLogo() {
      systemConfigApi.configGetUniq({ key: 'site_logo_square' }).then((data) => {
        this.logoSmall = data;
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}
.sidebar-logo-big {
  width: auto;
  height: 40px;
  vertical-align: middle;
  margin-right: 12px;
}
.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 55px;
  line-height: 55px;
  text-align: center;
  overflow: hidden;
  background: #fff;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo-small {
      width: 35px;
      height: 35px;
      vertical-align: middle;
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: #fff;
      font-weight: 600;
      line-height: 50px;
      font-size: 14px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }
}
</style>
