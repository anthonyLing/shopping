<template>
  <div>
    <el-dialog
      title="上传图片"
      :visible.sync="visible"
      width="950px"
      :modal="booleanVal"
      append-to-body
      :before-close="handleClose"
    >
      <upload-picture v-if="visible" :isMore="isMore" :modelName="modelName" @getImage="getImage"></upload-picture>
    </el-dialog>
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
export default {
  name: 'UploadFroms',
  data() {
    return {
      visible: false,
      callback: function () {},
      isMore: '',
      modelName: '',
      ISmodal: false,
      booleanVal: false,
    };
  },
  watch: {
    // show() {
    //   this.visible = this.show
    // }
  },
  methods: {
    handleClose() {
      this.visible = false;
    },
    getImage(img) {
      this.callback(img);
      this.visible = false;
    },
  },
};
</script>

<style scoped></style>
