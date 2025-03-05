<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="clearfix">
        <div class="container">
          <el-form size="small" label-width="100px" inline>
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
                style="width: 250px"
                @change="onchangeTime"
              />
            </el-form-item>
            <el-form-item label="用户id：">
              <el-input v-model="tableFrom.uid" placeholder="用户id" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" size="small" @click="getList(1)" />
              </el-input>
            </el-form-item>
            <el-form-item label="订单号：">
              <el-input v-model="tableFrom.keywords" placeholder="订单号" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" size="small" @click="getList(1)" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
    <el-card class="box-card">
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        class="table"
        highlight-current-row
      >
        <el-table-column prop="uid" label="UID" width="60" />
        <el-table-column label="头像" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户昵称" min-width="130" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column
          sortable
          label="支付金额"
          min-width="120"
          :sort-method="
            (a, b) => {
              return a.price - b.price;
            }
          "
          prop="price"
        />
        <el-table-column
          sortable
          label="赠送金额"
          min-width="120"
          prop="givePrice"
          :sort-method="
            (a, b) => {
              return a.givePrice - b.givePrice;
            }
          "
        />
        <el-table-column label="支付方式" min-width="80">
          <template slot-scope="scope">
            <span>{{ scope.row.payType === 'weixin' ? '微信' : '支付宝' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付渠道" min-width="80">
          <template slot-scope="scope">
            <span>{{ scope.row.payChannel | rechargeTypeFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付时间" min-width="150">
          <template slot-scope="scope">
            <span class="spBlock">{{ scope.row.payTime || '无' }}</span>
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
  </div>
</template>

<script>
import { rechargeLstApi } from '@/api/finance';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'AccountsBill',
  data() {
    return {
      editData: {},
      isCreate: 1,
      cardLists: [],
      timeVal: [],
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: true,
      tableFrom: {
        uid: '',
        // paid: '',
        dateLimit: '',
        keywords: '',
        page: 1,
        limit: 20,
      },
      fromList: this.$constants.fromList,
      dialogVisible: false,
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    checkPermi,
    // 选择时间
    selectChange(tab) {
      this.tableFrom.dateLimit = tab;
      this.timeVal = [];
      this.tableFrom.page = 1;
      this.getList();
    },
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.tableFrom.dateLimit = e ? this.timeVal.join(',') : '';
      this.tableFrom.page = 1;
      this.getList();
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      rechargeLstApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
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
  },
};
</script>

<style scoped>
.selWidth {
  width: 300px;
}
</style>
