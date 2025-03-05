<template>
  <div class="box">
    <checked-tab :configObj="defaultConfig" configNme="setUp" @getConfig="getConfig"></checked-tab>
    <div v-if="actived === 0">
      <c-title :configObj="defaultConfig" configNme="titleCent"></c-title>
      <div class="c_row-item" v-if="this.$route.query.type !== 2">
        <div class="label">模板名称</div>
        <div class="slider-box">
          <el-input size="small" v-model="name" placeholder="选填不超过15个字" maxlength="15" @change="changName" />
        </div>
      </div>
      <div class="c_row-item">
        <div class="label">页面标题</div>
        <div class="slider-box">
          <el-input size="small" v-model="title" placeholder="选填不超过30个字" maxlength="30" @change="changVal" />
        </div>
      </div>
      <div class="mt20"><txt-tab :configObj="defaultConfig" configNme="textPosition"></txt-tab></div>
    </div>

    <div v-if="actived === 1">
      <c-title :configObj="defaultConfig" configNme="titleColor"></c-title>
      <div class="c_row-item">
        <div class="label">顶部颜色</div>
        <div class="slider-box acea-row row-between row-middle">
          <el-color-picker v-model="titleBgColor" @change="titleBgPickerTap(titleBgColor)" />
          <el-input size="small" v-model="titleBgColor" class="widthIpt"></el-input>
          <span @click="resetBgA">重置</span>
        </div>
      </div>
      <div class="c_row-item">
        <div class="label">标题颜色</div>
        <div class="slider-box acea-row row-between row-middle">
          <el-radio-group v-model="titleColor" @change="handleChangeTitle(titleColor)">
            <el-radio label="#ffffff">白色</el-radio>
            <el-radio label="#000000">黑色</el-radio>
          </el-radio-group>
        </div>
      </div>
      <div class="c_row-item acea-row row-top">
        <div class="label">背景设置</div>
        <div class="slider-box">
          <div class="acea-row row-between row-top color">
            <el-checkbox v-model="bgColor" @change="bgColorTap">背景色</el-checkbox>
            <el-color-picker v-model="colorPicker" @change="colorPickerTap(colorPicker)" />
            <el-input size="small" v-model="colorPicker" style="width: 65%"></el-input>
          </div>
          <div class="acea-row row-between row-top color page_bg">
            <el-checkbox v-model="bgPic" @change="bgPicTap">背景图</el-checkbox>
            <el-radio-group v-model="tabVal" type="button" @change="radioTap">
              <el-radio :label="index" v-for="(item, index) in picList" :key="index">
                <span class="iconfont" :class="item"></span>
              </el-radio>
            </el-radio-group>
          </div>
          <div v-if="bgPic">
            <div class="title">建议尺寸：690 * 240px</div>
            <div class="boxs" @click="modalPicTap">
              <img :src="bgPicUrl" alt="" v-if="bgPicUrl" />
              <div class="upload-box" v-else><i class="iconfont icon-tianjia1" /></div>
              <div class="replace" v-if="bgPicUrl">更换图片</div>
              <!--<span class="iconfont-diy icondel_1" @click.stop="bindDelete" v-if="bgPicUrl"></span>-->
            </div>
          </div>
        </div>
      </div>
    </div>
    <linkaddress ref="linkaddres" @linkUrl="linkUrl"></linkaddress>
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
import { mapState } from 'vuex';
import uploadPictures from '@/components/base/uploadPicture';
import checkedTab from '../mobileConfigRight/c_checked_tab.vue';
import txtTab from '../mobileConfigRight/c_txt_tab.vue';
import cTitle from '../mobileConfigRight/c_title.vue';
import linkaddress from '@/components/linkaddress';
export default {
  name: 'pageTitle',
  components: {
    uploadPictures,
    checkedTab,
    linkaddress,
    txtTab,
    cTitle,
  },
  data() {
    return {
      actived: 0,
      // 默认初始化数据禁止修改
      defaultConfig: {
        titleCent: {
          tabTitle: '基础设置',
        },
        titleColor: {
          tabTitle: '颜色设置',
        },
        setUp: {
          tabVal: '0',
          cname: '页面设置',
        },
      },
      title: '', //页面标题
      name: '', //模板名称
      isShow: true,
      bgColor: false,
      bgPic: false,
      tabVal: '0',
      colorPicker: '#f5f5f5', //背景色
      isChoice: '单选',
      bgPicUrl: '', //背景图
      returnAddress: '', // 返回地址
      titleColor: '#000000', // 标题颜色
      titleBgColor: '#fff', //顶部颜色
      textPosition: 0, //文本位置
      picList: ['icon-dantu', 'icon-pingpu', 'icon-datu'],
      gridBtn: {
        xl: 4,
        lg: 8,
        md: 8,
        sm: 8,
        xs: 8,
      },
      gridPic: {
        xl: 6,
        lg: 8,
        md: 12,
        sm: 12,
        xs: 12,
      },
    };
  },
  computed: {
    ...mapState({
      titleTxt: (state) => state.mobildConfig.pageTitle || '首页',
      nameTxt: (state) => state.mobildConfig.pageName || '模板',
    }),
  },
  watch: {
    nameTxt(val) {
      //直接赋值给本地data中的属性，就不会报错啦
      this.name = val;
    },
  },
  created() {
    let state = this.$store.state.mobildConfig;
    this.title = state.pageTitle || '首页';
    this.name = state.pageName || '模板';
    this.isShow = state.pageShow ? true : false;
    this.bgColor = state.pageColor ? true : false;
    this.bgPic = state.pagePic ? true : false;
    this.colorPicker = state.pageColorPicker;
    this.tabVal = state.pageTabVal;
    this.bgPicUrl = state.pagePicUrl;
    this.returnAddress = state.returnAddress;
    this.titleColor = state.titleColor;
    this.titleBgColor = state.titleBgColor;
  },
  methods: {
    // 重置
    resetBgA() {
      this.titleBgColor = '#fff';
      this.$store.commit('mobildConfig/UPPTitleBgColor', this.titleBgColor);
    },
    getConfig(e) {
      this.actived = e.values;
    },
    getLink() {
      this.$refs.linkaddres.dialogVisible = true;
    },
    //返回链接
    linkUrl(e) {
      this.returnAddress = e;
      this.$store.commit('mobildConfig/UPPReturnAddress', this.returnAddress);
    },
    // 点击图文封面
    modalPicTap() {
      const _this = this;
      this.$modalUpload(function (img) {
        _this.bgPicUrl = img[0].sattDir;
        _this.$store.commit('mobildConfig/UPPICURL', _this.bgPicUrl);
      });
    },
    bindDelete() {
      this.bgPicUrl = '';
    },
    //标题背景颜色
    titleBgPickerTap(colorPicker) {
      this.$store.commit('mobildConfig/UPPTitleBgColor', colorPicker);
    },
    //标题颜色
    handleChangeTitle(colorPicker) {
      this.$store.commit('mobildConfig/UPPTitleColor', colorPicker);
    },
    //背景色
    colorPickerTap(colorPicker) {
      this.$store.commit('mobildConfig/UPPICKER', colorPicker);
    },
    radioTap(val) {
      this.$store.commit('mobildConfig/UPRADIO', val);
    },
    // 标题
    changVal(val) {
      this.$store.commit('mobildConfig/UPTITLE', val);
    },
    //模板名称
    changName(val) {
      this.$store.commit('mobildConfig/nameUpdata', val);
    },
    //是否选背景色
    bgColorTap(val) {
      this.$store.commit('mobildConfig/UPCOLOR', val);
    },
    //是否选背景图
    bgPicTap(val) {
      this.$store.commit('mobildConfig/UPPIC', val);
    },
  },
};
</script>

<style scoped lang="scss">
.widthIpt {
  width: 78%;
}
.label {
  font-size: 12px;
  color: #999;
}
.slider-box {
  width: 81%;
}
.upload-box {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 64px;
  height: 64px;
  border-radius: 3px 3px 3px 3px;
  opacity: 1;
  border: 1px solid #eeeeee;
}
.page_bg {
  ::v-deep .el-radio {
    display: inline-block;
    height: 32px;
    line-height: 30px;
    margin: 0;
    padding: 0 15px;
    color: #515a6e;
    transition: all 0.2s ease-in-out;
    cursor: pointer;
    border: 1px solid #dcdee2;
    border-left: 0;
    background: #fff;
    position: relative;
    &:first-child {
      border-radius: 4px 0 0 4px;
      border-left: 1px solid #dcdee2;
    }
    .el-radio__input {
      display: inline-block;
      margin-right: 4px;
      white-space: nowrap;
      position: relative;
      line-height: 1;
      vertical-align: middle;
      cursor: pointer;
      width: 0;
      margin-right: 0;
    }
    > span {
      margin-left: 0;
    }
    .el-radio__inner {
      opacity: 0;
      width: 0;
      height: 0;
    }
    .el-radio__label {
      padding-left: 0;
    }
  }
}

.slider-box .title {
  color: #999999;
  font-size: 13px;
  margin-bottom: 5px;
}
.c_row-item {
  padding: 0 15px;
  margin-top: 20px;
}
.slider-box .color {
  margin-bottom: 15px;
  .iconfont {
    font-size: 24px;
  }
}
.boxs {
  width: 60px;
  height: 60px;
  margin-bottom: 10px;
  position: relative;
  .replace {
    background: rgba(0, 0, 0, 0.4);
    border-radius: 0 0 6px 6px;
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    color: #fff;
    font-size: 12px;
    text-align: center;
    height: 24px;
    line-height: 24px;
  }

  .iconfont-diy,
  .iconfont {
    position: absolute;
    font-size: 23px;
    color: #ccc;
  }
  img {
    width: 100%;
    height: 100%;
    border-radius: 6px;
  }
}
</style>
