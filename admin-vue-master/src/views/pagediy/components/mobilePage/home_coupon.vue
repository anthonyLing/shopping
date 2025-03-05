<template>
  <div v-if="configObj">
    <div class="indexList" :style="boxStyle">
      <div class="title acea-row row-between-wrapper">
        <div class="text line1 tui-skeleton-rect acea-row">
          <el-image class="image" :src="src"></el-image>
          <span class="label" :style="titleColor">{{ configObj.titleConfig.val }}</span>
        </div>
        <div class="more tui-skeleton-rect" :style="moreColor">
          MORE
          <span class="iconfont icon-xuanze"></span>
        </div>
      </div>
      <div class="tips mb10">Receive coupons</div>
      <!-- 优惠券 -->
      <template v-if="listStyle == 0">
        <div class="scroll_box">
          <div class="item no_use" :style="contentConfig">
            <div class="itemCon acea-row row-between-wrapper">
              <div class="text">
                <div class="money"><span>¥</span>100</div>
                <div class="info">满1000元可用</div>
              </div>
              <div class="bnt"><span>立即领取</span></div>
            </div>
            <div class="roll up-roll"></div>
            <div class="roll down-roll"></div>
          </div>
          <div class="item">
            <div class="itemCon acea-row row-between-wrapper">
              <div class="text">
                <div class="money"><span>¥</span>100</div>
                <div class="info">满1000元可用</div>
              </div>
              <div class="bnt"><span>已领取</span></div>
            </div>
          </div>
        </div>
      </template>
      <template v-if="listStyle == 1">
        <div class="scroll_box">
          <div class="item1 no_use" :style="contentConfig" v-for="item in 2">
            <div class="text">
              <div class="money"><span>¥</span>100</div>
              <div class="info">满1000元可用</div>
            </div>
            <div class="get_btn">领取</div>
          </div>
        </div>
      </template>
    </div>
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
import { mapState, mapGetters } from 'vuex';
export default {
  name: 'home_coupon',
  cname: '优惠券',
  icon: 't-icon-zujian-youhuiquan',
  configName: 'c_home_coupon',
  type: 1, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'homeCoupon', // 外面匹配名称
  props: {
    index: {
      type: null,
    },
    num: {
      type: null,
    },
  },
  computed: {
    ...mapState('mobildConfig', ['defaultArray']),
    ...mapGetters(['mediaDomain']),
    //最外层盒子的样式
    boxStyle() {
      return [
        { 'border-radius': this.configObj.bgStyle.val ? this.configObj.bgStyle.val + 'px' : '0' },
        {
          background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + '10px' + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    //图片样式
    contentStyle() {
      return {
        'border-radius': this.configObj.contentStyle.val + 'px',
      };
    },
    //内容边距
    contentConfig() {
      return [
        { 'margin-right': this.configObj.contentConfig.val ? this.configObj.contentConfig.val + 'px' : '0' },
        { background: this.configObj.themeColor.color[0].item },
      ];
    },
    //更多颜色
    moreColor() {
      return { color: this.configObj.moreColor.color[0].item };
    },
    //标题颜色
    titleColor() {
      return { color: this.configObj.titleColor.color[0].item };
    },
  },
  watch: {
    pageData: {
      handler(nVal, oVal) {
        this.setConfig(nVal);
      },
      deep: true,
    },
    num: {
      handler(nVal, oVal) {
        let data = this.$store.state.mobildConfig.defaultArray[nVal];
        this.setConfig(data);
      },
      deep: true,
    },
    defaultArray: {
      handler(nVal, oVal) {
        let data = this.$store.state.mobildConfig.defaultArray[this.num];
        this.setConfig(data);
      },
      deep: true,
    },
  },
  data() {
    return {
      defaultConfig: {
        name: 'homeCoupon',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '优惠券',
        },
        tabConfig: {
          title: '展示样式',
          tabTitle: '布局设置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '样式1',
              icon: 'icon-youhuiquan-yangshiyi',
            },
            {
              val: '样式2',
              icon: 'icon-youhuiquan-yangshier',
            },
          ],
        },
        logoConfig: {
          isShow: 1,
          tabTitle: '图标设置',
          title: '上传图标',
          tips: '建议：124px*32px',
          url: localStorage.getItem('mediaDomain') + '/crmebimage/presets/yhq.png',
        },
        titleConfig: {
          tabTitle: '标题设置',
          title: '标题内容',
          val: '下单优惠多',
          place: '请输入标题',
          isShow: 1,
          max: 10,
        },
        linkConfig: {
          title: '更多链接',
          val: '/pages/activity/couponList/index',
          place: '请选择链接',
          isShow: 1,
          max: 100,
        },
        //数量
        numConfig: {
          tabTitle: '优惠券',
          title: '展示数量',
          val: 3,
          isShow: 1,
        },
        // 背景颜色
        bgColor: {
          tabTitle: '颜色设置',
          title: '背景颜色',
          color: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
            },
          ],
          default: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
            },
          ],
        },
        titleColor: {
          title: '标题颜色',
          color: [
            {
              item: '#999999',
            },
          ],
          default: [
            {
              item: '#999999',
            },
          ],
        },
        moreColor: {
          title: '更多按钮颜色',
          color: [
            {
              item: '#282828',
            },
          ],
          default: [
            {
              item: '#282828',
            },
          ],
        },
        themeColor: {
          title: '主题颜色',
          color: [
            {
              item: '#E93323',
            },
          ],
          default: [
            {
              item: '#E93323',
            },
          ],
        },
        bgStyle: {
          tabTitle: '圆角设置',
          title: '背景圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        // 上间距
        upConfig: {
          tabTitle: '边距设置',
          title: '上边距',
          val: 10,
          min: 0,
          max: 100,
        },
        // 下间距
        downConfig: {
          title: '下边距',
          val: 10,
          min: 0,
        },
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 25,
        },
        contentConfig: {
          title: '内容间距',
          val: 10,
          min: 0,
          max: 30,
        },
        mbConfig: {
          title: '顶部间距',
          val: 10,
          min: 0,
        },
      },
      configObj: null,
      listStyle: 0,
      src: '',
    };
  },
  mounted() {
    this.$nextTick(() => {
      let pageData = this.$store.state.mobildConfig.defaultArray[this.num];
      this.setConfig(pageData);
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.listStyle = this.configObj.tabConfig.tabVal;
        this.src = this.configObj.logoConfig.url
          ? this.configObj.logoConfig.url
          : localStorage.getItem('mediaDomain') + '/crmebimage/presets/yhq.png';
      }
    },
  },
};
</script>

<style scoped lang="scss">
.scroll_box .item {
  width: 142px;
  height: 70px;
  background-color: #bbb;
  border-radius: 4px;
  color: #fff;
  position: relative;
  display: inline-block;
  position: relative;
  background-image: radial-gradient(circle at 110px top, #fff, #fff 5px, transparent 6px),
    radial-gradient(circle at 110px bottom, #fff, #fff 5px, transparent 6px);
  .itemCon {
    height: 100%;
    width: 100%;

    .text {
      width: 110px;

      .money {
        font-size: 24px;
        text-align: center;

        span {
          font-size: 12px;
        }
      }

      .info {
        font-size: 12px;
        text-align: center;
      }
    }

    .bnt {
      position: relative;
      height: 100%;
      font-size: 10px;
      display: block;
      line-height: 1.2;
      width: 32px;
      border-left: 1px dashed #fff;
      span {
        position: absolute;
        left: 56%;
        top: 50%;
        transform: translate(-50%, -50%);
      }
    }
  }
}
.scroll_box .item1 {
  display: inline-block;
  width: 95px;
  height: 95px;
  background-color: #bbb;
  border-radius: 7px;
  color: #fff;
  -webkit-mask: radial-gradient(circle at 5px 67px, transparent 5px, red 0) -5px;
  .text {
    padding: 8px 0;
    border-bottom: 1px dashed rgba(255, 255, 255, 0.5);
    .money {
      font-size: 24px;
      text-align: center;
      span {
        font-size: 12px;
      }
    }
    .info {
      font-size: 12px;
      text-align: center;
    }
  }
  .get_btn {
    font-size: 14px;
    text-align: center;
    line-height: 30px;
  }
}
</style>
