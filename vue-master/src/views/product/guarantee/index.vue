<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container mt-1">
          <el-form inline size="small">
            <el-button
              type="primary"
              size="small"
              v-hasPermi="['merchant:product:guarantee:group:add']"
              @click="handlerOpenEdit(0)"
              >添加保障服务</el-button
            >
          </el-form>
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        :highlight-current-row="true"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="name" label="组合名称" />
        <el-table-column label="创建时间">
          <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="small"
              type="text"
              @click="handlerOpenEdit(1, scope.row)"
              v-hasPermi="['merchant:product:guarantee:group:edit', 'merchant:product:guarantee:group:edit']"
              >编辑</el-button
            >
            <el-button
              size="small"
              type="text"
              @click="handlerOpenEdit(1, scope.row, 'info')"
              v-hasPermi="['merchant:product:guarantee:group:edit']"
              >详情</el-button
            >
            <el-button
              size="small"
              type="text"
              @click="handlerOpenDel(scope.row)"
              v-hasPermi="['merchant:product:guarantee:group:delete']"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog title="提示" :visible.sync="dialogVisible" width="1000px" :before-close="handleClose">
      <el-form :model="guaranteeForm" :rules="rules" ref="guaranteeForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="组合名称" prop="name">
          <el-input v-model.trim="guaranteeForm.name" :disabled="isDisabled"></el-input>
        </el-form-item>
        <el-form-item label="服务列表" prop="guaranteeList">
          <el-table
            border
            ref="multipleTable"
            :data="guaranteeListNew"
            tooltip-effect="dark"
            style="width: 100%"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" :selectable="selectable" width="55"></el-table-column>
            <el-table-column prop="id" label="ID" min-width="50" />
            <el-table-column label="服务条款" min-width="150">
              <template slot-scope="scope">
                <div>{{ scope.row.name }}</div>
                <div v-show="!scope.row.isShow" class="color-red">该数据已无效</div>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="服务内容描述" min-width="200" />
          </el-table>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            v-hasPermi="['merchant:product:guarantee:group:edit']"
            @click="submitForm('guaranteeForm')"
            :disabled="isDisabled"
            >确定</el-button
          >
        </el-form-item>
      </el-form>
    </el-dialog>
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

import * as store from '@/api/product';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  data() {
    var validateGuarantee = (rule, value, callback) => {
      if (this.guaranteeChanged.length === 0) {
        callback(new Error('请至少选择一个服务选项'));
      } else {
        callback();
      }
    };
    return {
      listLoading: false,
      keyNum: 0,
      dialogVisible: false,
      guaranteeForm: {
        name: '',
        id: 0,
        gids: '',
      },
      guaranteeList: [],
      guaranteeListNew: [],
      tableData: {
        data: [],
      },
      rules: {
        name: [{ required: true, message: '请填写组合名称', trigger: 'blur' }],
        guaranteeList: [{ required: true, validator: validateGuarantee, trigger: 'change' }],
      },
      guaranteeChanged: [],
      isDisabled: false,
    };
  },
  mounted() {
    if (checkPermi(['merchant:product:guarantee:group:list'])) this.getList();
    if (checkPermi(['merchant:plat:product:guarantee:list'])) this.getProductGuarantee();
  },
  methods: {
    checkPermi,
    selectable(row, index) {
      if (this.isDisabled) return false;
      if (row.isShow) {
        return true;
      } else {
        return false;
      }
    },
    handleSelectionChange(val) {
      this.guaranteeChanged = val;
    },
    // 保障服务列表
    getProductGuarantee() {
      store.productGuaranteeApi().then((res) => {
        this.guaranteeList = res;
      });
    },
    handleClose() {
      this.dialogVisible = false;
      this.guaranteeForm = {
        name: '',
        id: 0,
        gids: '',
      };
      this.guaranteeChanged = [];
      this.$refs.multipleTable.clearSelection();
      this.$refs['guaranteeForm'].resetFields();
    },
    // 列表
    getList() {
      this.listLoading = true;
      store
        .guaranteeListApi()
        .then((res) => {
          this.tableData.data = res;
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
      this.getList();
    },
    handlerOpenEdit(isCreate, editDate, info) {
      let list = [];
      this.guaranteeListNew = [...this.guaranteeList];
      this.isDisabled = !!info;
      if (isCreate === 1) {
        this.guaranteeForm = Object.assign({}, editDate);
        this.guaranteeListNew.map((item) => {
          editDate.guaranteeList.map((obj) => {
            if (obj.gid === item.id && obj.isShow) list.push(item);
          });
        });
        if (list) {
          list.forEach((row) => {
            this.$nextTick(() => {
              this.$refs.multipleTable.toggleRowSelection(row, true);
            });
          });
        }
      } else {
        list = this.guaranteeListNew.filter((item) => item.isShow);
        this.guaranteeListNew = list;
      }
      this.dialogVisible = true;
    },
    close() {
      this.dialogVisible = false;
      this.guaranteeForm.name = '';
      this.getList();
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let ids = [];
          this.guaranteeChanged.map((item) => {
            ids.push(item.id);
          });
          this.$delete(this.guaranteeForm, 'guaranteeList');
          this.$delete(this.guaranteeForm, 'createTime');
          this.guaranteeForm.gids = ids.toString();
          !this.guaranteeForm.id
            ? store
                .guaranteeAddApi(this.guaranteeForm)
                .then((res) => {
                  this.$message.success('操作成功');
                  this.close();
                })
                .catch(() => {
                  this.loading = false;
                })
            : store
                .guaranteeUpdateApi(this.guaranteeForm)
                .then((res) => {
                  this.$message.success('操作成功');
                  this.close();
                })
                .catch(() => {
                  this.loading = false;
                });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    handlerOpenDel(rowData) {
      this.$modalSure('删除当前保障服务吗').then(() => {
        store.guaranteeDeleteApi(rowData.id).then((data) => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
  },
};
</script>
