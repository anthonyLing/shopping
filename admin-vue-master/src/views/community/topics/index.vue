<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container mt-1">
          <el-form :inline="true">
            <el-form-item label="话题名称：" class="mr10">
              <el-input v-model.trim="name" placeholder="请输入话题名称" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" @click="getList(1)" size="small" />
              </el-input>
            </el-form-item>
            <el-form-item label="推荐状态：" class="mr10">
              <el-select
                v-model="tableFrom.isHot"
                placeholder="请选择推荐状态"
                @change="getList(1)"
                size="small"
                class="selWidth"
                clearable
              >
                <el-option label="推荐" value="1"></el-option>
                <el-option label="不推荐" value="0"></el-option>
              </el-select>
            </el-form-item>
            <div class="acea-row">
              <el-button
                type="primary"
                class="mr20"
                size="small"
                v-hasPermi="['platform:community:topic:add']"
                @click="handlerOpenEdit(0)"
                >添加社区话题</el-button
              >
            </div>
          </el-form>
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        height="500px"
        :highlight-current-row="true"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="话题名称" prop="name" min-width="100" :show-overflow-tooltip="true"> </el-table-column>
        <el-table-column prop="countUse" label="文章数" min-width="100" align="center" />
        <el-table-column label="添加时间" min-width="120" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="是否推荐" fixed="right" min-width="90">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['platform:community:topic:recommend:switch'])"
              v-model="scope.row.isHot"
              :active-value="1"
              :inactive-value="0"
              active-text="推荐"
              inactive-text="不推荐"
              @click.native="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.isHot === 1 ? '推荐' : '不推荐' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="130" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              size="small"
              type="text"
              @click="handlerOpenEdit(1, scope.row)"
              v-hasPermi="['platform:community:topic:update']"
              >编辑</el-button
            >
            <el-button
              size="small"
              type="text"
              @click="handlerOpenDel(scope.row)"
              v-hasPermi="['platform:community:topic:delete']"
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
import * as community from '@/api/community';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  data() {
    return {
      form: {},
      rules: {},
      tableFrom: {
        page: 1,
        limit: 20,
        name: '',
        isHot: '',
      },
      name: '',
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: false,
      keyNum: 0,
      id: 0,
    };
  },
  mounted() {
    if (checkPermi(['platform:community:topic:page:list'])) this.getList();
  },
  methods: {
    checkPermi,
    onchangeIsShow(row) {
      community.communityTopicRecommendApi(row.id).then((res) => {
        this.$message.success('操作成功');
        this.getList();
      });
    },
    // 列表
    getList(num) {
      this.tableFrom.page = num ? num : this.tableFrom.page;
      this.tableFrom.name = encodeURIComponent(this.name);
      this.listLoading = true;
      community
        .communityTopicListApi(this.tableFrom)
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
      this.getList(1);
    },
    handlerOpenEdit(isCreate, editDate) {
      const _this = this;
      this.id = editDate ? editDate.id : 0;
      this.$modalParserFrom(
        isCreate === 0 ? '新建话题' : '编辑话题',
        'communityTopics',
        isCreate,
        isCreate === 0
          ? {
              id: 0,
              name: '',
              handlingFee: '',
            }
          : Object.assign({}, editDate),
        function (formValue) {
          _this.submit(formValue);
          _this.resetForm(formValue);
        },
        (this.keyNum = Math.random()),
      );
    },
    submit(formValue) {
      const data = {
        id: this.id,
        name: formValue.name,
      };
      !this.id
        ? community
            .communityTopicAddApi(data)
            .then((res) => {
              this.$message.success('操作成功');
              this.$msgbox.close();
              this.getList();
            })
            .catch(() => {
              this.loading = false;
            })
        : community
            .communityTopicUpdateApi(data)
            .then((res) => {
              this.$message.success('操作成功');
              this.$msgbox.close();
              this.getList();
            })
            .catch(() => {
              this.loading = false;
            });
    },
    handlerOpenDel(rowData) {
      this.$modalSure('删除当前话题吗？删除话题之后，与此相关的文章将取消关联话题！').then(() => {
        community.communityTopicDelApi(rowData.id).then((data) => {
          this.$message.success('删除话题成功');
          if (this.tableData.data.length === 1 && this.tableFrom.page > 1)
            this.tableFrom.page = this.tableFrom.page - 1;
          this.getList();
        });
      });
    },
  },
};
</script>
<style>
.alert_title {
  margin-right: 10px;
}
</style>
