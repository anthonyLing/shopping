<template>
  <div class="page-container" v-if="configObj">
    <div class="bg-img">
      <img :src="imgSrc" alt="" />
    </div>
    <div class="search-box">
      <img :src="logoUrl" alt="" v-if="configObj.logoConfig.isShow === 1 && logoUrl" mode="widthFix" />
      <div :style="contentStyle" class="box line1"><i class="el-icon-search" />{{ hotWord }}</div>
    </div>
    <!--选项卡-->
    <div class="menus">
      <div
        class="item line1"
        v-for="(item, index) in list"
        :class="{ on: curIndex === index }"
        :key="index"
        :style="{
          color: curIndex === index ? configObj.checkColor.color[0].item : textColor,
        }"
      >
        {{ item.title }} <span :style="checkColor"></span>
      </div>
      <div class="category">
        <span :style="checkColor" class="iconfont icon-shouye_xidingfenlei"></span>
      </div>
    </div>
    <!--轮播图-->
    <div class="banner">
      <img :style="contentStyleBanner" :src="imgSrc" alt="" v-if="imgSrc" />
      <div :style="contentStyleBanner" class="empty-box" v-else>
        <span class="iconfont iconfont icon-tu"></span>
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
import { mapState, mapGetters } from 'vuex';
export default {
  name: 'home_comb',
  cname: '组合组件',
  icon: 't-icon-zujian-zuhezujian',
  configName: 'c_home_comb',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'homeComb', // 外面匹配名称
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
    //标签文字颜色
    textColor() {
      return this.configObj.fontColor.color[0].item;
    },
    //选中颜色
    checkColor() {
      return { background: this.configObj.checkColor.color[0].item };
    },
    //指示器样式
    dotStyle() {
      return [
        { padding: '0 22px' },
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
    //搜索框样式
    contentStyle() {
      return [
        { 'border-radius': this.configObj.contentStyle.val ? this.configObj.contentStyle.val + 'px' : '0' },
        {
          background: this.configObj.borderColor.color[0].item,
        },
        {
          color: this.configObj.textColor.color[0].item,
        },
        {
          'text-align': this.configObj.textPosition.list[this.configObj.textPosition.tabVal].style,
        },
      ];
    },
    //轮播图圆角
    //内容圆角
    contentStyleBanner() {
      return [
        { 'border-radius': this.configObj.contentStyleBanner.val ? this.configObj.contentStyleBanner.val + 'px' : '0' },
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
        name: 'homeComb',
        timestamp: this.num,
        setUp: {
          cname: '组合组件',
        },
        tabConfig: {
          title: '选择组件',
          tabTitle: '设置内容',
          tabVal: 0,
          type: 0,
          isShow: 1,
          list: [
            {
              val: '搜索框',
              icon: 'icon-zuhe-sousuokuang',
              count: 1,
            },
            {
              val: '选项卡',
              icon: 'icon-zuhe-fenlei',
              count: 2,
            },
            {
              val: '轮播图',
              icon: 'icon-zuhe-lunbotu',
              count: 3,
            },
          ],
        },
        hotWords: {
          title: '搜索热词',
          tabTitle: '搜索热词',
          tips: '最多可设置20个热词，鼠标拖拽左侧圆点可调整热词顺序',
          list: [
            {
              val: '兰蔻小黑瓶',
            },
          ],
        },
        placeWords: {
          title: '提示文字',
          tabTitle: '提示文字设置',
          value: '搜索商品名称',
          isShow: 1,
        },
        titleConfig: {
          title: '切换时间',
          val: 3,
          place: '设置搜索热词显示时间',
          max: 100,
          unit: '秒',
          isShow: 1,
        },
        searConfig: {
          title: '选择模板',
          tabTitle: '布局设置',
          tabVal: 1,
          isShow: 1,
          list: [
            {
              val: '通栏',
              icon: 'icon-tonglan',
              count: 1,
            },
            {
              val: 'logo',
              icon: 'icon-logo',
              count: 2,
            },
          ],
        },
        logoConfig: {
          isShow: 1,
          tabTitle: '设置logo',
          title: '上传图标',
          tips: '建议上传大小：宽138px，高48px',
          header: '设置logo',
          url: localStorage.getItem('mediaDomain') + '/crmebimage/presets/shoplogo.png',
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
        // 框体颜色
        borderColor: {
          title: '框体颜色',
          color: [
            {
              item: '#fff',
            },
          ],
          default: [
            {
              item: '#fff',
            },
          ],
        },
        textColor: {
          tabTitle: '样式设置',
          title: '文字颜色',
          default: [
            {
              item: '#303133',
            },
          ],
          color: [
            {
              item: '#303133',
            },
          ],
        },
        //分类设置
        listConfig: {
          title: '鼠标拖拽左侧圆点可调整选项卡顺序',
          tabTitle: '选项卡设置',
          max: 10,
          list: [
            {
              title: '精选',
              val: '',
              type: 0,
              url: '',
            },
            {
              title: '靓丽美妆',
              val: '',
              type: 0,
              url: '',
            },
          ],
        },
        fontColor: {
          tabTitle: '样式设置',
          title: '文字颜色',
          default: [
            {
              item: '#303133',
            },
          ],
          color: [
            {
              item: '#303133',
            },
          ],
        },
        checkColor: {
          title: '选中颜色',
          name: 'checkColor',
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
        contentStyle: {
          title: '内容圆角',
          name: 'contentStyle',
          val: 7,
          min: 0,
          max: 30,
        },
        contentStyleBanner: {
          title: '内容圆角',
          name: 'contentStyleBanner',
          val: 7,
          min: 0,
          max: 30,
        },
        // mbConfig: {
        //   title: '顶部间距',
        //   val: 10,
        //   min: 0,
        // },
        // 轮播图 图片列表
        swiperConfig: {
          tabTitle: '内容设置',
          tips: '最多可添加10张图片，建议宽度750*332px；鼠标拖拽左侧圆点可调整图片顺序',
          title: '最多可添加10张图片，建议宽度750*332px；鼠标拖拽左侧圆点可调整图片顺序',
          maxList: 10,
          isSmall: true,
          list: [
            {
              img: '',
              info: [
                {
                  title: '标题',
                  value: '',
                  tips: '选填，不超过10个字',
                  max: 10,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                },
              ],
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
        // 左右间距
        // lrConfig: {
        //   title: '左右边距',
        //   val: 10,
        //   min: 0,
        // },
        // 轮播图点样式
        docConfig: {
          cname: 'swiper',
          title: '指示器样式',
          tabTitle: '样式设置',
          isShow: 1,
          tabVal: 0,
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
      // tabVal: '',
      pageData: {},
      logoUrl: '',
      hotWord: '',
      imgSrc: '',
      style: 0,
      list: [],
      curIndex: 0,
      bgUrl: '',
      docType: 0,
      configObj: null,
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.pageData = this.$store.state.mobildConfig.defaultArray[this.num];
      this.setConfig(this.pageData);
    });
  },
  methods: {
    getConfig(data) {},
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.list = data.listConfig.list;
        this.logoUrl = data.logoConfig.url
          ? data.logoConfig.url
          : localStorage.getItem('mediaDomain') + '/crmebimage/presets/shoplogo.png';
        this.docType = data.docConfig.tabVal;
        this.hotWord = data.hotWords.list.length > 0 ? data.hotWords.list[0].val : data.placeWords.val;
        this.imgSrc = data.swiperConfig.list.length ? data.swiperConfig.list[0].img : '';
        //this.style = data.tabConfig.tabVal;
      }
    },
  },
};
</script>

<style scoped lang="scss">
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
.page-container {
  background-size: 100% 100%;
  background-repeat: no-repeat;
  position: relative;
  backdrop-filter: blur(95px);
  filter: blur(30rpx);
  padding-bottom: 10px;
  .bg-img {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    z-index: -1;
    filter: blur(30rpx);
    overflow: hidden;
    img {
      width: 100%;
      height: 100%;
      transform: scale(1.2);
      filter: blur(30px);
    }
  }
}
.search-box {
  display: flex;
  align-items: center;
  width: 100%;
  /*height: 27px;*/
  padding: 10px 10px 0;
  cursor: pointer;
  img {
    width: 66px;
    margin-right: 10px;
  }
  .iconfont {
    margin-left: 13px;
    font-size: 18px;
    position: relative;
    color: #fff;
    &::after {
      content: '8';
      width: 11px;
      height: 11px;
      background: #fff;
      color: #e93323;
      border-radius: 50%;
      text-align: center;
      position: absolute;
      top: -3px;
      right: -3px;
      font-size: 1px;
    }
  }
  .box {
    flex: 1;
    height: 30px;
    line-height: 30px;
    color: #999;
    font-size: 12px;
    // padding-left: 10px;
    background: #fff;
    border-radius: 15px;
    position: relative;
    padding: 0 10px 0 25px;
    i {
      font-size: 14px;
      position: absolute;
      left: 8px;
      top: 8px;
    }
  }
}
.capsule {
  width: 80px;
  height: 30px;
  margin-left: 7px;
  opacity: 0.8;
  img {
    width: 80px;
    height: 30px;
  }
}
.menus {
  width: 100%;
  padding: 0 20px 0 15px;
  cursor: pointer;
  overflow: hidden;
  position: relative;
  height: 37px;

  .category {
    width: 20px;
    color: #fff;
    position: absolute;
    right: 15px;
    top: 12px;
    span {
      font-size: 18px;
    }
  }
  .item {
    position: relative;
    float: left;
    margin-right: 15px;
    text-align: center;
    color: #fff;
    font-size: 14px;
    height: 38px;
    line-height: 36px;
    // width: 60px;
    &.on {
      font-size: 15px;
      span {
        display: block;
        position: absolute;
        left: 50%;
        bottom: 5px;
        width: 16px;
        height: 2px;
        transform: translateX(-50%);
        background: #fff;
      }
    }
  }
}
.banner {
  width: 100%;
  height: 150px;
  margin-top: 3px;
  padding: 0 10px;
  &.on {
    margin-top: -160px;
  }
  img {
    width: 100%;
    height: 100%;
    border-radius: 10px;
    &.doc {
      border-radius: 0;
    }
  }
  .empty-box {
    height: 150px;
  }
}
.bg {
  width: 100%;
  height: 160px;
}
</style>
