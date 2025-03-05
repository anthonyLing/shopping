<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-tabs v-model="tabActive" @tab-click="handleClick">
          <el-tab-pane v-for="(item, index) in headeNum" :key="index" :name="item.type" :label="item.title" />
        </el-tabs>
        <div class="container">
          <el-form inline size="small" label-width="110px">
            <el-form-item label="审核状态：" v-if="tabActive === '10'">
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
            <el-form-item label="分类名称：" style="display: inline-block">
              <el-select
                v-model="tableFrom.categoryId"
                clearable
                filterable
                placeholder="请选择"
                class="selWidth"
                @change="getList(1)"
              >
                <el-option
                  v-for="item in cateSelect"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                  :disabled="item.isShow === 0"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="图文类型：">
              <el-select
                v-model="tableFrom.type"
                placeholder="请选择"
                class="filter-item selWidth"
                clearable
                @change="getList(1)"
              >
                <el-option label="图文" value="1" />
                <el-option label="短视频" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="话题名称：" style="display: inline-block">
              <el-select
                @change="getList(1)"
                class="selWidth"
                clearable
                filterable
                v-model="tableFrom.topicId"
                v-selectLoadMore="selectLoadMore"
                :loading="loading"
                remote
                :multiple="multiple"
                :remote-method="remoteMethod"
                placeholder="请选择话题"
              >
                <el-option v-for="user in topicSelect" :key="user.id" :label="user.name" :value="user.id"> </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="作者：">
              <el-input
                v-model="tableFrom.authorName"
                @keyup.enter.native="getList(1)"
                placeholder="请输入文章作者"
                class="selWidth"
              >
                <el-button slot="append" icon="el-icon-search" class="el-button-solt" @click="getList(1)" />
              </el-input>
            </el-form-item>
            <el-form-item label="标题：">
              <el-input v-model="title" @keyup.enter.native="getList(1)" placeholder="请输入文章标题" class="selWidth">
                <el-button slot="append" icon="el-icon-search" class="el-button-solt" @click="getList(1)" />
              </el-input>
            </el-form-item>
          </el-form>
          <el-button
            v-hasPermi="['platform:community:note:category:batch:update']"
            @click="categoryBatch"
            size="small"
            type="primary"
            >批量移动</el-button
          >
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        ref="table"
        :data="tableData.data"
        style="width: 100%"
        size="small"
        highlight-current-row
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column label="ID" prop="id" width="60" />
        <el-table-column label="标题" min-width="160" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div>{{ scope.row.title | filterEmpty }}</div>
          </template>
        </el-table-column>
        <el-table-column label="作者" prop="authorName" min-width="140" />
        <el-table-column label="内容类型" min-width="70">
          <template slot-scope="scope">
            <div>{{ scope.row.type === 1 ? '图文' : '视频' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="封面" min-width="50">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image :src="scope.row.cover" class="mr5" :preview-src-list="[scope.row.cover]" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="推荐级别" min-width="130">
          <template slot-scope="scope">
            <el-rate disabled v-model="scope.row.star"> </el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="likeNum" label="点赞数" min-width="100" />
        <el-table-column prop="replyNum" label="评论数" min-width="100" />
        <el-table-column prop="categoryName" label="分类" min-width="100" />
        <el-table-column label="话题" min-width="130" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div v-if="!scope.row.topicList">无</div>
            <div v-for="(item, i) in scope.row.topicList" :key="i">{{ item }}<br /></div>
          </template>
        </el-table-column>
        <el-table-column label="评论" min-width="90">
          <template slot-scope="scope">
            <div>{{ scope.row.replyStatus | communityReplyStatusFilter }}</div>
          </template>
        </el-table-column>
        <el-table-column v-if="tabActive === '10'" label="审核状态" min-width="100">
          <template slot-scope="scope">
            <el-tag type="info" v-if="scope.row.auditStatus === 0">待审核</el-tag>
            <el-tag v-if="scope.row.auditStatus === 1">审核成功</el-tag>
            <el-tag type="danger" v-if="scope.row.auditStatus === 2">审核失败</el-tag>
            <el-tag type="danger" v-if="scope.row.auditStatus === 3">平台关闭</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          v-if="tabActive === '10'"
          prop="refusal"
          label="拒绝原因"
          min-width="120"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <div v-if="Number(scope.row.auditStatus) > 1">拒绝原因{{ scope.row.refusal }}</div>
            <div v-else>无</div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" min-width="150" />
        <el-table-column label="操作" min-width="220" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-hasPermi="['platform:community:note:detail']"
              type="text"
              size="small"
              @click="onAudit(scope.row.id, false)"
              >详情</el-button
            >
            <el-button
              v-if="
                scope.row.auditStatus == 0 &&
                checkPermi(['platform:community:note:detail', 'platform:community:note:audit'])
              "
              type="text"
              size="small"
              @click="onAudit(scope.row.id, true)"
              >审核</el-button
            >
            <el-button
              v-hasPermi="['platform:community:note:repley:force:off:switch']"
              type="text"
              size="small"
              @click="onReplyOff(scope.row)"
              >{{ scope.row.replyStatus !== 3 ? '强制关闭评论' : '取消强制关闭评论' }}</el-button
            >
            <el-button
              v-if="scope.row.auditStatus == 1 && checkPermi(['platform:community:note:star:update'])"
              type="text"
              size="small"
              @click="onEdit(scope.row)"
              >编辑星级</el-button
            >
            <el-button
              v-if="scope.row.auditStatus == 1 && checkPermi(['platform:community:note:forced:down'])"
              type="text"
              size="small"
              @click="onOff(scope.row.id)"
              >强制下架</el-button
            >
            <el-button
              v-hasPermi="['platform:community:note:delete']"
              type="text"
              size="small"
              @click="handleDelete(scope.row.id, scope.$index)"
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
    <!--审核-->
    <el-drawer
      :visible.sync="dialogVisible"
      :direction="direction"
      :append-to-body="true"
      custom-class="demo-drawer"
      size="1000px"
      ref="drawer"
      :wrapperClosable="isExamine ? false : true"
      :modal-append-to-body="false"
      class="infoBox"
      @close="onClose"
    >
      <div slot="title" class="demo-drawer_title acea-row row-middle">
        <div class="pic">
          <span class="iconfont icon-shipinico"></span>
        </div>
        <div>
          <div>{{ isExamine ? '内容审核' : '内容详情' }}</div>
          <div class="info">审核状态：{{ formValidate.auditStatus | communityAuditStatusFilter }}</div>
        </div>
      </div>
      <div v-if="dialogVisible">
        <div v-loading="loading">
          <div class="demo-drawer__content">
            <div class="divBox" style="padding: 0 20px 60px 20px">
              <el-tabs v-model="currentTab">
                <el-tab-pane label="基础信息" name="0"></el-tab-pane>
                <el-tab-pane label="关联商品" name="1"></el-tab-pane>
                <el-tab-pane label="评论列表" name="2"></el-tab-pane>
              </el-tabs>
              <el-form
                ref="formValidate"
                v-loading="loadingAudit"
                class="formValidate"
                :model="formValidate"
                label-width="120px"
                @submit.native.prevent
              >
                <!-- 商品信息-->
                <div v-show="currentTab === '0'" :gutter="24">
                  <el-form-item label="封面：">
                    <div class="demo-image__preview">
                      <el-image
                        style="width: 60px; height: 60px"
                        :src="formValidate.cover"
                        :preview-src-list="[formValidate.cover]"
                      >
                      </el-image>
                    </div>
                  </el-form-item>
                  <el-form-item v-if="formValidate.type === 2 && formValidate.video" label="短视频：">
                    <div style="margin: 0 10px 10px 0">
                      <video
                        style="width: 40%; height: 180px; border-radius: 10px"
                        :src="formValidate.video"
                        controls="controls"
                      >
                        您的浏览器不支持 video 标签。
                      </video>
                    </div>
                  </el-form-item>
                  <el-form-item v-if="formValidate.type === 1 && formValidate.image" label="图片：">
                    <div
                      v-for="(item, index) in formValidate.image.split(',')"
                      :key="index"
                      style="display: inline-block; margin: 0 10px 10px 0"
                    >
                      <el-image
                        style="width: 80px; height: 80px"
                        :src="item || ''"
                        :preview-src-list="[item ? item : '']"
                      />
                    </div>
                  </el-form-item>
                  <el-form-item label="文章标题：">
                    <el-input readonly v-model="formValidate.title" placeholder="请输入内容"></el-input>
                  </el-form-item>
                  <el-form-item label="文章内容：">
                    <el-input readonly v-model="formValidate.content" placeholder="请输入内容"></el-input>
                  </el-form-item>
                  <el-form-item label="作者：">
                    <div>{{ formValidate.authorName }}</div>
                  </el-form-item>
                  <el-form-item label="作者ID：">
                    <div>{{ formValidate.authorId }}</div>
                  </el-form-item>
                  <el-form-item label="发布时间：">
                    <div>{{ formValidate.createTime }}</div>
                  </el-form-item>
                  <el-form-item
                    v-if="formValidate.auditStatus === 2 || formValidate.auditStatus === 3"
                    :label="formValidate.auditStatus === 2 ? '拒绝原因：' : '关闭原因：'"
                  >
                    <div>{{ formValidate.refusal }}</div>
                  </el-form-item>
                  <el-form-item
                    v-show="formValidate.operateTime"
                    :label="formValidate.auditStatus === 3 ? '关闭时间：' : '审核时间：'"
                  >
                    <div>{{ formValidate.operateTime }}</div>
                  </el-form-item>
                </div>
                <!-- 关联商品-->
                <div style="width: 100%" v-show="currentTab === '1'">
                  <el-table
                    ref="tableList"
                    row-key="id"
                    :data="formValidate.productList"
                    size="mini"
                    border
                    style="width: 100%"
                  >
                    <el-table-column prop="id" label="ID" min-width="45" />
                    <el-table-column min-width="180" label="商品信息">
                      <template slot-scope="scope">
                        <div class="acea-row">
                          <div class="demo-image__preview mr10">
                            <el-image
                              style="width: 36px; height: 36px"
                              :src="scope.row.productImage"
                              :preview-src-list="[scope.row.productImage]"
                            />
                          </div>
                          <div class="row_title line2">{{ scope.row.productName }}</div>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column prop="price" label="售价" width="120" />
                  </el-table>
                </div>
                <!-- 评论列表-->
                <div v-show="currentTab === '2'">
                  <el-table
                    ref="tableList"
                    row-key="id"
                    :data="replyData.data"
                    size="mini"
                    border
                    style="width: 100%"
                    :default-expand-all="isExpandAll"
                    :tree-props="{ children: 'replyList', hasChildren: 'hasChildren' }"
                    :header-cell-style="{ fontWeight: 'bold' }"
                  >
                    <el-table-column prop="用户名/Id" label="ID" min-width="100">
                      <template slot-scope="scope">
                        <span>{{ scope.row.nickname + '/' + scope.row.id }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="content" label="评论内容" width="150" :show-overflow-tooltip="true" />
                    <el-table-column prop="countReply" label="评论数" min-width="100" />
                    <el-table-column prop="countStart" label="点赞数" min-width="100" />
                    <el-table-column label="评论时间" min-width="150" prop="createTime" />
                    <el-table-column label="操作" min-width="120" fixed="right" align="center">
                      <template slot-scope="scope">
                        <el-button
                          type="text"
                          size="small"
                          @click="handleReplyDelete(scope.row.id, scope.$index)"
                          v-hasPermi="['platform:product:reply:delete']"
                          >删除</el-button
                        >
                      </template>
                    </el-table-column>
                  </el-table>
                  <div class="block">
                    <el-pagination
                      :page-sizes="[20, 40, 60, 80]"
                      :page-size="tableFromReply.limit"
                      :current-page="tableFromReply.page"
                      layout="total, sizes, prev, pager, next, jumper"
                      :total="replyData.total"
                      @size-change="handleSizeChangeReply"
                      @current-change="pageChangeReply"
                    />
                  </div>
                </div>
              </el-form>
            </div>
          </div>
        </div>
        <div class="demo-drawer__footer">
          <div v-show="isExamine" class="from-foot-btn drawer_fix">
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
                >{{ loadingBtn ? '提交中 ...' : '审核通过' }}</el-button
              >
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
    <!--编辑星级-->
    <el-dialog :visible.sync="visible" title="编辑星级" destroy-on-close :close-on-click-modal="false" width="700px">
      <el-rate v-model="star"></el-rate>
      <span slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          v-debounceClick="
            () => {
              submitForm();
            }
          "
          >确 定</el-button
        >
      </span>
    </el-dialog>
    <!--编辑分类-->
    <el-dialog
      :visible.sync="visibleCategory"
      title="编辑分类"
      destroy-on-close
      :close-on-click-modal="false"
      width="700px"
    >
      <el-select v-model="categoryId" clearable filterable placeholder="请选择" class="selWidth">
        <el-option
          v-for="item in cateSelect"
          :key="item.id"
          :label="item.name"
          :value="item.id"
          :disabled="item.isShow === 0"
        />
      </el-select>
      <span slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          v-debounceClick="
            () => {
              submitFormCategory();
            }
          "
          >确 定</el-button
        >
      </span>
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
import * as community from '@/api/community';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'communityTopic',
  directives: {
    // 计算是否滚动到最下面
    selectLoadMore: {
      bind(el, binding) {
        // 获取element-ui定义好的scroll盒子
        const SELECTWRAP_DOM = el.querySelector('.el-select-dropdown .el-select-dropdown__wrap');
        SELECTWRAP_DOM.addEventListener('scroll', function () {
          if (this.scrollHeight - this.scrollTop < this.clientHeight + 1) {
            binding.value();
          }
        });
      },
    },
  },
  data() {
    return {
      currentTab: 0,
      tabActive: '',
      star: 0,
      visible: false,
      direction: 'rtl',
      // 是否展开，默认全部折叠
      isExpandAll: false,
      multiple: false,
      moren: '',
      //colors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      isChecked: false,
      listLoading: true,
      tableData: {
        data: [],
        total: 0,
      },
      search: {
        limit: 10,
        page: 1,
        name: '',
      },
      tableFrom: {
        page: 1,
        limit: 20,
        auditStatus: 0,
        type: '',
        title: '',
        authorName: '',
        categoryId: '',
        topicId: '',
      },
      title: '',
      statusList: [
        { label: '待审核', value: 0 },
        { label: '已通过', value: 1 },
        { label: '已拒绝', value: 2 },
        { label: '平台关闭', value: 3 },
      ],
      headeNum: [
        { title: '待审核', type: '0' },
        { title: '全部', type: '10' }, //全部无值，此处为了做选中样式，赋值10，10代表全部状态
      ],
      cateSelect: [],
      topicSelect: [],
      dialogVisible: false,
      loading: false,
      loadingAudit: false,
      isExamine: false, // 是否是审核
      community_id: '',
      rules: {
        status: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
        refusal: [{ required: true, message: '请填写拒绝原因', trigger: 'blur' }],
      },
      //强制下架
      ruleForm: {
        id: 0,
        reason: '',
      },
      //评论列表分页
      tableFromReply: {
        page: 1,
        limit: 20,
        nid: 0,
      },
      //评论列表数据
      replyData: {
        data: [],
        total: 0,
      },
      id: 0,
      formValidate: {}, //详情
      //审核
      auditStatusFrom: {
        auditStatus: 0,
        id: 0,
        refusalReason: '',
      },
      loadingBtn: false,
      multipleSelectionAll: [],
      multipleSelection: [],
      idKey: 'id',
      visibleCategory: false, //分类弹窗
      categoryId: '', //分类
      noteIdList: [], //笔记列表id
    };
  },
  mounted() {
    if (checkPermi(['platform:community:note:page:list'])) this.getList(1);
    this.getCateSelect();
    this.getTopicSelect();
  },
  methods: {
    checkPermi,
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
      this.auditStatusFrom.id = this.community_id;
      community
        .communityNoteAuditApi(this.auditStatusFrom)
        .then((res) => {
          this.$message.success('操作成功');
          this.dialogVisible = false;
          this.loadingBtn = false;
          this.getList();
        })
        .catch((res) => {
          this.loadingBtn = false;
        });
    },
    //编辑星级
    submitForm() {
      community.communityStarUpdateApi({ id: this.id, star: this.star }).then((res) => {
        this.$message.success('编辑成功');
        this.visible = false;
        this.getList();
      });
    },
    handleClick(tab) {
      this.tabActive = tab.name;
      if (this.tabActive === '10') {
        this.tableFrom.auditStatus = '';
      } else {
        this.tableFrom.auditStatus = 0;
      }
      this.getList(1);
    },
    onClose() {
      this.dialogVisible = false;
    },
    // 评论列表；
    getReplyNoteList(id) {
      this.tableFromReply.nid = id;
      community.communityReplyNoteListApi(this.tableFromReply).then((res) => {
        this.replyData.total = res.total;
        this.replyData.data = res.list;
      });
    },
    pageChangeReply(page) {
      this.tableFromReply.page = page;
      this.getReplyNoteList(this.community_id);
    },
    handleSizeChangeReply(val) {
      this.tableFromReply.limit = val;
      this.getReplyNoteList(this.community_id);
    },
    //评论删除
    handleReplyDelete(id) {
      this.$modalSure('删除该评论').then(() => {
        community.communityReplyDelApi(id).then(() => {
          this.$message.success('删除成功');
          this.getReplyNoteList(this.community_id);
        });
      });
    },
    // 分类列表；
    getCateSelect() {
      community
        .communityCategoryListApi({
          limit: 100,
          page: 1,
        })
        .then((res) => {
          this.cateSelect = res.list;
        });
    },
    // 下拉加载更多
    selectLoadMore() {
      this.search.page = this.search.page + 1;
      if (this.search.page > this.totalPage) return;
      this.getTopicSelect(); // 请求接口
    },
    // 远程搜索
    remoteMethod(query) {
      this.loading = true;
      this.search.name = query;
      this.search.page = 1;
      this.topicSelect = [];
      setTimeout(() => {
        this.loading = false;
        this.getTopicSelect(); // 请求接口
      }, 200);
    },
    // 话题列表；
    getTopicSelect() {
      community.communityTopicListApi(this.search).then((res) => {
        this.totalPage = res.totalPage;
        this.total = res.total;
        this.topicSelect = this.topicSelect.concat(res.list);
      });
    },
    //批量移动
    categoryBatch() {
      if (this.noteIdList.length === 0) return this.$message.warning('请至少选择一个内容');
      this.visibleCategory = true;
    },
    //批量移动提交
    submitFormCategory() {
      if (this.categoryId === 0) return this.$message.warning('请选择要移动到的分类');
      community
        .communitycCategoryBatchApi({
          categoryId: this.categoryId,
          noteIdList: this.noteIdList,
        })
        .then((res) => {
          this.$message.success('移动成功');
          this.visibleCategory = false;
          this.getList();
        });
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
      setTimeout(() => {
        this.changePageCoreRecordData();
        let data = [];
        if (this.multipleSelectionAll.length) {
          this.multipleSelectionAll.map((item) => {
            data.push(item.id);
          });
        }
        this.noteIdList = data;
      }, 50);
    },
    // 设置选中的方法
    setSelectRow() {
      if (!this.multipleSelectionAll || this.multipleSelectionAll.length <= 0) {
        return;
      }
      // 标识当前行的唯一键的名称
      let idKey = this.idKey;
      let selectAllIds = [];
      this.multipleSelectionAll.forEach((row) => {
        selectAllIds.push(row[idKey]);
      });
      this.$refs.table.clearSelection();
      for (var i = 0; i < this.tableData.data.length; i++) {
        if (selectAllIds.indexOf(this.tableData.data[i][idKey]) >= 0) {
          // 设置选中，记住table组件需要使用ref="table"
          this.$refs.table.toggleRowSelection(this.tableData.data[i], true);
        }
      }
    },
    // 记忆选择核心方法
    changePageCoreRecordData() {
      // 标识当前行的唯一键的名称
      let idKey = this.idKey;
      let that = this;
      // 如果总记忆中还没有选择的数据，那么就直接取当前页选中的数据，不需要后面一系列计算
      if (this.multipleSelectionAll.length <= 0) {
        this.multipleSelectionAll = this.multipleSelection;
        return;
      }
      // 总选择里面的key集合
      let selectAllIds = [];
      this.multipleSelectionAll.forEach((row) => {
        selectAllIds.push(row[idKey]);
      });
      let selectIds = [];
      // 获取当前页选中的id
      this.multipleSelection.forEach((row) => {
        selectIds.push(row[idKey]);
        // 如果总选择里面不包含当前页选中的数据，那么就加入到总选择集合里
        if (selectAllIds.indexOf(row[idKey]) < 0) {
          that.multipleSelectionAll.push(row);
        }
      });
      let noSelectIds = [];
      // 得到当前页没有选中的id
      this.tableData.data.forEach((row) => {
        if (selectIds.indexOf(row[idKey]) < 0) {
          noSelectIds.push(row[idKey]);
        }
      });
      noSelectIds.forEach((id) => {
        if (selectAllIds.indexOf(id) >= 0) {
          for (let i = 0; i < that.multipleSelectionAll.length; i++) {
            if (that.multipleSelectionAll[i][idKey] == id) {
              // 如果总选择中有未被选中的，那么就删除这条
              that.multipleSelectionAll.splice(i, 1);
              break;
            }
          }
        }
      });
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      this.tableFrom.title = encodeURIComponent(this.title);
      community
        .communityNoteListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.$nextTick(function () {
            this.setSelectRow(); //调用跨页选中方法
          });
          this.listLoading = false;
        })
        .catch((res) => {
          this.listLoading = false;
        });
    },
    pageChange(page) {
      this.changePageCoreRecordData();
      this.tableFrom.page = page;
      this.getList('');
    },
    handleSizeChange(val) {
      this.changePageCoreRecordData();
      this.tableFrom.limit = val;
      this.getList('');
    },
    // 编辑
    onEdit(row) {
      this.star = row.star;
      this.id = row.id;
      this.visible = true;
    },
    // 查看评论
    onReply(id) {
      this.$router.push({
        path: 'reply',
        query: {
          community_id: id,
        },
      });
    },
    // 审核
    onAudit(id, bl) {
      this.loadingAudit = true;
      this.currentTab = 0;
      this.getReplyNoteList(id);
      this.community_id = id;
      this.dialogVisible = true;
      this.isExamine = bl;
      community.communityNoteDetailApi(id).then((res) => {
        this.formValidate = res;
        this.loadingAudit = false;
      });
    },
    // 强制下架
    onOff(id) {
      this.$modalPrompt('textarea', '强制下架原因').then((V) => {
        this.ruleForm.reason = V;
        this.submit(id);
      });
    },
    submit(id) {
      this.ruleForm.id = id;
      community.communityNoteForcedDownApi(this.ruleForm).then((res) => {
        this.$message.success('下架成功');
        this.getList();
      });
    },
    onSubmit() {
      community
        .communityAuditApi(this.community_id, this.ruleForm)
        .then((res) => {
          this.$message.success(res.message);
          this.dialogVisible = false;
          this.getList('');
        })
        .catch((res) => {
          this.$message.error(res.message);
        });
    },
    //强制关闭评论
    onReplyOff(row) {
      this.$modalSure(
        row.replyStatus !== 3
          ? '关闭评论吗？关闭之后该内容将无法评论'
          : '取消强制关闭评论吗？取消后评论将变成用户关闭状态',
      ).then(() => {
        community.communityNoteReplyOffApi(row.id).then(() => {
          this.$message.success('关闭成功');
          this.getList();
        });
      });
    },
    // 删除
    handleDelete(id) {
      this.$modalSure('删除该内容吗').then(() => {
        community.communityNoteDelApi(id).then(() => {
          this.$message.success('删除成功');
          if (this.tableData.data.length === 1 && this.tableFrom.page > 1)
            this.tableFrom.page = this.tableFrom.page - 1;
          this.getList();
        });
      });
    },
    onchangeIsShow(row) {
      community.communityNoteReplyOffApi(row.replyStatus).then(() => {
        this.getList('');
        this.$message.success('操作成功');
      });
    },
  },
};
</script>

<style scoped lang="scss">
.infoBox {
  ::v-deep.el-drawer__header,
  .demo-drawer_title {
    margin-bottom: -7px !important;
  }
}
.pic {
  width: 60px;
  height: 60px;
  line-height: 60px;
  background: #1890ff;
  border-radius: 6px 6px 6px 6px;
  opacity: 1;
  text-align: center;
  margin-right: 12px;
  .iconfont {
    color: #fff;
    font-size: 30px;
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
