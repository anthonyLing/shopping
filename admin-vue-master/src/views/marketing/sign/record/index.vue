<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        :highlight-current-row="true"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="头像" min-width="80">
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
        <el-table-column label="昵称" min-width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.nickname }}</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" prop="phone" min-width="100" :show-overflow-tooltip="true"> </el-table-column>
        <el-table-column label="连续签到天数" prop="day" min-width="100" :show-overflow-tooltip="true">
        </el-table-column>
        <el-table-column prop="experience" label="签到经验" min-width="100" align="center" />
        <el-table-column prop="integral" label="签到积分" min-width="100" align="center" />
        <el-table-column prop="awardExperience" label="连续签获经验" min-width="100" align="center" />
        <el-table-column prop="awardIntegral" label="连续签获积分" min-width="100" align="center" />
        <el-table-column label="签到日期" min-width="120" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.date }}</span>
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
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { signeRecordListApi } from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  data() {
    return {
      tableFrom: {
        page: 1,
        limit: 20,
      },
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: false,
    };
  },
  mounted() {
    if (checkPermi(['platform:sign:user:record:list'])) this.getList();
  },
  methods: {
    checkPermi,
    // 列表
    getList() {
      this.listLoading = true;
      signeRecordListApi(this.tableFrom)
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
