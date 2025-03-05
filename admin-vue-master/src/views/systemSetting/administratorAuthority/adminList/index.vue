<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form inline size="small" @submit.native.prevent v-hasPermi="['platform:admin:list']">
        <el-form-item>
          <el-select v-model="listPram.roles" placeholder="身份" clearable class="selWidth">
            <el-option v-for="item in roleList.list" :key="item.id" :label="item.roleName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="listPram.status" placeholder="状态" clearable class="selWidth">
            <el-option
              v-for="item in constants.roleListStatus"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model.trim="realName" placeholder="姓名或者账号" clearable class="selWidth" />
        </el-form-item>
        <el-form-item>
          <el-button size="mini" type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-form inline @submit.native.prevent v-hasPermi="['platform:admin:save']">
        <el-form-item>
          <el-button size="mini" type="primary" @click="handlerOpenEdit(0)">添加管理员</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="listData.list" size="mini" :header-cell-style="{ fontWeight: 'bold' }">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column label="姓名" prop="realName" min-width="120" />
        <el-table-column label="账号" prop="account" min-width="120" />
        <el-table-column label="手机号" prop="lastTime" min-width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.phone | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="身份" prop="realName" min-width="230">
          <template slot-scope="scope" v-if="scope.row.roleNames">
            <el-tag
              size="small"
              type="info"
              v-for="(item, index) in scope.row.roleNames.split(',')"
              :key="index"
              class="mr5"
              >{{ item }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column label="最后登录时间" prop="lastTime" min-width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.lastTime | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最后登录IP" prop="lastIp" min-width="150">
          <template slot-scope="scope">
            <span>{{ scope.row.lastIp | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['platform:admin:update:status'])"
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              :disabled="scope.row.roles === '1'"
              @change="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.status ? '开启' : '关闭' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="删除标记" prop="status" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.isDel | filterYesOrNo }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="130" fixed="right">
          <template slot-scope="scope">
            <template v-if="scope.row.isDel">
              <span>-</span>
            </template>
            <template v-else>
              <el-button
                :disabled="scope.row.roles === '1'"
                type="text"
                size="mini"
                @click="handlerOpenEdit(1, scope.row)"
                v-hasPermi="['platform:admin:update', 'platform:admin:info']"
                >编辑</el-button
              >
              <el-button
                type="text"
                size="mini"
                :disabled="scope.row.roles === '1'"
                @click="handlerOpenDel(scope.row)"
                v-hasPermi="['platform:admin:delete']"
                >删除</el-button
              >
            </template>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="listPram.page"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="listData.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    <el-dialog
      :visible.sync="editDialogConfig.visible"
      :title="editDialogConfig.isCreate === 0 ? '创建管理员' : '编辑管理员'"
      destroy-on-close
      :close-on-click-modal="false"
      width="700px"
    >
      <edit
        v-if="editDialogConfig.visible"
        :is-create="editDialogConfig.isCreate"
        :edit-data="editDialogConfig.editData"
        @hideEditDialog="hideEditDialog"
      />
    </el-dialog>
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
import * as systemAdminApi from '@/api/systemadmin.js';
import * as roleApi from '@/api/role.js';
import edit from './edit';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  // name: "index"
  components: { edit },
  data() {
    return {
      constants: this.$constants,
      listData: { list: [] },
      listPram: {
        account: null,
        addTime: null,
        lastIp: null,
        lastTime: null,
        level: null,
        loginCount: null,
        realName: null,
        roles: null,
        status: null,
        page: 1,
        limit: this.$constants.page.limit[0],
      },
      realName: '',
      roleList: [],
      menuList: [],
      editDialogConfig: {
        visible: false,
        isCreate: 0, // 0=创建，1=编辑
        editData: {},
      },
    };
  },
  mounted() {
    if (checkPermi(['platform:admin:list'])) this.handleGetAdminList();
    if (checkPermi(['platform:admin:role:list'])) this.handleGetRoleList();
  },
  methods: {
    checkPermi,
    onchangeIsShow(row) {
      systemAdminApi
        .updateStatusApi({ id: row.id, status: row.status })
        .then(async () => {
          this.$message.success('修改成功');
          this.handleGetAdminList();
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
    handleSearch() {
      this.listPram.page = 1;
      this.handleGetAdminList();
    },
    handleSizeChange(val) {
      this.listPram.limit = val;
      this.handleGetAdminList();
      this.handleGetRoleList(this.listPram);
    },
    handleCurrentChange(val) {
      this.listPram.page = val;
      this.handleGetAdminList();
      this.handleGetRoleList(this.listPram);
    },
    handleGetRoleList() {
      const _pram = {
        page: 1,
        limit: this.constants.page.limit[4],
      };
      roleApi.getRoleList(_pram).then((data) => {
        this.roleList = data;
      });
    },
    handlerOpenDel(rowData) {
      this.$confirm('确认删除当前数据').then(() => {
        const _pram = { id: rowData.id };
        systemAdminApi.adminDel(_pram).then((data) => {
          this.$message.success('删除数据成功');
          this.listPram.page = 1;
          this.handleGetAdminList();
        });
      });
    },
    handleGetAdminList() {
      this.listPram.realName = encodeURIComponent(this.realName);
      systemAdminApi.adminList(this.listPram).then((data) => {
        this.listData = data;
      });
    },
    handlerOpenEdit(isCreate, editDate) {
      this.editDialogConfig.editData = editDate;
      this.editDialogConfig.isCreate = isCreate;
      this.editDialogConfig.visible = true;
    },
    hideEditDialog() {
      this.editDialogConfig.visible = false;
      this.handleGetAdminList();
    },
  },
};
</script>

<style scoped></style>
