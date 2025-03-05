<template>
  <div class="divBox relative">
    <el-card class="box-card" v-hasPermi="['platform:order:page:list']">
      <div class="clearfix">
        <div class="container">
          <el-form size="small" label-width="100px">
            <el-form-item label="订单类型：">
              <el-radio-group v-model="tableFrom.type" type="button" class="mr20" size="small" @change="getList(1)">
                <el-radio-button v-for="(item, i) in fromType" :key="i" :label="item.value"
                  >{{ item.text }}
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="订单状态：" v-if="checkPermi(['platform:order:status:num'])">
              <el-radio-group v-model="tableFrom.status" type="button" @change="seachList">
                <el-radio-button label="all"
                  >全部 {{ '(' + orderChartType.all ? orderChartType.all : 0 + ')' }}</el-radio-button
                >
                <el-radio-button label="unPaid"
                  >未支付 {{ '(' + orderChartType.unPaid ? orderChartType.unPaid : 0 + ')' }}</el-radio-button
                >
                <el-radio-button label="notShipped"
                  >未发货 {{ '(' + orderChartType.notShipped ? orderChartType.notShipped : 0 + ')' }}</el-radio-button
                >
                <el-radio-button label="spike"
                  >待收货 {{ '(' + orderChartType.spike ? orderChartType.spike : 0 + ')' }}</el-radio-button
                >
                <el-radio-button label="awaitVerification"
                  >待核销
                  {{ '(' + orderChartType.verification ? orderChartType.verification : 0 + ')' }}</el-radio-button
                >
                <el-radio-button label="complete"
                  >交易完成 {{ '(' + orderChartType.complete ? orderChartType.complete : 0 + ')' }}</el-radio-button
                >
                <el-radio-button label="refunded"
                  >已退款 {{ '(' + orderChartType.refunded ? orderChartType.refunded : 0 + ')' }}</el-radio-button
                >
                <el-radio-button label="deleted"
                  >已删除 {{ '(' + orderChartType.deleted ? orderChartType.deleted : 0 + ')' }}</el-radio-button
                >
              </el-radio-group>
            </el-form-item>
            <el-form-item label="时间选择：" class="width100">
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
                style="width: 220px"
                @change="onchangeTime"
              />
            </el-form-item>
            <el-form-item label="订单号：" class="width100">
              <el-input
                v-model.trim="tableFrom.orderNo"
                placeholder="请输入订单号"
                class="selWidth"
                size="small"
                clearable
              >
                <el-button slot="append" icon="el-icon-search" size="small" @click="seachList" />
              </el-input>
            </el-form-item>
            <el-form-item label="商户名称：" v-hasPermi="['platform:merchant:page:list']">
              <merchant-name @getMerId="getMerId"></merchant-name>
            </el-form-item>
            <el-form-item class="width100">
              <el-button size="small" @click="exports" v-hasPermi="['platform:export:order:excel']">导出</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
    <el-card class="box-card">
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        size="mini"
        class="table"
        highlight-current-row
        :header-cell-style="{ fontWeight: 'bold' }"
        :row-key="
          (row) => {
            return row.orderNo;
          }
        "
      >
        <el-table-column label="订单号" min-width="220" v-if="checkedCities.includes('订单号')">
          <template slot-scope="scope">
            <div class="acea-row">
              <font v-show="scope.row.type === 1" color="#f6ae02">[视频号]</font>
              <font v-show="scope.row.type === 2" color="#1890ff">[秒杀]</font>
              <span
                class="ml5"
                style="display: block"
                :class="parseInt(scope.row.refundStatus) > 0 ? 'red' : ''"
                v-text="scope.row.orderNo"
              />
            </div>
            <span v-show="parseInt(scope.row.refundStatus) > 0" style="color: #ed4014; display: block">{{
              scope.row.refundStatus | orderRefundStatusFilter
            }}</span>
            <span v-show="scope.row.isUserDel" style="color: #ed4014; display: block">用户已删除</span>
            <span v-show="scope.row.isMerchantDel" style="color: #ed4014; display: block">商户已删除</span>
          </template>
        </el-table-column>
        <el-table-column prop="merName" label="商户名称" min-width="150" v-if="checkedCities.includes('商户名称')" />
        <el-table-column prop="nickName" label="用户昵称" min-width="150" v-if="checkedCities.includes('收货人')">
          <template slot-scope="scope">
            <span :class="scope.row.isLogoff == true ? 'red' : ''">{{ scope.row.nickName }}</span>
            <span :class="scope.row.isLogoff == true ? 'red' : ''" v-if="scope.row.isLogoff == true">|</span>
            <span v-if="scope.row.isLogoff == true" class="red">(已注销)</span>
          </template>
        </el-table-column>
        <el-table-column prop="payPrice" label="实际支付" min-width="80" v-if="checkedCities.includes('实际支付')" />
        <el-table-column label="支付方式" min-width="80" v-if="checkedCities.includes('支付方式')">
          <template slot-scope="scope">
            <span>{{ scope.row.payType | payTypeFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" min-width="100" v-if="checkedCities.includes('订单状态')">
          <template slot-scope="scope">
            <span class="fontColor3" v-if="scope.row.refundStatus === 3">已退款</span>
            <span v-else>{{ scope.row.status | orderStatusFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" min-width="140" v-if="checkedCities.includes('下单时间')" />
        <el-table-column label="操作" min-width="150" fixed="right" align="center" :render-header="renderHeader">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              class="mr10"
              @click.native="onOrderDetails(scope.row.orderNo)"
              v-if="checkPermi(['platform:order:info'])"
              >订单详情</el-button
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
    <div class="card_abs" v-show="card_select_show">
      <template>
        <div class="cell_ht">
          <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange"
            >全选</el-checkbox
          >
          <el-button type="text" @click="checkSave()">保存</el-button>
        </div>
        <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange">
          <el-checkbox v-for="item in columnData" :label="item" :key="item" class="check_cell">{{ item }}</el-checkbox>
        </el-checkbox-group>
      </template>
    </div>

    <!--详情-->
    <details-from ref="orderDetail" :orderNo="orderNo" />
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
import { orderListDataApi, orderStatusNumApi, orderListApi, orderExcelApi } from '@/api/order';
import detailsFrom from '@/components/OrderDetail';
import merchantName from '@/components/merchantName';
import { isWriteOff } from '@/utils';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'orderlistDetails',
  components: {
    detailsFrom,
    merchantName,
  },
  data() {
    return {
      options: [
        {
          value: 0,
          label: '全部',
        },
        {
          value: 2,
          label: '商城订单',
        },
        {
          value: 1,
          label: '视频号订单',
        },
      ],
      RefuseVisible: false,
      RefuseData: {},
      orderNo: '',
      refundVisible: false,
      refundData: {},
      dialogVisibleJI: false,
      tableDataLog: {
        data: [],
        total: 0,
      },
      tableFromLog: {
        page: 1,
        limit: 10,
        orderNo: 0,
      },
      LogLoading: false,
      isCreate: 1,
      editData: null,
      dialogVisible: false,
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: true,
      tableFrom: {
        status: 'all',
        dateLimit: '',
        orderNo: '',
        page: 1,
        limit: 20,
        merId: '',
        type: '',
      },
      orderChartType: {},
      timeVal: [],
      fromList: this.$constants.fromList,
      fromType: [
        { value: '', text: '全部' },
        { value: '0', text: '普通' },
        { value: '1', text: '视频号' },
        { value: '2', text: '秒杀' },
      ],
      selectionList: [],
      ids: '',
      orderids: '',
      cardLists: [],
      isWriteOff: isWriteOff(),
      proType: 0,
      active: false,
      card_select_show: false,
      checkAll: false,
      checkedCities: [
        '订单号',
        '订单类型',
        '商户名称',
        '收货人',
        '商品名称',
        '实际支付',
        '支付方式',
        '订单状态',
        '下单时间',
      ],
      columnData: [
        '订单号',
        '订单类型',
        '商户名称',
        '收货人',
        '商品名称',
        '实际支付',
        '支付方式',
        '订单状态',
        '下单时间',
      ],
      isIndeterminate: true,
    };
  },
  mounted() {
    if (checkPermi(['platform:order:page:list'])) this.getList();
    if (checkPermi(['platform:order:status:num'])) this.getOrderStatusNum();
  },
  methods: {
    checkPermi,
    getMerId(id) {
      this.tableFrom.merId = id;
      this.seachList();
    },
    resetFormRefundhandler() {
      this.refundVisible = false;
    },
    resetFormRefusehand() {
      this.RefuseVisible = false;
    },
    resetForm(formValue) {
      this.dialogVisible = false;
    },
    seachList() {
      this.tableFrom.page = 1;
      this.getList();
      this.getOrderStatusNum();
    },
    // 详情
    onOrderDetails(id) {
      this.orderNo = id;
      this.$refs.orderDetail.getDetail(id);
      this.$refs.orderDetail.getOrderInvoiceList(id);
      this.$refs.orderDetail.dialogVisible = true;
    },
    handleClose() {
      this.dialogVisible = false;
    },
    // 选择时间
    selectChange(tab) {
      this.timeVal = [];
      this.tableFrom.page = 1;
      this.getList();
      this.getOrderStatusNum();
    },
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.tableFrom.dateLimit = e ? this.timeVal.join(',') : '';
      this.tableFrom.page = 1;
      this.getList();
      this.getOrderStatusNum();
    },
    // 列表
    getList() {
      this.listLoading = true;
      orderListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list || [];
          this.tableData.total = res.total;
          this.listLoading = false;
          this.checkedCities = this.$cache.local.has('order_stroge')
            ? this.$cache.local.getJSON('order_stroge')
            : this.checkedCities;
        })
        .catch(() => {
          this.listLoading = false;
        });
    },
    // 获取各状态数量
    getOrderStatusNum() {
      orderStatusNumApi({ dateLimit: this.tableFrom.dateLimit, type: this.tableFrom.type }).then((res) => {
        this.orderChartType = res;
      });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
    exports() {
      let data = {
        dateLimit: this.tableFrom.dateLimit,
        orderNo: this.tableFrom.orderNo,
        status: this.tableFrom.status,
        type: this.tableFrom.type,
        merId: this.tableFrom.merId,
      };
      orderExcelApi(data).then((res) => {
        window.open(res.fileName);
      });
    },
    renderHeader(h) {
      return (
        <p>
          <span style="padding-right:5px;">操作</span>
          <i class="el-icon-setting" onClick={() => this.handleAddItem()}></i>
        </p>
      );
    },
    handleAddItem() {
      if (this.card_select_show) {
        this.$set(this, 'card_select_show', false);
      } else if (!this.card_select_show) {
        this.$set(this, 'card_select_show', true);
      }
    },
    handleCheckAllChange(val) {
      this.checkedCities = val ? this.columnData : [];
      this.isIndeterminate = false;
    },
    handleCheckedCitiesChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.columnData.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.columnData.length;
    },
    checkSave() {
      this.card_select_show = false;
      this.$modal.loading('正在保存到本地，请稍候...');
      this.$cache.local.setJSON('order_stroge', this.checkedCities);
      setTimeout(this.$modal.closeLoading(), 1000);
    },
  },
};
</script>
<style lang="scss" scoped>
.ml5 {
  margin-left: 5px;
}
.red {
  color: #ed4014;
}
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}
.demo-table-expand {
  ::v-deeplabel {
    width: 83px !important;
  }
}
.refunding {
  span {
    display: block;
  }
}
.selWidth {
  width: 300px;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
  font-size: 12px;
}
.el-icon-arrow-down {
  font-size: 12px;
}
.tabBox_tit {
  font-size: 12px !important;
  /*margin: 0 2px 0 10px;*/
  letter-spacing: 1px;
  /*padding: 5px 0;*/
  box-sizing: border-box;
}
.text_overflow {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 400px;
}
.pup_card {
  width: 200px;
  border-radius: 5px;
  padding: 5px;
  box-sizing: border-box;
  font-size: 12px;
  line-height: 16px;
}
.flex-column {
  display: flex;
  flex-direction: column;
}
.mt20 {
  margin-top: 20px;
}
.relative {
  position: relative;
}
.card_abs {
  position: absolute;
  padding-bottom: 15px;
  top: 520px;
  right: 40px;
  width: 200px;
  background: #fff;
  z-index: 99999;
  box-shadow: 0px 0px 14px 0px rgba(0, 0, 0, 0.1);
}
.cell_ht {
  height: 50px;
  padding: 15px 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.check_cell {
  width: 100%;
  padding: 15px 20px 0;
}
::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #606266;
}
</style>
