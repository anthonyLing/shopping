<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div slot="header" class="clearfix" ref="headerBox">
        <div class="container" v-hasPermi="['platform:seckill:activity:page']">
          <el-form size="small" inline>
            <el-form-item label="活动日期：">
              <el-date-picker
                v-model="tableFrom.date"
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
                <el-button slot="append" icon="el-icon-search" @click="getList(1)" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <router-link :to="{ path: '/marketing/seckill/creatSeckill' }">
          <el-button size="small" type="primary" class="mr10" v-hasPermi="['platform:seckill:activity:add']"
            >添加秒杀活动</el-button
          >
        </router-link>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="small"
        ref="multipleTable"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="name" :show-overflow-tooltip="true" label="活动名称" min-width="150" />
        <el-table-column prop="productNum" label="商品数量" min-width="90" />
        <el-table-column prop="oneQuota" label="单次限购" min-width="90" />
        <el-table-column prop="allQuota" label="活动限购" min-width="90" />
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
        <el-table-column label="活动开关" fixed="right" min-width="90">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['platform:seckill:activity:switch'])"
              v-model="scope.row.isOpen"
              :active-value="1"
              :inactive-value="0"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.isOpen ? '开启' : '关闭' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="140" fixed="right" align="center">
          <template slot-scope="scope">
            <router-link :to="{ path: '/marketing/seckill/creatSeckill/' + scope.row.id + '/add' }">
              <el-button type="text" size="small" v-hasPermi="['platform:seckill:activity:update']">添加商品</el-button>
            </router-link>
            <router-link :to="{ path: '/marketing/seckill/creatSeckill/' + scope.row.id + '/edit' }">
              <el-button type="text" size="small" v-hasPermi="['platform:seckill:activity:update']">编辑</el-button>
            </router-link>
            <el-button
              type="text"
              size="small"
              @click="handleDelete(scope.row.id, scope.$index)"
              class="mr10"
              v-hasPermi="['platform:seckill:activity:delete']"
              >删除</el-button
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
  </div>
</template>

<script>
import { seckillActivityListApi, seckillAtivityDelApi, seckillAtivitySwitchApi } from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'SeckillList',
  data() {
    return {
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        name: '',
        date: '',
      },
      name: '',
      seckillTime: [],
      fromList: this.$constants.fromList,
    };
  },
  mounted() {
    if (checkPermi(['platform:seckill:time:interval:list'])) this.getList(1);
    if (!localStorage.getItem('adminProductClassify')) this.$store.dispatch('product/getAdminProductClassify');
  },
  methods: {
    checkPermi,
    onchangeIsShow(row) {
      seckillAtivitySwitchApi(row.id)
        .then(async () => {
          this.$message.success('修改成功');
          this.getList();
        })
        .catch(() => {
          row.isOpen = !row.isOpen;
        });
    },
    // 删除
    handleDelete(id) {
      this.$modalSure(`删除该秒杀活动吗？`).then(() => {
        seckillAtivityDelApi(id).then(() => {
          this.$message.success('删除成功');
          this.getList(1);
        });
      });
    },
    // 列表
    getList(num) {
      this.tableFrom.page = num ? num : this.tableFrom.page;
      this.tableFrom.name = encodeURIComponent(this.name);
      this.listLoading = true;
      seckillActivityListApi(this.tableFrom)
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
