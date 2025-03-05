<template>
  <el-dialog :visible.sync="modals" title="发货" class="order_box" :before-close="handleClose" width="1000px">
    <el-form ref="formItem" :model="formItem" label-width="110px" @submit.native.prevent :rules="rules">
      <div>
        <el-form-item label="发货类型：" prop="expressId">
          <el-radio-group v-model="formItem.expressRecordType" @change="changeRadio(formItem.expressRecordType)">
            <el-radio label="1">普通快递</el-radio>
            <!--<el-radio label="2" v-if="checkPermi(['admin:order:sheet:info'])">电子面单打印</el-radio>-->
          </el-radio-group>
        </el-form-item>
        <el-form-item label="快递公司：" prop="expressCode">
          <el-select
            v-model="formItem.expressCode"
            filterable
            style="width: 80%"
            @change="onChangeExport(formItem.expressCode)"
          >
            <el-option v-for="(item, i) in express" :value="item.code" :key="i" :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="formItem.expressRecordType === '1'" label="快递单号：" prop="expressNumber">
          <el-input v-model.trim="formItem.expressNumber" placeholder="请输入快递单号" style="width: 80%"></el-input>
        </el-form-item>
        <template v-if="formItem.expressRecordType === '2'">
          <el-form-item label="电子面单：" class="express_temp_id" prop="expressTempId">
            <div class="acea-row">
              <el-select
                v-model.trim="formItem.expressTempId"
                placeholder="请选择电子面单"
                :class="[formItem.expressTempId ? 'width9' : 'width8']"
                @change="onChangeImg"
              >
                <el-option
                  v-for="(item, i) in exportTempList"
                  :value="item.temp_id"
                  :key="i"
                  :label="item.title"
                ></el-option>
              </el-select>
              <div v-if="formItem.expressTempId" style="position: relative">
                <!--<span class="tempImg" @click="">预览</span>-->
                <div class="tempImgList ml10">
                  <div class="demo-image__preview">
                    <el-image style="width: 36px; height: 36px" :src="tempImg" :preview-src-list="[tempImg]" />
                  </div>
                </div>
              </div>
              <!--<Button v-if="formItem.expressTempId" type="text" @click="preview">预览</Button>-->
            </div>
          </el-form-item>
          <el-form-item label="寄件人姓名：" prop="toName">
            <el-input v-model.trim="formItem.toName" placeholder="请输入寄件人姓名" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="寄件人电话：" prop="toTel">
            <el-input v-model.trim="formItem.toTel" placeholder="请输入寄件人电话" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="寄件人地址：" prop="toAddr">
            <el-input v-model.trim="formItem.toAddr" placeholder="请输入寄件人地址" style="width: 80%"></el-input>
          </el-form-item>
        </template>
      </div>
      <el-form-item label="分单发货：" prop="isSplit">
        <el-switch
          v-model="formItem.isSplit"
          :active-value="true"
          :inactive-value="false"
          active-text="开启"
          inactive-text="关闭"
        />
        <p v-show="formItem.isSplit" class="area-desc">
          可选择表格中的商品单独发货，发货后会生成新的订单且不能撤回，请谨慎操作！
        </p>
      </el-form-item>
      <template v-if="formItem.isSplit">
        <el-table
          style="width: 92%; margin-left: 35px"
          ref="multipleSelection"
          :data="productList"
          tooltip-effect="dark"
          size="mini"
          :row-key="
            (row) => {
              return row.id;
            }
          "
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            align="center"
            type="selection"
            :selectable="selectable"
            :reserve-selection="true"
            min-width="50"
          />
          <el-table-column align="center" label="商品信息" width="200">
            <template slot-scope="scope">
              <div class="acea-row" style="align-items: center">
                <div class="demo-image__preview">
                  <el-image :src="scope.row.image" :preview-src-list="[scope.row.image]" />
                </div>
                <span class="priceBox" style="width: 150px">{{ scope.row.productName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" label="规格" min-width="120">
            <template slot-scope="scope">
              <span class="priceBox">{{ scope.row.sku }}</span>
            </template>
          </el-table-column>
          <!--<el-table-column align="center" label="商品售价" min-width="80">-->
          <!--<template slot-scope="scope">-->
          <!--<span class="priceBox">{{ scope.row.price }}</span>-->
          <!--</template>-->
          <!--</el-table-column>-->
          <el-table-column align="center" label="总数（件）" min-width="80">
            <template slot-scope="scope">
              <span class="priceBox">{{ scope.row.payNum }}</span>
              <div class="priceBox fontColor3">已发{{ scope.row.deliveryNum }}</div>
              <div class="priceBox fontColor3">已退款{{ scope.row.refundNum }}</div>
            </template>
          </el-table-column>
          <el-table-column label="发货数量（件）" align="center" min-width="120">
            <template slot-scope="scope">
              <el-input-number
                :disabled="scope.row.deliveryNum === scope.row.payNum"
                v-model.trim="scope.row['num']"
                :min="1"
                :max="Number(scope.row.payNum) - Number(scope.row.deliveryNum) - Number(scope.row.refundNum)"
                class="priceBox"
                :step="1"
                @blur="limitCount(scope.row, scope.$index)"
              />
            </template>
          </el-table-column>
        </el-table>
      </template>
      <div v-if="formItem.type === '2'">
        <el-form-item label="送货人姓名：" prop="deliveryName">
          <el-input v-model.trim="formItem.deliveryName" placeholder="请输入送货人姓名" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="送货人电话：" prop="deliveryTel">
          <el-input v-model.trim="formItem.deliveryTel" placeholder="请输入送货人电话" style="width: 80%"></el-input>
        </el-form-item>
      </div>
    </el-form>
    <div slot="footer">
      <el-button type="primary" @click="putSend('formItem')">提交</el-button>
      <el-button @click="cancel('formItem')">取消</el-button>
    </div>
  </el-dialog>
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

import { orderSendApi, sheetInfoApi, orderProDetailApi } from '@/api/order';
import { expressAllApi, exportTempApi } from '@/api/logistics';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';
const validatePhone = (rule, value, callback) => {
  if (!value) {
    return callback(new Error('请填写手机号'));
  } else if (!/^1[3456789]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确!'));
  } else {
    callback();
  }
};
export default {
  name: 'orderSend',
  props: {
    orderNo: String,
  },
  watch: {},
  data() {
    return {
      productList: [],
      formItem: {
        deliveryType: 'express',
        ///type: '1',
        isSplit: false,
        expressRecordType: '1',
        expressId: '',
        expressCode: '',
        //  deliveryName: '',
        // deliveryTel: '',
        // expressName: '',
        expressNumber: '',
        expressTempId: '',
        toAddr: '',
        toName: '',
        toTel: '',
        orderNo: '',
        detailList: [],
      },
      modals: false,
      express: [],
      exportTempList: [],
      tempImg: '',
      rules: {
        toName: [{ required: true, message: '请输寄件人姓名', trigger: 'blur' }],
        toTel: [{ required: true, validator: validatePhone, trigger: 'blur' }],
        toAddr: [{ required: true, message: '请输入寄件人地址', trigger: 'blur' }],
        expressCode: [{ required: true, message: '请选择快递公司', trigger: 'change' }],
        expressNumber: [{ required: true, message: '请输入快递单号', trigger: 'blur' }],
        expressTempId: [{ required: true, message: '请选择电子面单', trigger: 'change' }],
        deliveryName: [{ required: true, message: '请输入送货人姓名', trigger: 'blur' }],
        deliveryTel: [{ required: true, validator: validatePhone, trigger: 'blur' }],
        isSplit: [{ required: true, message: '请选择分单发货', trigger: 'change' }],
      },
      expressType: 'normal',
      detailList: [],
      multipleSelection: [],
    };
  },
  mounted() {
    //if(this.modals) this.orderProDetail()
  },
  methods: {
    checkPermi,
    selectable(row, index) {
      if (row.deliveryNum === row.payNum) {
        return false;
      } else {
        return true;
      }
    },
    // 默认信息
    sheetInfo() {
      sheetInfoApi().then(async (res) => {
        this.formItem.toAddr = res.exportToAddress || '';
        this.formItem.toName = res.exportToName || '';
        this.formItem.toTel = res.exportToTel || '';
      });
    },
    limitCount(row, i) {
      if (row.num > row.payNum) row.num = row.payNum;
    },
    // 分单发货选择商品
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 快递公司选择
    onChangeExport(val) {
      this.formItem.expressTempId = '';
      if (this.formItem.expressRecordType === '2') this.exportTemp(val);
    },
    // 电子面单模板
    exportTemp(val) {
      exportTempApi({ com: val }).then(async (res) => {
        this.exportTempList = res.data.data || [];
      });
    },
    onChangeImg(item) {
      this.exportTempList.map((i) => {
        if (i.temp_id === item) this.tempImg = i.pic;
      });
    },
    changeRadioType() {
      this.formItem.expressId = '';
      this.formItem.expressCode = '';
    },
    changeRadio(o) {
      if (o == 2) {
        this.expressType = 'elec';
      } else {
        this.expressType = 'normal';
      }
      this.formItem.expressId = '';
      this.formItem.expressCode = '';
      this.getList();
    },
    // 商品信息
    orderProDetail(orderNo) {
      orderProDetailApi(orderNo).then(async (res) => {
        this.productList = res;
        this.productList.map((item) => this.$set(item, 'num', 1));
      });
    },
    // 物流公司列表
    getList() {
      expressAllApi({ type: this.expressType }).then(async (res) => {
        this.express = res;
      });
    },
    // 提交
    putSend: Debounce(function (name) {
      let data = {};
      let attr = [];
      this.formItem.orderNo = this.orderNo;
      this.multipleSelection.map((item) => {
        attr.push({ orderDetailId: item.id, num: item.num });
      });
      this.formItem.detailList = attr;
      if (!this.formItem.isSplit) {
        data = { ...this.formItem };
        delete data.detailList;
      } else {
        if (!this.formItem.detailList.length) return this.$message.warning('请选择分单发货商品');
        let flag = false;
        this.formItem.detailList.map((item) => {
          if (!item.num) {
            flag = true;
          }
        });
        if (flag) {
          this.$message.warning('请填写发货数量');
          return;
        }
        data = this.formItem;
      }
      this.$refs[name].validate((valid) => {
        if (valid) {
          orderSendApi(data).then((async) => {
            this.$message.success('发货成功');
            this.modals = false;
            this.$refs[name].resetFields();
            this.$emit('submitFail');
          });
        } else {
          this.$message.error('请填写信息');
        }
      });
    }),
    handleClose() {
      this.cancel('formItem');
    },
    cancel(name) {
      this.modals = false;
      this.$refs[name].resetFields();
      //this.formItem.type = '1';
      this.formItem.expressRecordType = '1';
    },
  },
};
</script>

<style scoped lang="scss">
.width8 {
  width: 80%;
}

.width9 {
  width: 70%;
}

.tempImgList {
  // opacity: 1;
  width: 38px !important;
  height: 30px !important;
  // margin-top: -30px;
  cursor: pointer;
  position: absolute;
  z-index: 11;
  img {
    width: 38px !important;
    height: 30px !important;
  }
}
</style>
