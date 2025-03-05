<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-button size="small" type="primary" @click="onAdd" v-hasPermi="['platform:product:brand:add']"
          >添加品牌</el-button
        >
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="small"
        row-key="brand_id"
        :default-expand-all="false"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column label="品牌名称" prop="name" min-width="150" />
        <el-table-column label="分类图标" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.icon"
                :preview-src-list="[scope.row.icon]"
                v-if="scope.row.icon"
              />
              <img style="width: 36px; height: 36px" v-else :src="defaultImg" alt="" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" min-width="50" />
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
        <el-table-column prop="status" label="是否显示" min-width="100" fixed="right">
          <template slot-scope="scope" v-if="checkPermi(['platform:product:brand:show:status'])">
            <el-switch
              v-model="scope.row.isShow"
              :active-value="true"
              :inactive-value="false"
              active-text="显示"
              inactive-text="隐藏"
              @change="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="onEdit(scope.row)"
              v-hasPermi="['platform:product:brand:update']"
              >编辑</el-button
            >
            <el-button
              type="text"
              size="small"
              v-hasPermi="['platform:product:brand:delete']"
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
    <creat-band ref="creatBands" :editData="editData" @getList="getList" />
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
import * as storeApi from '@/api/product.js';
import creatBand from './creatBand';
import { mapGetters } from 'vuex';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'BrandList',
  components: { creatBand },
  computed: {
    ...mapGetters(['adminProductClassify']),
  },
  data() {
    return {
      props: {
        value: 'store_brand_category_id',
        label: 'cate_name',
        children: 'children',
        emitPath: false,
      },
      defaultImg: require('@/assets/imgs/moren.jpg'),
      isChecked: false,
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
      },
      editData: {},
    };
  },
  mounted() {
    // this.getBrandCategory();
    if (!localStorage.getItem('adminProductClassify')) this.$store.dispatch('product/getAdminProductClassify');
    if (checkPermi(['platform:product:brand:list'])) this.getList();
  },
  methods: {
    checkPermi,
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      storeApi
        .brandListApi(this.tableFrom)
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
    // 添加
    onAdd() {
      this.editData = {};
      this.$refs.creatBands.dialogVisible = true;
    },
    // 编辑
    onEdit(row) {
      this.editData = row;
      this.$refs.creatBands.dialogVisible = true;
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure('删除品牌吗？，该品牌优惠券将同步删除。').then(() => {
        storeApi.brandDeleteApi(id).then((res) => {
          this.$message.success('删除成功');
          this.$store.commit('product/SET_ProductBrand', []);
          this.getList();
        });
      });
    },
    onchangeIsShow(row) {
      storeApi.brandShowApi(row.id).then((res) => {
        this.$message.success('操作成功');
        this.$store.commit('product/SET_ProductBrand', []);
        this.getList();
      });
    },
  },
};
</script>

<style scoped lang="scss">
.selWidth {
  width: 300px !important;
}
</style>
