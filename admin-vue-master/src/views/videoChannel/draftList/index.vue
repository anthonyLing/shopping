<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix" v-hasPermi="['platform:pay:component:product:draft:list']">
        <div class="container">
          <el-form inline>
            <el-form-item label="商品名称：">
              <el-input v-model="search" placeholder="请输入商品名称" class="selWidth" clearable>
                <el-button slot="append" icon="el-icon-search" @click="getList(1)" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <!-- <router-link :to="{ path: '/videoChannel/creatVideoChannel' }">
          <el-button size="small" type="primary" class="mr10" v-hasPermi="['platform:pay:component:product:add']"
            >添加视频号商品</el-button
          >
        </router-link> -->
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        ref="multipleTable"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="Id" min-width="80" />
        <el-table-column label="名称" prop="title" min-width="300">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="right" :open-delay="800">
              <div class="text_overflow" slot="reference">{{ scope.row.title }}</div>
              <div class="pup_card">{{ scope.row.title }}</div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="商品图片" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="JSON.parse(scope.row.headImg)[0]"
                :preview-src-list="JSON.parse(scope.row.headImg)"
              />
            </div>
          </template>
        </el-table-column>
        <!--<el-table-column prop="componentProductId" label="视频号商品ID" min-width="150" align="center" />-->
        <!--<el-table-column-->
        <!--label="品牌ID"-->
        <!--min-width="150"-->
        <!--prop="brandId"-->
        <!--/>-->
        <el-table-column label="类目" min-width="150" prop="thirdCatName" align="center" />
        <el-table-column label="获得积分" prop="giveIntegral" min-width="100" align="center" />
        <el-table-column prop="sales" label="销量" min-width="90" align="center" />
        <el-table-column prop="stock" label="库存" min-width="90" align="center" />
        <el-table-column label="微信审核" min-width="90" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.editStatus | editStatusFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column label="平台审核" min-width="90" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.platformEditStatus | platformStatusFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="addTime" min-width="150" />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.platformEditStatus === 2 && checkPermi(['platform:pay:component:product:draft:review'])"
              size="small"
              type="text"
              @click="handleAudit(scope.row.id, true)"
              >审核</el-button
            >
            <el-button
              size="small"
              type="text"
              @click="handleAudit(scope.row.id, false)"
              v-hasPermi="['platform:pay:component:product:draft:info']"
              >详情</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="block mb20">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>
    <info-from ref="infoFrom" :is-atud="isAtud" :productId="productId" @subSuccess="subSuccess" />
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
import { draftListApi, seckillStoreUpdateApi, seckillStoreStatusApi, catListApi } from '@/api/videoChannel';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import infoFrom from '../info';
export default {
  name: 'videoList',
  data() {
    return {
      isAtud: false,
      productId: 0,
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        proId: '',
        search: '',
      },
      search: '',
    };
  },
  components: { infoFrom },
  mounted() {
    if (checkPermi(['platform:pay:component:product:draft:list'])) this.getList();
    if (!JSON.parse(sessionStorage.getItem('videoCategory'))) this.getCatList();
  },
  methods: {
    checkPermi,
    // 类目；
    getCatList() {
      catListApi().then((res) => {
        this.options = res;
        sessionStorage.setItem('videoCategory', JSON.stringify(res));
      });
    },
    subSuccess() {
      this.getList('');
    },
    // 审核
    handleAudit(id, c) {
      this.productId = id;
      this.$refs.infoFrom.dialogVisible = true;
      this.isAtud = c;
      this.$refs.infoFrom.getInfo(id);
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      this.tableFrom.search = encodeURIComponent(this.search);
      draftListApi(this.tableFrom)
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
  },
};
</script>

<style scoped>
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
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
</style>
