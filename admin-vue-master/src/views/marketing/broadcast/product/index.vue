<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form size="small" label-width="100px">
            <el-form-item label="状态：">
              <el-radio-group v-model="tableForm.reviewStatus" type="button" @change="getList(1)">
                <el-radio-button label>全部</el-radio-button>
                <el-radio-button label="0">商户创建/撤回</el-radio-button>
                <el-radio-button label="1">平台待审核/商户重新提交审核</el-radio-button>
                <el-radio-button label="2">平台审核通过</el-radio-button>
                <el-radio-button label="3">平台审核失败</el-radio-button>
                <el-radio-button label="4">微信审核成功</el-radio-button>
                <el-radio-button label="5">微信审核失败</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="商户分类" prop="categoryId" class="inline">
              <el-select v-model="tableForm.merchant_type" placeholder="请选择商户分类" clearable @change="getList(1)">
                <el-option
                  v-for="item in merchantClassify"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关键字：" class="width100" style="display: inline-block">
              <el-input
                v-model="keywords"
                @keyup.enter.native="getList(1)"
                placeholder="请输入直播商品名称/ID,商户名称,微信直播间id,微信审核单id"
                class="selWidth"
                size="small"
              >
                <el-button slot="append" icon="el-icon-search" class="el-button-solt" @click="getList(1)" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small" highlight-current-row>
        <el-table-column label="ID" width="50" prop="id" />
        <el-table-column label="直播商品ID" min-width="80">
          <template slot-scope="scope">
            <span>{{ scope.row.goodsId | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" min-width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ scope.row.name + '/' + scope.row.productId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="商品图" min-width="60">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.coverImgUrlLocal"
                :preview-src-list="[scope.row.coverImgUrlLocal]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="价格类型" min-width="80">
          <template slot-scope="scope">
            <span>{{ scope.row.priceType | priceTypeFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column label="价格" min-width="150">
          <template slot-scope="scope">
            <span v-if="scope.row.priceType === 1">{{ scope.row.price }}</span>
            <span v-else-if="scope.row.priceType === 1">{{ scope.row.price + '~' + scope.row.price2 }}</span>
            <span v-else>(原){{ scope.row.price }}，(折扣){{ scope.row.price2 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="商户名称" min-width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.merName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="商户分类" min-width="80">
          <template slot-scope="scope">
            <span class="widths">{{ scope.row.merType | merCategoryFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" min-width="60" label="排序" />
        <el-table-column label="审核状态" min-width="110">
          <template slot-scope="scope">
            <el-tooltip
              v-if="scope.row.reviewStatus === 3 || scope.row.reviewStatus === 5"
              class="item"
              effect="dark"
              :content="scope.row.reviewReason"
              placement="top"
            >
              <el-tag type="danger" v-if="scope.row.reviewStatus === 3">平台审核失败</el-tag>
              <el-tag type="danger" v-if="scope.row.reviewStatus === 5">微信审核失败</el-tag>
            </el-tooltip>
            <div v-else>
              <el-tag type="info" v-if="scope.row.reviewStatus === 1">平台待审核</el-tag>
              <el-tag type="info" v-if="scope.row.reviewStatus === 2">平台审核通过</el-tag>
              <el-tag v-if="scope.row.reviewStatus === 4">微信审核成功</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              class="mr10"
              v-if="
                scope.row.reviewStatus === 1 &&
                checkPermi(['platform:mp:live:goods:review', 'platform:mp:live:goods:info'])
              "
              @click="toExamine(scope.row)"
              >审核</el-button
            >
            <el-button
              v-hasPermi="['platform:mp:live:goods:sort']"
              type="text"
              size="small"
              class="mr10"
              @click="handleSort(scope.row.id)"
              >排序</el-button
            >
            <el-button
              v-hasPermi="['platform:mp:live:goods:delete']"
              type="text"
              size="small"
              @click="handleDelete(scope.row.id)"
              >删除</el-button
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
    <!--审核-->
    <el-drawer
      custom-class="demo-drawer"
      direction="rtl"
      :visible.sync="dialogVisible"
      size="1000px"
      @close="close('ruleForm')"
    >
      <div slot="title" class="demo-drawer_title">直播商品审核</div>
      <div class="demo-drawer__content" v-loading="loading">
        <div class="description">
          <div class="acea-row">
            <div class="description-term lang"><label class="name">商品名称：</label>{{ proInfo.name }}</div>
            <div class="description-term sp100"><label class="name">商品ID：</label>{{ proInfo.productId }}</div>
            <div class="description-term sp100"><label class="name">商户名称：</label>{{ proInfo.merName }}</div>
            <div class="description-term sp100">
              <label class="name">商户分类：</label>{{ proInfo.merType | merCategoryFilter }}
            </div>
            <div v-if="proInfo.priceType === 1" class="description-term sp100">
              <label class="name">一口价：</label>{{ proInfo.price }}
            </div>
            <div v-else class="description-term sp100">
              <label class="name">价格区间：</label>{{ proInfo.price + '~' + proInfo.price2 }}
            </div>
            <div class="description-term sp100">
              <label class="name">审核结果：</label>{{ proInfo.reviewStatus | liveReviewStatusFilter }}
            </div>
            <div v-show="proInfo.reviewStatus === 3 || proInfo.reviewStatus === 5" class="description-term sp100">
              <label class="name">审核失败原因：</label>{{ proInfo.reviewReason }}
            </div>
            <div class="acea-row sp100 description-term">
              <label class="name" style="color: #606266">商品图：</label>
              <div class="demo-image__preview mr10">
                <el-image
                  style="width: 50px; height: 50px"
                  :src="proInfo.coverImgUrlLocal"
                  :preview-src-list="[proInfo.coverImgUrlLocal]"
                />
              </div>
            </div>
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
                  onSubmit(3);
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
                  onSubmit(2);
                }
              "
              >{{ loadingBtn ? '提交中 ...' : '审核通过' }}</el-button
            >
          </div>
        </div>
      </div>
    </el-drawer>
    <!--排序-->
    <details-from ref="ProDetail" @getList="getList" />
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
import { liveGoodsListApi, liveGoodsReviewApi, liveGoodsDelApi } from '@/api/marketing';
import detailsFrom from './proDetail';
import { mapGetters } from 'vuex';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'BroadcastProList',
  computed: {
    ...mapGetters(['merchantClassify', 'merchantType']),
  },
  components: { detailsFrom },
  data() {
    return {
      loading: false,
      listLoading: true,
      id: '',
      dialogVisible: false,
      tableData: {
        data: [],
        total: 0,
      },
      keywords: '',
      tableForm: {
        page: 1,
        limit: 20,
        reviewStatus: '',
        keywords: '',
        merchant_type: '',
      },
      liveRoomStatus: '',
      ruleForm: {
        reviewReason: '',
        reviewStatus: 2,
        id: '',
      },
      proInfo: '', //直播商品详情
      loadingBtn: false, //审核按钮
    };
  },
  mounted() {
    if (!this.merchantClassify.length) this.$store.dispatch('merchant/getMerchantClassify');
    if (checkPermi(['platform:mp:live:goods:list'])) this.getList();
  },
  methods: {
    checkPermi,
    close() {
      this.dialogVisible = false;
    },
    //审核拒绝
    cancelForm() {
      this.$modalPrompt('textarea', '拒绝原因').then((V) => {
        this.ruleForm.reviewReason = V;
        this.submit();
      });
    },
    // 审核提交
    onSubmit(type) {
      this.ruleForm.reviewStatus = type;
      if (type === 2) {
        this.$modalSure('审核通过该直播商品吗？').then(() => {
          this.submit();
        });
      } else {
        this.cancelForm();
      }
    },
    submit() {
      this.loadingBtn = true;
      this.ruleForm.id = this.id;
      liveGoodsReviewApi(this.ruleForm)
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
    // 排序
    handleSort(id) {
      this.id = id;
      this.$refs.ProDetail.dialogVisible = true;
      this.$refs.ProDetail.isEdit = true;
      this.$refs.ProDetail.getData(id);
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure('删除该商品吗？').then(() => {
        liveGoodsDelApi(id).then(() => {
          this.$message.success('删除成功');
          if (this.tableData.data.length === 1 && this.tableForm.page > 1)
            this.tableForm.page = this.tableForm.page - 1;
          this.getList('');
        });
      });
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableForm.page = num ? num : this.tableForm.page;
      this.tableForm.keywords = encodeURIComponent(this.keywords);
      liveGoodsListApi(this.tableForm)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch((res) => {
          this.listLoading = false;
          this.$message.error(res.message);
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
    // 审核
    toExamine(row) {
      this.loading = true;
      this.proInfo = row;
      this.id = row.id;
      this.dialogVisible = true;
      this.loading = false;
    },
  },
};
</script>

<style scoped lang="scss">
.lang {
  width: 100% !important;
}
.demo-drawer__content {
  padding: 0 30px;
  padding-bottom: 86px;
}

.title {
  margin-bottom: 14px;
  color: #303133;
  font-weight: 500;
  font-size: 14px;
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
.selWidth {
  width: 400px;
}
.seachTiele {
  line-height: 35px;
}
.fa {
  color: #0a6aa1;
  display: block;
}
.sheng {
  color: #ff0000;
  display: block;
}
</style>
