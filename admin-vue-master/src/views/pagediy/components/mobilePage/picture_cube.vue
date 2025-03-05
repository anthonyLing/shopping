<template>
  <div class="mobile-page" v-if="configObj" :style="boxStyle">
    <div class="advert">
      <template v-if="style === 0">
        <div class="advertItem01 acea-row" v-for="(item, index) in picList" :key="index">
          <img :src="item.image" v-if="item.image" />
          <div class="empty-box" v-else>
            <span class="iconfont icon-tu"></span>
          </div>
        </div>
      </template>

      <div class="advertItem02 acea-row" v-if="style === 1">
        <div class="item" v-for="(item, index) in picList" :key="index">
          <img :src="item.image" v-if="item.image" />
          <div class="empty-box" v-else>
            <span class="iconfont icon-tu"></span>
          </div>
        </div>
      </div>
      <div class="advertItem02 advertItem03 acea-row" v-if="style === 2">
        <div class="item" v-for="(item, index) in picList" :key="index">
          <img :src="item.image" v-if="item.image" />
          <div class="empty-box" v-else>
            <span class="iconfont icon-tu"></span>
          </div>
        </div>
      </div>
      <div class="advertItem04 acea-row" v-if="style === 3">
        <div class="item">
          <img :src="picList[0].image" v-if="picList[0].image" />
          <div class="empty-box" v-else>
            <span class="iconfont icon-tu"></span>
          </div>
        </div>
        <div class="item">
          <div class="pic">
            <img :src="picList[1].image" v-if="picList[1].image" />
            <div class="empty-box" v-else>
              <span class="iconfont icon-tu"></span>
            </div>
          </div>
          <div class="pic">
            <img :src="picList[2].image" v-if="picList[2].image" />
            <div class="empty-box" v-else>
              <span class="iconfont icon-tu"></span>
            </div>
          </div>
        </div>
      </div>
      <div class="advertItem02 advertItem05 acea-row" v-if="style === 4">
        <div class="item" v-for="(item, index) in picList" :key="index">
          <img :src="item.image" v-if="item.image" />
          <div class="empty-box" v-else>
            <span class="iconfont icon-tu"></span>
          </div>
        </div>
      </div>
      <div class="advertItem06 acea-row" v-if="style === 5">
        <div class="item" v-for="(item, index) in picList" :key="index">
          <img :src="item.image" v-if="item.image" />
          <div class="empty-box" v-else>
            <span class="iconfont icon-tu"></span>
          </div>
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
  name: 'picture_cube',
  cname: '图片魔方',
  configName: 'c_picture_cube',
  icon: 't-icon-zujian-tupianmofang',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'pictureCube', // 外面匹配名称
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
        name: 'pictureCube',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '图片魔方',
        },
        tabConfig: {
          title: '选择样式',
          tabTitle: '内容设置',
          isShow: 1,
          tabVal: 0,
          list: [
            {
              val: '样式一',
              icon: 'icon-tupian-yangshiyi',
              count: 1,
            },
            {
              val: '样式二',
              icon: 'icon-tupian-yangshier',
              count: 2,
            },
            {
              val: '样式三',
              icon: 'icon-tupian-yangshisan',
              count: 3,
            },
            {
              val: '样式四',
              icon: 'icon-tupian-yangshisi',
              count: 3,
            },
            {
              val: '样式五',
              icon: 'icon-tupian-yangshiwu',
              count: 4,
            },
            {
              val: '样式六',
              icon: 'icon-tupian-yangshiliu',
              count: 4,
            },
          ],
        },
        picStyle: {
          tabVal: 0,
          picList: [
            {
              image: '',
              link: '',
            },
          ],
        },
        menuConfig: {
          title: '',
          maxList: 1,
          isCube: 1,
          isSmall: false,
          list: [
            {
              img: '',
              info: [
                {
                  title: '链接',
                  tips: '请输入链接',
                  value: '',
                  max: 100,
                },
              ],
            },
          ],
        },
        bgColor: {
          title: '背景颜色',
          tabTitle: '颜色设置',
          default: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
            },
          ],
          color: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
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
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 40,
        },
        // 页面间距
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
        mbConfig: {
          title: '页面间距',
          val: 10,
          min: 0,
        },
      },
      pageData: {},
      style: 0,
      picList: [],
      configObj: null,
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
        this.style = data.tabConfig.tabVal;
        if (!data.picStyle.picList.length) {
          this.picList = [
            {
              image: '',
              link: '',
            },
          ];
        } else {
          this.picList = data.picStyle.picList;
        }
      }
    },
  },
};
</script>
<style scoped lang="scss">
.pageOn {
  border-radius: 12px !important;
  .advertItem01 {
    img {
      border-radius: 10px;
    }
  }
  .advertItem02 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 10px;
        }
      }
      &:nth-child(2) {
        img {
          border-radius: 0 10px 10px 0;
        }
      }
    }
  }
  .advertItem03 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 10px;
        }
      }
      &:nth-child(2) {
        img {
          border-radius: 0;
        }
      }
      &:nth-child(3) {
        img {
          border-radius: 0 10px 10px 0;
        }
      }
    }
  }
  .advertItem04 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 10px;
        }
      }
      &:nth-child(2) {
        .pic {
          &:nth-child(1) {
            img {
              border-radius: 0 10px 0 0;
            }
          }
          &:nth-child(2) {
            img {
              border-radius: 0 0 10px 0;
            }
          }
        }
      }
    }
  }
  .advertItem05 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 10px;
        }
      }
      &:nth-child(2) {
        img {
          border-radius: 0;
        }
      }
      &:nth-child(4) {
        img {
          border-radius: 0 10px 10px 0;
        }
      }
    }
  }
  .advertItem06 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 0;
        }
      }
      &:nth-child(2) {
        img {
          border-radius: 0 10px 0 0;
        }
      }
      &:nth-child(3) {
        img {
          border-radius: 0 0 0 10px;
        }
      }
      &:nth-child(4) {
        img {
          border-radius: 0 0 10px 0;
        }
      }
    }
  }
}
.mobile-page {
  .advert {
    .advertItem01 {
      width: 100%;
      height: 100%;
      .empty-box {
        width: 100%;
        height: 379px;
        border-radius: 0;
        .icon-tu {
          font-size: 50px;
          color: #999;
        }
      }
      img {
        width: 100%;
        height: 100%;
      }
    }
    .advertItem02 {
      width: 100%;
      .item {
        width: 50%;
        height: auto;
        img {
          width: 100%;
          height: 100%;
        }
        .empty-box {
          width: 100%;
          height: 189.5px;
          border-radius: 0;
        }
      }
    }
    .advertItem03 {
      .item {
        width: 33.3333%;
        .empty-box {
          width: 100%;
          height: 126.4px;
          border-radius: 0;
        }
      }
    }
    .advertItem04 {
      .item {
        width: 50%;
        height: 189.5px;
        .empty-box {
          width: 100%;
          height: 100%;
          border-radius: 0;
        }
        img {
          width: 100%;
          height: 100%;
        }
        .pic {
          width: 100%;
          height: 94.75px;
        }
      }
    }
    .advertItem05 {
      .item {
        width: 25%;
        .empty-box {
          width: 100%;
          height: 94.75px;
          border-radius: 0;
        }
      }
    }
    .advertItem06 {
      .item {
        width: 50%;
        height: 95px;
        img {
          width: 100%;
          height: 100%;
        }
        .empty-box {
          width: 100%;
          height: 100%;
          border-radius: 0;
        }
      }
    }
  }
}
</style>
