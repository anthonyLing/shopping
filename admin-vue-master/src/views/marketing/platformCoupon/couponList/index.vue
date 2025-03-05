<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <div slot="header" class="clearfix" ref="headerBox">
        <div class="container" v-hasPermi="['platform:coupon:page:list']">
          <el-form size="small" inline>
            <el-form-item label="开启状态：">
              <el-select
                v-model="tableFrom.status"
                placeholder="请选择开启状态"
                @change="getList(1)"
                clearable
                class="filter-item selWidth mr20"
              >
                <el-option label="开启" :value="1"></el-option>
                <el-option label="关闭" :value="0"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="领取方式：">
              <el-select
                v-model="tableFrom.receiveType"
                clearable
                placeholder="请选择领取方式"
                @change="getList(1)"
                class="filter-item selWidth mr20"
              >
                <el-option label="手动领取" :value="1"></el-option>
                <el-option label="平台自动发放" :value="3"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="使用范围：">
              <el-select
                v-model="tableFrom.category"
                clearable
                placeholder="请选择使用范围"
                @change="getList(1)"
                class="filter-item selWidth mr20"
              >
                <el-option label="商品" :value="2"></el-option>
                <el-option label="通用" :value="3"></el-option>
                <el-option label="品类" :value="4"></el-option>
                <el-option label="品牌" :value="5"></el-option>
                <el-option label="跨店" :value="6"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="优惠券名称：">
              <el-input v-model.trim="name" placeholder="请输入优惠券名称" style="width: 350px" clearable>
                <el-button slot="append" icon="el-icon-search" size="small" @click="getList(1)" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <router-link v-hasPermi="['platform:coupon:add']" :to="{ path: '/marketing/platformCoupon/creatCoupon' }">
          <el-button size="small" type="primary" class="mr10">添加优惠券</el-button>
        </router-link>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="small"
        ref="multipleTable"
        :header-cell-style="{ fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="name" :show-overflow-tooltip="true" label="优惠券名称" min-width="150" />
        <el-table-column prop="category" label="使用范围" min-width="90">
          <template slot-scope="scope">
            <span>{{ scope.row.category | couponCategory }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="money" label="面值" min-width="90" />
        <el-table-column prop="minPrice" label="使用门槛" min-width="90" />
        <el-table-column prop="receiveType" label="领取方式" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.receiveType | receiveType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="receiveStartTime" label="领取日期" min-width="150">
          <template slot-scope="scope">
            <div v-if="scope.row.receiveEndTime">
              {{ scope.row.receiveStartTime }} -<br />
              {{ scope.row.receiveEndTime }}
            </div>
            <span v-else>不限时</span>
          </template>
        </el-table-column>
        <el-table-column prop="issuedNum" label="使用/发放数量" min-width="90">
          <template slot-scope="scope">
            <span>{{ scope.row.usedNum }}/{{ scope.row.issuedNum }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="isLimited" label="发布数量" min-width="90">
          <template slot-scope="scope">
            <span>{{ !scope.row.isLimited ? '不限量' : scope.row.total }}</span>
          </template>
        </el-table-column>
        <el-table-column label="是否开启" fixed="right" min-width="90">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['platform:coupon:switch'])"
              v-model="scope.row.status"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.status ? '开启' : '关闭' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="160" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              v-if="checkPermi(['platform:coupon:detail'])"
              type="text"
              size="small"
              @click="look(scope.row.id)"
              >详情</el-button
            >
            <el-divider direction="vertical"></el-divider>
            <el-dropdown trigger="click">
              <span class="el-dropdown-link"> 更多<i class="el-icon-arrow-down el-icon--right" /> </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-hasPermi="['platform:coupon:detail', 'platform:coupon:update']">
                  <router-link :to="{ path: '/marketing/platformCoupon/creatCoupon/' + scope.row.id }">
                    编辑
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item v-hasPermi="['platform:coupon:detail', 'platform:coupon:add']">
                  <router-link :to="{ path: '/marketing/platformCoupon/creatCoupon/' + scope.row.id + '/' + 1 }">
                    复制
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item @click.native="handleDelete(scope.row.id)" v-hasPermi="['platform:coupon:delete']">
                  删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
    <el-drawer title="优惠券详情" size="800px" :visible.sync="showInfo" direction="rtl" :before-close="handleClose">
      <el-tabs v-model="activeNames" v-if="showTab">
        <el-tab-pane label="基础信息" name="one"></el-tab-pane>
        <el-tab-pane :label="tabPaneName" name="two"></el-tab-pane>
      </el-tabs>
      <div class="px20" v-show="activeNames == 'one'">
        <div class="section">
          <div class="title">优惠券信息</div>
          <div class="cell">优惠券名称：{{ cell.name }}</div>
          <div class="cell">使用范围：{{ cell.category | couponCategory }}</div>
          <div class="cell">优惠券面值：{{ cell.money }}元</div>
          <div class="cell">使用门槛：{{ cell.minPrice }}元</div>
          <div class="cell">领取方式：{{ cell.receiveType | receiveType }}</div>
          <div class="cell">
            领取时间：{{ cell.isTimeReceive ? cell.receiveStartTime + ' - ' + cell.receiveEndTime : '不限时' }}
          </div>
          <div class="cell">
            使用有效期：{{
              cell.isFixedTime
                ? cell.useStartTime + ' - ' + cell.useEndTime + ' 有效'
                : '领取后' + cell.day + '天内有效'
            }}
          </div>
          <div class="cell">发布数量：{{ !cell.isLimited ? '不限量' : cell.total }}</div>
          <div class="cell">重复领取：{{ cell.isRepeated ? '可重复领取' : '不可重复领取' }}</div>
          <div class="cell">是否开启：{{ cell.status ? '开启' : '关闭' }}</div>
          <div class="title mt20">优惠券情况</div>
          <div class="cell">已发放数量：{{ cell.issuedNum }}</div>
          <div class="cell">已使用数量：{{ cell.usedNum }}</div>
          <template v-if="cell.category == 4 && cell.linkedDataStr">
            <div class="title mt20">优惠券商品品类</div>
            <div class="cell">适用品类：{{ cell.linkedDataStr }}</div>
          </template>
          <template v-if="cell.category == 5 && cell.linkedDataStr">
            <div class="title mt20">优惠券商品品牌</div>
            <div class="cell">适用品牌：{{ cell.linkedDataStr }}</div>
          </template>
        </div>
      </div>
      <div class="px20" v-if="cell.category == 2 && activeNames == 'two'">
        <el-table ref="tableList" v-loading="listLoading" :data="cell.productList" class="mt20" size="mini">
          <el-table-column prop="id" label="ID" width="55"> </el-table-column>
          <el-table-column label="商品图" min-width="80">
            <template slot-scope="scope">
              <div class="demo-image__preview">
                <el-image :src="scope.row.image" :preview-src-list="[scope.row.image]" />
              </div>
            </template>
          </el-table-column>
          <el-table-column :show-overflow-tooltip="true" prop="name" label="商品名称" min-width="150" />
          <el-table-column prop="price" label="售价" min-width="90" />
          <el-table-column prop="stock" label="库存" min-width="70" />
        </el-table>
      </div>
      <div class="px20" v-if="cell.category == 6 && activeNames == 'two'">
        <el-table ref="tableList" :data="cell.merchantList" class="mt20" size="mini">
          <el-table-column prop="categoryId" label="ID" min-width="55"> </el-table-column>
          <el-table-column label="商户头像" min-width="80">
            <template slot-scope="scope">
              <div class="demo-image__preview">
                <el-image :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" />
              </div>
            </template>
          </el-table-column>
          <el-table-column :show-overflow-tooltip="true" prop="name" label="商户名称" min-width="150" />
          <el-table-column label="商户类别" min-width="80">
            <template slot-scope="scope">
              <span>{{ scope.row.isSelf | selfTypeFilter }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>
    <el-dialog title="删除" :visible.sync="delShow" width="430px" :before-close="handleClose">
      <div>
        <el-radio v-model="loseEfficacyStatus" :label="0">已领取的优惠券仍可正常使用</el-radio><br />
        <el-radio v-model="loseEfficacyStatus" :label="1" class="mt20"
          >同步删除已领取的优惠券，已领取的优惠券展示为已失效</el-radio
        >
      </div>
      <div class="acea-row row-right mt20">
        <el-button size="mini" @click="delShow = false">取消</el-button>
        <el-button size="mini" type="danger" @click="confirmDelete()">删除</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { couponDeleteApi, couponInfoApi, couponStatusApi, platformCouponListApi } from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'platformCoupon',
  data() {
    return {
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        category: '',
        name: '',
        status: '',
        receiveType: '',
      },
      name: '',
      fromList: this.$constants.fromList,
      showInfo: false,
      delShow: false,
      cell: {},
      loseEfficacyStatus: 0,
      tabPaneName: '更多',
      activeNames: 'one',
      showTab: false,
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
  mounted() {
    if (checkPermi(['platform:coupon:page:list'])) this.getList(1);
    if (!localStorage.getItem('adminProductClassify')) this.$store.dispatch('product/getAdminProductClassify');
  },
  methods: {
    checkPermi,
    onchangeIsShow(row) {
      couponStatusApi(row.id)
        .then(async () => {
          this.$message.success('修改成功');
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
    look(id) {
      this.showTab = false;
      this.activeNames = 'one';
      couponInfoApi(id).then((res) => {
        if (res.category == 6) {
          this.tabPaneName = '商户列表';
          this.showTab = true;
        } else if (res.category == 2) {
          this.tabPaneName = '商品列表';
          this.showTab = true;
        }
        this.cell = res;
        this.showInfo = true;
      });
    },
    // 删除
    handleDelete(id) {
      this.rowId = id;
      this.delShow = true;
    },
    // 列表
    getList(num) {
      this.tableFrom.page = num ? num : this.tableFrom.page;
      this.tableFrom.name = encodeURIComponent(this.name);
      this.listLoading = true;
      platformCouponListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
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
    handleClose() {
      this.showInfo = false;
      this.delShow = false;
    },
    confirmDelete() {
      couponDeleteApi({
        id: this.rowId,
        loseEfficacyStatus: this.loseEfficacyStatus,
      }).then(() => {
        this.$message.success('删除成功');
        this.delShow = false;
        if (this.tableData.data.length === 1 && this.tableFrom.page > 1) this.tableFrom.page = this.tableFrom.page - 1;
        this.getList(1);
      });
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep.el-tabs--top {
  padding: 0 20px;
}
::v-deep.el-dropdown-link {
  color: #1890ff;
  font-size: 12px;
  cursor: pointer;
}
.selWidth {
  width: 350px !important;
  height: 40px;
}
.section {
  padding: 25px 0;
  border-bottom: 1px dashed #eeeeee;
  .title {
    padding-left: 10px;
    border-left: 3px solid #1890ff;
    font-size: 15px;
    line-height: 15px;
    font-weight: bold;
    color: #333;
  }
  .cell {
    font-size: 14px;
    color: #333;
    margin-top: 20px;
  }
}
::v-deep .el-tabs__content {
  display: none;
}
::v-deep .el-tabs--border-card {
  box-shadow: none;
}
</style>
