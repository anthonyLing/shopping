<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-tabs
          v-model="tableFrom.type"
          @tab-click="getList(1), goodHeade()"
          v-if="checkPermi(['merchant:product:page:list'])"
        >
          <el-tab-pane
            :label="item.name + '(' + item.count + ')'"
            :name="item.type.toString()"
            v-for="(item, index) in headeNum"
            :key="index"
          />
        </el-tabs>
        <div class="container mt-1" v-if="checkPermi(['merchant:product:page:list'])">
          <el-form inline size="small">
            <el-form-item label="平台端商品分类：">
              <el-cascader
                v-model="tableFrom.categoryId"
                :options="adminProductClassify"
                :props="propsPlant"
                clearable
                class="selWidth mr20"
                @change="seachList"
                size="small"
              />
            </el-form-item>
            <el-form-item label="商户商品分类：">
              <el-cascader
                v-model="tableFrom.cateId"
                :options="merProductClassify"
                :props="propsMer"
                clearable
                class="selWidth mr20"
                @change="seachList"
                size="small"
              />
            </el-form-item>
            <el-form-item label="商品搜索：">
              <el-input
                v-model.trim="keywords"
                placeholder="请输入商品名称，关键字"
                class="selWidth"
                size="small"
                clearable
              >
                <el-button
                  slot="append"
                  icon="el-icon-search"
                  @click="seachList"
                  size="small"
                  v-hasPermi="['merchant:product:page:list']"
                />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <!--id:商品id，isDisabled：是否能编辑(1不能，2能)，isCopy：是否是采集商品(1是，2不是)-->
        <router-link :to="{ path: '/product/list/creatProduct/0/2/2' }">
          <el-button size="small" type="primary" class="mr10" v-hasPermi="['merchant:product:save']"
            >添加商品</el-button
          >
        </router-link>
        <router-link :to="{ path: '/product/list/creatProduct/0/2/1' }">
          <el-button size="small" type="success" class="mr10" v-hasPermi="['merchant:product:import:product']"
            >商品采集</el-button
          >
        </router-link>

        <!--<el-button size="small" icon="el-icon-upload2" @click="exports" v-hasPermi="['admin:export:excel:product']">导出</el-button>-->
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        :highlight-current-row="true"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="收藏：">
                <span>{{ props.row.collectCount }}</span>
              </el-form-item>
              <el-form-item label="虚拟销量：">
                <span>{{ props.row.ficti }}</span>
              </el-form-item>
              <el-form-item label="拒绝原因：" v-if="tableFrom.type == 7">
                <span>{{ props.row.reason }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" min-width="50" v-if="checkedCities.includes('ID')" />
        <el-table-column label="商品图" min-width="80" v-if="checkedCities.includes('商品图')">
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
        <el-table-column
          label="商品名称"
          prop="name"
          min-width="200"
          v-if="checkedCities.includes('商品名称')"
          :show-overflow-tooltip="true"
        >
        </el-table-column>
        <el-table-column
          prop="price"
          label="商品售价"
          min-width="90"
          align="center"
          v-if="checkedCities.includes('商品售价')"
        />
        <el-table-column
          prop="sales"
          label="销量"
          min-width="90"
          align="center"
          v-if="checkedCities.includes('销量')"
        />
        <el-table-column
          prop="stock"
          label="库存"
          min-width="90"
          align="center"
          v-if="checkedCities.includes('库存')"
        />
        <el-table-column label="失败原因" min-width="150" v-if="tableFrom.type === '7'" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span class="fontColor3">{{ scope.row.reason }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80" fixed="right" v-if="checkedCities.includes('状态')">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['merchant:product:up', 'merchant:product:down'])"
              :disabled="
                Number(tableFrom.type) !== 1 &&
                Number(tableFrom.type) !== 4 &&
                (scope.row.auditStatus === 1 || scope.row.auditStatus === 3)
              "
              v-model="scope.row.isShow"
              :active-value="true"
              :inactive-value="false"
              active-text="上架"
              inactive-text="下架"
              @change="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.isShow ? '上架' : '下架' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150" fixed="right" align="center" :render-header="renderHeader">
          <template slot-scope="scope">
            <router-link :to="{ path: '/product/list/creatProduct/' + scope.row.id + '/1' }">
              <el-button type="text" size="small" class="mr10" v-hasPermi="['merchant:product:info']">详情</el-button>
            </router-link>
            <el-button
              type="text"
              size="small"
              class="mr10"
              @click="onEdit(scope.row.id)"
              v-if="tableFrom.type !== '5' && tableFrom.type !== '6'"
              v-hasPermi="['merchant:product:update']"
              >编辑</el-button
            >
            <el-button
              type="text"
              size="small"
              @click="handleAudit(scope.row.id)"
              v-if="tableFrom.type === '2' && scope.row.auditStatus === 1"
              v-hasPermi="['merchant:product:submit:audit']"
              >提审</el-button
            >
            <el-button
              v-if="tableFrom.type === '5'"
              type="text"
              size="small"
              @click="handleRestore(scope.row.id, scope.$index)"
              v-hasPermi="['merchant:product:restore']"
              >恢复商品</el-button
            >
            <el-button
              v-if="Number(tableFrom.type) !== 1"
              type="text"
              size="small"
              @click="handleDelete(scope.row.id, tableFrom.type)"
              v-hasPermi="['merchant:product:delete']"
              >{{ tableFrom.type === '5' ? '删除' : '加入回收站' }}</el-button
            >
            <el-button
              v-if="tableFrom.type === '1'"
              type="text"
              size="small"
              @click="handleEdit(scope.row.id, true)"
              v-hasPermi="['merchant:product:quick:stock:add']"
              >编辑库存</el-button
            >
            <el-button
              v-if="tableFrom.type === '2' && scope.row.auditStatus == 2"
              type="text"
              size="small"
              @click="handleEdit(scope.row.id, false)"
              v-hasPermi="['merchant:product:review:free:edit']"
              >免审编辑</el-button
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
    <div class="card_abs" v-show="card_select_show">
      <template>
        <div class="cell_ht">
          <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange"
            >全选</el-checkbox
          >
          <el-button type="text" @click="checkSave()">保存</el-button>
        </div>
        <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange">
          <el-checkbox v-for="item in columnData" :label="item" :key="item" class="check_cell">{{ item }}</el-checkbox>
        </el-checkbox-group>
      </template>
    </div>
    <!--编辑库存-->
    <el-drawer
      title="编辑库存"
      :visible.sync="drawer"
      :direction="direction"
      :size="1280"
      :before-close="handleCloseEdit"
    >
      <store-edit :productId="productId" v-if="drawer" @subSuccess="subSuccess" :stockEdit="stockEdit"></store-edit>
    </el-drawer>
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

import {
  storeCategoryAllApi,
  productLstApi,
  productDeleteApi,
  categoryApi,
  putOnShellApi,
  offShellApi,
  productHeadersApi,
  productExportApi,
  restoreApi,
  productExcelApi,
  productAuditApi,
} from '@/api/product';
import { getToken } from '@/utils/auth';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { mapGetters } from 'vuex';
import storeEdit from './components/storeEdit';
const objTitle = ['出售中', '仓库中', '已售罄', '警戒库存', '回收站', '待审核', '审核失败'];
export default {
  name: 'ProductList',
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
  components: { storeEdit },
  data() {
    return {
      drawer: false,
      direction: 'rtl',
      propsPlant: {
        children: 'childList',
        label: 'name',
        value: 'id',
        multiple: false,
        emitPath: false,
      },
      propsMer: {
        children: 'childList',
        label: 'name',
        value: 'id',
        multiple: false,
        emitPath: false,
        checkStrictly: true,
      },
      headeNum: [],
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        cateId: '',
        keywords: '',
        type: '1',
        categoryId: null,
      },
      keywords: '',
      categoryList: [],
      objectUrl: process.env.VUE_APP_BASE_API,
      card_select_show: false,
      checkAll: false,
      checkedCities: ['ID', '商品图', '商品名称', '商品售价', '销量', '库存', '排序', '状态'],
      columnData: ['ID', '商品图', '商品名称', '商品售价', '销量', '库存', '排序', '状态'],
      isIndeterminate: true,
      productId: 0,
      stockEdit: false,
    };
  },
  computed: {
    ...mapGetters(['adminProductClassify', 'merProductClassify', 'productBrand']),
  },
  activated() {
    this.goodHeade();
    this.getList();
  },
  mounted() {
    if (checkPermi(['merchant:product:tabs:headers'])) this.goodHeade();
    if (checkPermi(['merchant:product:page:list'])) this.getList();
    this.checkedCities = this.$cache.local.has('goods_stroge')
      ? this.$cache.local.getJSON('goods_stroge')
      : this.checkedCities;
    //this.$store.dispatch('product/getAdminProductClassify');
    this.$store.dispatch('product/getAdminProductClassify');
    if (!localStorage.getItem('merProductClassify')) this.$store.dispatch('product/getMerProductClassify');
    this.$store.dispatch('product/getMerProductBrand');
  },
  methods: {
    checkPermi,
    onEdit(id) {
      if (this.tableFrom.type === '1') {
        this.$modalSure('下架该商品吗？出售商品需下架之后可编辑。').then(() => {
          offShellApi(id).then(() => {
            this.$router.push({ path: `/product/list/creatProduct/${id}/2` });
          });
        });
      } else {
        this.$router.push({ path: `/product/list/creatProduct/${id}/2` });
      }
    },
    subSuccess() {
      this.handleCloseEdit();
    },
    handleEdit(id, stockEdit) {
      this.productId = id;
      this.drawer = true;
      this.stockEdit = stockEdit;
    },
    handleCloseEdit() {
      this.drawer = false;
      this.seachList();
    },
    handleAudit(id) {
      this.$modalSure('提审商品吗').then(() => {
        productAuditApi(id).then((res) => {
          this.goodHeade();
          this.getList();
        });
      });
    },
    handleRestore(id) {
      this.$modalSure('恢复商品').then(() => {
        restoreApi(id).then((res) => {
          this.$message.success('操作成功');
          this.goodHeade();
          this.getList();
        });
      });
    },
    seachList() {
      this.tableFrom.page = 1;
      this.getList();
      this.goodHeade();
    },
    // 导出
    exports() {
      productExcelApi({
        cateId: this.tableFrom.cateId,
        keywords: this.tableFrom.keywords,
        type: this.tableFrom.type,
      }).then((res) => {
        window.location.href = res.fileName;
      });
    },
    // 获取商品表单头数量
    goodHeade() {
      productHeadersApi()
        .then((res) => {
          res.map((item) => {
            item.name = objTitle[Number(item.type) - 1];
          });
          this.headeNum = res;
        })
        .catch((res) => {
          this.$message.error(res.message);
        });
    },
    changeNodes(data) {
      if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
          if (!data[i].childList || data[i].childList.length < 1) {
            data[i].childList = undefined;
          } else {
            this.changeNodes(data[i].childList);
          }
        }
      }
      return data;
    },
    // 列表
    getList() {
      this.listLoading = true;
      this.tableFrom.keywords = encodeURIComponent(this.keywords);
      productLstApi(this.tableFrom)
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
      this.tableFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
    // 删除
    handleDelete(id, type) {
      this.$modalSure(type == 5 ? `删除 id 为 ${id} 的商品` : `id为${id}的商品加入回收站吗`).then(() => {
        const deleteFlag = type == 5 ? 'delete' : 'recycle';
        productDeleteApi({
          id: id,
          type: deleteFlag,
        }).then(() => {
          this.$message.success('删除成功');
          this.getList();
          this.goodHeade();
        });
      });
    },
    onchangeIsShow(row) {
      row.isShow
        ? putOnShellApi(row.id)
            .then(() => {
              this.$message.success('上架成功');
              this.getList();
              this.goodHeade();
            })
            .catch(() => {
              row.isShow = !row.isShow;
            })
        : offShellApi(row.id)
            .then(() => {
              this.$message.success('下架成功');
              this.getList();
              this.goodHeade();
            })
            .catch(() => {
              row.isShow = !row.isShow;
            });
    },
    renderHeader(h) {
      return (
        <p>
          <span style="padding-right:5px;">操作</span>
          <i class="el-icon-setting" onClick={() => this.handleAddItem()}></i>
        </p>
      );
    },
    handleAddItem() {
      if (this.card_select_show) {
        this.$set(this, 'card_select_show', false);
      } else if (!this.card_select_show) {
        this.$set(this, 'card_select_show', true);
      }
    },
    handleCheckAllChange(val) {
      this.checkedCities = val ? this.columnData : [];
      this.isIndeterminate = false;
    },
    handleCheckedCitiesChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.columnData.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.columnData.length;
    },
    checkSave() {
      this.$set(this, 'card_select_show', false);
      this.$modal.loading('正在保存到本地，请稍候...');
      this.$cache.local.setJSON('goods_stroge', this.checkedCities);
      setTimeout(this.$modal.closeLoading(), 1000);
    },
  },
};
</script>

<style scoped lang="scss">
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}
.taoBaoModal {
  //  z-index: 3333 !important;
}
.demo-table-expand {
  ::v-deep label {
    width: 82px;
  }
}
.demo-table-expand {
  ::v-deep .el-form-item__content {
    width: 77%;
  }
}
.selWidth {
  width: 350px !important;
}
.seachTiele {
  line-height: 30px;
}
.relative {
  position: relative;
}
.card_abs {
  position: absolute;
  padding-bottom: 15px;
  top: 260px;
  right: 40px;
  width: 200px;
  background: #fff;
  z-index: 99999;
  box-shadow: 0px 0px 14px 0px rgba(0, 0, 0, 0.1);
}
.cell_ht {
  height: 50px;
  padding: 15px 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.check_cell {
  width: 100%;
  padding: 15px 20px 0;
}
.mt-1 {
  margin-top: 6px;
}
::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #606266;
}
::v-deep .el-drawer__header {
  font-size: 20px;
}
::v-deep .el-drawer__close-btn {
  font-size: 20px;
}
::v-deep .el-drawer__body {
  margin-top: 20px;
}
</style>
