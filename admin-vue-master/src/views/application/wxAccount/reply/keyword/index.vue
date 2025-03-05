<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form size="small" :inline="true" v-hasPermi="['platform:wechat:public:keywords:reply:list']">
            <el-form-item label="回复类型：">
              <el-select
                v-model="tableFrom.type"
                placeholder="请选择类型"
                @change="seachList"
                class="selWidth"
                clearable
              >
                <el-option label="文本消息" value="text"></el-option>
                <el-option label="图片消息" value="image"></el-option>
                <el-option label="图文消息" value="news"></el-option>
                <el-option label="音频消息" value="voice"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关键字：">
              <el-input v-model="tableFrom.keywords" placeholder="请输入关键字" class="selWidth" size="small" clearable>
                <el-button
                  slot="append"
                  icon="el-icon-search"
                  size="small"
                  @click="seachList"
                  v-hasPermi="['platform:wechat:public:keywords:reply:list']"
                />
              </el-input>
            </el-form-item>
          </el-form>
          <router-link :to="{ path: '/operation/application/publicAccount/wxReply/keyword/save' }">
            <el-button size="small" type="primary" v-hasPermi="['platform:wechat:public:keywords:reply:save']"
              >添加关键字</el-button
            >
          </router-link>
        </div>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small" highlight-current-row>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="keywords" label="关键字" min-width="150" />
        <el-table-column label="回复类型" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.type | keywordStatusFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="是否显示" min-width="100">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['admin:wechat:keywords:reply:status'])"
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="显示"
              inactive-text="隐藏"
              @change="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.status ? '显示' : '隐藏' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100">
          <template slot-scope="scope">
            <router-link :to="{ path: '/operation/application/publicAccount/wxReply/keyword/save/' + scope.row.id }">
              <el-button
                type="text"
                size="small"
                v-if="
                  scope.row.keywords !== 'subscribe' &&
                  scope.row.keywords !== 'default' &&
                  checkPermi(['platform:wechat:public:keywords:reply:update'])
                "
                >编辑</el-button
              >
            </router-link>
            <el-button
              type="text"
              size="small"
              @click="handleDelete(scope.row.id, scope.$index)"
              v-hasPermi="['platform:wechat:public:keywords:reply:delete']"
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
import { wechatReplyListApi, wechatReplyDeleteApi, wechatReplyUpdateApi, wechatReplyStatusApi } from '@/api/wxApi';
import { getToken } from '@/utils/auth';
import { checkPermi } from '@/utils/permission';
export default {
  name: 'WechatKeyword',
  data() {
    return {
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        keywords: '',
        type: '',
      },
      listLoading: false,
    };
  },
  created() {
    if (checkPermi(['platform:wechat:public:keywords:reply:list'])) this.getList(1);
  },
  methods: {
    checkPermi,
    seachList() {
      this.getList(1);
    },
    onchangeIsShow(row) {
      wechatReplyStatusApi(row.id)
        .then(() => {
          this.$message.success('修改成功');
          this.getList(1);
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
    // 列表
    getList(num) {
      this.tableFrom.page = num ? num : this.tableFrom.page;
      this.listLoading = true;
      wechatReplyListApi(this.tableFrom)
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
    // 删除
    handleDelete(id, idx) {
      this.$modalSure().then(() => {
        wechatReplyDeleteApi(id).then(() => {
          this.$message.success('删除成功');
          this.getList(1);
        });
      });
    },
  },
};
</script>

<style scoped></style>
