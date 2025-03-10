<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div slot="header" class="clearfix" ref="tableheader" v-hasPermi="['merchant:user:page:list']">
        <div class="container">
          <el-form
            inline
            size="small"
            :model="userFrom"
            ref="userFrom"
            :label-position="labelPosition"
            label-width="100px"
          >
            <el-row>
              <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                <el-col v-bind="grid">
                  <el-form-item label="用户昵称：">
                    <el-input v-model.trim="nikename" placeholder="请输入用户昵称" clearable class="selWidth" />
                  </el-form-item>
                </el-col>
              </el-col>
              <template v-if="collapse">
                <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                  <el-form-item label="访问时间：">
                    <el-date-picker
                      v-model="timeVal"
                      align="right"
                      unlink-panels
                      value-format="yyyy-MM-dd"
                      format="yyyy-MM-dd"
                      size="small"
                      type="daterange"
                      placement="bottom-end"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      class="selWidth"
                      :picker-options="pickerOptions"
                      @change="onchangeTime"
                    />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                  <el-col v-bind="grid">
                    <el-form-item label="手机号：">
                      <el-input
                        v-model.trim="userFrom.phone"
                        placeholder="请输入手机号，不支持模糊搜索"
                        clearable
                        class="selWidth"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col v-bind="grid">
                    <el-form-item label="性别：">
                      <el-radio-group v-model="userFrom.sex" type="button" class="selWidth">
                        <el-radio-button label="">
                          <span>全部</span>
                        </el-radio-button>
                        <el-radio-button label="0">
                          <span>未知</span>
                        </el-radio-button>
                        <el-radio-button label="1">
                          <span>男</span>
                        </el-radio-button>
                        <el-radio-button label="2">
                          <span>女</span>
                        </el-radio-button>
                        <el-radio-button label="3">
                          <span>保密</span>
                        </el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                  <el-col v-bind="grid">
                    <el-form-item label="注册类型：">
                      <el-select v-model="userFrom.registerType" placeholder="请选择" @change="getList(1)" clearable>
                        <el-option
                          v-for="item in registerTypeList"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        >
                        </el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-col>
              </template>
              <el-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6" class="text-right userFrom userbtn">
                <el-form-item>
                  <el-button
                    type="primary"
                    icon="ios-search"
                    label="default"
                    class="mr15"
                    size="small"
                    @click="userSearchs"
                    v-hasPermi="['merchant:user:page:list']"
                    >查询</el-button
                  >
                  <el-button class="ResetSearch mr10" @click="reset('userFrom')" size="small">重置</el-button>
                  <a class="ivu-ml-8" @click="onCollapse">
                    <template v-if="!collapse"> 展开 <i class="el-icon-arrow-down"></i> </template>
                    <template v-else> 收起 <i class="el-icon-arrow-up"></i> </template>
                  </a>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </div>
      <el-table
        ref="table"
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        :height="tableHeight"
        highlight-current-row
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="真实姓名：">
                <span>{{ props.row.realName | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="性别">
                <span>{{ props.row.sex | sexFilter }}</span>
              </el-form-item>
              <el-form-item label="首次访问：">
                <span>{{ props.row.createTime | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="近次访问：">
                <span>{{ props.row.lastLoginTime | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="备注：">
                <span>{{ props.row.mark | filterEmpty }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column label="头像" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="昵称" min-width="130" v-if="checkedCities.includes('昵称')">
          <template slot-scope="scope">
            <span :class="scope.row.isLogoff == true ? 'red' : ''">{{ scope.row.nickname | filterEmpty }}</span>
            <span :class="scope.row.isLogoff == true ? 'red' : ''" v-if="scope.row.isLogoff == true">|</span>
            <span v-if="scope.row.isLogoff == true" class="red">(已注销)</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.phone | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="registerType" label="注册类型" min-width="100">
          <template slot-scope="scope">
            <el-tag :color="filterRegisterType(scope.row.registerType)">{{
              scope.row.registerType | registerTypeFilter
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="130" fixed="right" align="center" :render-header="renderHeader">
          <template slot-scope="scope">
            <el-button type="text" @click.native="onDetails(scope.row.id)" v-if="checkPermi(['merchant:user:detail'])"
              >用户详情</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[15, 30, 45, 60]"
          :page-size="userFrom.limit"
          :current-page="userFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>
    <div class="card_abs" v-show="card_select_show" :style="{ top: collapse ? 570 + 'px' : 270 + 'px' }">
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
    <!--用户详情-->
    <detail-user ref="userDetailFrom"></detail-user>
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

import { userListApi } from '@/api/user';
import userList from '@/components/userList';
import detailUser from './userDetails.vue';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';
export default {
  name: 'UserIndex',
  components: { detailUser },
  filters: {
    sexFilter(status) {
      const statusMap = {
        0: '未知',
        1: '男',
        2: '女',
        3: '保密',
      };
      return statusMap[status];
    },
    typeFilter(value) {
      const statusMap = {
        facebook: 'Facebook',
        twitter: 'Twitter',
        google: 'Google',
        email: 'Email',
        phone: 'Phone',
        visitor: '游客',
      };
      return statusMap[value];
    },
  },
  data() {
    return {
      registerTypeList: [
        {
          value: 'wechat',
          label: '公众号',
        },
        {
          value: 'routine',
          label: '小程序',
        },
        {
          value: 'h5',
          label: 'H5',
        },
        {
          value: 'iosWx',
          label: '微信ios',
        },
        {
          value: 'androidWx',
          label: '微信安卓',
        },
        {
          value: 'ios',
          label: 'ios',
        },
      ],
      tableHeight: 0,
      formExtension: {
        image: '',
        spreadUid: '',
        userId: '',
      },
      ruleInline: {},
      extensionVisible: false,
      userVisible: false,
      levelInfo: '',
      pickerOptions: this.$timeOptions,
      loadingBtn: false,
      PointValidateForm: {
        integralType: 2,
        integralValue: 0,
        moneyType: 2,
        moneyValue: 0,
        uid: '',
      },
      loadingPoint: false,
      VisiblePoint: false,
      visible: false,
      userIds: '',
      dialogVisible: false,
      levelVisible: false,
      levelData: [],
      groupData: [],
      labelData: [],
      selData: [],
      labelPosition: 'right',
      collapse: false,
      props: {
        children: 'child',
        label: 'name',
        value: 'name',
        emitPath: false,
      },
      propsCity: {
        children: 'child',
        label: 'name',
        value: 'name',
      },
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      nikename: '',
      userFrom: {
        registerType: '',
        sex: '',
        dateLimit: '',
        nikename: '',
        page: 1,
        limit: 15,
        phone: '',
      },
      grid: {
        xl: 8,
        lg: 12,
        md: 12,
        sm: 24,
        xs: 24,
      },
      levelList: [],
      labelLists: [],
      groupList: [],
      selectedData: [],
      timeVal: [],
      dynamicValidateForm: {
        groupId: [],
      },
      loading: false,
      groupIdFrom: [],
      selectionList: [],
      batchName: '',
      uid: 0,
      Visible: false,
      keyNum: 0,
      address: [],
      multipleSelectionAll: [],
      idKey: 'uid',
      card_select_show: false,
      checkAll: false,
      checkedCities: ['ID', '头像', '昵称', '用户等级', '分组', '推荐人', '手机号', '余额', '积分'],
      columnData: ['ID', '头像', '昵称', '用户等级', '分组', '推荐人', '手机号', '余额', '积分'],
      isIndeterminate: true,
    };
  },
  created() {
    // 浏览器高度
    let windowHeight = document.documentElement.clientHeight || document.body.clientHeight;

    // 此处减去100即为当前屏幕内除了表格高度以外其它内容的总高度，
    // this.tableHeight = windowHeight - 388;
  },
  activated() {
    this.userFrom.nikename = '';
    if (checkPermi(['merchant:user:page:list'])) this.getList(1);
  },
  mounted() {
    if (checkPermi(['merchant:user:page:list'])) this.getList();
    this.$nextTick(() => {
      let tableHeader = this.$refs.tableheader.offsetHeight;
      this.tableHeight = this.$selfUtil.getTableHeight(tableHeader + 100);
    });
  },
  methods: {
    checkPermi,
    filterRegisterType(status) {
      const statusMap = {
        wechat: '#FD5ACC',
        routine: '#A277FF',
        h5: '#E8B600',
        iosWx: '#1BBE6B',
        androidWx: '#EF9C20',
        ios: '#1890FF',
      };
      return statusMap[status];
    },
    onCollapse() {
      this.collapse = !this.collapse;
      this.$nextTick(() => {
        let tableHeader = this.$refs.tableheader.offsetHeight;
        this.tableHeight = this.$selfUtil.getTableHeight(tableHeader + 150);
      });
    },
    getTemplateRow(row) {
      this.formExtension.image = row.avatar;
      this.formExtension.spreadUid = row.uid;
    },
    // setExtension(row){
    //   this.formExtension = {
    //       image: '',
    //       spreadUid: '',
    //       userId: row.uid
    //   };
    //   this.extensionVisible = true
    // },
    handleCloseExtension() {
      this.extensionVisible = false;
    },
    modalPicTap() {
      this.userVisible = true;
    },
    resetForm() {
      this.visible = false;
    },
    reset(formName) {
      this.userFrom = {
        userType: '',
        sex: '',
        phone: '',
        dateLimit: '',
        nikename: '',
        page: 1,
        limit: 15,
      };
      this.nikename = '';
      this.levelData = [];
      this.groupData = [];
      this.labelData = [];
      this.timeVal = [];
      this.getList();
    },
    // 发送文章
    sendNews() {
      if (this.selectionList.length === 0) return this.$message.warning('请先选择用户');
      const _this = this;
      this.$modalArticle(function (row) {}, 'send');
    },
    // 发送优惠劵
    onSend() {
      if (this.selectionList.length === 0) return this.$message.warning('请选择要设置的用户');
      const _this = this;
      this.$modalCoupon(
        'send',
        (this.keyNum += 1),
        [],
        function (row) {
          _this.formValidate.give_coupon_ids = [];
          _this.couponData = [];
          row.map((item) => {
            _this.formValidate.give_coupon_ids.push(item.coupon_id);
            _this.couponData.push(item.title);
          });
          _this.selectionList = [];
        },
        this.userIds,
        'user',
      );
    },
    Close() {
      this.Visible = false;
      this.levelVisible = false;
    },
    // 账户详情
    onDetails(id) {
      this.$refs.userDetailFrom.getUserDetail(id);
      this.$refs.userDetailFrom.dialogUserDetail = true;
    },
    // 等级
    // onLevel(id,level){
    //   var userLevel = new Object();
    //   this.levelList.forEach(item=>{
    //     if(item.id == level){
    //       userLevel.gradeLevel = item.grade;
    //     }
    //   })
    //   userLevel.uid = id;
    //   userLevel.level = level;
    //   this.levelInfo = userLevel;
    //   this.levelVisible = true;
    // },
    // 积分余额
    editPoint(id) {
      this.uid = id;
      this.VisiblePoint = true;
    },
    // 积分余额
    submitPointForm: Debounce(function (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.PointValidateForm.uid = this.uid;
          this.loadingBtn = true;
          foundsApi(this.PointValidateForm)
            .then((res) => {
              this.$message.success('设置成功');
              this.loadingBtn = false;
              this.handlePointClose();
              this.getList();
            })
            .catch(() => {
              this.loadingBtn = false;
            });
        } else {
          return false;
        }
      });
    }),
    // 积分余额
    handlePointClose() {
      this.VisiblePoint = false;
      this.PointValidateForm = {
        integralType: 2,
        integralValue: 0,
        moneyType: 2,
        moneyValue: 0,
        uid: '',
      };
    },
    handleClose() {
      this.dialogVisible = false;
      this.$refs['dynamicValidateForm'].resetFields();
    },
    // 搜索
    userSearchs() {
      this.userFrom.page = 1;
      this.getList();
    },
    // 选择国家
    changeCountry() {
      if (this.userFrom.country === 'OTHER' || !this.userFrom.country) {
        this.selectedData = [];
        this.userFrom.province = '';
        this.userFrom.city = '';
        this.address = [];
      }
    },
    // 选择地址
    handleChange(value) {
      this.userFrom.province = value[0];
      this.userFrom.city = value[1];
    },
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.userFrom.dateLimit = e ? this.timeVal.join(',') : '';
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.userFrom.page = num ? num : this.userFrom.page;
      this.userFrom.nikename = encodeURIComponent(this.nikename);
      // this.userFrom.level = this.levelData.join(',')
      this.userFrom.groupId = this.groupData.join(',');
      this.userFrom.labelId = this.labelData.join(',');
      userListApi(this.userFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
      this.checkedCities = this.$cache.local.has('user_stroge')
        ? this.$cache.local.getJSON('user_stroge')
        : this.checkedCities;
      this.$set(this, 'card_select_show', false);
    },
    pageChange(page) {
      this.$selfUtil.changePageCoreRecordData(
        this.multipleSelectionAll,
        this.multipleSelection,
        this.tableData.data,
        (e) => {
          this.multipleSelectionAll = e;
        },
      );
      this.userFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.$selfUtil.changePageCoreRecordData(
        this.multipleSelectionAll,
        this.multipleSelection,
        this.tableData.data,
        (e) => {
          this.multipleSelectionAll = e;
        },
      );
      this.userFrom.limit = val;
      this.getList();
    },
    renderHeader(h) {
      return (
        <p>
          <span style="padding-right:5px;">操作</span>
        </p>
      );
      //<i class="el-icon-setting" onClick={()=>this.handleAddItem()}></i>
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
      this.$modal.loading('正在保存到本地，请稍候...');
      this.$cache.local.setJSON('user_stroge', this.checkedCities);
      setTimeout(this.$modal.closeLoading(), 1000);
    },
  },
};
</script>

<style scoped lang="scss">
.red {
  color: #ed4014;
}
// .timeBox{
//   width: 100%;
//   ::v-deep.el-form-item__content{
//     width: 87% !important;
//   }
// }
.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
  font-size: 12px;
}
.el-icon-arrow-down {
  font-size: 12px;
}
.text-right {
  text-align: right;
}
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 33.33%;
}
.selWidth {
  width: 100% !important;
}
.seachTiele {
  line-height: 30px;
}
.container {
  min-width: 821px;
  ::v-deepel-form-item {
    width: 100%;
  }
  ::v-deepel-form-item__content {
    width: 72%;
  }
}
.ivu-ml-8 {
  font-size: 12px;
  color: #1682e6;
}
.btn_bt {
  border-top: 1px dashed #ccc;
  padding-top: 20px;
}
.relative {
  position: relative;
}
.card_abs {
  position: absolute;
  padding-bottom: 15px;
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
::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #606266;
}
.userbtn {
  position: absolute;
  right: 0;
}
::v-deep.el-tag {
  color: #fff !important;
}
</style>
