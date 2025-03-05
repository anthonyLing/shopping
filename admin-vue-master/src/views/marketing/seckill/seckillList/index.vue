<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div slot="header" class="clearfix" ref="headerBox">
        <el-tabs v-model="tableFrom.auditStatus" @tab-click="getList(1)" v-hasPermi="['platform:seckill:product:list']">
          <el-tab-pane :label="item.name" :name="item.type" v-for="(item, index) in headeNum" :key="index" />
        </el-tabs>
        <div class="container" v-hasPermi="['platform:seckill:product:list']">
          <el-form size="small" label-width="90px" :inline="true">
            <el-row>
              <el-col :span="24">
                <el-form-item label="商品名称：">
                  <el-input v-model="proName" placeholder="请输入商品名称" class="selWidth" clearable>
                    <el-button
                      v-hasPermi="['platform:seckill:product:list']"
                      slot="append"
                      icon="el-icon-search"
                      @click="getList(1)"
                    />
                  </el-input>
                </el-form-item>
                <el-form-item label="活动名称：">
                  <el-input v-model="activityName" placeholder="请输入活动名称" class="selWidth" clearable>
                    <el-button
                      v-hasPermi="['platform:seckill:product:list']"
                      slot="append"
                      icon="el-icon-search"
                      @click="getList(1)"
                    />
                  </el-input>
                </el-form-item>
                <el-form-item label="商户星级：" class="inline">
                  <el-rate
                    v-hasPermi="['platform:seckill:product:list']"
                    v-model="tableFrom.merStars"
                    style="margin-top: 8px"
                    @change="getList(1)"
                  ></el-rate>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="商品状态：" class="inline">
                  <el-select
                    v-hasPermi="['platform:seckill:product:list']"
                    v-model="tableFrom.proStatus"
                    clearable
                    placeholder="请选择"
                    class="selWidth"
                    @change="getList(1)"
                  >
                    <el-option label="上架" :value="1" />
                    <el-option label="下架" :value="0" />
                  </el-select>
                </el-form-item>
                <el-form-item label="活动状态：" class="inline">
                  <el-select
                    v-hasPermi="['platform:seckill:product:list']"
                    v-model="tableFrom.activityStatus"
                    clearable
                    placeholder="请选择"
                    class="selWidth"
                    @change="getList(1)"
                  >
                    <el-option label="进行中" :value="1" />
                    <el-option label="已结束" :value="2" />
                  </el-select>
                </el-form-item>
                <el-form-item v-hasPermi="['platform:seckill:product:list']" label="商户名称：">
                  <merchant-name @getMerId="getMerId" :merIdChecked="merIds"></merchant-name>
                </el-form-item>
              </el-col>
              <el-form-item>
                <el-button
                  v-hasPermi="['platform:seckill:product:list']"
                  type="primary"
                  size="small"
                  @click="getList(1)"
                  >查询</el-button
                >
                <el-button size="small" @click="reset('tableFrom')">重置</el-button>
                <el-button
                  v-hasPermi="['platform:seckill:product:delete']"
                  size="small"
                  @click="batchDel"
                  :disabled="!multipleSelection.length"
                  >批量删除</el-button
                >
                <el-button
                  v-if="tableFrom.auditStatus === '2' && checkPermi(['platform:seckill:product:down'])"
                  size="small"
                  @click="batchDown"
                  :disabled="!multipleSelection.length"
                  >批量下架</el-button
                >
              </el-form-item>
            </el-row>
          </el-form>
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="small"
        ref="multipleTable"
        row-key="id"
        @selection-change="handleSelectionChange"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column type="selection" :reserve-selection="true" width="45"></el-table-column>
        <el-table-column prop="id" label="ID" min-width="45" />
        <el-table-column label="商品图" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.image"
                :preview-src-list="[scope.row.image]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="name" min-width="180" :show-overflow-tooltip="true"> </el-table-column>
        <el-table-column prop="categoryName" label="商品分类" min-width="100" />
        <el-table-column prop="merName" :show-overflow-tooltip="true" label="商户名称" min-width="130" />
        <el-table-column prop="activityName" label="活动名称" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column prop="price" label="商品售价" min-width="90" />
        <el-table-column prop="seckillPrice" label="活动价" min-width="90" />
        <el-table-column label="商户星级" min-width="140">
          <template slot-scope="scope">
            <el-rate disabled v-model="scope.row.merStarLevel" style="margin-top: 8px"></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" min-width="60" />
        <el-table-column label="商品状态" fixed="right" min-width="70">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isShow">上架</el-tag>
            <el-tag v-else>下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="活动状态" fixed="right" min-width="90">
          <template slot-scope="scope">
            <el-tag type="danger" v-if="scope.row.activityStatus === 0">未开始</el-tag>
            <el-tag v-if="scope.row.activityStatus === 1">进行中</el-tag>
            <el-tag type="info" v-if="scope.row.activityStatus === 2">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" fixed="right" min-width="90">
          <template slot-scope="scope">
            <el-tag type="danger" v-if="scope.row.auditStatus === 1">待审核</el-tag>
            <el-tag v-if="scope.row.auditStatus === 2">审核成功</el-tag>
            <el-tag type="info" v-if="scope.row.auditStatus === 3">审核失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          v-if="tableFrom.auditStatus === '3'"
          prop="reason"
          label="失败原因"
          fixed="right"
          min-width="120"
          :show-overflow-tooltip="true"
        />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              v-if="tableFrom.auditStatus === '1' && checkPermi(['platform:seckill:time:interval:delete'])"
              type="text"
              size="small"
              @click="handleAudit(scope.row, 1)"
              >审核</el-button
            >
            <el-button
              v-if="Number(tableFrom.auditStatus) > 1 && checkPermi(['platform:seckill:product:list'])"
              type="text"
              size="small"
              @click="handleAudit(scope.row, 2)"
              >详情</el-button
            >
            <el-button
              v-if="tableFrom.auditStatus === '2' && checkPermi(['platform:seckill:product:list'])"
              type="text"
              size="small"
              @click="handleAudit(scope.row, 3)"
              >编辑</el-button
            >
            <el-button
              v-if="tableFrom.auditStatus === '2' && scope.row.isShow && checkPermi(['platform:seckill:product:down'])"
              type="text"
              size="small"
              @click="handleDown(scope.row)"
              class="mr10"
              >下架</el-button
            >
            <el-button
              type="text"
              size="small"
              @click="handleDelete(scope.row)"
              class="mr10"
              v-if="checkPermi(['platform:seckill:product:delete'])"
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

    <!--秒杀商品 审核 详情-->
    <el-drawer
      custom-class="demo-drawer"
      direction="rtl"
      :visible.sync="dialogVisible"
      size="1000px"
      @close="close('ruleForm')"
    >
      <div slot="title">
        {{ isShow === 1 ? '审核秒杀商品' : isShow === 3 ? '秒杀商品编辑' : '秒杀商品详情' }}
      </div>
      <div class="demo-drawer__content" v-loading="loading">
        <div class="description mt10">
          <div class="title">活动信息</div>
          <div class="acea-row">
            <div class="description-term sp100">
              <label class="name">活动名称：</label>{{ seckillInfo.activityName }}
            </div>
            <div class="description-term sp100">
              <label class="name">活动状态：</label>{{ seckillInfo.activityStatus | activityStatusFilter }}
            </div>
            <div class="description-term sp100">
              <label class="name">审核状态：</label
              >{{ seckillInfo.auditStatus == 1 ? '待审核' : seckillInfo.auditStatus == 2 ? '审核通过' : '审核失败' }}
            </div>
            <div class="description-term sp100">
              <label class="name">商品分类：</label>{{ seckillInfo.categoryName }}
            </div>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="description">
          <div class="title">商户信息</div>
          <div class="acea-row">
            <div class="description-term sp100"><label class="name">商户名称：</label>{{ seckillInfo.merName }}</div>
            <div class="description-term sp100 acea-row">
              <label class="name">商户星级：</label>
              <el-rate disabled v-model="seckillInfo.merStarLevel"></el-rate>
            </div>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="description">
          <div class="title">商品信息</div>
          <div class="acea-row mb20">
            <div class="description-term mb20"><label class="name">商品名称：</label>{{ seckillInfo.name }}</div>
            <div style="width: 100%">
              <el-table
                ref="tableList"
                row-key="id"
                :data="seckillInfo.attrValue"
                v-loading="listLoading"
                size="mini"
                border
                default-expand-all
                :tree-props="{ children: 'children' }"
                style="width: 100%"
              >
                <el-table-column min-width="140" label="商品信息">
                  <template slot-scope="scope">
                    <div class="acea-row">
                      <div class="demo-image__preview mr10">
                        <el-image
                          style="width: 36px; height: 36px"
                          :src="scope.row.image"
                          :preview-src-list="[scope.row.image]"
                        />
                      </div>
                      <div class="row_title line2">{{ scope.row.sku }}</div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="otPrice" label="售价" width="120" />
                <el-table-column prop="quotaShow" label="限量" width="80" />
                <el-table-column prop="quota" label="限量剩余" width="120" />
                <el-table-column prop="price" label="活动价格" min-width="120">
                  <template slot-scope="scope">
                    <el-input-number
                      :disabled="isShow < 3"
                      v-model="scope.row.price"
                      type="number"
                      :precision="2"
                      :min="0"
                      :max="99999"
                      :controls="false"
                      class="input_width"
                    >
                    </el-input-number>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <div class="demo-drawer__footer">
        <div v-show="isShow === 1" class="from-foot-btn drawer_fix">
          <div class="acea-row justify-content">
            <el-button
              v-hasPermi="['platform:seckill:product:audit']"
              v-debounceClick="
                () => {
                  onSubmit('fail');
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
                  onSubmit('success');
                }
              "
              >{{ loadingBtn ? '提交中 ...' : '审核通过' }}</el-button
            >
          </div>
        </div>
        <div v-show="isShow === 3" class="from-foot-btn drawer_fix">
          <div class="acea-row justify-content">
            <el-button
              size="small"
              type="primary"
              @click="setPrice(seckillInfo.id, seckillInfo.attrValue)"
              v-hasPermi="['platform:seckill:product:price']"
              >保存活动价</el-button
            >
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import {
  seckillProListApi,
  seckillProSetPriceApi,
  seckillProDelApi,
  seckillProDownApi,
  seckillProAuditApi,
} from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import merchantName from '@/components/merUseCategory';
export default {
  name: 'SeckillList',
  components: {
    merchantName,
  },
  data() {
    return {
      isShow: 0, //1审核，2查看，3编辑
      loading: false,
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      proName: '',
      activityName: '',
      tableFrom: {
        page: 1,
        limit: 20,
        proName: '',
        activityName: '',
        merStars: 0,
        auditStatus: '1',
        merIds: '',
        activityStatus: '',
        proStatus: '',
      },
      headeNum: [
        {
          name: '待审核',
          type: '1',
        },
        {
          name: '审核成功',
          type: '2',
        },
        {
          name: '审核失败',
          type: '3',
        },
      ],
      multipleSelection: [],
      ruleForm: {
        reason: '',
        auditStatus: 'success',
        id: '',
      },
      rules: {
        auditStatus: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
        reason: [{ required: true, message: '请填写拒绝原因', trigger: 'blur' }],
      },
      dialogVisible: false,
      loadingBtn: false,
      id: '', //秒杀商品id
      merIds: [], //商户id
      seckillInfo: {},
    };
  },
  mounted() {
    if (checkPermi(['platform:user:tag:list'])) this.getList(1);
    if (!localStorage.getItem('adminProductClassify')) this.$store.dispatch('product/getAdminProductClassify');
  },
  methods: {
    checkPermi,
    handleAudit(row, n) {
      this.id = row.id;
      this.isShow = n;
      this.dialogVisible = true;
      this.seckillInfo = row;
    },
    close(refName) {
      this.dialogVisible = false;
      if (this.$refs[refName]) {
        this.$refs[refName].resetFields();
      }
    },
    reset(formName) {
      this.proName = '';
      this.activityName = '';
      this.merIds = [];
      this.tableFrom = {
        page: 1,
        limit: 20,
        proName: '',
        activityName: '',
        merStars: 0,
        auditStatus: '1',
        merIds: '',
        activityStatus: '',
        proStatus: '',
      };
      this.getList('');
    },
    //审核拒绝
    cancelForm() {
      this.$modalPrompt('textarea', '拒绝原因').then((V) => {
        this.ruleForm.reason = V;
        this.submit();
      });
    },
    // 审核提交
    onSubmit(type) {
      this.ruleForm.auditStatus = type;
      if (type === 'success') {
        this.$modalSure('审核通过该秒杀商品吗？').then(() => {
          this.submit();
        });
      } else {
        this.cancelForm();
      }
    },
    submit() {
      this.loadingBtn = true;
      this.ruleForm.id = this.id;
      seckillProAuditApi(this.ruleForm)
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
    //表格选中
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    getMerId(id) {
      this.merIds = id;
      this.tableFrom.merIds = id.toString();
      this.getList();
    },
    // 列表
    getList(num) {
      this.tableFrom.page = num ? num : this.tableFrom.page;
      this.tableFrom.activityName = encodeURIComponent(this.activityName);
      this.tableFrom.proName = encodeURIComponent(this.proName);
      this.listLoading = true;
      seckillProListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch((res) => {
          this.listLoading = false;
        });
    },
    //批量删除
    batchDel() {
      let ids = this.multipleSelection.map((item) => item.id).toString();
      this.$modalSure('批量删除秒杀商品吗？').then(() => {
        seckillProDelApi({ ids: ids }).then(() => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
    // 删除
    handleDelete(row) {
      this.$modalSure('删除该秒杀商品吗？').then(() => {
        seckillProDelApi({ ids: row.id }).then(() => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
    //批量下架
    batchDown() {
      let ids = this.multipleSelection.map((item) => item.id).toString();
      this.$modalSure('批量下架秒杀商品吗？').then(() => {
        seckillProDownApi({ ids: ids }).then(() => {
          this.$message.success('下架成功');
          this.getList();
        });
      });
    },
    //下架
    handleDown(row) {
      this.$modalSure('下架该秒杀商品吗？').then(() => {
        seckillProDownApi({ ids: row.id }).then(() => {
          this.$message.success('下架成功');
          this.getList();
        });
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
    //编辑价格
    setPrice(id, row) {
      let productList = {
        productList: [
          {
            attrValue: row.map((item) => {
              return {
                activityPrice: item.price,
                id: item.id,
              };
            }),
            id: id,
          },
        ],
      };
      seckillProSetPriceApi(productList)
        .then((res) => {
          this.$message.success('保存成功');
          this.getList();
          this.dialogVisible = false;
        })
        .catch((res) => {});
    },
  },
};
</script>

<style lang="scss" scoped>
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
    line-height: 20px;
    width: 50%;
    font-size: 14px;
    color: #606266;
  }
  ::v-deep .el-divider--horizontal {
    margin: 12px 0 !important;
  }
}
</style>
