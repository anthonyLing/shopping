<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container mt-1" v-hasPermi="['merchant:coupon:page:list']">
          <el-form inline size="small">
            <el-form-item label="开启状态：">
              <el-select
                v-model="tableFrom.status"
                placeholder="请选择"
                class="filter-item selWidth mr20"
                @change="seachList"
                clearable
              >
                <el-option label="未开启" :value="0" />
                <el-option label="开启" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="领取方式：">
              <el-select
                v-model="tableFrom.receiveType"
                placeholder="请选择"
                class="filter-item selWidth mr20"
                @change="seachList"
                clearable
              >
                <el-option label="手动领取" :value="1" />
                <el-option label="赠送券" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="优惠券类别：">
              <el-select
                v-model="tableFrom.category"
                placeholder="请选择"
                class="filter-item selWidth mr20"
                @change="seachList"
                clearable
              >
                <el-option label="商家券" :value="1" />
                <el-option label="商品券" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="优惠券名称：">
              <el-input v-model.trim="name" placeholder="请输入优惠券名称" style="width: 350px" clearable>
                <el-button
                  slot="append"
                  icon="el-icon-search"
                  size="small"
                  @click="seachList"
                  v-hasPermi="['merchant:coupon:page:list']"
                />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <router-link :to="{ path: '/coupon/list/save' }">
          <el-button size="small" type="primary" v-hasPermi="['merchant:coupon:save']">添加优惠劵</el-button>
        </router-link>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="name" :show-overflow-tooltip="true" label="名称" min-width="180" />
        <el-table-column label="类别" min-width="80">
          <template slot-scope="{ row }">
            <span>{{ row.category | couponCategoryFilter }}</span>
          </template>
        </el-table-column>
        <!--<el-table-column label="类型" min-width="80">-->
        <!--<template slot-scope="{ row }">-->
        <!--<span>{{ row.couponType | couponTypeFilter }}</span>-->
        <!--</template>-->
        <!--</el-table-column>-->
        <el-table-column prop="money" label="面值" min-width="100" />
        <el-table-column prop="name" label="领取方式" min-width="100">
          <template slot-scope="{ row }">
            <span>{{ row.receiveType | couponUserTypeFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="260" label="领取日期">
          <template slot-scope="{ row }">
            <div v-if="row.receiveEndTime">{{ row.receiveStartTime }} - {{ row.receiveEndTime }}</div>
            <span v-else>不限时</span>
          </template>
        </el-table-column>
        <el-table-column min-width="260" label="使用时间">
          <template slot-scope="{ row }">
            <div v-if="row.day">{{ row.day }}天</div>
            <span v-else> {{ row.useStartTime }} - {{ row.useEndTime }} </span>
          </template>
        </el-table-column>
        <el-table-column min-width="100" label="发布数量">
          <template slot-scope="{ row }">
            <span v-if="!row.isLimited">不限量</span>
            <div v-else>
              <span class="fa">发布：{{ row.total }}</span>
              <span class="sheng">剩余：{{ row.lastTotal }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="是否开启" min-width="100" fixed="right">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['merchant:coupon:update:status'])"
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @click.native="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.status ? '开启' : '关闭' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100" fixed="right">
          <template slot-scope="scope">
            <!--<el-button type="text" class="mr10" size="small" @click="receive(scope.row)" v-hasPermi="['admin:coupon:user:list']">领取记录</el-button>-->
            <router-link :to="{ path: '/coupon/list/save/' + scope.row.id }">
              <el-button
                v-if="scope.row.status"
                type="text"
                size="small"
                class="mr10"
                v-hasPermi="['merchant:coupon:info']"
                >复制</el-button
              >
            </router-link>
            <el-button
              type="text"
              class="mr10"
              size="small"
              @click="handleDelMenu(scope.row)"
              v-hasPermi="['merchant:coupon:delete']"
              >删除</el-button
            >
            <router-link
              v-show="scope.row.category === 2"
              :to="{ path: '/coupon/list/save/' + scope.row.id + '/' + 'edit' }"
            >
              <el-button type="text" class="mr10" size="small" v-hasPermi="['merchant:coupon:product:join:edit']"
                >编辑</el-button
              >
            </router-link>
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
    <!--领取记录-->
    <el-dialog title="领取记录" :visible.sync="dialogVisible" width="500px" :before-close="handleClose">
      <el-table v-loading="Loading" :data="issueData.data" style="width: 100%">
        <el-table-column prop="nickname" label="用户名" min-width="120" />
        <el-table-column label="用户头像" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="领取时间" min-width="180" />
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFromIssue.limit"
          :current-page="tableFromIssue.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="issueData.total"
          @size-change="handleSizeChangeIssue"
          @current-change="pageChangeIssue"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { marketingListApi, couponIssueStatusApi, couponUserListApi, couponDeleteApi } from '@/api/product';
import { roterPre } from '@/settings';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { mapGetters } from 'vuex';
export default {
  name: 'CouponList',
  data() {
    return {
      Loading: false,
      dialogVisible: false,
      roterPre: roterPre,
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        status: '',
        name: '',
        type: '',
        useType: '',
      },
      name: '',
      tableFromIssue: {
        page: 1,
        limit: 10,
        couponId: '',
      },
      issueData: {
        data: [],
        total: 0,
      },
    };
  },
  mounted() {
    if (!this.merProductClassify.length) this.$store.dispatch('product/getMerProductClassify');
    if (checkPermi(['merchant:coupon:page:list'])) this.getList();
  },
  computed: {
    ...mapGetters(['merProductClassify']),
  },
  methods: {
    checkPermi,
    seachList() {
      this.tableFrom.page = 1;
      this.getList();
    },
    handleClose() {
      this.dialogVisible = false;
    },
    // 领取记录
    receive(row) {
      this.dialogVisible = true;
      this.tableFromIssue.couponId = row.id;
      this.getIssueList();
    },
    // 列表
    getIssueList() {
      this.Loading = true;
      couponUserListApi(this.tableFromIssue)
        .then((res) => {
          this.issueData.data = res.list;
          this.issueData.total = res.total;
          this.Loading = false;
        })
        .catch((res) => {
          this.Loading = false;
          this.$message.error(res.message);
        });
    },
    pageChangeIssue(page) {
      this.tableFromIssue.page = page;
      this.getIssueList();
    },
    handleSizeChangeIssue(val) {
      this.tableFromIssue.limit = val;
      this.getIssueList();
    },
    // 列表
    getList() {
      this.listLoading = true;
      this.tableFrom.name = encodeURIComponent(this.name);
      marketingListApi(this.tableFrom)
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
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
    // 修改状态
    onchangeIsShow(row) {
      couponIssueStatusApi(row.id)
        .then(() => {
          this.$message.success('修改成功');
          this.getList();
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
    handleDelMenu(rowData) {
      this.$modalSure('删除当前数据?').then(() => {
        couponDeleteApi(rowData.id).then((data) => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
  },
};
</script>

<style scoped lang="scss">
.selWidth {
  width: 350px !important;
  height: 40px;
}
.fa {
  color: #0a6aa1;
  display: block;
}
.sheng {
  color: #ff0000;
  display: block;
}
</style>
