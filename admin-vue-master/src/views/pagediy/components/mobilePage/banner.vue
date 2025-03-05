<template>
  <div class="mobile-page" :style="boxStyle" v-if="configObj">
    <div class="banner">
      <img :src="imgSrc" alt="" v-if="imgSrc" :style="contentStyle" />
      <div class="empty-box" :style="contentStyle" v-else>
        <span class="iconfont-diy iconfont icon-tu"></span>
      </div>
      <div>
        <div class="dot" :style="dotStyle" v-if="docType === 0">
          <div class="dot-item" :style="{ background: configObj.docColor.color[0].item }"></div>
          <div class="dot-item"></div>
          <div class="dot-item"></div>
        </div>
        <div class="dot line-dot" :style="dotStyle" v-if="docType === 1">
          <div class="line_dot-item" :style="{ background: configObj.docColor.color[0].item }"></div>
          <div class="line_dot-item"></div>
          <div class="line_dot-item"></div>
        </div>
      </div>
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
import { mapState } from 'vuex';
export default {
  name: 'banner', // 组件名称
  cname: '轮播图', // 标题名称
  icon: 't-icon-zujian-lunbotu',
  defaultName: 'swiperBg', // 外面匹配名称
  configName: 'c_banner', // 右侧配置名称
  type: 0, // 0 基础组件 1 营销组件 2工具组件
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
    //最外层盒子的样式
    boxStyle() {
      return [
        { 'border-radius': this.configObj.bgStyle.val ? this.configObj.bgStyle.val + 'px' : '0' },
        {
          background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + 0 + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    //内容圆角
    contentStyle() {
      return [{ 'border-radius': this.configObj.contentStyle.val ? this.configObj.contentStyle.val + 'px' : '0' }];
    },
    //指示器样式
    dotStyle() {
      return [
        { 'padding-left': this.configObj.lrConfig.val ? this.configObj.lrConfig.val + 10 + 'px' : '10' + 'px' },
        { 'padding-right': this.configObj.lrConfig.val ? this.configObj.lrConfig.val + 10 + 'px' : '10' + 'px' },
        {
          'justify-content':
            this.configObj.txtStyle.tabVal === 1
              ? 'center'
              : this.configObj.txtStyle.tabVal === 2
              ? 'flex-end'
              : 'flex-start',
        },
      ];
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
      // 默认初始化数据禁止修改
      defaultConfig: {
        name: 'swiperBg',
        isShow: true,
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '轮播图',
        },
        // 图片列表
        swiperConfig: {
          tabTitle: '版块设置',
          tips: '建议：图片尺寸750*332px；鼠标拖拽版块可调整图片顺序',
          title: '建议：图片尺寸750*332px；鼠标拖拽版块可调整图片顺序',
          maxList: 1000,
          list: [
            {
              img: '',
              info: [
                {
                  title: '标题',
                  value: '今日推荐',
                  tips: '选填，不超过8个字',
                  max: 8,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请选择链接',
                  max: 100,
                },
              ],
            },
          ],
        },
        // 上间距
        upConfig: {
          title: '上边距',
          val: 10,
          min: 0,
          max: 100,
        },
        // 下间距
        downConfig: {
          tabTitle: '边距设置',
          title: '下边距',
          val: 10,
          min: 0,
        },
        bgStyle: {
          tabTitle: '圆角设置',
          title: '背景圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 40,
        },
        // 页面间距
        mbConfig: {
          title: '页面间距',
          val: 0,
          min: 0,
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
        // 指示器颜色
        docColor: {
          title: '指示器颜色',
          name: 'docColor',
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
        // 轮播图点样式
        docConfig: {
          cname: 'swiper',
          title: '指示器样式',
          tabTitle: '指示器设置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '圆形',
              icon: 'icon-yuandian',
            },
            {
              val: '直线',
              icon: 'icon-xiantiao',
            },
            {
              val: '无指示器',
              icon: 'icon-buxianshi',
            },
          ],
        },
        contentStyle: {
          title: '内容圆角',
          name: 'contentStyle',
          val: 7,
          min: 0,
          max: 30,
        },
        txtStyle: {
          title: '指示器位置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '居左',
              icon: 'icon-juzuo',
            },
            {
              val: '居中',
              icon: 'icon-juzhong',
            },
            {
              val: '居右',
              icon: 'icon-juyou',
            },
          ],
        },
      },
      configObj: null,
      pageData: {},
      imgSrc: '',
      docType: 0,
    };
  },
  mounted() {
    this.$nextTick(() => {
      if (this.num) {
        this.pageData = this.$store.state.mobildConfig.defaultArray[this.num];
        this.setConfig(this.pageData);
      }
    });
  },
  methods: {
    onChange() {},
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.imgSrc = data.swiperConfig.list.length ? data.swiperConfig.list[0].img : '';
        this.docType = data.docConfig.tabVal;
      }
    },
  },
};
</script>
<style scoped lang="scss">
.empty-box {
  height: 150px;
}
.mobile-page {
  position: relative;
  width: auto;
  .banner {
    width: 100%;
    height: 150px;
    margin-top: 0;
    position: relative;
    &.on {
      margin-top: -160px;
    }
    img {
      width: 100%;
      height: 100%;
      border-radius: 10px;
      &.doc {
        border-radius: 0 !important;
      }
    }
  }
  .bg {
    width: 100%;
    height: 160px;
  }
}
.dot {
  position: absolute;
  left: 0;
  bottom: 20px;
  width: 100%;
  display: flex;
  align-items: center;
  &.number {
    bottom: 4px;
  }
  .num {
    width: 25px;
    height: 18px;
    line-height: 18px;
    background-color: #000;
    color: #fff;
    opacity: 0.3;
    border-radius: 8px;
    font-size: 12px;
    text-align: center;
  }
  .dot-item {
    width: 5px;
    height: 5px;
    background: #aaaaaa;
    border-radius: 50%;
    margin: 0 3px;
  }
  &.line-dot {
    bottom: 20px;
    .line_dot-item {
      width: 8px;
      height: 2px;
      background: #aaaaaa;
      margin: 0 3px;
    }
  }
}
</style>
