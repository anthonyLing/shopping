<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-button size="small" type="primary" @click="onAdd(null)" v-hasPermi="['platform:user:tag:save']">
          添加用户标签
        </el-button>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small">
        <el-table-column label="ID" min-width="80" prop="id" />
        <el-table-column label="标签名称" min-width="180">
          <template slot-scope="{ row }">
            <span v-text="row.name"></span>
          </template>
        </el-table-column>
        <!--<el-table-column-->
        <!--prop="create_time"-->
        <!--label="创建时间"-->
        <!--min-width="150"-->
        <!--/>-->
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              class="mr10"
              type="text"
              size="small"
              @click="onAdd(scope.row)"
              v-hasPermi="['platform:user:tag:update']"
              >编辑</el-button
            >
            <el-button
              type="text"
              size="small"
              @click="handleDelete(scope.row.id, scope.$index)"
              disable
              v-hasPermi="['platform:user:tag:delete']"
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
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------

import { tagListApi, tagDeleteApi, tagSaveApi, tagInfoApi, tagUpdateApi } from '@/api/user';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'UserGroup',
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
      listLoading: true,
    };
  },
  mounted() {
    if (checkPermi(['platform:seckill:product:list'])) this.getList();
  },
  methods: {
    checkPermi,
    onAdd(row) {
      this.$modalPrompt('text', '标签名称', row ? row.name : '').then((V) => {
        row
          ? tagUpdateApi({ id: row.id, name: V }).then(() => {
              this.$message.success('编辑成功');
              this.getList();
            })
          : tagSaveApi({ name: V }).then(() => {
              this.$message.success('新增成功');
              this.getList();
            });
      });
    },
    // 列表
    getList() {
      this.listLoading = true;
      tagListApi(this.tableFrom)
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
    // 删除
    handleDelete(id, idx) {
      this.$modalSure('删除吗？所有用户已经关联的数据都会清除').then(() => {
        tagDeleteApi(id).then(() => {
          this.$message.success('删除成功');
          this.tableData.data.splice(idx, 1);
        });
      });
    },
  },
};
</script>

<style scoped lang="stylus"></style>
