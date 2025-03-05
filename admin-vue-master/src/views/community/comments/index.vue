<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix" v-hasPermi="['platform:product:reply:list']">
        <div class="container">
          <el-form :inline="true">
            <el-form-item label="时间选择：" class="width100">
              <el-radio-group
                v-model="tableFrom.dateLimit"
                type="button"
                @change="selectChange(tableFrom.dateLimit)"
                class="mr20"
                size="small"
              >
                <el-radio-button :label="item.val" v-for="(item, i) in fromList.fromTxt" :key="i">{{
                  item.text
                }}</el-radio-button>
              </el-radio-group>
              <el-date-picker
                @change="onchangeTime"
                v-model="timeVal"
                value-format="yyyy-MM-dd"
                format="yyyy-MM-dd"
                size="small"
                type="daterange"
                placement="bottom-end"
                placeholder="自定义时间"
                style="width: 220px"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="审核状态">
              <el-select
                v-model="tableFrom.auditStatus"
                placeholder="请选择"
                class="filter-item selWidth"
                clearable
                @change="getList(1)"
              >
                <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="内容标题：" class="mr10">
              <el-input v-model.trim="title" placeholder="请输入文章标题" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" @click="seachList" size="small" />
              </el-input>
            </el-form-item>
            <el-form-item label="用户名称：">
              <el-input
                v-model.trim="tableFrom.userName"
                placeholder="请输入用户名称"
                class="selWidth"
                size="small"
                clearable
              >
                <el-button slot="append" icon="el-icon-search" @click="seachList" size="small" />
              </el-input>
            </el-form-item>
            <el-form-item label="评论内容：">
              <el-input v-model.trim="keywords" placeholder="请输入评论内容" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" @click="seachList" size="small" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <el-table :data="tableData.data" size="mini" :header-cell-style="{ fontWeight: 'bold' }">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column label="用户名/ID" min-width="150">
          <template slot-scope="{ row }">
            <span>{{ row.userNickname + ' / ' + row.uid }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="noteTitle" label="文章标题" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column prop="content" label="评论内容" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column prop="countReply" label="评论数" min-width="100" />
        <el-table-column prop="countStart" label="点赞数" min-width="100" />
        <el-table-column label="评论时间" min-width="150" prop="createTime" />
        <el-table-column label="审核状态" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.auditStatus | communityStatusFilter }}</span>
            <span v-if="scope.row.status == 2" style="display: block; font-size: 12px; color: red"
              >原因 {{ scope.row.refusal }}</span
            >
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.auditStatus === 0 && checkPermi(['platform:community:reply:audit'])"
              type="text"
              size="small"
              @click="onAudit(scope.row)"
              >审核</el-button
            >
            <el-button
              type="text"
              size="small"
              @click="handleDelete(scope.row.id, scope.$index)"
              v-hasPermi="['platform:community:reply:delete']"
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

    <!--审核 详情-->
    <el-drawer
      custom-class="demo-drawer"
      direction="rtl"
      :visible.sync="dialogVisible"
      size="1000px"
      @close="onClose()"
    >
      <div slot="title" class="demo-drawer_title">评论审核</div>
      <div class="demo-drawer__content" v-loading="loading">
        <div class="description">
          <!--<div class="title">评论信息</div>-->
          <div class="acea-row">
            <div class="description-term sp100"><label class="name">标题：</label>{{ commentsInfo.noteTitle }}</div>
            <div class="description-term sp100"><label class="name">内容：</label>{{ commentsInfo.content }}</div>
            <div class="description-term sp100">
              <label class="name">审核结果：</label>{{ commentsInfo.auditStatus | communityStatusFilter }}
            </div>
            <div v-show="commentsInfo.auditStatus === 2" class="description-term sp100">
              <label class="name">审核失败原因：</label>{{ commentsInfo.refusal }}
            </div>
            <div class="description-term sp100">
              <label class="name">用户昵称：</label>{{ commentsInfo.userNickname }}
            </div>
            <div class="description-term sp100"><label class="name">用户ID：</label>{{ commentsInfo.uid }}</div>
            <div class="description-term sp100">
              <label class="name">评论类型：</label>{{ commentsInfo.type === 1 ? '评论' : '回复' }}
            </div>
            <div class="description-term sp100"><label class="name">评论数：</label>{{ commentsInfo.countReply }}</div>
            <div class="description-term sp100"><label class="name">点赞数：</label>{{ commentsInfo.countStart }}</div>
          </div>
        </div>
      </div>
      <div class="demo-drawer__footer">
        <div class="from-foot-btn drawer_fix">
          <div class="acea-row justify-content">
            <el-button
              v-hasPermi="['platform:seckill:product:audit']"
              v-debounceClick="
                () => {
                  onAuditStatus(2);
                }
              "
              style="margin-left: 0"
              >{{ loadingBtn ? '提交中 ...' : '审核拒绝' }}</el-button
            >
            <el-button
              type="primary"
              v-hasPermi="['platform:seckill:product:audit']"
              v-debounceClick="
                () => {
                  onAuditStatus(1);
                }
              "
              >{{ loadingBtnSuccess ? '提交中 ...' : '审核通过' }}</el-button
            >
          </div>
        </div>
      </div>
    </el-drawer>
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
import { communityReplyListApi, communityReplyDelApi, communityReplyAuditApi } from '@/api/community';
import { formatDates } from '@/utils/index';
import { userListApi } from '@/api/user';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'StoreComment',
  filters: {
    formatDate(time) {
      if (time !== 0) {
        const date = new Date(time * 1000);
        return formatDates(date, 'yyyy-MM-dd hh:mm');
      }
    },
  },
  data() {
    return {
      loadingBtn: false,
      dialogVisible: false,
      statusList: [
        { label: '待审核', value: 0 },
        { label: '已通过', value: 1 },
        { label: '已拒绝', value: 2 },
      ],
      props: {
        children: 'child',
        label: 'name',
        value: 'id',
        emitPath: false,
      },
      fromList: this.$constants.fromList,
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: false,
      tableFrom: {
        page: 1,
        limit: 20,
        auditStatus: '',
        dateLimit: '',
        keywords: '',
        userName: '',
        title: '',
      },
      keywords: '',
      title: '',
      timeVal: [],
      loading: false,
      uids: [],
      options: [],
      timer: '',
      commentsInfo: {},
      //审核
      auditStatusFrom: {
        auditStatus: 1,
        id: 0,
        refusalReason: '',
      },
      id: 0,
      loadingBtnSuccess: false, //审核成功通过
    };
  },
  watch: {
    $route(to, from) {
      this.getList();
    },
  },
  mounted() {
    if (checkPermi(['platform:community:reply:page:list'])) this.getList();
  },
  methods: {
    checkPermi,
    onClose() {
      this.dialogVisible = false;
    },
    //审核拒绝
    cancelForm() {
      this.$modalPrompt('textarea', '拒绝原因').then((V) => {
        this.auditStatusFrom.refusalReason = V;
        this.onAuditSubmit();
      });
    },
    // 审核点击
    onAuditStatus(type) {
      this.auditStatusFrom.auditStatus = type;
      if (type === 1) {
        this.$modalSure('审核通过该内容吗？').then(() => {
          this.onAuditSubmit();
        });
      } else {
        this.cancelForm();
      }
    },
    //审核提交
    onAuditSubmit() {
      this.auditStatusFrom.id = this.id;
      if (this.auditStatusFrom.auditStatus === 1) {
        this.loadingBtnSuccess = true;
      } else {
        this.loadingBtn = true;
      }
      communityReplyAuditApi(this.auditStatusFrom)
        .then((res) => {
          this.$message.success('操作成功');
          this.dialogVisible = false;
          if (this.auditStatusFrom.auditStatus === 1) {
            this.loadingBtnSuccess = false;
          } else {
            this.loadingBtn = false;
          }
          this.getList();
        })
        .catch((res) => {
          if (this.auditStatusFrom.auditStatus === 1) {
            this.loadingBtnSuccess = false;
          } else {
            this.loadingBtn = false;
          }
        });
    },
    seachList() {
      this.tableFrom.page = 1;
      this.getList();
    },
    // 选择时间
    selectChange(tab) {
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
    // 删除
    handleDelete(id, idx) {
      this.$modalSure('删除该评论吗？').then(() => {
        communityReplyDelApi(id).then(() => {
          this.$message.success('删除成功');
          if (this.tableData.data.length === 1 && this.tableFrom.page > 1)
            this.tableFrom.page = this.tableFrom.page - 1;
          this.getList();
        });
      });
    },
    // 列表
    getList() {
      this.listLoading = true;
      this.tableFrom.title = encodeURIComponent(this.title);
      this.tableFrom.keywords = encodeURIComponent(this.keywords);
      communityReplyListApi(this.tableFrom)
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
    // 审核
    onAudit(row) {
      this.commentsInfo = row;
      this.id = row.id;
      this.dialogVisible = true;
    },
  },
};
</script>

<style scoped lang="scss">
.demo-drawer__content {
  padding: 0 30px;
  padding-bottom: 86px;
}
.description {
  &-term {
    display: table-cell;
    padding-bottom: 5px;
    line-height: 30px;
    width: 50%;
    font-size: 14px;
    color: #606266;
  }
  ::v-deep .el-divider--horizontal {
    margin: 12px 0 !important;
  }
}
::v-deep.el-drawer__header {
  margin-bottom: 0 !important;
}
.box-container {
  overflow: hidden;
}
.box-container .list {
  float: left;
  line-height: 40px;
}
.box-container .sp {
  width: 50%;
}
.box-container .sp3 {
  width: 33.3333%;
}
.box-container .sp100 {
  width: 100%;
}
.box-container .list .name {
  display: inline-block;
  width: 150px;
  text-align: right;
  color: #606266;
}
.box-container .list.image {
  margin-bottom: 40px;
}
.box-container .list.image img {
  position: relative;
  top: 40px;
}
::v-deep.el-form-item__content .el-rate {
  position: relative;
  top: 8px;
}
</style>
