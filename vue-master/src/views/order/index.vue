<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div class="clearfix">
        <div class="container">
          <el-form size="small" label-width="100px">
            <el-form-item label="订单类型：" v-if="checkPermi(['merchant:order:page:list'])">
              <el-radio-group v-model="tableFrom.type" type="button" class="mr20" size="small" @change="getList(1)">
                <el-radio-button v-for="(item, i) in fromType" :key="i" :label="item.value"
                  >{{ item.text }}
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              label="订单状态："
              v-if="checkPermi(['merchant:order:status:num', 'merchant:order:page:list'])"
            >
              <el-radio-group v-model="tableFrom.status" type="button" @change="seachList">
                <el-radio-button label="all"
                  >全部 {{ '(' + orderChartType.all ? orderChartType.all : 0 + ')' }}
                </el-radio-button>
                <el-radio-button label="notShipped"
                  >未发货 {{ '(' + orderChartType.notShipped ? orderChartType.notShipped : 0 + ')' }}
                </el-radio-button>
                <el-radio-button label="spike"
                  >待收货 {{ '(' + orderChartType.spike ? orderChartType.spike : 0 + ')' }}
                </el-radio-button>
                <el-radio-button label="awaitVerification"
                  >待核销 {{ '(' + orderChartType.verification ? orderChartType.verification : 0 + ')' }}
                </el-radio-button>
                <el-radio-button label="complete"
                  >交易完成 {{ '(' + orderChartType.complete ? orderChartType.complete : 0 + ')' }}
                </el-radio-button>
                <el-radio-button label="refunded"
                  >已退款 {{ '(' + orderChartType.refunded ? orderChartType.refunded : 0 + ')' }}
                </el-radio-button>
                <el-radio-button label="deleted"
                  >已删除 {{ '(' + orderChartType.deleted ? orderChartType.deleted : 0 + ')' }}
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="时间选择：" class="width100" v-if="checkPermi(['merchant:order:page:list'])">
              <el-radio-group
                v-model="tableFrom.dateLimit"
                type="button"
                class="mr20"
                size="small"
                @change="selectChange(tableFrom.dateLimit)"
              >
                <el-radio-button v-for="(item, i) in fromList.fromTxt" :key="i" :label="item.val"
                  >{{ item.text }}
                </el-radio-button>
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
            <el-form-item label="订单号：" class="width100" v-if="checkPermi(['merchant:order:page:list'])">
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
            <el-form-item class="width100">
              <el-button size="small" type="primary" @click="onWriteOff" v-hasPermi="['merchant:order:verification']"
                >核销订单</el-button
              >
              <el-button size="small" @click="exports" v-hasPermi="['merchant:export:order:excel']">导出</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
    <div class="mt20"></div>
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
                style="display: block; margin-left: 5px"
                :class="parseInt(scope.row.refundStatus) > 0 ? 'red' : ''"
                v-text="scope.row.orderNo"
              />
            </div>
            <span v-show="parseInt(scope.row.refundStatus) > 0" style="color: #ed4014; display: block">{{
              scope.row.refundStatus | orderRefundStatusFilter
            }}</span>
            <span v-show="scope.row.isUserDel" style="color: #ed4014; display: block">用户已删除</span>
          </template>
        </el-table-column>
        <el-table-column label="用户昵称" min-width="100" v-if="checkedCities.includes('用户昵称')">
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
        <el-table-column label="备注" min-width="150">
          <template slot-scope="scope">
            <span>{{ scope.row.merRemark | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" min-width="150" v-if="checkedCities.includes('下单时间')" />
        <el-table-column label="操作" min-width="150" fixed="right" align="center" :render-header="renderHeader">
          <template slot-scope="scope">
            <el-button
              v-if="(scope.row.status === 1 || scope.row.status === 2) && parseFloat(scope.row.refundStatus) < 3"
              type="text"
              size="small"
              class="mr10"
              @click="sendOrder(scope.row)"
              v-hasPermi="['merchant:order:send']"
              >发货
            </el-button>
            <el-button
              type="text"
              size="small"
              class="mr10"
              @click.native="onOrderDetails(scope.row.orderNo)"
              v-if="checkPermi(['merchant:order:info'])"
              >订单详情
            </el-button>
            <el-dropdown trigger="click">
              <span class="el-dropdown-link"> 更多<i class="el-icon-arrow-down el-icon--right" /> </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="onOrderMark(scope.row)" v-if="checkPermi(['merchant:order:mark'])"
                  >订单备注
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="scope.row.isUserDel === 1 && checkPermi(['merchant:order:delete'])"
                  @click.native="handleDelete(scope.row, scope.$index)"
                  >删除订单
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
            >全选
          </el-checkbox>
          <el-button size="small" type="text" @click="checkSave()">保存</el-button>
        </div>
        <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange">
          <el-checkbox v-for="item in columnData" :label="item" :key="item" class="check_cell">{{ item }}</el-checkbox>
        </el-checkbox-group>
      </template>
    </div>

    <!--记录-->
    <el-dialog title="操作记录" :visible.sync="dialogVisibleJI" width="700px">
      <el-table v-loading="LogLoading" border :data="tableDataLog.data" style="width: 100%">
        <el-table-column prop="oid" align="center" label="ID" min-width="80" />
        <el-table-column prop="changeMessage" label="操作记录" align="center" min-width="280" />
        <el-table-column prop="createTime" label="操作时间" align="center" min-width="280" />
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFromLog.limit"
          :current-page="tableFromLog.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableDataLog.total"
          @size-change="handleSizeChangeLog"
          @current-change="pageChangeLog"
        />
      </div>
    </el-dialog>

    <!--详情-->
    <details-from ref="orderDetail" :orderNo="orderNo" />

    <!-- 发送货 -->
    <order-send ref="send" :orderNo="orderNo" @submitFail="seachList"></order-send>
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
  orderStatusNumApi,
  orderListApi,
  orderLogApi,
  orderMarkApi,
  orderDeleteApi,
  orderRefundApi,
  orderPrint,
  orderDetailApi,
  writeUpdateApi,
  orderExcelApi,
} from '@/api/order';
import detailsFrom from '@/components/orderDetail';
import orderSend from './orderSend';
import Cookies from 'js-cookie';
import { isWriteOff } from '@/utils';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'orderlistDetails',
  components: {
    detailsFrom,
    orderSend,
  },
  data() {
    return {
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
      listLoading: false,
      tableFrom: {
        status: 'all',
        dateLimit: '',
        orderNo: '',
        page: 1,
        limit: 20,
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
      checkedCities: ['订单号', '订单类型', '用户昵称', '实际支付', '支付方式', '订单状态', '下单时间'],
      columnData: ['订单号', '订单类型', '用户昵称', '实际支付', '支付方式', '订单状态', '下单时间'],
      isIndeterminate: true,
      orderDatalist: null,
    };
  },
  mounted() {
    if (checkPermi(['merchant:order:page:list'])) this.getList();
    if (checkPermi(['merchant:order:status:num'])) this.getOrderStatusNum();
  },
  methods: {
    checkPermi,
    // 核销订单
    onWriteOff(row) {
      this.$modalPrompt('text', '核销码').then((V) => {
        writeUpdateApi({ verifyCode: V }).then(() => {
          this.$message.success('核销成功');
          this.seachList();
        });
      });
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
    // 发送
    sendOrder(row) {
      if (row.isLogoff) {
        this.$modalSure('当前用户已注销，有责发货！').then(() => {
          this.onSend(row);
        });
      } else {
        this.onSend(row);
      }
    },
    //发货操作
    onSend(row) {
      this.orderNo = row.orderNo;
      this.$refs.send.modals = true;
      this.$refs.send.getList();
      this.$refs.send.orderProDetail(row.orderNo);
    },
    // 订单删除
    handleDelete(row, idx) {
      if (row.isDel) {
        this.$modalSure().then(() => {
          orderDeleteApi({ orderNo: row.orderNo }).then(() => {
            this.$message.success('删除成功');
            this.tableData.data.splice(idx, 1);
          });
        });
      } else {
        this.$confirm('您选择的的订单存在用户未删除的订单，无法删除用户未删除的订单！', '提示', {
          confirmButtonText: '确定',
          type: 'error',
        });
      }
    },
    // 详情
    onOrderDetails(id) {
      this.orderNo = id;
      this.$refs.orderDetail.getDetail(id);
      this.$refs.orderDetail.getOrderInvoiceList(id);
      this.$refs.orderDetail.dialogVisible = true;
    },
    getDetail(id) {
      this.loading = true;
      orderDetailApi(id)
        .then((res) => {
          this.orderDatalist = res;
          this.loading = false;
        })
        .catch(() => {
          this.orderDatalist = null;
          this.loading = false;
        });
    },
    // 订单记录
    onOrderLog(id) {
      this.dialogVisibleJI = true;
      this.LogLoading = true;
      this.tableFromLog.orderNo = id;
      orderLogApi(this.tableFromLog)
        .then((res) => {
          this.tableDataLog.data = res.list;
          this.tableDataLog.total = res.total;
          this.LogLoading = false;
        })
        .catch(() => {
          this.LogLoading = false;
        });
    },
    pageChangeLog(page) {
      this.tableFromLog.page = page;
      this.onOrderLog();
    },
    handleSizeChangeLog(val) {
      this.tableFromLog.limit = val;
      this.onOrderLog();
    },
    handleClose() {
      this.dialogVisible = false;
    },
    // 备注
    onOrderMark(row) {
      this.$modalPrompt('textarea', '订单备注', row.merRemark).then((V) => {
        orderMarkApi({ remark: V, orderNo: row.orderNo }).then(() => {
          this.$message.success('操作成功');
          this.getList();
        });
      });
    },
    handleSelectionChange(val) {
      this.selectionList = val;
      const data = [];
      this.selectionList.map((item) => {
        data.push(item.orderNo);
      });
      this.ids = data.join(',');
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
      this.$set(this, 'card_select_show', false);
      this.$modal.loading('正在保存到本地，请稍候...');
      this.$cache.local.setJSON('order_stroge', this.checkedCities);
      setTimeout(this.$modal.closeLoading(), 1000);
    },
    //打印小票
    onOrderPrint(data) {
      orderPrint(data.orderNo)
        .then((res) => {
          this.$modal.msgSuccess('打印成功');
        })
        .catch((error) => {
          this.$modal.msgError(error.message);
        });
    },
  },
};
</script>
<style lang="scss" scoped>
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
  top: 340px;
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
