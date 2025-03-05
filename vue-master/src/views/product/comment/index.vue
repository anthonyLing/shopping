<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container" v-hasPermi="['merchant:product:reply:page:list']">
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
            <div>
              <el-form-item label="回复状态：" class="mr20">
                <el-select
                  v-model="tableFrom.isReply"
                  placeholder="请选择回复状态"
                  @change="seachList"
                  size="small"
                  class="selWidth"
                  clearable
                >
                  <el-option label="已回复" :value="1"></el-option>
                  <el-option label="未回复" :value="0"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="评价星级：" class="star">
                <el-rate v-model="tableFrom.star" @change="seachList"></el-rate>
              </el-form-item>
            </div>
            <el-form-item label="商品搜索：" class="mr20">
              <el-input
                v-model.trim="tableFrom.productSearch"
                placeholder="请输入商品名称"
                class="selWidth"
                size="small"
                clearable
              >
                <el-button
                  slot="append"
                  icon="el-icon-search"
                  @click="seachList"
                  size="small"
                  v-hasPermi="['merchant:product:reply:page:list']"
                />
              </el-input>
            </el-form-item>
            <el-form-item label="用户名称：">
              <el-input
                v-model="tableFrom.nickname"
                placeholder="请输入用户名称"
                class="selWidth"
                size="small"
                clearable
              >
                <el-button
                  slot="append"
                  icon="el-icon-search"
                  @click="seachList"
                  size="small"
                  v-hasPermi="['merchant:product:reply:page:list']"
                />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <el-button size="small" type="primary" @click="add" v-hasPermi="['merchant:product:reply:virtual']"
          >添加自评</el-button
        >
      </div>
      <el-table :data="tableData.list" size="mini" :header-cell-style="{ fontWeight: 'bold' }">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column label="商品信息" prop="productImage" min-width="120">
          <template slot-scope="scope">
            <div class="demo-image__preview acea-row row-middle" v-if="scope.row.productName">
              <el-image
                style="width: 30px; height: 30px"
                :src="scope.row.productImage"
                :preview-src-list="[scope.row.productImage]"
                class="mr10"
              />
              <div class="info">{{ scope.row.productName }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户名称" min-width="120">
          <template slot-scope="scope">
            <span :class="scope.row.isLogoff == true ? 'red' : ''">{{ scope.row.nickname }}</span>
            <span :class="scope.row.isLogoff == true ? 'red' : ''" v-if="scope.row.isLogoff == true">|</span>
            <span v-if="scope.row.isLogoff == true" class="red">(已注销)</span>
          </template>
        </el-table-column>
        <el-table-column prop="star" label="评价星级" min-width="90" />
        <el-table-column prop="comment" label="评价内容" min-width="90" />
        <el-table-column prop="merchantReplyContent" label="回复内容" min-width="200" :show-overflow-tooltip="true" />
        <el-table-column label="评价时间" min-width="150">
          <template slot-scope="scope">
            <span> {{ scope.row.createTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="reply(scope.row.id)"
              class="mr10"
              v-if="!scope.row.merchantReplyContent"
              v-hasPermi="['merchant:product:reply:comment']"
              >回复</el-button
            >
            <el-button
              type="text"
              size="small"
              @click="handleDelete(scope.row.id, scope.$index)"
              v-hasPermi="['merchant:product:reply:delete']"
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
      <el-dialog
        title="添加自评"
        :append-to-body="false"
        :visible.sync="dialogVisible"
        :modal-append-to-body="false"
        width="920px"
        z-index="4"
        @close="handleClose"
      >
        <creat-comment :key="timer" ref="formValidate" @getList="seachList"></creat-comment>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import creatComment from './creatComment.vue';
import { replyListApi, replyDeleteApi, replyCommentApi } from '@/api/product';
import { formatDates } from '@/utils/index';
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
  components: { creatComment },
  data() {
    return {
      fromList: this.$constants.fromList,
      tableData: {
        total: 0,
      },
      listLoading: false,
      tableFrom: {
        page: 1,
        limit: 20,
        isReply: '',
        dateLimit: '',
        star: null,
        nickname: '',
        productSearch: '',
      },
      timeVal: [],
      loading: false,
      dialogVisible: false,
      timer: '',
    };
  },
  watch: {
    $route(to, from) {
      this.getList();
    },
  },
  mounted() {
    if (checkPermi(['merchant:product:reply:page:list'])) this.getList();
  },
  methods: {
    checkPermi,
    seachList() {
      this.dialogVisible = false;
      this.tableFrom.page = 1;
      this.getList();
    },
    // 回复
    reply(id) {
      this.$modalPrompt('textarea', '回复内容').then((V) => {
        replyCommentApi({
          id: id,
          merchantReplyContent: V,
        }).then((res) => {
          this.$message({
            type: 'success',
            message: '回复成功',
          });
          this.getList();
        });
      });
    },
    // 选择时间
    selectChange(tab) {
      this.timeVal = [];
      this.tableFrom.page = 1;
      this.getList();
    },
    add() {
      this.dialogVisible = true;
      this.timer = new Date().getTime();
    },
    handleClose() {
      this.dialogVisible = false;
      this.$refs.formValidate.pics = [];
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
      this.$modalSure().then(() => {
        replyDeleteApi(id).then(() => {
          this.$message.success('删除成功');
          // this.tableData.data.splice(idx, 1);
          this.getList();
        });
      });
    },
    // 列表
    getList() {
      this.listLoading = true;
      if (this.tableFrom.star === 0) this.tableFrom.star = null;
      replyListApi(this.tableFrom)
        .then((res) => {
          this.tableData = res;
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
  },
};
</script>

<style scoped lang="scss">
.red {
  color: #ed4014;
}
.table {
  ::v-deep .el-tooltip {
    white-space: inherit !important;
  }
}

.selWidth {
  width: 350px !important;
}
.star {
  ::v-deep.el-rate {
    line-height: 2.5 !important;
  }
}
.table {
  ::v-deep el-image__inner,
  .el-image__placeholder,
  .el-image__error {
    width: auto !important;
  }
}
.info {
  width: 63%;
}
</style>
