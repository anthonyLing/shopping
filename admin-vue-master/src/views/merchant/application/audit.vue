<template>
  <div class="box">
    <el-drawer
      :visible.sync="dialogVisible"
      :title="isSHOW ? '商户入驻审核' : '商户详情'"
      :direction="direction"
      @close="close"
      size="700px"
    >
      <div class="demo-drawer__content divBox">
        <el-form ref="dataForm" :inline="true" :model="dataForm" label-width="100px" v-if="dialogVisible">
          <el-form-item label="商户名称：" prop="name">
            <el-input v-model.trim="dataForm.name" :readonly="isDisabled" placeholder="请输入商户名称" />
          </el-form-item>
          <el-form-item label="商户账号：">
            <el-input v-model.trim="dataForm.phone" :readonly="isDisabled" placeholder="请输入商户账号" />
          </el-form-item>
          <el-form-item label="商户分类：" prop="categoryId">
            <span class="widths">{{ dataForm.categoryId | merCategoryFilter }}</span>
          </el-form-item>
          <el-form-item label="店铺类型：" prop="typeId">
            <span class="widths">{{ dataForm.typeId | merchantTypeFilter }}</span>
          </el-form-item>
          <el-form-item v-if="dataForm.password" label="登录密码" prop="password">
            <el-input v-model.trim="dataForm.password" :readonly="isDisabled" placeholder="请输入登录密码" />
          </el-form-item>
          <el-form-item label="商户姓名：" prop="realName">
            <el-input v-model.trim="dataForm.realName" :readonly="isDisabled" placeholder="请输入商户姓名" />
          </el-form-item>
          <el-form-item label="商户手机号：" prop="phone">
            <span class="widths">{{ dataForm.phone | filterEmpty }}</span>
          </el-form-item>
          <el-form-item label="手续费(%)：" prop="handlingFee">
            <el-input :disabled="isDisabled" v-model.trim="dataForm.handlingFee" :min="0" :precision="2"></el-input>
          </el-form-item>
          <el-form-item label="商户地址：" prop="address" class="lang">
            <span class="langcent">{{ dataForm.address }}</span>
          </el-form-item>
          <el-form-item label="简介：" prop="keywords" class="inline">
            <div>{{ dataForm.keywords || '无' }}</div>
            <!-- <el-input v-model="" :readonly="isDisabled" placeholder="请输入简介" /> -->
          </el-form-item>
          <el-form-item label="备注：" prop="remark" class="lang">
            <span class="widths">{{ dataForm.remark ? dataForm.remark : '无' }}</span>
          </el-form-item>
          <el-form-item label="资质图片：" class="lang">
            <div class="acea-row">
              <div
                v-for="(item, index) in dataForm.qualificationPictureData"
                :key="index"
                class="pictrue"
                draggable="true"
                @dragstart="handleDragStart($event, item)"
                @dragover.prevent="handleDragOver($event, item)"
                @dragenter="handleDragEnter($event, item)"
                @dragend="handleDragEnd($event, item)"
              >
                <el-image :src="item" :preview-src-list="dataForm.qualificationPictureData"> </el-image>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="是否开启：">
            <span class="widths">{{ dataForm.isSwitch ? '显示' : '隐藏' }}</span>
          </el-form-item>
          <el-form-item label="是否推荐：">
            <span class="widths">{{ dataForm.isRecommend ? '推荐' : '不推荐' }}</span>
          </el-form-item>
          <el-form-item label="是否自营：">
            <span class="widths">{{ dataForm.isSelf ? '自营' : '非自营' }}</span>
          </el-form-item>
          <el-form-item label="商品审核：">
            <span class="widths">{{ dataForm.productSwitch ? '开启' : '关闭' }}</span>
          </el-form-item>
        </el-form>
      </div>
      <div class="demo-drawer__footer">
        <div v-if="isSHOW" class="from-foot-btn drawer_fix">
          <div class="acea-row justify-content">
            <el-button
              v-debounceClick="
                () => {
                  onSubmit(3);
                }
              "
              style="margin-left: 0"
              >{{ loadingBtn ? '提交中 ...' : '审核拒绝' }}</el-button
            >
            <el-button
              type="primary"
              v-debounceClick="
                () => {
                  onSubmit(2);
                }
              "
              >{{ loadingBtn ? '提交中 ...' : '审核通过' }}</el-button
            >
          </div>
        </div>
      </div>
    </el-drawer>
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
import { merApplyAuditApi } from '@/api/merchant';
import { mapGetters } from 'vuex';
export default {
  name: 'audit',
  data() {
    return {
      dialogVisible: false,
      direction: 'rtl',
      isDisabled: true,
      rules: {
        auditStatus: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
        denialReason: [{ required: true, message: '请填写拒绝原因', trigger: 'blur' }],
      },
      ruleForm: {
        denialReason: '',
        auditStatus: 2,
        id: '',
      },
      loadingBtn: false,
    };
  },
  props: {
    merData: {
      type: Object,
      default: () => null,
    },
    isSHOW: {
      type: String,
      default: () => '',
    },
  },
  computed: {
    ...mapGetters(['merchantClassify', 'merchantType']),
  },
  watch: {
    merData: {
      handler: function (val) {
        if (val.qualificationPicture) val.qualificationPictureData = JSON.parse(val.qualificationPicture);
        this.dataForm = { ...val };
      },
      deep: true,
    },
  },
  methods: {
    close() {
      this.dialogVisible = false;
      this.ruleForm = {
        denialReason: '',
        auditStatus: 2,
      };
    },
    //审核拒绝
    cancelForm() {
      this.$modalPrompt('textarea', '拒绝原因').then((V) => {
        this.ruleForm.denialReason = V;
        this.submit();
      });
    },
    // 审核提交
    onSubmit(type) {
      this.ruleForm.auditStatus = type;
      if (type === 2) {
        this.$modalSure('审核通过该商户吗？').then(() => {
          this.submit();
        });
      } else {
        this.cancelForm();
      }
    },
    submit() {
      this.loadingBtn = true;
      this.ruleForm.id = this.dataForm.id;
      merApplyAuditApi(this.ruleForm)
        .then((res) => {
          this.$message.success('操作成功');
          this.dialogVisible = false;
          this.$emit('subSuccess');
          this.loadingBtn = false;
        })
        .catch((res) => {
          this.loadingBtn = false;
        });
    },
  },
};
</script>

<style scoped lang="scss">
.demo-drawer__content {
  padding-bottom: 86px;
}
.box {
  ::v-deep.el-drawer__header {
    margin-bottom: 0 !important;
    font-size: 20px;
  }
}
.demo-drawer__content {
  min-height: 600px;
}
.widths {
  width: 169px;
  display: inline-block;
  color: #606266;
}
.langcent {
  display: inline-block;
  color: #606266;
  width: 100%;
}
.lang {
  width: 100%;
  ::v-deep.el-form-item__content {
    width: 79%;
  }
}
.divBox {
  ::v-deep .el-input__inner:hover,
  ::v-deep.el-input > input,
  ::v-deep.el-textarea > textarea {
    border: none;
    padding: 0;
  }
  ::v-deep .el-form-item {
    margin-bottom: 6px;
  }
  ::v-deep.el-card__body {
    padding: 5px;
  }
  ::v-deep .el-input.is-disabled .el-input__inner {
    background: none;
    cursor: none;
    color: #606266;
  }
}
::v-deep .el-image {
  width: 60px;
  height: 60px;
}
</style>
