<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div slot="header" class="clearfix" ref="headerBox" v-hasPermi="['merchant:seckill:activity:page']">
        <div class="container">
          <el-form size="small" inline>
            <el-form-item label="活动日期：">
              <el-date-picker
                v-model="tableForm.date"
                value-format="yyyy-MM-dd"
                format="yyyy-MM-dd"
                size="small"
                type="date"
                placeholder="活动日期"
                style="width: 250px"
                @change="getList(1)"
              />
            </el-form-item>
            <el-form-item label="活动名称：">
              <el-input v-model="name" placeholder="请输入活动名称" class="selWidth" clearable>
                <el-button
                  v-hasPermi="['merchant:seckill:activity:page']"
                  slot="append"
                  icon="el-icon-search"
                  @click="getList(1)"
                />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        ref="multipleTable"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="name" label="活动名称" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column prop="productNum" label="商品数量" min-width="90" />
        <el-table-column prop="oneQuota" label="单次限购" min-width="90" />
        <el-table-column prop="allQuota" label="活动限购" min-width="90" />
        <el-table-column prop="productCategoryNames" label="商品分类" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column label="商家星级" min-width="140">
          <template slot-scope="scope">
            <el-rate disabled v-model="scope.row.merStars" style="margin-top: 8px"></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="活动状态" min-width="90">
          <template slot-scope="scope">
            <el-tag type="danger" v-if="scope.row.status == 0">未开始</el-tag>
            <el-tag v-if="scope.row.status == 1">进行中</el-tag>
            <el-tag type="info" v-if="scope.row.status == 2">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="end_time" label="活动日期" min-width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.startDate }} - {{ scope.row.endDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="end_time" label="活动时间" min-width="110">
          <template slot-scope="scope">
            <div v-for="(item, i) in scope.row.timeList" :key="i">{{ item }}<br /></div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
        <el-table-column label="操作" min-width="90" fixed="right" align="center">
          <template slot-scope="scope" v-hasPermi="['merchant:seckill:product:add']">
            <el-button
              :disabled="scope.row.status === 2 || Number(merStarLevel) < scope.row.merStars"
              type="text"
              size="small"
              v-hasPermi="['merchant:seckill:product:add']"
              @click="goOn(scope.row.id)"
              >参加活动</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableForm.limit"
          :current-page="tableForm.page"
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
import { seckillActivityListApi, seckillAtivityDelApi } from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import Cookies from 'js-cookie';
export default {
  name: 'SeckillList',
  data() {
    return {
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableForm: {
        page: 1,
        limit: 20,
        name: '',
        date: '',
      },
      name: '',
      fromList: this.$constants.fromList,
      merStarLevel: Cookies.get('merStarLevel'),
    };
  },
  mounted() {
    if (checkPermi(['merchant:seckill:activity:page'])) this.getList(1);
    if (checkPermi(['merchant:plat:product:category:cache:tree']))
      this.$store.dispatch('product/getAdminProductClassify');
  },
  methods: {
    checkPermi,
    goOn(id) {
      this.$router.push({ path: `/marketing/seckill/creatActivity/${id}` });
    },
    // 删除
    handleDelete(id) {
      this.$modalSure(`删除该秒杀活动吗？`).then(() => {
        seckillAtivityDelApi(id).then(() => {
          this.$message.success('删除成功');
          if (this.tableData.data.length === 1 && this.tableForm.page > 1)
            this.tableForm.page = this.tableForm.page - 1;
          this.getList();
        });
      });
    },
    // 列表
    getList(num) {
      this.tableForm.page = num ? num : this.tableForm.page;
      this.tableForm.name = encodeURIComponent(this.name);
      this.listLoading = true;
      seckillActivityListApi(this.tableForm)
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
      this.tableForm.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.tableForm.limit = val;
      this.getList();
    },
  },
};
</script>

<style scoped lang="scss">
.divBox {
  ::v-deep {
    .el-badge__content.is-fixed.is-dot {
      right: 1px;
      top: 5px;
    }
  }
}
</style>
