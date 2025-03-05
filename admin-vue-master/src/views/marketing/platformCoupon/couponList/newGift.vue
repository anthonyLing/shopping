<template>
  <div class="divBox">
    <div class="container_box">
      <el-card class="box-card flex-1" :body-style="{ padding: '0 20px 20px' }">
        <div class="acea-row">
          <div class="flex-1">
            <el-tabs v-if="tabList.length > 0" v-model="currentTab" @tab-click="tabChange">
              <el-tab-pane v-for="(item, index) in tabList" :key="index" :name="item.value" :label="item.title" />
            </el-tabs>
            <el-form
              ref="formValidate"
              class="formValidate"
              :model="formValidate"
              label-width="73px"
              @submit.native.prevent
            >
              <div v-if="currentTab == 1 && checkPermi(['platform:marketing:activity:new:people:present:config'])">
                <h2 class="form_label">规则设置</h2>
                <el-form-item label="活动状态:">
                  <el-switch
                    :width="56"
                    v-model="formValidate.newPeopleSwitch"
                    active-text="开启"
                    inactive-text="关闭"
                  />
                  <p class="desc mt10">
                    活动开启商城新注册用户可获得下方设置的优惠券福利，活动关闭之后新注册的用户不会赠送新人福利
                  </p>
                </el-form-item>
                <el-form-item label="活动对象:">
                  <span>开启活动期间所有注册商城的用户</span>
                </el-form-item>
                <el-form-item label="优惠券:">
                  <el-button type="text" class="mr10" @click="addCoupon">添加优惠券</el-button>
                  <div class="grid_box" v-if="couponList.length">
                    <div
                      class="coupon_item acea-row"
                      :class="item.isDel ? 'del' : 'show'"
                      v-for="(item, index) in couponList"
                      :key="index"
                    >
                      <i class="close el-icon-error" @click="delItem(item.id, index)"></i>
                      <div class="_left">
                        <div>
                          <span class="_symbol">¥</span>
                          <span class="_price">{{ item.money }}</span>
                        </div>
                        <span class="_type">满{{ item.minPrice }}可用</span>
                      </div>
                      <div class="_right">
                        <div v-if="item.isDel">优惠券已删除</div>
                        <div v-else>
                          <div v-if="item.isFixedTime">
                            {{ getTime(item.useStartTime) + ' ~ ' + getTime(item.useEndTime) + '可用' }}
                          </div>
                          <div v-else>{{ '领取后' + item.day + '天内可用' }}</div>
                        </div>
                        <span class="_sales">{{ !item.isLimited ? '不限量' : '剩余' + item.lastTotal + '张' }}</span>
                      </div>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button
                    size="small"
                    type="primary"
                    class="mr10"
                    v-hasPermi="['platform:marketing:activity:new:people:present:edit']"
                    v-debounceClick="
                      () => {
                        confirmEdit(1);
                      }
                    "
                    :disabled="!couponList.length"
                    >保存
                  </el-button>
                </el-form-item>
              </div>
              <div v-if="currentTab == 2 && checkPermi(['platform:marketing:activity:birthday:present:config'])">
                <h2 class="form_label">规则设置</h2>
                <el-form-item label="活动状态:">
                  <el-switch
                    :width="56"
                    v-model="formValidate.birthdaySwitch"
                    active-text="开启"
                    inactive-text="关闭"
                  />
                  <p class="desc mt10">活动开启商城用户可获得下方设置的生日福利，活动关闭之后用户生日不会赠送福利。</p>
                </el-form-item>
                <el-form-item label="活动对象:">
                  <span>商城已经设置生日的用户</span>
                </el-form-item>
                <el-form-item label="优惠券:">
                  <el-button type="text" class="mr10" @click="addCoupon">添加优惠券</el-button>
                  <div class="grid_box" v-if="couponList.length">
                    <div
                      v-for="(item, index) in couponList"
                      :key="index"
                      class="coupon_item acea-row"
                      :class="item.isDel ? 'del' : 'show'"
                    >
                      <i class="close el-icon-error" @click="delItem(item.id, index)"></i>
                      <div class="_left">
                        <div>
                          <span class="_symbol">¥</span>
                          <span class="_price">{{ item.money }}</span>
                        </div>
                        <span class="_type">满{{ item.minPrice }}可用</span>
                      </div>
                      <div class="_right">
                        <div v-if="item.isDel">优惠券已删除</div>
                        <div v-else>
                          <div v-if="item.isFixedTime">
                            {{ getTime(item.useStartTime) + ' ~ ' + getTime(item.useEndTime) + '可用' }}
                          </div>
                          <div v-else>{{ '领取后' + item.day + '天内可用' }}</div>
                        </div>
                        <span class="_sales">{{ !item.isLimited ? '不限量' : '剩余' + item.lastTotal + '张' }}</span>
                      </div>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button
                    size="small"
                    type="primary"
                    class="mr10"
                    v-hasPermi="['platform:marketing:activity:birthday:present:edit']"
                    v-debounceClick="
                      () => {
                        confirmEdit(2);
                      }
                    "
                    :disabled="!couponList.length"
                    >保存
                  </el-button>
                </el-form-item>
              </div>
            </el-form>
          </div>
          <div v-if="currentTab === '1'" class="proview acea-row row-center-wrapper">
            <img :src="proviewImg" alt="" />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>
<script>
import {
  canSendListApi,
  newPeoplePresentEditApi,
  newPeoplePresentConfigApi,
  birthdayPresentConfigApi,
  birthdayPresentEditApi,
} from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'newGift',
  data() {
    return {
      currentTab: '1',
      tabList: [
        { value: '1', title: '新人礼' },
        { value: '2', title: '生日有礼' },
      ],
      formValidate: {
        newPeopleSwitch: false,
        birthdaySwitch: false,
        couponIdList: [],
      },
      visibleCoupon: false,
      listLoading: true,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 10,
        keywords: '',
      },
      multipleSelection: [],
      couponList: [],
      keyNum: 0,
      proviewImg: require('@/assets/imgs/new_gift_modal.png'),
    };
  },
  filters: {
    receiveType(val) {
      const typeObj = {
        1: '手动领取',
        2: '商品赠送券',
        3: '平台自动发放',
      };
      return typeObj[val];
    },
  },
  watch: {
    currentTab: {
      handler: function (val) {
        this.getConfig(val);
      },
      immediate: false,
      deep: true,
    },
  },
  mounted() {
    if (checkPermi(['platform:marketing:activity:new:people:present:config'])) this.getConfig(1);
  },
  methods: {
    checkPermi,
    getTime(time) {
      let reg = new RegExp('-', 'g'); //g代表全部
      return time.split(' ')[0].replace(reg, '.').substring(2, 10);
    },
    tabChange(e) {
      this.currentTab = e.name;
    },
    //添加优惠券
    addCoupon() {
      const _this = this;
      _this.formValidate.couponIdList = [];
      this.$modalCoupon('wu', (this.keyNum += 1), this.couponList, function (row) {
        _this.couponList = row;
        _this.couponList.forEach((item) => {
          _this.formValidate.couponIdList.push(item.id);
        });
      });
    },
    // 优惠券详情
    getConfig(val) {
      this.formValidate.couponIdList = [];
      if (val == 1) {
        newPeoplePresentConfigApi().then((res) => {
          this.$set(this, 'couponList', res.couponList ? res.couponList : []);
          this.formValidate.newPeopleSwitch = res.newPeopleSwitch;
          this.couponList.forEach((item) => {
            this.formValidate.couponIdList.push(item.id);
          });
        });
      } else {
        birthdayPresentConfigApi().then((res) => {
          this.$set(this, 'couponList', res.couponList ? res.couponList : []);
          this.formValidate.birthdaySwitch = res.birthdaySwitch;
          this.couponList.forEach((item) => {
            this.formValidate.couponIdList.push(item.id);
          });
        });
      }
    },
    rowStyle: function ({ row, rowIndex }) {
      Object.defineProperty(row, 'rowIndex', { value: rowIndex, writable: true, enumerable: false });
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      canSendListApi(this.tableFrom)
        .then((res) => {
          this.visibleCoupon = true;
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
      this.getList('');
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList('');
    },
    // 保存
    confirmEdit(val) {
      if (this.couponList.filter((item) => item.isDel).length > 0) {
        this.$confirm('当前活动中存在已删除优惠券，提交后会自动删除。', '提示').then((result) => {
          this.formValidate.couponIdList = [];
          this.couponList.forEach((item) => {
            if (!item.isDel) {
              this.formValidate.couponIdList.push(item.id);
            }
          });
          this.handlerEdit(val);
        });
      } else {
        this.handlerEdit(val);
      }
    },
    //保存提交数据
    handlerEdit(val) {
      if (val === 1) {
        delete this.formValidate.birthdaySwitch;
        newPeoplePresentEditApi(this.formValidate).then((res) => {
          this.$message.success('保存成功');
          this.getConfig(val);
        });
      } else {
        delete this.formValidate.newPeopleSwitch;
        birthdayPresentEditApi(this.formValidate).then((res) => {
          this.$message.success('保存成功');
          this.getConfig(val);
        });
      }
    },
    // 删除
    delItem(id, index) {
      let i = this.formValidate.couponIdList.findIndex((item) => item == id);
      this.couponList.splice(index, 1);
      this.formValidate.couponIdList.splice(i, 1);
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep .form_label {
  border: none !important;
}
.proview {
  width: 500px;
  height: 800px;
  border-left: 1px solid #f5f5f5;
  img {
    width: 375px;
  }
}

.flex-1 {
  flex: 1;
}

.form_label {
  font-size: 16px;
  padding-bottom: 16px;
  font-weight: bolder;
  margin-top: 20px;
  margin-bottom: 16px;
  color: rgb(38, 38, 38);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

::v-deep .el-tabs__item {
  height: 52px !important;
  line-height: 52px !important;
}

.suffix_text {
  color: #333;
}
.grid_box {
  display: flex;
  grid-template-rows: auto;
  grid-template-columns: repeat(4, 1fr);
  grid-gap: 20px;
  flex-wrap: wrap;
}
.show {
  background-color: #fde2db;
}
.del {
  background-color: #f8f8f8;
}
.coupon_item {
  width: 310px;
  height: 75px;
  background-image: radial-gradient(circle at 60px top, #fff, #fff 7px, transparent 7px),
    radial-gradient(circle at 60px bottom, #fff, #fff 7px, transparent 7px);
  border-radius: 4px;
  position: relative;
  .close {
    font-size: 20px;
    color: #999;
    position: absolute;
    right: -5px;
    top: -5px;
    cursor: pointer;
  }

  ._left {
    width: 116px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #e93323;
    border-right: 1px dashed #fff;

    ._symbol {
      font-size: 16px;
    }

    ._price {
      font-size: 30px;
      font-weight: bol;
    }

    ._type {
      font-size: 12px;
      line-height: 14px;
    }
  }

  ._right {
    flex: 1;
    padding: 10px 20px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    line-height: 14px;
    color: #666;

    ._sales {
      color: #999;
    }
  }
}
</style>
