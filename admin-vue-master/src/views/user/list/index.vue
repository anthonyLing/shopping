<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container" v-hasPermi="['platform:user:page:list']">
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
                    <el-input v-model.trim="keywords" placeholder="请输入用户昵称" clearable class="selWidth" />
                  </el-form-item>
                </el-col>
                <el-col v-bind="grid">
                  <el-form-item label="手机号：">
                    <el-input v-model.trim="phone" placeholder="请输入手机号" clearable class="selWidth" />
                  </el-form-item>
                </el-col>
              </el-col>
              <template v-if="collapse">
                <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                  <el-col v-bind="grid">
                    <el-form-item label="用户标签：">
                      <el-select
                        v-model.trim="labelData"
                        placeholder="请选择"
                        class="selWidth"
                        clearable
                        filterable
                        multiple
                      >
                        <el-option
                          :value="item.id"
                          v-for="(item, index) in labelLists"
                          :key="index"
                          :label="item.name"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col v-bind="grid">
                    <el-form-item label="消费情况：">
                      <el-select v-model="userFrom.payCount" placeholder="请选择" class="selWidth" clearable>
                        <el-option value="" label="全部"></el-option>
                        <el-option value="0" label="0"></el-option>
                        <el-option value="1" label="1+"></el-option>
                        <el-option value="2" label="2+"></el-option>
                        <el-option value="3" label="3+"></el-option>
                        <el-option value="4" label="4+"></el-option>
                        <el-option value="5" label="5+"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-col>

                <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
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
                    <el-form-item label="身份：">
                      <el-radio-group v-model="userFrom.isPromoter" type="button" class="selWidth">
                        <el-radio-button label="">
                          <span>全部</span>
                        </el-radio-button>
                        <el-radio-button label="1">
                          <span>推广员</span>
                        </el-radio-button>
                        <el-radio-button label="0">
                          <span>普通用户</span>
                        </el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                </el-col>
                <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                  <el-form-item label="时间选择：" class="timeBox">
                    <el-date-picker
                      v-model="timeVal"
                      align="right"
                      unlink-panels
                      value-format="yyyy-MM-dd"
                      format="yyyy-MM-dd"
                      size="small"
                      type="daterange"
                      placement="bottom-end"
                      placeholder="自定义时间"
                      class="selWidth"
                      :picker-options="pickerOptions"
                      @change="onchangeTime"
                    />
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
                    >搜索</el-button
                  >
                  <el-button class="ResetSearch mr10" @click="reset('userFrom')" size="small">重置</el-button>
                  <a class="ivu-ml-8" @click="collapse = !collapse">
                    <template v-if="!collapse"> 展开 <i class="el-icon-arrow-down"></i> </template>
                    <template v-else> 收起 <i class="el-icon-arrow-up"></i> </template>
                  </a>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <div class="btn_bt">
          <el-button
            :disabled="!multipleSelectionAll.length"
            size="small"
            v-hasPermi="['platform:user:tag']"
            @click="setBatch('label')"
            class="mr10"
            >批量设置标签</el-button
          >
          <el-button
            v-hasPermi="['platform:coupon:can:send:list', 'platform:coupon:batch:send']"
            :disabled="!multipleSelectionAll.length"
            size="small"
            @click="sendCoupon"
            >发送优惠券</el-button
          >
        </div>
      </div>
      <el-table
        ref="table"
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        @selection-change="handleSelectionChange"
        @select-all="selectAll"
        @select="selectOne"
        highlight-current-row
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="身份：">
                <span>{{ props.row.isPromoter | filterIsPromoter }}</span>
              </el-form-item>
              <el-form-item label="首次访问：">
                <span>{{ props.row.createTime | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="近次访问：">
                <span>{{ props.row.lastLoginTime | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="标签：">
                <span>{{ props.row.tagId | tagFilter }}</span>
              </el-form-item>
              <el-form-item label="地址：">
                <span>{{ (props.row.province + props.row.city) | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="备注：" style="width: 100%; display: flex; margin-right: 10px">
                <span>{{ props.row.mark | filterEmpty }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column prop="id" label="ID" min-width="80" v-if="checkedCities.includes('ID')" />
        <el-table-column label="头像" min-width="80" v-if="checkedCities.includes('头像')">
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
        <el-table-column label="姓名" min-width="160" v-if="checkedCities.includes('姓名')">
          <template slot-scope="scope">
            <span :class="scope.row.isLogoff == true ? 'red' : ''"
              >{{ scope.row.nickname | filterEmpty }} | {{ scope.row.sex | sexFilter }}</span
            >
            <span :class="scope.row.isLogoff == true ? 'red' : ''" v-if="scope.row.isLogoff == true">|</span>
            <span v-if="scope.row.isLogoff == true" class="red">(已注销)</span>
          </template>
        </el-table-column>
        <el-table-column label="推荐人" min-width="130" v-if="checkedCities.includes('推荐人')">
          <template slot-scope="scope">
            <span>{{ scope.row.spreadName | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" min-width="100" v-if="checkedCities.includes('手机号')">
          <template slot-scope="scope">
            <span>{{ scope.row.phone | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nowMoney" label="余额" min-width="100" v-if="checkedCities.includes('余额')" />
        <el-table-column prop="integral" label="积分" min-width="100" v-if="checkedCities.includes('积分')" />
        <el-table-column prop="registerType" label="注册类型" min-width="100" v-if="checkedCities.includes('注册类型')">
          <template slot-scope="scope">
            <el-tag :color="filterRegisterType(scope.row.registerType)">{{
              scope.row.registerType | registerTypeFilter
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="130" fixed="right" align="center" :render-header="renderHeader">
          <template slot-scope="scope">
            <el-button type="text" @click="editUser(scope.row)" size="small" v-hasPermi="['platform:user:update']"
              >编辑</el-button
            >
            <el-divider direction="vertical"></el-divider>
            <el-dropdown
              trigger="click"
              v-hasPermi="[
                'platform:user:detail',
                'platform:user:operate:integer',
                'platform:user:operate:balance',
                'platform:user:tag',
                'platform:retail:store:update:user:spread',
                'platform:retail:store:clean:user:spread',
              ]"
            >
              <span class="el-dropdown-link"> 更多<i class="el-icon-arrow-down el-icon--right" /> </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="onDetails(scope.row.id)" v-if="checkPermi(['platform:user:detail'])"
                  >用户详情</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="editPoint(scope.row, 'integral')"
                  v-if="checkPermi(['platform:user:operate:integer'])"
                  >修改积分</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="editPoint(scope.row, 'balance')"
                  v-if="checkPermi(['platform:user:operate:balance'])"
                  >修改余额</el-dropdown-item
                >
                <el-dropdown-item @click.native="setBatch('label', scope.row)" v-if="checkPermi(['platform:user:tag'])"
                  >设置标签</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="setExtension(scope.row)"
                  v-if="checkPermi(['platform:retail:store:update:user:spread'])"
                  >修改上级推广人</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="clearSpread(scope.row)"
                  v-if="
                    scope.row.spreadUid &&
                    scope.row.spreadUid > 0 &&
                    checkPermi(['platform:retail:store:clean:user:spread'])
                  "
                  >清除上级推广人</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
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
    <!--修改推广人-->
    <el-dialog title="修改推广人" :visible.sync="extensionVisible" width="500px" :before-close="handleCloseExtension">
      <el-form
        class="formExtension mt20"
        ref="formExtension"
        :model="formExtension"
        :rules="ruleInline"
        label-width="120px"
        @submit.native.prevent
        v-loading="loading"
      >
        <el-form-item label="选择用户:" prop="image">
          <div class="upLoadPicBox" @click="modalPicTap">
            <div v-if="selectedUser.image">
              <el-row>
                <el-col :span="12"><img class="pictrue" :src="selectedUser.image" /></el-col>
                <el-col :span="12"
                  ><span>{{ selectedUser.nickname }}</span></el-col
                >
              </el-row>
            </div>
            <div v-else class="upLoad">
              <i class="el-icon-camera cameraIconfont" />
            </div>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onSubExtension('formExtension')">确 定</el-button>
      </span>
    </el-dialog>
    <!--用户列表-->
    <el-dialog title="用户列表" :visible.sync="userVisible" width="900px">
      <user-list v-if="userVisible" @getTemplateRow="getTemplateRow"></user-list>
      <span slot="footer" class="dialog-footer">
        <el-button @click="userVisible = false">取 消</el-button>
        <el-button type="primary" @click="userVisible = false">确 定</el-button>
      </span>
    </el-dialog>
    <!--批量设置-->
    <el-dialog title="设置" :visible.sync="dialogVisible" width="500px" :before-close="handleClose">
      <el-form
        :model="dynamicValidateForm"
        ref="dynamicValidateForm"
        label-width="100px"
        class="demo-dynamic"
        v-loading="loading"
      >
        <el-form-item
          prop="groupId"
          label="用户标签"
          :rules="[{ required: true, message: '请选择用户标签', trigger: 'change' }]"
        >
          <el-select
            v-model="dynamicValidateForm.groupId"
            placeholder="请选择标签"
            style="width: 80%"
            filterable
            multiple
          >
            <el-option :value="item.id" v-for="(item, index) in labelLists" :key="index" :label="item.name"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button
          type="primary"
          v-debounceClick="
            () => {
              submitForm('dynamicValidateForm');
            }
          "
          >确 定</el-button
        >
      </span>
    </el-dialog>
    <!--编辑-->
    <el-dialog title="编辑" :visible.sync="visible" width="600px">
      <edit-from v-if="visible" :userInfo="userInfo" @resetForm="resetForm"></edit-from>
    </el-dialog>
    <!--积分余额-->
    <el-dialog
      :title="type === 'integral' ? '积分' : '余额'"
      :visible.sync="VisiblePoint"
      width="500px"
      :close-on-click-modal="false"
      :before-close="handlePointClose"
    >
      <el-form
        :model="PointValidateForm"
        ref="PointValidateForm"
        label-width="100px"
        class="demo-dynamic"
        v-loading="loadingPoint"
      >
        <el-form-item :label="type === 'integral' ? '积分' : '余额'" required>
          <span>{{ type === 'integral' ? userInfo.integral : userInfo.nowMoney }}</span>
        </el-form-item>
        <template v-if="type === 'integral'">
          <el-form-item label="修改积分" required>
            <el-radio-group v-model="PointValidateForm.operateType">
              <el-radio label="add">增加</el-radio>
              <el-radio label="sub">减少</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="积分" required>
            <el-input-number
              type="text"
              step-strictly
              v-model.trim="PointValidateForm.integral"
              :min="0"
              :max="999999"
            ></el-input-number>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="修改余额" required>
            <el-radio-group v-model="PointValidateForm.operateType">
              <el-radio label="add">增加</el-radio>
              <el-radio label="sub">减少</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="余额" required>
            <el-input-number
              type="text"
              v-model.trim="PointValidateForm.money"
              :precision="2"
              :step="0.1"
              :min="0"
              :max="999999"
            ></el-input-number>
          </el-form-item>
        </template>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handlePointClose">取 消</el-button>
        <el-button type="primary" :loading="loadingBtn" @click="submitPointForm('PointValidateForm')">确 定</el-button>
      </span>
    </el-dialog>
    <!--用户详情-->
    <detail-user ref="userDetailFrom"></detail-user>
    <!-- 选择优惠券 -->
    <el-dialog v-if="visibleCoupon" title="优惠券列表" :visible.sync="visibleCoupon" width="1000px">
      <coupon-List v-if="visibleCoupon" ref="couponList" :checkedIds="checkedIds" @sendSuccess="sendSuccess" />
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
import {
  userListApi,
  tagAllListApi,
  tagPiApi,
  foundsApi,
  updateSpreadApi,
  updatePhoneApi,
  balanceApi,
} from '@/api/user';
import { spreadClearApi } from '@/api/distribution';
import detailUser from '@/components/detailUser';
import editFrom from './edit';
import levelEdit from './level';
import userList from '@/components/userList';
import couponList from '../components/couponList';
import * as logistics from '@/api/logistics.js';
import Cookies from 'js-cookie';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';
export default {
  name: 'UserIndex',
  components: { editFrom, userList, levelEdit, detailUser, couponList },
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
      formExtension: {
        spreadUid: '',
        userId: '',
      },
      selectedUser: {
        image: '',
        nickname: '',
      },
      ruleInline: {},
      extensionVisible: false,
      userVisible: false,
      levelInfo: '',
      pickerOptions: this.$timeOptions,
      loadingBtn: false,
      PointValidateForm: {
        integral: null,
        operateType: 'add',
        uid: '',
        money: null,
      },
      loadingPoint: false,
      VisiblePoint: false,
      visible: false,
      dialogVisible: false,
      levelVisible: false,
      labelData: [],
      selData: [],
      keywords: '',
      labelPosition: 'right',
      collapse: false,
      listLoading: true,
      tableData: {
        data: [],
        total: 0,
      },
      userFrom: {
        tagIds: '',
        sex: '',
        registerType: '',
        payCount: '',
        accessType: 0,
        dateLimit: '',
        nikename: '',
        phone: '',
        page: 1,
        limit: 15,
      },
      phone: '',
      grid: {
        xl: 8,
        lg: 12,
        md: 12,
        sm: 24,
        xs: 24,
      },
      labelLists: [],
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
      multipleSelectionAll: [],
      idKey: 'uid',
      card_select_show: false,
      checkAll: false,
      checkedCities: ['ID', '头像', '姓名', '注册类型', '推荐人', '手机号', '余额', '积分'],
      columnData: ['ID', '头像', '姓名', '注册类型', '推荐人', '手机号', '余额', '积分'],
      isIndeterminate: true,
      type: 'integral',
      userInfo: {},
      checkedIds: [],
      selectAllPage: false,
      visibleCoupon: false, //发送优惠券显示
    };
  },
  activated() {
    this.userFrom.nikename = '';
    this.getList(1);
  },
  mounted() {
    if (checkPermi(['platform:user:page:list'])) this.getList(1);
    if (checkPermi(['platform:user:tag:all:list'])) this.getTagList();
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
    // 清除
    clearSpread(row) {
      this.$modalSure('解除【' + row.nickname + '】的上级推广人吗').then(() => {
        spreadClearApi(row.id).then((res) => {
          this.$message.success('清除成功');
          this.getList();
        });
      });
    },
    onSubExtension(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          updateSpreadApi(this.formExtension).then((res) => {
            this.$message.success('设置成功');
            this.extensionVisible = false;
            this.getList();
          });
        } else {
          return false;
        }
      });
    },
    getTemplateRow(row) {
      this.formExtension.spreadUid = row.id;
      this.selectedUser = {
        image: row.avatar,
        nickname: row.nickname,
      };
    },
    setExtension(row) {
      this.formExtension = {
        spreadUid: '',
        userId: row.id,
      };
      this.selectedUser = {
        image: '',
        nickname: '',
      };
      this.extensionVisible = true;
    },
    handleCloseExtension() {
      this.extensionVisible = false;
      this.selectedUser = {
        image: '',
        nickname: '',
      };
    },
    modalPicTap() {
      this.userVisible = true;
    },
    resetForm() {
      this.visible = false;
    },
    reset(formName) {
      this.userFrom = {
        tagIds: '',
        sex: '',
        isPromoter: '',
        payCount: '',
        accessType: 0,
        dateLimit: '',
        province: '',
        city: '',
        page: 1,
        limit: 15,
        nikename: '',
      };
      this.keywords = '';
      this.phone = '';
      this.labelData = [];
      this.timeVal = [];
      this.getList();
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
    // 积分余额
    editPoint(row, type) {
      this.uid = row.id;
      this.type = type;
      this.userInfo = row;
      this.VisiblePoint = true;
    },
    // 积分余额
    submitPointForm: Debounce(function (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.PointValidateForm.uid = this.uid;
          this.loadingBtn = true;
          if (this.type === 'integral') {
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
            balanceApi(this.PointValidateForm)
              .then((res) => {
                this.$message.success('设置成功');
                this.loadingBtn = false;
                this.handlePointClose();
                this.getList();
              })
              .catch(() => {
                this.loadingBtn = false;
              });
          }
        } else {
          return false;
        }
      });
    }),
    // 积分余额
    handlePointClose() {
      this.VisiblePoint = false;
      this.PointValidateForm = {
        integral: null,
        operateType: 'add',
        uid: '',
        money: null,
      };
    },
    editUser(row) {
      this.userInfo = row;
      this.visible = true;
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          tagPiApi({ tagIds: this.dynamicValidateForm.groupId.join(','), ids: this.checkedIds.toString() })
            .then((res) => {
              this.$message.success('设置成功');
              this.loading = false;
              this.handleClose();
              this.getList();
              this.$refs.table.clearSelection();
            })
            .catch(() => {
              this.loading = false;
            });
        } else {
          return false;
        }
      });
    },
    setBatch(name, row) {
      this.batchName = name;
      if (row) {
        this.checkedIds = row.id;
        this.dynamicValidateForm.groupId = row.tagId ? row.tagId.split(',').map(Number) : [];
      } else {
        this.dynamicValidateForm.groupId = '';
      }
      if (this.multipleSelectionAll.length === 0 && !row) return this.$message.warning('请选择要设置的用户');
      this.dialogVisible = true;
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
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.userFrom.dateLimit = e ? this.timeVal.join(',') : '';
    },
    //标签列表
    getTagList() {
      tagAllListApi().then((res) => {
        this.labelLists = res;
        localStorage.setItem('tagAllList', JSON.stringify(res));
      });
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.userFrom.page = num ? num : this.userFrom.page;
      this.userFrom.tagIds = this.labelData.join(',');
      this.userFrom.nikename = encodeURIComponent(this.keywords);
      this.userFrom.phone = encodeURIComponent(this.phone);
      userListApi(this.userFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          if (this.selectAllPage == 'all') {
            this.$nextTick(() => {
              this.$refs.tableList.toggleAllSelection();
            });
          }
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
    // 设置选中的方法
    handleSelectionChange(val) {
      this.multipleSelectionAll = val;
      const data = [];
      this.multipleSelectionAll.map((item) => {
        data.push(item.id);
      });
      this.checkedIds = data;
    },
    selectAll(data) {
      let id = data.map((i, index) => {
        return i.id;
      });
      this.checkedIds = Array.from(new Set([...this.checkedIds, ...id]));
    },
    selectOne(data, row) {
      let id = data.map((i, index) => {
        return i.id;
      });
      let index = this.checkedIds.findIndex((e) => {
        return e == row.id;
      });
      this.checkedIds.splice(index, 1);
      this.checkedIds = Array.from(new Set([...this.checkedIds, ...id]));
    },
    pageChange(page) {
      this.userFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.userFrom.limit = val;
      this.getList(1);
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure().then(() => {
        productDeleteApi(id).then(() => {
          this.$message.success('删除成功');
          this.getList(1);
        });
      });
    },
    onchangeIsShow(row) {
      row.isShow
        ? putOnShellApi(row.id)
            .then(() => {
              this.$message.success('上架成功');
              this.getList();
            })
            .catch(() => {
              row.isShow = !row.isShow;
            })
        : offShellApi(row.id)
            .then(() => {
              this.$message.success('下架成功');
              this.getList();
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
      this.card_select_show = false;
      this.$modal.loading('正在保存到本地，请稍候...');
      this.$cache.local.setJSON('user_stroge', this.checkedCities);
      setTimeout(this.$modal.closeLoading(), 1000);
    },
    //发送优惠券
    sendCoupon() {
      if (!this.checkedIds.length) {
        this.$message.warning('请选择用户');
      } else {
        this.visibleCoupon = true;
      }
    },
    sendSuccess() {
      this.visibleCoupon = false;
    },
  },
};
</script>

<style scoped lang="scss">
.red {
  color: #ed4014;
}
/*.timeBox{*/
/*width: 100%;*/
/*::v-deep.el-form-item__content{*/
/*width: 87% !important;*/
/*}*/
/*}*/
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
