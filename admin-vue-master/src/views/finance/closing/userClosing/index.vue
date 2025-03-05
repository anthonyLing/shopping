<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix" v-hasPermi="['platform:finance:user:closing:page:list']">
        <div class="container">
          <el-form size="small" inline label-width="100px">
            <el-form-item label="时间选择：" class="width100" style="display: block">
              <el-radio-group
                v-model="tableFrom.dateLimit"
                type="button"
                class="mr20"
                size="small"
                @change="selectChange(tableFrom.dateLimit)"
              >
                <el-radio-button v-for="(item, i) in fromList.fromTxt" :key="i" :label="item.val">{{
                  item.text
                }}</el-radio-button>
              </el-radio-group>
              <el-date-picker
                v-model="timeVal"
                value-format="yyyy-MM-dd"
                format="yyyy-MM-dd"
                size="small"
                type="daterange"
                placement="bottom-end"
                placeholder="自定义时间"
                style="width: 250px"
                @change="onchangeTime"
              />
            </el-form-item>
            <el-form-item label="审核状态：">
              <el-radio-group v-model="tableFrom.auditStatus" type="button" @change="getList(1)">
                <el-radio-button label="">全部 </el-radio-button>
                <el-radio-button label="0">待审核</el-radio-button>
                <el-radio-button label="1">审核通过</el-radio-button>
                <el-radio-button label="2">审核失败</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="到账状态：">
              <el-select
                v-model="tableFrom.accountStatus"
                placeholder="请选择"
                class="filter-item selWidth mr20"
                clearable
                @change="getList(1)"
              >
                <el-option
                  v-for="item in arrivalStatusList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="结算类型：">
              <el-select
                v-model="tableFrom.closingType"
                placeholder="请选择"
                class="filter-item selWidth mr20"
                clearable
                @change="getList(1)"
              >
                <el-option v-for="item in closingTypeList" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <br />
            <el-form-item label="关键字：" class="width100">
              <el-input
                v-model.trim="keywords"
                @keyup.enter.native="getList(1)"
                placeholder="微信号、支付宝账号、银行卡号、持卡人姓名"
                class="selWidth"
                size="small"
              >
                <el-button
                  slot="append"
                  icon="el-icon-search"
                  size="small"
                  class="el-button-solt"
                  @click="getList(1)"
                  v-hasPermi="['platform:finance:user:closing:page:list']"
                />
              </el-input>
              <!--<el-button size="small" type="primary" icon="el-icon-top" @click="exportRecord">列表导出</el-button>-->
              <!--<el-button size="small" type="primary" @click="getExportFileList">导出记录</el-button>-->
            </el-form-item>
            <br />
          </el-form>
        </div>
      </div>
      <el-table v-loading="listLoading" tooltip-effect="dark" :data="tableData.data" style="width: 100%" class="table">
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="closingNo" label="结算单号" min-width="150" />
        <el-table-column prop="nickName" label="用户昵称" min-width="120" />
        <el-table-column prop="closingPrice" label="金额" min-width="120" />
        <el-table-column label="结算类型" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.closingType | closingTypeFilter }}</span>
          </template>
        </el-table-column>

        <el-table-column label="审核状态" min-width="120">
          <template slot-scope="scope">
            <span>{{
              scope.row.auditStatus == 0 ? '待审核' : scope.row.auditStatus == 1 ? '审核通过' : '审核失败'
            }}</span>
          </template>
        </el-table-column>
        <el-table-column label="到账状态" min-width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.accountStatus == 1 ? '已转账' : '未转账' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ scope.row.mark | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="closingDetail(scope.row, 1)"
              v-hasPermi="['platform:finance:user:closing:page:list']"
              >详情</el-button
            >
            <el-button
              v-if="scope.row.auditStatus === 0 && checkPermi(['platform:finance:user:closing:audit'])"
              type="text"
              size="small"
              @click="closingDetail(scope.row, 2)"
              >审核</el-button
            >
            <el-button
              v-if="
                scope.row.auditStatus === 1 &&
                scope.row.accountStatus === 0 &&
                checkPermi(['platform:finance:user:closing:proof'])
              "
              type="text"
              size="small"
              @click="closingDetail(scope.row, 3)"
              >结算凭证</el-button
            >
            <el-button
              type="text"
              size="small"
              v-hasPermi="['platform:finance:user:closing:remark']"
              @click="onRemark(scope.row)"
              >备注</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>
    <!--转账凭证 审核 详情-->
    <el-drawer direction="rtl" :visible.sync="dialogVisible" size="700px" @close="close('ruleForm')">
      <div slot="title">结算详情</div>
      <div class="box-container" v-loading="loading">
        <div class="acea-row">
          <div class="list sp100"><label class="name">用户昵称：</label>{{ closingData.nickName }}</div>
          <div class="list sp100"><label class="name">结算金额：</label>{{ closingData.closingPrice }}</div>
          <div class="list sp100"><label class="name">用户余额：</label>{{ closingData.balance }}</div>
          <div class="list sp100">
            <label class="name">结算类型：</label>{{ closingData.closingType | closingTypeFilter }}
          </div>
          <template v-if="closingData.closingType === 'bank'">
            <div class="list sp100"><label class="name">开户户名 ：</label>{{ closingData.cardholder }}</div>
            <div class="list sp100"><label class="name">银行名称：</label>{{ closingData.bankName }}</div>
            <div class="list sp100"><label class="name">银行账号：</label>{{ closingData.bankCardNo }}</div>
          </template>
          <div v-if="closingData.closingType === 'wechat'" class="list sp100">
            <label class="name">微信号：</label>{{ closingData.wechatNo }}
          </div>
          <div v-if="closingData.closingType === 'alipay'" class="list sp100">
            <label class="name">支付宝账号：</label>{{ closingData.alipayAccount }}
          </div>
          <div v-if="closingData.closingType === 'wechat' || closingData.closingType === 'alipay'" class="list sp100">
            <label class="name">真实姓名：</label>{{ closingData.realName }}
          </div>
          <div v-if="closingData.closingType !== 'bank'" class="list sp100 acea-row">
            <label class="name">收款二维码：</label>
            <div class="demo-image__preview">
              <el-image :src="closingData.paymentCode" :preview-src-list="[closingData.paymentCode]" />
            </div>
          </div>
          <div class="list sp100">
            <label class="name">审核状态：</label
            >{{ closingData.auditStatus == 0 ? '待审核' : closingData.auditStatus == 1 ? '审核通过' : '审核失败' }}
          </div>
          <div v-if="closingData.auditStatus == 1" class="list sp100">
            <label class="name">审核时间：</label>{{ closingData.auditTime | filterEmpty }}
          </div>
          <div v-if="closingData.closingProof" class="list sp100">
            <label class="name">结算凭证：</label>
            <div class="acea-row">
              <div v-for="(item, index) in JSON.parse(closingData.closingProof)" :key="index" class="pictrue">
                <img @click="getPicture(item)" :src="item" />
              </div>
            </div>
          </div>
          <div v-if="closingData.auditStatus == 1 && closingData.closingTime" class="list sp100">
            <label class="name">结算时间：</label>{{ closingData.closingTime }}
          </div>
          <div v-if="closingData.auditStatus == 2 && closingData.refusalReason" class="list sp100">
            <label class="name">审核未通过原因：</label>{{ closingData.refusalReason }}
          </div>
          <div class="list sp100"><label class="name">备注：</label>{{ closingData.mark | filterEmpty }}</div>
        </div>
        <div v-if="isShow !== 1" class="from-foot-btn fix btn-shadow">
          <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px" class="demo-ruleForm">
            <template v-if="isShow === 2 && closingData.auditStatus == 0">
              <el-form-item label="审核状态" prop="auditStatus">
                <el-radio-group v-model="ruleForm.auditStatus">
                  <el-radio :label="1">通过</el-radio>
                  <el-radio :label="2">拒绝</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item v-if="ruleForm.auditStatus === 2" label="原因" prop="refusalReason">
                <el-input v-model.trim="ruleForm.refusalReason" type="textarea" placeholder="请输入原因" />
              </el-form-item>
            </template>
            <el-form-item
              label="转账凭证："
              prop="closingProof"
              v-if="isShow === 3 && closingData.auditStatus === 1 && closingData.accountStatus === 0"
            >
              <div class="acea-row">
                <div class="acea-row" v-if="ruleForm.closingProof.length > 0">
                  <div v-for="(item, index) in ruleForm.closingProof" :key="index" class="pictrue">
                    <img :src="item" @click="getPicture(item)" />
                    <i class="el-icon-error btndel" @click="handleRemove(index)" />
                  </div>
                </div>
                <el-upload
                  v-show="ruleForm.closingProof.length < 6"
                  class="upload-demo"
                  action
                  :http-request="handleUploadForm"
                  :headers="myHeaders"
                  :show-file-list="false"
                  multiple
                >
                  <div class="upLoadPicBox">
                    <div class="upLoad">
                      <i class="el-icon-upload2" />
                    </div>
                  </div>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button @click="close('ruleForm')">取 消</el-button>
              <el-button type="primary" @click="onSubmit('ruleForm')">{{
                loadingBtn ? '提交中 ...' : '确 定'
              }}</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-drawer>
    <!--查看图片-->
    <el-dialog v-if="pictureVisible" :visible.sync="pictureVisible" width="700px">
      <img :src="pictureUrl" class="pictures" />
    </el-dialog>
    <!--导出订单列表-->
    <!--<file-list ref="exportList" />-->
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
import {
  userClosingListApi,
  userClosingProofApi,
  transferRecordsExportApi,
  userClosingAuditApi,
  userClosingRemarkApi,
} from '@/api/finance';
import merchantName from '@/components/merchantName';
import { getToken } from '@/utils/auth';
import { fileImageApi } from '@/api/systemSetting';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'transferAccount',
  data() {
    return {
      myHeaders: { 'X-Token': getToken() },
      isShow: 0,
      loadingBtn: false,
      rules: {
        auditStatus: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
        refusalReason: [{ required: true, message: '请填写拒绝原因', trigger: 'blur' }],
        closingProof: [{ required: true, message: '请上传结算凭证', type: 'array', trigger: 'change' }],
      },
      tableData: {
        data: [],
        total: 0,
      },
      arrivalStatusList: [
        { label: '已到账', value: 1 },
        { label: '未到账', value: 0 },
      ],
      closingTypeList: [
        { label: '银行卡', value: 'bank' },
        { label: '微信', value: 'wechat' },
        { label: '支付宝', value: 'alipay' },
      ],
      listLoading: true,
      tableFrom: {
        dateLimit: '',
        page: 1,
        limit: 20,
        keywords: '',
        auditStatus: '',
        accountStatus: '',
        closingType: '',
      },
      keywords: '',
      timeVal: [],
      fromList: this.$constants.fromList,
      loading: false,
      dialogVisible: false,
      pictureVisible: false,
      closingData: {},
      baseInfoform: {
        amount: 0,
        mark: '',
        transferType: '',
      },
      merchantList: [],
      search: {
        limit: 10,
        page: 1,
        keywords: '',
      },
      ruleForm: {
        refusalReason: '',
        auditStatus: 1,
        id: '',
        closingProof: [],
      },
      localImg: '',
    };
  },
  components: { merchantName },
  mounted() {
    if (checkPermi(['platform:finance:user:closing:page:list'])) this.getList(1);
  },
  methods: {
    checkPermi,
    onRemark(row) {
      this.$modalPrompt('textarea', '备注', row.mark).then((V) => {
        userClosingRemarkApi({
          closingNo: row.closingNo,
          remark: V,
        }).then((res) => {
          this.$message({
            type: 'success',
            message: '提交成功',
          });
          this.getList('');
        });
      });
    },
    onSubmit(formName) {
      if (this.isShow === 2) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.loadingBtn = true;
            const data = {
              closingNo: this.closingData.closingNo,
              refusalReason: this.ruleForm.refusalReason,
              auditStatus: this.ruleForm.auditStatus,
            };
            userClosingAuditApi(data)
              .then((res) => {
                this.$message.success('操作成功');
                this.dialogVisible = false;
                this.getList(1);
                this.close('ruleForm');
                this.loadingBtn = false;
              })
              .catch((res) => {
                this.loadingBtn = false;
              });
          } else {
            return false;
          }
        });
      } else {
        this.loadingBtn = true;
        const data = {
          closingNo: this.closingData.closingNo,
          closingProof: JSON.stringify(this.ruleForm.closingProof),
        };
        this.$refs[formName].validate((valid) => {
          if (valid) {
            userClosingProofApi(data)
              .then((res) => {
                this.$message.success('操作成功');
                this.dialogVisible = false;
                this.getList(1);
                this.close('ruleForm');
                this.loadingBtn = false;
              })
              .catch((res) => {
                this.loadingBtn = false;
              });
          }
        });
      }
    },
    handleUploadForm(param) {
      const formData = new FormData();
      const data = {
        model: 'finance',
        pid: 0,
      };
      formData.append('multipart', param.file);
      let loading = this.$loading({
        lock: true,
        text: '上传中，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
      });
      fileImageApi(formData, data)
        .then((res) => {
          loading.close();
          this.$message.success('上传成功');
          this.ruleForm.closingProof.push(res.url);
        })
        .catch((res) => {
          loading.close();
        });
    },
    handleRemove(i) {
      this.ruleForm.closingProof.splice(i, 1);
    },
    close(refName) {
      this.dialogVisible = false;
      if (this.$refs[refName]) {
        this.$refs[refName].resetFields();
      }
      this.ruleForm.closingProof = [];
      this.loadingBtn = false;
    },
    // 商户结算记录详情
    closingDetail(row, num) {
      this.isShow = num;
      this.closingData = row;
      this.dialogVisible = true;
    },
    // 查看图片
    getPicture(url) {
      this.pictureVisible = true;
      this.pictureUrl = url;
    },
    // 选择时间
    selectChange(tab) {
      this.tableFrom.dateLimit = tab;
      this.timeVal = [];
      this.getList(1);
    },
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.tableFrom.dateLimit = e ? this.timeVal.join(',') : '';
      this.getList(1);
    },
    // 导出
    exportRecord() {
      transferRecordsExportApi(this.tableFrom)
        .then((res) => {
          const h = this.$createElement;
          this.$msgbox({
            title: '提示',
            message: h('p', null, [
              h('span', null, '文件正在生成中，请稍后点击"'),
              h('span', { style: 'color: teal' }, '导出记录'),
              h('span', null, '"查看~ '),
            ]),
            confirmButtonText: '我知道了',
          }).then((action) => {});
        })
        .catch((res) => {
          this.$message.error(res.message);
        });
    },
    // 导出列表
    getExportFileList() {
      this.$refs.exportList.exportFileList();
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      this.tableFrom.keywords = encodeURIComponent(this.keywords);
      userClosingListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch((res) => {
          this.listLoading = false;
        });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList('');
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList('');
    },

    handleClose() {
      this.dialogLogistics = false;
    },
  },
};
</script>

<style lang="scss" scoped>
.demo-image__preview {
  width: 60px;
  height: 60px;
  .el-image {
    width: 100%;
    height: 100%;
  }
}
.title {
  font-size: 20px;
}
.wid100 {
  width: 100px;
}
.ml100 {
  margin-left: 100px;
}
.transferMinAmount {
  ::v-deep.el-form-item__content {
    line-height: normal;
  }
}
::v-deep .el-dialog__title {
  font-weight: bold;
}
.selWidth {
  width: 400px;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
  font-size: 12px;
}
.el-icon-arrow-down {
  font-size: 12px;
}
.font-red {
  color: #ff4949;
}
.tabBox_tit {
  width: 60%;
  font-size: 12px !important;
  margin: 0 2px 0 10px;
  letter-spacing: 1px;
  padding: 5px 0;
  box-sizing: border-box;
}
.pictrue {
  width: 60px;
  height: 60px;
  border: 1px dotted rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  position: relative;
  cursor: pointer;
  img {
    width: 100%;
    height: 100%;
  }
}
.box-container {
  overflow: hidden;
  padding: 0 30px;
  font-size: 14px;
}

.box-container .list.image {
  margin: 20px 0;
  position: relative;
}
.box-container .list.image img {
  position: absolute;
  top: -20px;
}
.box-container .list {
  float: left;
  line-height: 40px;
}
.box-container .sp100 {
  width: 100%;
}

.acea-row {
  margin-bottom: 25px;
}
.pictures {
  width: 100%;
  height: 100%;
}
</style>
