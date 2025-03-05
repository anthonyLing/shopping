<template>
  <div>
    <el-drawer :visible.sync="dialogVisible" :direction="direction" size="1200px" :before-close="handleClose">
      <div slot="title" class="demo-drawer_title">订单详情</div>
      <div class="demo-drawer__content" v-loading="loading">
        <template v-if="refundInfo.refundOrderNo">
          <div class="title">退款信息</div>
          <div class="acea-row">
            <div class="description-term">
              <span v-show="refundInfo.type === 1" class="iconfont icon-shipinhao mr5" style="color: #f6ae02"></span>
              <span v-show="refundInfo.type === 2" class="iconfont icon-miaosha mr5" style="color: #1890ff"></span>
              退款单号：{{ refundInfo.refundOrderNo }}
            </div>
            <div class="description-term" style="color: red">
              退款状态：
              <span>{{ refundInfo.refundStatus | refundStatusFilter }}</span>
            </div>
            <div class="description-term fontColor3">退款金额：{{ refundInfo.refundPrice }}</div>
            <div class="description-term fontColor3">退款数量：{{ refundInfo.applyRefundNum }}</div>
            <div class="description-term">扣除赠送积分：{{ refundInfo.refundGainIntegral }}</div>
            <div class="description-term">退回积分：{{ refundInfo.refundUseIntegral }}</div>
            <div class="description-term">退一级返佣：{{ refundInfo.refundFirstBrokerageFee }}</div>
            <div class="description-term">退二级返佣：{{ refundInfo.refundSecondBrokerageFee }}</div>
            <div class="description-term">退款时间：{{ refundInfo.refundTime }}</div>
            <div class="description-term">退款原因：{{ refundInfo.refundReasonWap }}</div>
            <div class="description-term">退款说明：{{ refundInfo.refundReasonWapExplain | filterEmpty }}</div>
            <div v-if="refundInfo.refundReason" class="description-term">
              拒绝退款说明：{{ refundInfo.refundReason | filterEmpty }}
            </div>
            <div class="description-term acea-row" style="width: 100%; display: flex">
              退款商品：
              <div class="demo-image__preview">
                <el-image class="" :src="refundInfo.image" :preview-src-list="[refundInfo.image]" />
              </div>
              <div class="ml20">
                <div>{{ refundInfo.productName }}</div>
                <div>{{ refundInfo.sku + 'x' + refundInfo.payNum }}</div>
              </div>
            </div>
          </div>
          <el-divider></el-divider>
        </template>
        <div class="description" v-if="orderDatalist">
          <div class="title">用户信息</div>
          <div class="acea-row">
            <div class="description-term">用户昵称：{{ orderDatalist.nikeName }} | {{ orderDatalist.uid }}</div>
            <div class="description-term">用户电话：{{ orderDatalist.phone }}</div>
          </div>
          <template v-if="parseFloat(orderDatalist.shippingType) === 1">
            <el-divider></el-divider>
            <div class="title">收货信息</div>
            <div class="acea-row">
              <div class="description-term">收货人：{{ orderDatalist.realName }}</div>
              <div class="description-term">收货电话：{{ orderDatalist.userPhone }}</div>
              <div class="description-term">收货地址：{{ orderDatalist.userAddress }}</div>
            </div>
          </template>
          <el-divider></el-divider>
          <div class="title">商品信息</div>
          <el-table size="mini" :data="orderDatalist.orderDetailList" style="width: 100%">
            <el-table-column label="商品图" width="80">
              <template slot-scope="scope">
                <div class="demo-image__preview">
                  <el-image :src="scope.row.image" :preview-src-list="[scope.row.image]" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="商品名称" prop="productName" min-width="200" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="price" label="商品售价" min-width="90" align="center" />
            <el-table-column prop="sku" label="商品规格" min-width="90" align="center" />
          </el-table>
          <el-divider></el-divider>
          <div class="title">订单信息</div>
          <div class="acea-row">
            <div class="description-term">
              <font v-show="orderDatalist.type === 1" color="#f6ae02">[视频号]</font>
              <font v-show="orderDatalist.type === 2" color="#1890ff">[秒杀]</font>
              订单号：{{ orderDatalist.orderNo }}
            </div>
            <div class="description-term" style="color: red">
              订单状态：
              <span v-if="orderDatalist.refundStatus === 3">已退款</span>
              <span v-else>{{ orderDatalist.status | orderStatusFilter }}</span>
            </div>
            <div class="description-term">支付状态：{{ orderDatalist.paid ? '已支付' : '未支付' }}</div>
            <div class="description-term">商品总数：{{ orderDatalist.totalNum }}</div>
            <div class="description-term">商品总价：{{ orderDatalist.proTotalPrice }}</div>
            <div class="description-term">支付邮费：{{ orderDatalist.payPostage }}</div>
            <div class="description-term">平台优惠金额：{{ orderDatalist.platCouponPrice }}</div>
            <div class="description-term">商户优惠金额：{{ orderDatalist.merCouponPrice }}</div>
            <div class="description-term">积分抵扣金额：{{ orderDatalist.integralPrice }}</div>
            <div class="description-term">实际支付：{{ orderDatalist.payPrice }}</div>
            <div class="description-term">支付方式：{{ orderDatalist.payType | payTypeFilter }}</div>
            <div class="description-term">
              配送方式：{{ parseFloat(orderDatalist.shippingType) === 1 ? '邮寄' : '门店自提' }}
            </div>
            <div class="description-term">赠送积分：{{ orderDatalist.gainIntegral }}</div>
            <div class="description-term">创建时间：{{ orderDatalist.createTime }}</div>
            <div class="description-term">支付时间：{{ orderDatalist.payTime }}</div>
            <div class="description-term">用户备注：{{ orderDatalist.userRemark | filterEmpty }}</div>
            <div class="description-term">商家备注：{{ orderDatalist.merchantRemark | filterEmpty }}</div>
          </div>
          <template v-if="parseFloat(orderDatalist.shippingType) === 1 && InvoiceList.length">
            <el-divider></el-divider>
            <div class="title">物流信息</div>
            <el-collapse v-model="activeNames" class="InvoiceList">
              <template v-for="item in InvoiceList">
                <el-collapse-item :title="item.expressName + '：' + item.trackingNumber" :name="item.id">
                  <template slot="title">
                    <div class="acea-row">
                      <div>{{ item.expressName + '：' + item.trackingNumber }}</div>
                      <el-button
                        type="text"
                        size="mini"
                        @click="openLogistics(item.id)"
                        style="margin-left: 5px; line-height: 1; height: auto"
                        v-hasPermi="['platform:order:logistics:info']"
                        >物流查询
                      </el-button>
                    </div>
                  </template>
                  <div class="acea-row">
                    <div class="wrapper-pro acea-row">
                      <div v-for="j in item.detailList" :key="j.id" class="wrapper-img">
                        <el-image :src="j.image"></el-image>
                      </div>
                    </div>
                  </div>
                  <div class="wrapper-num">共{{ item.totalNum }}件商品</div>
                </el-collapse-item>
              </template>
            </el-collapse>
          </template>
          <template v-if="parseFloat(orderDatalist.shippingType) === 2">
            <el-divider></el-divider>
            <div class="title">门店自提</div>
            <div class="acea-row">
              <div class="description-term">
                核销员名称 ：{{ orderDatalist.clerkName }} | {{ orderDatalist.clerkId }}
              </div>
            </div>
          </template>
        </div>
      </div>
    </el-drawer>
    <el-dialog v-if="orderDatalist" title="提示" :visible.sync="modal2" width="600px">
      <div class="logistics acea-row row-top">
        <div class="logistics_img"><img src="@/assets/imgs/expressi.jpg" /></div>
        <div class="logistics_cent">
          <span class="mb10">物流公司：{{ resultInfo.expName }}</span>
          <span>物流单号：{{ resultInfo.number }}</span>
          <span v-show="resultInfo.courierPhone">快递站：{{ resultInfo.courierPhone }}</span>
          <span v-show="resultInfo.courierPhone">快递员电话：{{ resultInfo.courierPhone }}</span>
        </div>
      </div>
      <div class="acea-row row-column-around trees-coadd">
        <div class="scollhide">
          <el-timeline :reverse="reverse">
            <el-timeline-item v-for="(item, i) in result" :key="i">
              <p class="time" v-text="item.time"></p>
              <p class="content" v-text="item.status"></p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="modal2 = false">关闭</el-button>
      </span>
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
  getLogisticsInfoApi,
  orderInvoiceListApi,
  orderDetailApi,
  getOrderInvoiceList,
  refundOrderDetailApi,
} from '@/api/order';

export default {
  name: 'OrderDetail',
  props: {
    orderNo: {
      type: String,
      default: 0,
    },
  },
  data() {
    return {
      activeNames: ['1'],
      direction: 'rtl',
      reverse: true,
      dialogVisible: false,
      orderDatalist: {},
      loading: false,
      modal2: false,
      result: [],
      resultInfo: {},
      InvoiceList: [],
      refundInfo: {},
    };
  },
  watch: {
    // orderData: {
    //   handler(newVal, oldVal) {
    //     this.orderDatalist = newVal;
    //   },
    //   deep: true, // 对象内部属性的监听，关键。
    // },
  },
  mounted() {},
  methods: {
    handleClose() {
      this.dialogVisible = false;
    },
    openLogistics(id) {
      this.getOrderData(id);
      this.modal2 = true;
    },
    // 获取订单退款信息
    getRefundOrderDetail(id) {
      refundOrderDetailApi(id).then(async (res) => {
        this.refundInfo = res;
      });
    },
    // 获取订单物流信息
    getOrderData(id) {
      getLogisticsInfoApi(id).then(async (res) => {
        this.resultInfo = res;
        this.result = res.list;
      });
    },
    // 获取订单发货单列表
    getOrderInvoiceList(id) {
      orderInvoiceListApi(id)
        .then((res) => {
          this.InvoiceList = res;
        })
        .catch(() => {});
    },
    getDetail(id) {
      this.loading = true;
      orderDetailApi({ orderNo: id })
        .then((res) => {
          this.orderDatalist = res;
          this.loading = false;
        })
        .catch(() => {
          this.orderDatalist = null;
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped lang="scss">
.InvoiceList {
  ::v-deep.el-collapse-item__header {
    font-size: 12px;
    color: #606266;
  }
}

.wrapper {
  background-color: #fff;
  margin-top: 7px;
  padding: 10px 12px;
  &-num {
    font-size: 10px;
    color: #999999;
  }

  &-title {
    color: #666666;
    font-size: 12px;
  }

  &-img {
    width: 60px;
    height: 60px;
    margin-right: 10px;
    border-radius: 7px;
    overflow: hidden;
    margin-bottom: 10px;

    image {
      width: 100%;
      height: 100%;
    }

    &:nth-child(5n) {
      margin-right: 0;
    }
  }
}

.title {
  font-size: 36px;
}

.demo-drawer__content {
  padding: 0 30px;
}

.demo-image__preview {
  display: inline-block;
  .el-image {
    width: 50px;
    height: 50px;
  }
}

.logistics {
  align-items: center;
  padding: 10px 0px;
  .logistics_img {
    width: 45px;
    height: 45px;
    margin-right: 12px;
    img {
      width: 100%;
      height: 100%;
    }
  }
  .logistics_cent {
    span {
      display: block;
      font-size: 12px;
    }
  }
}

.trees-coadd {
  width: 100%;
  height: 400px;
  border-radius: 4px;
  overflow: hidden;
  .scollhide {
    width: 100%;
    height: 100%;
    overflow: auto;
    margin-left: 18px;
    padding: 10px 0 10px 0;
    box-sizing: border-box;
    .content {
      font-size: 12px;
    }

    .time {
      font-size: 12px;
      color: #2d8cf0;
    }
  }
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
    font-size: 12px;
    color: #606266;
  }
  ::v-deep .el-divider--horizontal {
    margin: 12px 0 !important;
  }
}
</style>
