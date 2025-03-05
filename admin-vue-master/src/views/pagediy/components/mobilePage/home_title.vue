<template>
  <div v-if="configObj" class="title-box" :style="boxStyle">
    {{ titleTxt }}
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
  name: 'home_title',
  cname: '标题',
  icon: 't-icon-zujian-biaoti',
  configName: 'c_home_title',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'titles', // 外面匹配名称
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
        {
          'border-radius':
            this.configObj.bgTopStyle.val +
            'px' +
            ' ' +
            this.configObj.bgTopStyle.val +
            'px' +
            ' ' +
            this.configObj.bgDownStyle.val +
            'px' +
            ' ' +
            this.configObj.bgDownStyle.val +
            'px',
        },
        {
          background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + '10px' + ' ' + this.configObj.downConfig.val + 'px' },
        {
          'text-align': this.configObj.textPosition.list[this.configObj.textPosition.tabVal].style,
        },
        {
          fontSize: this.configObj.fontSize.val + 'px',
        },
        {
          'font-weight': this.configObj.textStyle.list[this.configObj.textStyle.tabVal].style,
        },
        { 'font-style': this.configObj.textStyle.list[this.configObj.textStyle.tabVal].style },

        { color: this.configObj.fontColor.color[0].item },
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
      defaultConfig: {
        name: 'titles',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '标题',
        },
        titleConfig: {
          tabTitle: '文本标题',
          title: '标题',
          val: '标题',
          place: '请输入标题，限制10个字以内',
          max: 10,
          isShow: 1,
        },
        linkConfig: {
          title: '链接',
          val: '',
          place: '请输入链接地址',
          max: 100,
          isShow: 1,
        },
        fontColor: {
          title: '文字颜色',
          tabTitle: '文字显示设置',
          name: 'fontColor',
          default: [
            {
              item: '#282828',
            },
          ],
          color: [
            {
              item: '#282828',
            },
          ],
        },
        // 背景颜色
        bgColor: {
          tabTitle: '颜色设置',
          title: '背景颜色',
          color: [
            {
              item: 'rgba(255, 255, 255)',
            },
            {
              item: 'rgba(255, 255, 255)',
            },
          ],
          default: [
            {
              item: 'rgba(255, 255, 255)',
            },
            {
              item: 'rgba(255, 255, 255)',
            },
          ],
        },
        bgTopStyle: {
          tabTitle: '圆角设置',
          title: '上圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        bgDownStyle: {
          title: '下圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        textPosition: {
          title: '文本位置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '居左',
              style: 'left',
              icon: 'icon-juzuo',
            },
            {
              val: '居中',
              style: 'center',
              icon: 'icon-juzhong',
            },
            {
              val: '居右',
              style: 'right',
              icon: 'icon-juyou',
            },
          ],
        },
        textStyle: {
          title: '文本样式',
          tabVal: 0,
          list: [
            {
              val: '正常',
              style: 'normal',
              icon: 'icon-zhengchang',
            },
            {
              val: '斜体',
              style: 'italic',
              icon: 'icon-qingxie',
            },
            {
              val: '加粗',
              style: 'bold',
              icon: 'icon-jiacu',
            },
          ],
        },
        fontSize: {
          title: '文本大小',
          val: 12,
          min: 12,
        },
        // 上间距
        upConfig: {
          title: '上边距',
          tabTitle: '边距设置',
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
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 40,
        },
        mbConfig: {
          title: '页面间距',
          val: 10,
          min: 0,
        },
      },
      configObj: null,
      titleTxt: '',
      link: '',
      txtPosition: '',
      txtStyle: '',
      fontSize: 0,
      mTOP: 0,
      titleColor: '',
      themeColor: '',
      prConfig: 0,
      bgStyle: 0,
      pageData: {},
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
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.titleTxt = data.titleConfig.val;
        this.link = data.linkConfig.val;
        // this.txtPosition = data.textPosition.list[data.textPosition.type].style;
        // this.txtStyle = data.textStyle.list[data.textStyle.type].style;
        // this.themeColor = data.themeColor.color[0].item;
        // this.fontSize = data.fontSize.val;
        // this.mTOP = data.mbConfig.val;
        // this.prConfig = data.prConfig.val;
        // this.bgStyle = data.bgStyle.type;
        // this.titleColor = data.titleColor.color[0].item;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.titleOn {
  border-radius: 10px !important;
}
.title-box {
  color: #282828;
  padding: 5px 10px;
}
</style>
