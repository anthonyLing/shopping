<template>
  <div class="components-container">
    <div class="container" v-hasPermi="['platform:system:group:data:list']">
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="listPram.status" placeholder="状态" clearable @change="handlerSearch" class="selWidth">
            <el-option
              v-for="item in constants.roleListStatus"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-button
      type="primary"
      size="mini"
      @click="handlerOpenEditData({}, 0)"
      v-hasPermi="['platform:system:group:data:save']"
      >添加数据</el-button
    >
    <!-- v-if="((formData.id==55 || formData.name==='签到天数配置') && dataList.list.length<7) || (formData.id!=55|| formData.name!=='签到天数配置')" -->
    <el-dialog
      :title="editDataConfig.isCreate === 0 ? '添加数据' : '编辑数据'"
      :visible.sync="editDataConfig.visible"
      append-to-body
      destroy-on-close
      width="700px"
    >
      <edit
        v-if="editDataConfig.visible"
        :form-data="formData"
        :edit-data="editDataConfig.editData"
        :is-create="editDataConfig.isCreate"
        @hideDialog="handlerHideDia"
      />
    </el-dialog>
    <el-table
      :data="dataList.list"
      style="width: 100%; margin-bottom: 20px"
      :header-cell-style="{ fontWeight: 'bold' }"
    >
      <el-table-column label="编号" prop="id" />
      <el-table-column
        v-for="(item, index) in formConf.fields"
        :key="index"
        :label="item.__config__.label"
        :prop="item.__vModel__"
      >
        <template slot-scope="scope">
          <div v-if="['img', 'image', 'pic'].indexOf(item.__vModel__) > -1" class="demo-image__preview">
            <el-image
              style="width: 36px; height: 36px"
              :src="scope.row[item.__vModel__]"
              :preview-src-list="[scope.row[item.__vModel__]]"
            />
          </div>
          <span v-else>{{ scope.row[item.__vModel__] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status">
        <template slot-scope="scope">
          <span>{{ scope.row.status | filterShowOrHide }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            @click="handlerOpenEditData(scope.row, 1)"
            v-hasPermi="['platform:system:group:data:update']"
            >编辑</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="handlerDelete(scope.row)"
            v-if="formMark !== 99 && checkPermi(['platform:system:group:data:delete'])"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="listPram.page"
      :page-sizes="constants.page.limit"
      :layout="constants.page.layout"
      :total="dataList.total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
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
import edit from './combineEdit';
import * as systemGroupDataApi from '@/api/systemGroupData.js';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  // name: "combineDataList"
  components: { edit },
  props: {
    formData: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      constants: this.$constants,
      listPram: {
        gid: null,
        keywords: null,
        status: null, // 1=开启 2=关闭
        page: 1,
        pageSize: this.$constants.page.limit[0],
      },
      editDataConfig: {
        visible: false,
        isCreate: 0, // 0=create 1=edit
        editData: {},
      },
      formConf: { fields: [] },
      dataList: { list: [], total: 0 },
      formMark: 0,
    };
  },
  mounted() {
    if (checkPermi(['platform:system:config:info'])) this.handlerGetFormConfig();
    this.listPram.gid = this.formData.id;
    if (checkPermi(['platform:system:group:data:list'])) this.handlerGetListData(this.listPram);
  },
  methods: {
    checkPermi,
    handlerSearch() {
      this.listPram.page = 1;
      this.handlerGetListData(this.listPram);
    },
    handlerGetListData(pram) {
      // 获取列表数据
      systemGroupDataApi.groupDataList(pram).then((data) => {
        const _selfList = [];
        data.list.forEach((_lItem) => {
          _lItem.value = JSON.parse(_lItem.value);
          const _fields = _lItem.value.fields;
          const _rowData = {};
          _fields.map((item) => {
            _rowData[item.name] = item.value;
          });
          _rowData.id = _lItem.id;
          _rowData.sort = _lItem.sort;
          _rowData.status = _lItem.status;
          _selfList.push(_rowData);
        });
        this.dataList.list = _selfList;
        this.dataList.total = data.total;
      });
    },
    handlerGetFormConfig() {
      // 获取表单配置后生成table列
      const _pram = { id: this.formData.formId };
      systemFormConfigApi.getFormConfigInfo(_pram).then((data) => {
        this.formMark = parseInt(data.id);
        this.formConf = JSON.parse(data.content);
      });
    },
    handlerOpenEditData(rowData, isCreate) {
      this.editDataConfig.editData = rowData;
      this.editDataConfig.isCreate = isCreate;
      this.editDataConfig.visible = true;
    },
    handlerHideDia() {
      this.handlerGetListData(this.listPram);
      this.editDataConfig.visible = false;
    },
    handlerDelete(rowData) {
      this.$confirm('确实删除当前数据', '提示').then(() => {
        systemGroupDataApi.groupDataDelete(rowData).then((data) => {
          this.$message.success('删除数据成功');
          this.handlerHideDia();
        });
      });
    },
    handleSizeChange(val) {
      this.listPram.limit = val;
      this.handlerGetListData(this.listPram);
    },
    handleCurrentChange(val) {
      this.listPram.page = val;
      this.handlerGetListData(this.listPram);
    },
  },
};
</script>

<style scoped></style>
