<template>
  <div class="divBox">
    <el-card v-if="isCopy">
      <div class="line-ht mb15">
        生成的商品默认是没有上架的，请手动上架商品！
        <span v-if="copyConfig.copyType && copyConfig.copyType == 1"
          >您当前剩余{{ copyConfig.copyNum }}条采集次数。
          <!--<router-link :to="{ path: '/operation/systemSms/pay?type=copy' }">-->
          <!--<span style="color: #1890ff">增加采集次数</span>-->
          <!--</router-link>-->
        </span>
        <el-link
          v-if="copyConfig.copyType && copyConfig.copyType != 1"
          type="primary"
          :underline="false"
          href="https://help.crmeb.net/crmeb_java/2103903"
          target="_blank"
          >如何配置密钥
        </el-link>
        <br />
        商品采集设置：设置 > 系统设置 > 第三方接口设置 >
        采集商品配置（如配置一号通采集，请先登录一号通账号，无一号通，请选择99Api设置）
      </div>
      <div class="mb15" v-if="copyConfig.copyType && copyConfig.copyType != 1">
        <el-radio-group v-model="form">
          <el-radio :label="1">淘宝</el-radio>
          <el-radio :label="2">京东</el-radio>
          <el-radio :label="3">苏宁</el-radio>
          <el-radio :label="4">拼多多</el-radio>
          <el-radio :label="5">天猫</el-radio>
        </el-radio-group>
      </div>
      <div :span="24" v-if="copyConfig.copyType">
        <el-input v-model.trim="url" placeholder="请输入链接地址" class="selWidth" size="small">
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="addProduct"
            size="small"
            v-hasPermi="['admin:product:copy:product', 'admin:product:import:product']"
          />
        </el-input>
      </div>
    </el-card>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-steps :active="currentTab" align-center finish-status="success">
          <el-step title="商品信息" />
          <el-step title="商品详情" />
          <el-step title="其他设置" />
        </el-steps>
      </div>
      <el-form
        ref="formValidate"
        v-loading="fullscreenLoading"
        class="formValidate mt20"
        :rules="ruleValidate"
        :model="formValidate"
        label-width="120px"
        @submit.native.prevent
      >
        <el-row v-show="currentTab === 0" :gutter="24">
          <!-- 商品信息-->
          <el-col v-bind="grid2">
            <el-form-item label="商品名称：" prop="name">
              <el-input
                v-model.trim="formValidate.name"
                maxlength="249"
                placeholder="请输入商品名称"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商户商品分类：" prop="cateIds">
              <el-cascader
                v-model="formValidate.cateIds"
                :options="merProductClassify"
                :props="props2"
                clearable
                class="selWidth"
                :show-all-levels="false"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="平台商品分类：" prop="categoryId">
              <el-cascader
                @change="onChangeCategory"
                v-model="formValidate.categoryId"
                :options="productClassify"
                :props="props1"
                filterable
                clearable
                class="selWidth"
                :show-all-levels="false"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="品牌：" prop="brandId">
              <el-select
                class="selWidth"
                clearable
                filterable
                v-model="formValidate.brandId"
                v-selectLoadMore="selectLoadMore"
                :loading="loading"
                remote
                :disabled="isDisabled || !formValidate.categoryId"
                :remote-method="remoteMethod"
                placeholder="请选择品牌"
              >
                <el-option v-for="user in brandList" :key="user.id" :label="user.name" :value="user.id"> </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="单位：" prop="unitName">
              <el-input v-model.trim="formValidate.unitName" placeholder="请输入单位" :disabled="isDisabled" />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品简介：" prop="intro">
              <el-input
                v-model.trim="formValidate.intro"
                type="textarea"
                maxlength="250"
                :rows="3"
                placeholder="请输入商品简介"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品封面图：" prop="image">
              <div class="upLoadPicBox acea-row" @click="modalPicTap('1')" :disabled="isDisabled">
                <div v-if="formValidate.image" class="pictrue"><img :src="formValidate.image" /></div>
                <div v-else class="upLoad">
                  <i class="el-icon-camera cameraIconfont" />
                </div>
                <span v-show="!isDisabled">请上传小于500kb的图片</span>
              </div>
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="保障服务：">
              <div class="acea-row row-middle mb5">
                <el-select
                  style="width: 76%"
                  v-model="formValidate.guaranteeIdsList"
                  placeholder="请选择保障服务"
                  clearable
                  filterable
                  :multiple="multiples"
                  :disabled="isDisabled"
                  @change="changeGuarantee"
                >
                  <el-option
                    :value="item.id"
                    v-for="(item, index) in guaranteeNew"
                    :key="index"
                    :label="item.name"
                  ></el-option>
                </el-select>
                <el-switch
                  :disabled="isDisabled"
                  class="ml10"
                  v-model="isShowGroup"
                  :active-value="true"
                  :inactive-value="false"
                  active-text="多选"
                  inactive-text="单选"
                  @change="onchangeIsShow"
                />
              </div>
              <el-tag v-for="(item, index) in guaranteeName" :key="index" class="mr10">{{ item }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品关键字：" prop="labelarr">
              <keyword @getLabelarr="getLabelarr" :labelarr="labelarr" :isDisabled="isDisabled"></keyword>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品轮播图：" prop="sliderImages">
              <div class="acea-row">
                <div
                  v-for="(item, index) in formValidate.sliderImages"
                  :key="index"
                  class="pictrue"
                  draggable="true"
                  @dragstart="handleDragStart($event, item)"
                  @dragover.prevent="handleDragOver($event, item)"
                  @dragenter="handleDragEnter($event, item)"
                  @dragend="handleDragEnd($event, item)"
                >
                  <img :src="item" />
                  <i v-show="!isDisabled" class="el-icon-error btndel" @click="handleRemove(index)" />
                </div>
                <div
                  v-if="formValidate.sliderImages.length < 10 && !isDisabled"
                  class="upLoadPicBox"
                  @click="modalPicTap('2')"
                >
                  <div class="upLoad">
                    <i class="el-icon-camera cameraIconfont" />
                  </div>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :xs="18" :sm="18" :md="18" :lg="12" :xl="12">
            <el-form-item label="运费模板：" prop="tempId">
              <el-select
                v-model="formValidate.tempId"
                placeholder="请选择"
                class="mr20"
                :disabled="isDisabled"
                style="width: 100%"
              >
                <el-option v-for="item in shippingTemplates" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品规格：" props="specType">
              <el-radio-group
                v-model="formValidate.specType"
                @change="onChangeSpec(formValidate.specType)"
                :disabled="isDisabled"
              >
                <el-radio :label="false" class="radio">单规格</el-radio>
                <el-radio :label="true">多规格</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="佣金设置：" props="isSub">
              <el-radio-group
                v-model="formValidate.isSub"
                @change="onChangetype(formValidate.isSub)"
                :disabled="isDisabled"
              >
                <el-radio :label="true" class="radio">单独设置</el-radio>
                <el-radio :label="false">默认设置</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <!-- 多规格添加-->
          <el-col v-if="formValidate.specType && !isDisabled" :span="24" class="noForm">
            <el-form-item label="选择规格：" prop="">
              <div class="acea-row">
                <el-select v-model="formValidate.selectRule">
                  <el-option v-for="item in ruleList" :key="item.id" :label="item.ruleName" :value="item.id" />
                </el-select>
                <el-button type="primary" class="mr20" @click="confirm">确认</el-button>
                <el-button class="mr15" @click="addRule">添加规格</el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <div v-for="(item, index) in formValidate.attr" :key="index">
                <div class="acea-row row-middle">
                  <span class="mr5">{{ item.attrName }}</span
                  ><i class="el-icon-circle-close" @click="handleRemoveAttr(index)" />
                </div>
                <div class="rulesBox">
                  <el-tag
                    v-for="(j, indexn) in item.attrValue"
                    :key="indexn"
                    closable
                    size="medium"
                    :disable-transitions="false"
                    class="mb5 mr10"
                    @close="handleClose(item.attrValue, indexn)"
                  >
                    {{ j }}
                  </el-tag>
                  <el-input
                    v-if="item.inputVisible"
                    ref="saveTagInput"
                    v-model.trim="item.attrValue.attrsVal"
                    class="input-new-tag"
                    size="small"
                    @keyup.enter.native="createAttr(item.attrValue.attrsVal, index)"
                    @blur="createAttr(item.attrValue.attrsVal, index)"
                  />
                  <el-button v-else class="button-new-tag" size="small" @click="showInput(item)">+ 添加</el-button>
                </div>
              </div>
            </el-form-item>
            <el-row>
              <el-col :xl="8" :lg="8" :md="12" :sm="24" :xs="24">
                <el-form-item label="规格：">
                  <el-input v-model.trim="formDynamic.attrsName" placeholder="请输入规格" />
                </el-form-item>
              </el-col>
              <el-col :xl="8" :lg="8" :md="12" :sm="24" :xs="24">
                <el-form-item label="规格值：">
                  <el-input v-model.trim="formDynamic.attrsVal" placeholder="请输入规格值" />
                </el-form-item>
              </el-col>
              <el-col :xl="8" :lg="8" :md="12" :sm="24" :xs="24">
                <el-form-item>
                  <el-button type="primary" @click="createAttrName">确定</el-button>
                  <el-button @click="offAttrName">取消</el-button>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item v-if="!isBtn">
              <el-button type="primary" icon="md-add" class="mr15" @click="addBtn">添加新规格</el-button>
            </el-form-item>
          </el-col>
          <!-- 批量设置-->
          <el-col v-if="formValidate.attr.length > 0 && formValidate.specType && !isDisabled" :span="24" class="noForm">
            <el-form-item label="批量设置：">
              <el-table :data="oneFormBatch" border class="tabNumWidth" size="mini">
                <el-table-column align="center" label="图片" min-width="80">
                  <template slot-scope="scope">
                    <div class="upLoadPicBox" @click="modalPicTap('1', 'pi')">
                      <div v-if="scope.row.image" class="pictrue tabPic"><img :src="scope.row.image" /></div>
                      <div v-else class="upLoad tabPic">
                        <i class="el-icon-camera cameraIconfont" />
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column align="center" label="一级返佣(%)" min-width="120">
                  <template slot-scope="scope">
                    <el-input
                      maxlength="6"
                      v-model.trim="scope.row.brokerage"
                      type="number"
                      :min="0"
                      :max="scope.row.price"
                      class="priceBox"
                      @keyup.native="keyupEventBrokerage('brokerage', scope.$index, 1)"
                    />
                  </template>
                </el-table-column>
                <el-table-column align="center" label="二级返佣(%)" min-width="120">
                  <template slot-scope="scope">
                    <el-input
                      maxlength="6"
                      v-model.trim="scope.row.brokerageTwo"
                      type="number"
                      :min="0"
                      :max="scope.row.price"
                      class="priceBox"
                      @keyup.native="keyupEventBrokerage('brokerageTwo', scope.$index, 1)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  v-for="(item, iii) in attrValue"
                  :key="iii"
                  :label="formThead[iii].title"
                  align="center"
                  min-width="120"
                  :type="formThead[iii].title === '商品编号' ? 'text' : 'number'"
                  :min="0"
                >
                  <template slot-scope="scope">
                    <el-input
                      maxlength="6"
                      v-model.trim="scope.row[iii]"
                      type="number"
                      :min="0"
                      class="priceBox"
                      @keyup.native="keyupEvent(iii, scope.row[iii], scope.$index, 1)"
                    />
                  </template>
                </el-table-column>
                <template v-if="formValidate.isSub"> </template>
                <el-table-column align="center" label="操作" min-width="80">
                  <template>
                    <el-button type="text" class="submission" @click="batchAdd">批量添加</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
          <el-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
            <!-- 单规格表格-->
            <el-form-item v-if="formValidate.specType === false">
              <el-table :data="OneattrValue" border class="tabNumWidth" size="mini">
                <el-table-column align="center" label="图片" min-width="80">
                  <template slot-scope="scope">
                    <div class="upLoadPicBox" @click="modalPicTap('1', 'dan', 'pi')">
                      <div v-if="formValidate.image" class="pictrue tabPic"><img :src="scope.row.image" /></div>
                      <div v-else class="upLoad tabPic">
                        <i class="el-icon-camera cameraIconfont" />
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <template v-if="formValidate.isSub">
                  <el-table-column align="center" label="一级返佣(%)" min-width="120">
                    <template slot-scope="scope">
                      <el-input
                        maxlength="6"
                        :disabled="isDisabled"
                        v-model.trim="scope.row.brokerage"
                        type="number"
                        :min="0"
                        class="priceBox"
                        @keyup.native="keyupEventBrokerage('brokerage', scope.$index, 2)"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="二级返佣(%)" min-width="120">
                    <template slot-scope="scope">
                      <el-input
                        maxlength="6"
                        :disabled="isDisabled"
                        v-model.trim="scope.row.brokerageTwo"
                        type="number"
                        :min="0"
                        class="priceBox"
                        @keyup.native="keyupEventBrokerage('brokerageTwo', scope.$index, 2)"
                      />
                    </template>
                  </el-table-column>
                </template>
                <el-table-column
                  v-for="(item, iii) in attrValue"
                  :key="iii"
                  :label="formThead[iii].title"
                  align="center"
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <el-input
                      maxlength="6"
                      :disabled="isDisabled"
                      v-model.trim="scope.row[iii]"
                      :type="formThead[iii].title === '商品编号' ? 'text' : 'number'"
                      :min="0"
                      type="number"
                      class="priceBox"
                      @keyup.native="keyupEvent(iii, scope.row[iii], scope.$index, 2)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
            <!-- <div>manyTabDate:{{manyTabDate}}</div> -->
            <el-form-item label="全部sku：" v-if="$route.params.id && showAll">
              <el-button type="default" @click="showAllSku()" :disabled="isDisabled">展示</el-button>
            </el-form-item>
            <!-- 多规格表格-->
            <el-form-item
              v-if="formValidate.attr.length > 0 && formValidate.specType"
              label="商品属性："
              class="labeltop"
              :class="isDisabled ? 'disLabel' : 'disLabelmoren'"
            >
              <el-table :data="ManyAttrValue" border class="tabNumWidth" size="mini">
                <template v-if="manyTabDate">
                  <el-table-column
                    v-for="(item, iii) in manyTabDate"
                    :key="iii"
                    align="center"
                    :label="manyTabTit[iii].title"
                    min-width="80"
                  >
                    <template slot-scope="scope">
                      <span class="priceBox" v-text="scope.row[iii]" />
                    </template>
                  </el-table-column>
                </template>
                <el-table-column align="center" label="图片" min-width="80">
                  <template slot-scope="scope">
                    <div class="upLoadPicBox" @click="modalPicTap('1', 'duo', scope.$index)">
                      <div v-if="scope.row.image" class="pictrue tabPic"><img :src="scope.row.image" /></div>
                      <div v-else class="upLoad tabPic">
                        <i class="el-icon-camera cameraIconfont" />
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column align="center" label="一级返佣(%)" min-width="120">
                  <template slot-scope="scope">
                    <el-input
                      :disabled="isDisabled"
                      v-model.trim="scope.row.brokerage"
                      maxlength="6"
                      type="number"
                      :min="0"
                      class="priceBox"
                      @keyup.native="keyupEventBrokerage('brokerage', scope.$index, 3)"
                    />
                  </template>
                </el-table-column>
                <el-table-column align="center" label="二级返佣(%)" min-width="120">
                  <template slot-scope="scope">
                    <el-input
                      maxlength="6"
                      :disabled="isDisabled"
                      v-model.trim="scope.row.brokerageTwo"
                      type="number"
                      :min="0"
                      class="priceBox"
                      @keyup.native="keyupEventBrokerage('brokerageTwo', scope.$index, 3)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  v-for="(item, iii) in attrValue"
                  :key="iii"
                  :label="formThead[iii].title"
                  align="center"
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <el-input
                      :disabled="isDisabled"
                      v-model.trim="scope.row[iii]"
                      type="number"
                      class="priceBox"
                      @keyup.native="keyupEvent(iii, scope.row[iii], scope.$index, 3)"
                    />
                  </template>
                </el-table-column>
                <template v-if="formValidate.isSub"> </template>
                <el-table-column v-if="!isDisabled" key="3" align="center" label="操作" min-width="80">
                  <template slot-scope="scope">
                    <el-button type="text" class="submission" @click="delAttrTable(scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 商品详情-->
        <el-row v-show="currentTab === 1 && !isDisabled">
          <el-col :span="24">
            <el-form-item label="商品详情：">
              <Tinymce v-model.trim="formValidate.content"></Tinymce>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-show="currentTab === 1 && isDisabled">
          <el-col :span="24">
            <el-form-item label="商品详情：">
              <span v-html="formValidate.content || '无'"></span>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 其他设置-->
        <el-row v-show="currentTab === 2">
          <el-col :span="24">
            <el-col v-bind="grid">
              <el-form-item label="排序：">
                <el-input-number
                  v-model.trim="formValidate.sort"
                  :min="1"
                  :max="9999"
                  placeholder="请输入排序"
                  @keyup.native="proving1"
                  :disabled="isDisabled"
                />
              </el-form-item>
            </el-col>
          </el-col>
          <el-col :span="24">
            <el-tooltip class="item" effect="dark" content="用户购买商品后赠送的优惠券" placement="top-start">
              <i class="el-icon-warning"></i>
            </el-tooltip>
            <el-form-item label="赠送优惠券：" class="proCoupon">
              <div class="acea-row">
                <el-tag
                  v-for="(tag, index) in formValidate.coupons"
                  :key="index"
                  class="mr10 mb10"
                  :closable="!isDisabled"
                  :disable-transitions="false"
                  @close="handleCloseCoupon(tag)"
                >
                  {{ tag.name }}
                </el-tag>
                <span class="mr15" v-if="formValidate.couponIds == null">无</span>
                <el-button v-if="!isDisabled" size="mini" class="mr15" @click="addCoupon">选择优惠券</el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button v-show="currentTab > 0" class="submission priamry_border" @click="handleSubmitUp"
            >上一步</el-button
          >
          <el-button v-show="currentTab < 2" type="primary" class="submission" @click="handleSubmitNest('formValidate')"
            >下一步</el-button
          >
          <el-button
            v-show="(currentTab === 2 || $route.params.id) && !isDisabled"
            type="primary"
            class="submission"
            @click="handleSubmit('formValidate')"
            :loading="loadingBtn"
            v-if="checkPermi(['merchant:product:update'])"
            >提交</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
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

import Tinymce from '@/components/Tinymce/index';
import {
  brandListApi,
  productGuaranteeApi,
  templateListApi,
  productCreateApi,
  categoryApi,
  productDetailApi,
  productUpdateApi,
  productCouponListApi,
  guaranteeListApi,
  copyConfigApi,
  copyProductApi,
  importProductApi,
} from '@/api/product';
import { marketingListApi } from '@/api/product';
import { Debounce } from '@/utils/validate';
import { mapGetters } from 'vuex';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
const defaultObj = {
  image: '',
  sliderImages: [],
  sliderImage: '',
  name: '',
  intro: '',
  keyword: '',
  cateIds: [], // 商品分类id
  cateId: null, // 商品分类id传值
  unitName: '',
  sort: 0,
  isShow: false,
  tempId: '',
  attrValue: [
    {
      image: '',
      price: 0,
      cost: 0,
      otPrice: 0,
      stock: 0,
      barCode: '',
      weight: 0,
      volume: 0,
      brokerage: 0,
      brokerageTwo: 0,
    },
  ],
  attr: [],
  selectRule: '',
  isSub: false,
  content: '',
  specType: false,
  id: undefined,
  couponIds: [],
  coupons: [],
  categoryId: 0,
  guaranteeIds: '',
  guaranteeIdsList: [] || null,
  brandId: '',
};
const objTitle = {
  price: {
    title: '售价（元）',
  },
  cost: {
    title: '成本价（元）',
  },
  otPrice: {
    title: '原价（元）',
  },
  stock: {
    title: '库存',
  },
  barCode: {
    title: '商品编号',
  },
  weight: {
    title: '重量（KG）',
  },
  volume: {
    title: '体积(m³)',
  },
  brokerage: {
    title: '一级返佣(%)',
  },
  brokerageTwo: {
    title: '二级返佣(%)',
  },
};
export default {
  name: 'ProductProductAdd',
  directives: {
    // 计算是否滚动到最下面
    selectLoadMore: {
      bind(el, binding) {
        // 获取element-ui定义好的scroll盒子
        const SELECTWRAP_DOM = el.querySelector('.el-select-dropdown .el-select-dropdown__wrap');
        SELECTWRAP_DOM.addEventListener('scroll', function () {
          const condition = this.scrollHeight - this.scrollTop <= this.clientHeight;
          if (condition) {
            binding.value();
          }
          // if (this.scrollHeight - this.scrollTop < this.clientHeight + 1) {
          //   binding.value();
          // }
        });
      },
    },
  },
  components: { Tinymce },
  data() {
    const validateVal = (rule, value, callback) => {
      if (this.labelarr.length === 0) {
        callback(new Error('请输入后回车'));
      } else {
        callback();
      }
    };
    return {
      form: 2,
      url: '',
      copyConfig: {},
      labelarr: [],
      isDisabled: this.$route.params.isDisabled === '1' ? true : false,
      isCopy: this.$route.params.isCopy === '1' ? true : false,
      props2: {
        children: 'childList',
        label: 'name',
        value: 'id',
        multiple: true,
        emitPath: false,
        checkStrictly: true,
      },
      props1: {
        children: 'childList',
        label: 'name',
        value: 'id',
        multiple: false,
        emitPath: false,
      },
      tabs: [],
      fullscreenLoading: false,
      props: { multiple: true },
      active: 0,
      OneattrValue: [Object.assign({}, defaultObj.attrValue[0])], // 单规格
      ManyAttrValue: [Object.assign({}, defaultObj.attrValue[0])], // 多规格
      ruleList: [],
      merCateList: [], // 商户分类筛选
      shippingList: [], // 运费模板
      formThead: Object.assign({}, objTitle),
      formValidate: Object.assign({}, defaultObj),
      formDynamics: {
        ruleName: '',
        ruleValue: [],
      },
      tempData: {
        page: 1,
        limit: 9999,
      },
      manyTabTit: {},
      manyTabDate: {},
      grid2: {
        xl: 12,
        lg: 12,
        md: 12,
        sm: 24,
        xs: 24,
      },
      // 规格数据
      formDynamic: {
        attrsName: '',
        attrsVal: '',
      },
      isBtn: false,
      manyFormValidate: [],
      currentTab: 0,
      isChoice: '',
      grid: {
        xl: 8,
        lg: 8,
        md: 12,
        sm: 24,
        xs: 24,
      },
      ruleValidate: {
        name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择平台商品分类', trigger: 'change' }],
        cateIds: [{ required: true, message: '请选择商户商品分类', trigger: 'change', type: 'array', min: '1' }],
        labelarr: [{ required: true, validator: validateVal, trigger: 'blur' }],
        unitName: [{ required: true, message: '请输入单位', trigger: 'blur' }],
        intro: [{ required: true, message: '请输入商品简介', trigger: 'blur' }],
        tempId: [{ required: true, message: '请选择运费模板', trigger: 'change' }],
        image: [{ required: true, message: '请上传商品图', trigger: 'change' }],
        sliderImages: [{ required: true, message: '请上传商品轮播图', type: 'array', trigger: 'change' }],
        specType: [{ required: true, message: '请选择商品规格', trigger: 'change' }],
        brandId: [{ required: true, message: '请选择商品品牌', trigger: 'change' }],
      },
      attrInfo: {},
      tableFrom: {
        page: 1,
        limit: 9999,
        keywords: '',
      },
      tempRoute: {},
      keyNum: 0,
      isAttr: false,
      showAll: false,
      guaranteeList: [],
      brandList: [],
      search: {
        limit: 10,
        page: 1,
        cid: '',
        brandName: '',
      },
      totalPage: 0,
      total: 0,
      loading: false,
      loadingBtn: false,
      isShowGroup: false,
      guaranteeGroupList: [],
      guaranteeNew: [],
      guaranteeName: [],
      multiples: true,
      productClassify: [],
    };
  },
  computed: {
    visitedViews() {
      return this.$store.state.tagsView.visitedViews;
    },

    ...mapGetters(['adminProductClassify', 'merProductClassify', 'productBrand', 'shippingTemplates']),

    attrValue() {
      const obj = Object.assign({}, defaultObj.attrValue[0]);
      delete obj.image;
      delete obj.brokerage;
      delete obj.brokerageTwo;
      return obj;
    },
    oneFormBatch() {
      const obj = [Object.assign({}, defaultObj.attrValue[0])];
      delete obj[0].barCode;
      return obj;
    },
  },
  watch: {
    'formValidate.attr': {
      handler: function (val) {
        //his.watCh(val); //重要！！！
        if (this.formValidate.specType && this.isAttr) this.watCh(val); //重要！！！
      },
      immediate: false,
      deep: true,
    },
  },
  created() {
    this.tempRoute = Object.assign({}, this.$route);
    if (parseFloat(this.$route.params.id) > 0 && this.formValidate.specType) {
      this.$watch('formValidate.attr', this.watCh);
    }
    if (this.isCopy && checkPermi(['merchant:plat:product:brand:cache:list'])) this.getCopyConfig();
  },
  mounted() {
    this.formValidate.sliderImages = [];
    if (checkPermi(['merchant:plat:product:category:cache:tree']))
      this.$store.dispatch('product/getAdminProductClassify');
    if (!localStorage.getItem('merProductClassify') && checkPermi(['merchant:product:category:cache:tree']))
      this.$store.dispatch('product/getMerProductClassify');
    if (checkPermi(['merchant:plat:product:brand:cache:list'])) this.$store.dispatch('product/getMerProductBrand');
    if (!localStorage.getItem('shippingTemplates')) this.$store.dispatch('product/getShippingTemplates');
    if (checkPermi(['merchant:plat:product:guarantee:list'])) this.getProductGuarantee();
    if (checkPermi(['merchant:product:guarantee:group:list'])) this.getGuaranteeGroupList();
    this.setTagsViewTitle();
    if (this.$route.params.id && this.$route.params.id != 0) {
      if (checkPermi(['merchant:product:info'])) this.getInfo();
    }
    this.productClassify = this.addDisabled(this.adminProductClassify);
  },
  methods: {
    checkPermi,
    addDisabled(dropdownList) {
      const list = [];
      try {
        dropdownList.forEach((e, index) => {
          let e_new = {
            id: e.id,
            name: e.name,
            level: e.level,
            pid: e.pid,
            isShow: e.isShow,
          };
          if (!e.childList && (e.level === 1 || e.level === 2)) {
            e_new = { ...e_new, disabled: true };
          }
          if (e.childList) {
            const childList = this.addDisabled(e.childList);
            e_new = { ...e_new, childList: childList };
          }
          list.push(e_new);
        });
      } catch (error) {
        console.log(error);
        return [];
      }
      return list;
    },
    // 生成商品表单
    addProduct() {
      if (this.url) {
        this.loading = true;
        this.isAttr = true;
        this.copyConfig.copyType == 1
          ? copyProductApi({ url: this.url })
              .then((res) => {
                this.getData(res.info);
                this.loading = false;
              })
              .catch(() => {
                this.loading = false;
              })
          : importProductApi({ url: this.url, form: this.form })
              .then((res) => {
                this.getData(res);
                this.loading = false;
              })
              .catch(() => {
                this.loading = false;
              });
        // this.getShopImg(this.formValidate.sliderImage, this.formValidate.attrValue);
      } else {
        this.$message.warning('请输入链接地址！');
      }
    },
    /**
     * 复制商品转图片路径
     * @param sliderImage轮播图, attrValue规格
     */
    async getShopImg(sliderImage, attrValue) {
      let imgList = [...JSON.parse(sliderImage)];
      attrValue.map((item) => imgList.push(item.image));
      let attrs = [...new Set(imgList)];
      let ShopImg = [];
      attrs.map((item) => {
        ShopImg.push({
          key: item,
          img: item,
        });
      });
      let result = await this.getShopImgUpload(ShopImg, []);
      this.formValidate.images = this.formValidate.images.map((item, index) => {
        if (result[index].key && item === result[index].key) {
          return result[index].img;
        } else {
          this.formValidate.images.splice(index, 1);
        }
      });
      this.ManyAttrValue.map((item, index) => {
        result.map((items, index) => {
          if (items.key && item.image === items.key) {
            return (item.image = items.img);
          }
          // else {
          //   this.ManyAttrValue.splice(index, 1);
          // }
        });
      });
      this.fullscreenLoading = false;
    },
    async getShopImgUpload(attrs, arr) {
      for (const key in attrs) {
        let res = await this.getImgData(attrs[key].img || attrs[key].sattDir);
        arr.push({
          key: attrs[key].img || attrs[key].imgsattDir,
          img: res,
        });
      }
      return arr;
    },
    /**
     * 复制商品图片路径请求接口
     * @param imgUrl
     * @returns {Promise<any>}
     */
    getImgData(imgUrl) {
      return new Promise((resolve, reject) => {
        shopImgUploadApi({
          imgUrl: imgUrl,
          respType: 1,
          uploadType: 1,
        }).then((res) => {
          if (res.errcode > 0) {
            this.fullscreenLoading = false;
            this.$message.error(res.errmsg);
            resolve('');
          } else {
            resolve(res.img_info.temp_img_url);
          }
        });
      });
    },
    getCopyConfig() {
      copyConfigApi().then((res) => {
        this.copyConfig = res;
      });
    },
    changeGuarantee(val) {
      if (val) {
        this.guaranteeName = [];
        if (this.isShowGroup) {
          let guaranteeGroupids = [];
          this.guaranteeGroupList
            .filter((item) => item.id === val)[0]
            .guaranteeList.map((item) => item.isShow && guaranteeGroupids.push(item.gid));
          this.formValidate.guaranteeIds = guaranteeGroupids.toString();
          this.guaranteeList.map((item) => {
            guaranteeGroupids.map((j) => {
              if (item.id === j) this.guaranteeName.push(item.name);
            });
          });
        }
      } else {
        this.guaranteeName = [];
      }
    },
    onchangeIsShow() {
      this.multiples = !this.multiples;
      this.guaranteeName = [];
      this.formValidate.guaranteeIdsList = [] || null;
      // this.guaranteeNew = [];
      if (this.isShowGroup) {
        this.guaranteeNew = this.guaranteeGroupList;
      } else {
        this.guaranteeNew = this.guaranteeList;
        this.formValidate.guaranteeIdsList = [] || null;
      }
    },
    // 服务组合列表
    getGuaranteeGroupList() {
      guaranteeListApi().then((res) => {
        this.guaranteeGroupList = res;
      });
    },
    getLabelarr(attr) {
      this.labelarr = attr;
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
    // 保障服务列表
    getProductGuarantee() {
      productGuaranteeApi().then((res) => {
        this.guaranteeList = res.filter((item) => item.isShow);
        this.guaranteeNew = this.guaranteeList;
      });
    },

    onChangeCategory() {
      this.formValidate.brandId = '';
      this.brandList = [];
      this.getbrandList();
      this.brandList.push({ name: '其他', id: 0 });
    },
    // 下拉加载更多
    selectLoadMore() {
      this.search.page = this.search.page + 1;
      if (this.search.page > this.totalPage) return;
      this.getbrandList(); // 请求接口
    },
    // 远程搜索
    remoteMethod(query) {
      this.loading = true;
      this.search.brandName = query;
      this.search.page = 1;
      setTimeout(() => {
        this.loading = false;
        this.getbrandList(); // 请求接口
      }, 200);
    },
    // 品牌列表
    getbrandList() {
      this.search.cid = this.formValidate.categoryId;
      brandListApi(this.search).then((res) => {
        if (!res.list.length) {
          this.$message.warning('此分类下面无品牌，请联系管理员添加此分类下的品牌！');
        }
        this.totalPage = res.totalPage;
        this.total = res.total;
        this.brandList = this.brandList.concat(res.list);
      });
    },
    //返佣输入
    keyupEventBrokerage(val, index, num) {
      switch (num) {
        case 1:
          this.oneFormBatch[index][val] = parseInt(this.oneFormBatch[index][val].replace(/\D|^0/g, ''));
          break;
        case 2:
          this.OneattrValue[index][val] = parseInt(this.OneattrValue[index][val].replace(/\D|^0/g, ''));
          break;
        default:
          this.ManyAttrValue[index][val] = parseInt(this.ManyAttrValue[index][val].replace(/\D|^0/g, ''));
      }
    },
    //价格输入
    keyupEvent(key, val, index, num) {
      var re = /^\D*([0-9]\d*\.?\d{0,2})?.*$/;
      switch (num) {
        case 1:
          this.oneFormBatch[index][key] =
            key === 'stock' ? parseInt(val) : this.$set(this.oneFormBatch[index], key, val.replace(re, '$1'));
          break;
        case 2:
          this.OneattrValue[index][key] =
            key === 'stock' ? parseInt(val) : this.$set(this.OneattrValue[index], key, val.replace(re, '$1'));
          break;
        default:
          this.ManyAttrValue[index][key] =
            key === 'stock' ? parseInt(val) : this.$set(this.ManyAttrValue[index], key, val.replace(re, '$1'));
          break;
      }
    },
    proving1(e) {
      this.formValidate.sort = e.target.value.replace(/[^\.\d]/g, '');
      this.formValidate.sort = e.target.value.replace('.', '');
    },
    handleCloseCoupon(tag) {
      this.isAttr = true;
      this.formValidate.coupons.splice(this.formValidate.coupons.indexOf(tag), 1);
      this.formValidate.couponIds.splice(this.formValidate.couponIds.indexOf(tag.id), 1);
    },
    addCoupon() {
      const _this = this;
      this.$modalCoupon(
        'wu',
        (this.keyNum += 1),
        this.formValidate.coupons,
        function (row) {
          _this.formValidate.couponIds = [];
          _this.formValidate.coupons = row;
          row.map((item) => {
            _this.formValidate.couponIds.push(item.id);
          });
        },
        '',
      );
    },
    setTagsViewTitle() {
      if (this.$route.params.id && this.$route.params.id != 0) {
        const title = this.isDisabled ? '商品详情' : '编辑商品';
        const route = Object.assign({}, this.tempRoute, { title: `${title}-${this.$route.params.id}` });
        this.$store.dispatch('tagsView/updateVisitedView', route);
      } else {
        if (this.isCopy) {
          const title = '采集商品';
          const route = Object.assign({}, this.tempRoute, { title: `${title}` });
          this.$store.dispatch('tagsView/updateVisitedView', route);
        }
      }
    },
    watCh(val) {
      const tmp = {};
      const tmpTab = {};
      this.formValidate.attr.forEach((o, i) => {
        tmp[o.attrName] = { title: o.attrName };
        tmpTab[o.attrName] = '';
      });
      this.ManyAttrValue = this.attrFormat(val);
      if (this.formValidate.attrValue) {
        this.ManyAttrValue.forEach((val, index) => {
          const key = Object.values(val.attrValue).sort().join('/');
          if (this.attrInfo[key]) this.ManyAttrValue[index] = this.attrInfo[key];
        });
        this.attrInfo = [];
        this.ManyAttrValue.forEach((val) => {
          this.attrInfo[Object.values(val.attrValue).sort().join('/')] = val;
        });
      }

      this.manyTabTit = tmp;
      this.manyTabDate = tmpTab;
      this.formThead = Object.assign({}, this.formThead, tmp);
    },
    attrFormat(arr) {
      let data = [];
      const res = [];
      return format(arr);
      function format(arr) {
        if (arr.length > 1) {
          arr.forEach((v, i) => {
            if (i === 0) data = arr[i]['attrValue'];
            const tmp = [];
            if (!data) return;
            data.forEach(function (vv) {
              arr[i + 1] &&
                arr[i + 1]['attrValue'] &&
                arr[i + 1]['attrValue'].forEach((g) => {
                  const rep2 = (i !== 0 ? '' : arr[i]['attrName'] + '_') + vv + '$&' + arr[i + 1]['attrName'] + '_' + g;
                  tmp.push(rep2);
                  if (i === arr.length - 2) {
                    const rep4 = {
                      image: '',
                      price: 0,
                      cost: 0,
                      otPrice: 0,
                      barCode: '',
                      stock: 0,
                      weight: 0,
                      volume: 0,
                      brokerage: 0,
                      brokerageTwo: 0,
                    };
                    rep2.split('$&').forEach((h, k) => {
                      const rep3 = h.split('_');
                      if (!rep4['attrValue']) rep4['attrValue'] = {};
                      rep4['attrValue'][rep3[0]] = rep3.length > 1 ? rep3[1] : '';
                    });
                    for (let attrValueKey in rep4.attrValue) {
                      rep4[attrValueKey] = rep4.attrValue[attrValueKey];
                    }
                    res.push(rep4);
                  }
                });
            });
            data = tmp.length ? tmp : [];
          });
        } else {
          const dataArr = [];
          arr.forEach((v, k) => {
            v['attrValue'].forEach((vv, kk) => {
              dataArr[kk] = v['attrName'] + '_' + vv;
              res[kk] = {
                image: '',
                price: 0,
                cost: 0,
                otPrice: 0,
                stock: 0,
                barCode: '',
                weight: 0,
                volume: 0,
                brokerage: 0,
                brokerageTwo: 0,
                attrValue: { [v['attrName']]: vv },
              };
              for (let attrValueKey in res[kk].attrValue) {
                res[kk][attrValueKey] = res[kk].attrValue[attrValueKey];
              }
            });
          });
          data.push(dataArr.join('$&'));
        }
        return res;
      }
    },
    // 添加规则；
    addRule() {
      const _this = this;
      this.$modalAttr(this.formDynamics, function () {
        _this.productGetRule();
      });
    },
    // 选择规格
    onChangeSpec(num) {
      this.isAttr = true;
      if (num) this.productGetRule();
    },
    // 选择属性确认
    confirm() {
      this.isAttr = true;
      if (!this.formValidate.selectRule) {
        return this.$message.warning('请选择属性');
      }
      const data = [];
      this.ruleList.forEach((item) => {
        if (item.id === this.formValidate.selectRule) {
          item.ruleValue.forEach((i) => {
            data.push({
              attrName: i.value,
              attrValue: i.detail,
            });
          });
        }
        this.formValidate.attr = data;
      });
    },
    // 获取商品属性模板；
    productGetRule() {
      templateListApi(this.tableFrom).then((res) => {
        const list = res.list;
        for (var i = 0; i < list.length; i++) {
          list[i].ruleValue = JSON.parse(list[i].ruleValue);
        }
        this.ruleList = list;
      });
    },
    showInput(item) {
      this.$set(item, 'inputVisible', true);
    },
    onChangetype(item) {
      // if (item === 1) {
      //   this.OneattrValue.map((item) => {
      //     this.$set(item, 'brokerage', 0);
      //     this.$set(item, 'brokerageTwo', 0);
      //   });
      //   this.ManyAttrValue.map((item) => {
      //     this.$set(item, 'brokerage', 0);
      //     this.$set(item, 'brokerageTwo', 0);
      //   });
      // } else {
      //   this.OneattrValue.map((item) => {
      //     // delete item.brokerage;
      //     // delete item.brokerageTwo;
      //     this.$set(item, 'brokerage', 0);
      //     this.$set(item, 'brokerageTwo', 0);
      //   });
      //   this.ManyAttrValue.map((item) => {
      //     this.$set(item, 'brokerage', 0);
      //     this.$set(item, 'brokerageTwo', 0);
      //     // delete item.brokerage;
      //     // delete item.brokerageTwo;
      //   });
      // }
    },
    // 删除表格中的属性
    delAttrTable(index) {
      this.ManyAttrValue.splice(index, 1);
    },
    // 批量添加
    batchAdd() {
      // if (!this.oneFormBatch[0].pic || !this.oneFormBatch[0].price || !this.oneFormBatch[0].cost || !this.oneFormBatch[0].ot_price ||
      //     !this.oneFormBatch[0].stock || !this.oneFormBatch[0].bar_code) return this.$Message.warning('请填写完整的批量设置内容！');
      for (const val of this.ManyAttrValue) {
        this.$set(val, 'image', this.oneFormBatch[0].image);
        this.$set(val, 'price', this.oneFormBatch[0].price);
        this.$set(val, 'cost', this.oneFormBatch[0].cost);
        this.$set(val, 'otPrice', this.oneFormBatch[0].otPrice);
        this.$set(val, 'barCode', this.oneFormBatch[0].barCode);
        this.$set(val, 'stock', this.oneFormBatch[0].stock);
        this.$set(val, 'weight', this.oneFormBatch[0].weight);
        this.$set(val, 'volume', this.oneFormBatch[0].volume);
        this.$set(val, 'brokerage', this.oneFormBatch[0].brokerage);
        this.$set(val, 'brokerageTwo', this.oneFormBatch[0].brokerageTwo);
      }
    },
    // 添加按钮
    addBtn() {
      this.clearAttr();
      this.isBtn = true;
    },
    // 取消
    offAttrName() {
      this.isBtn = false;
    },
    clearAttr() {
      this.isAttr = true;
      this.formDynamic.attrsName = '';
      this.formDynamic.attrsVal = '';
    },
    // 删除规格
    handleRemoveAttr(index) {
      this.isAttr = true;
      this.formValidate.attr.splice(index, 1);
      this.manyFormValidate.splice(index, 1);
    },
    // 删除属性
    handleClose(item, index) {
      item.splice(index, 1);
    },
    // 添加规则名称
    createAttrName() {
      this.isAttr = true;
      if (this.formDynamic.attrsName && this.formDynamic.attrsVal) {
        const data = {
          attrName: this.formDynamic.attrsName,
          attrValue: [this.formDynamic.attrsVal],
        };
        this.formValidate.attr.push(data);
        var hash = {};
        this.formValidate.attr = this.formValidate.attr.reduce(function (item, next) {
          /* eslint-disable */
          hash[next.attrName] ? '' : (hash[next.attrName] = true && item.push(next));
          return item;
        }, []);
        this.clearAttr();
        this.isBtn = false;
      } else {
        this.$Message.warning('请添加完整的规格！');
      }
    },
    // 添加属性
    createAttr(num, idx) {
      this.isAttr = true;
      if (num) {
        this.formValidate.attr[idx].attrValue.push(num);
        var hash = {};
        this.formValidate.attr[idx].attrValue = this.formValidate.attr[idx].attrValue.reduce(function (item, next) {
          /* eslint-disable */
          hash[next] ? '' : (hash[next] = true && item.push(next));
          return item;
        }, []);
        this.formValidate.attr[idx].inputVisible = false;
      } else {
        this.$message.warning('请添加属性');
      }
    },
    //点击展示所有多规格属性
    showAllSku() {
      if (this.isAttr == false) {
        this.isAttr = true;
        if (this.formValidate.specType && this.isAttr) this.watCh(this.formValidate.attr); //重要！！！
      } else if (this.isAttr == true) {
        this.isAttr = false;
        this.getInfo();
      }
    },
    getData(res) {
      let info = res;
      this.formValidate = {
        image: this.$selfUtil.setDomain(info.image),
        sliderImage: info.sliderImage,
        sliderImages: JSON.parse(info.sliderImage),
        name: info.name,
        intro: info.intro,
        keyword: info.keyword,
        cateIds: info.cateId ? info.cateId.split(',') : [], // 商品分类id
        cateId: info.cateId, // 商品分类id传值
        unitName: info.unitName,
        sort: info.sort ? info.sort : 0,
        isShow: info.isShow,
        tempId: info.tempId,
        attr: info.attr || [],
        attrValue: info.attrValue ? info.attrValue : [],
        selectRule: info.selectRule,
        isSub: info.isSub ? info.isSub : false,
        content: this.$selfUtil.replaceImgSrcHttps(info.content),
        specType: info.specType,
        id: info.id ? info.id : 0,
        coupons: info.coupons ? info.coupons : [],
        couponIds: info.couponIds ? info.couponIds : [],
        brandId: info.brandId,
        categoryId: info.categoryId,
        guaranteeIds: info.guaranteeIds, //保障服务传值
        guaranteeIdsList: info.guaranteeIds ? info.guaranteeIds.split(',').map(Number) : [], //保障服务
      };
      this.labelarr = info.keyword ? info.keyword.split(',') : [];
      if (this.formValidate.categoryId) this.getbrandList();
      if (this.formValidate.couponIds) {
        productCouponListApi().then((res) => {
          let ids = this.formValidate.couponIds.toString();
          let arr = res;
          let obj = {};
          for (let i in arr) {
            obj[arr[i].id] = arr[i];
          }
          let strArr = ids.split(',');
          let newArr = [];
          for (let item of strArr) {
            if (obj[item]) {
              newArr.push(obj[item]);
            }
          }
          this.$set(this.formValidate, 'coupons', newArr); //在编辑回显时，让返回数据中的优惠券id，通过接口匹配显示,
        });
      }

      let imgs = JSON.parse(info.sliderImage);
      let imgss = [];
      Object.keys(imgs).map((i) => {
        imgss.push(this.$selfUtil.setDomain(imgs[i]));
      });
      this.formValidate.sliderImages = [...imgss];
      if (info.specType) {
        this.productGetRule(); //加载商品规格选项
        if (info.attrValue) {
          this.formValidate.attr = info.attr.map((item) => {
            return {
              attrName: item.attrName,
              attrValue: item.attrValues.split(','),
            };
          });
          this.ManyAttrValue = info.attrValue;
          this.ManyAttrValue.forEach((val) => {
            val.image = this.$selfUtil.setDomain(val.image);
            val.attrValue = JSON.parse(val.attrValue);
            this.attrInfo[Object.values(val.attrValue).sort().join('/')] = val;
            val.brokerage = val.brokerage || 0;
            val.brokerageTwo = val.brokerageTwo || 0;
          });
          /***多规格商品如果被删除过sku，优先展示api返回的数据,否则会有没有删除的错觉***/
          let manyAttr = this.attrFormat(this.formValidate.attr);
          if (manyAttr.length !== this.ManyAttrValue.length) {
            this.$set(this, 'showAll', true);
            this.isAttr = false;
          } else {
            this.isAttr = true;
          }
          /*******/
          const tmp = {};
          const tmpTab = {};
          this.formValidate.attr.forEach((o, i) => {
            tmp[o.attrName] = { title: o.attrName };
            tmpTab[o.attrName] = '';
          });

          // 此处手动实现后台原本value0 value1的逻辑
          this.formValidate.attrValue.forEach((item) => {
            for (let attrValueKey in item.attrValue) {
              item[attrValueKey] = item.attrValue[attrValueKey];
            }
          });
          this.manyTabTit = tmp;
          this.manyTabDate = tmpTab;
          this.formThead = Object.assign({}, this.formThead, tmp);
        } else {
          if (this.formValidate.attr.length) {
            this.oneFormBatch[0].image = this.$selfUtil.setDomain(info.image);
            for (var i = 0; i < this.formValidate.attr.length; i++) {
              this.formValidate.attr[i].attrValue = JSON.parse(this.formValidate.attr[i].attrValues);
            }
          }
        }
      } else {
        this.OneattrValue = info.attrValue;
      }
    },
    // 详情
    getInfo() {
      this.fullscreenLoading = true;
      productDetailApi(this.$route.params.id)
        .then(async (res) => {
          // this.isAttr = true;
          this.getData(res);
          this.fullscreenLoading = false;
        })
        .catch((res) => {
          this.fullscreenLoading = false;
        });
    },
    handleRemove(i) {
      this.formValidate.sliderImages.splice(i, 1);
    },
    // 点击商品图
    modalPicTap(tit, num, i, status) {
      const _this = this;
      if (_this.isDisabled) return;
      this.$modalUpload(
        function (img) {
          if (tit === '1' && !num) {
            _this.formValidate.image = img[0].sattDir;
            _this.OneattrValue[0].image = img[0].sattDir;
          }
          if (tit === '2' && !num) {
            if (img.length > 10) return this.$message.warning('最多选择10张图片！');
            if (img.length + _this.formValidate.sliderImages.length > 10)
              return this.$message.warning('最多选择10张图片！');
            img.map((item) => {
              _this.formValidate.sliderImages.push(item.sattDir);
            });
          }
          if (tit === '1' && num === 'dan') {
            _this.OneattrValue[0].image = img[0].sattDir;
          }
          if (tit === '1' && num === 'duo') {
            _this.ManyAttrValue[i].image = img[0].sattDir;
          }
          if (tit === '1' && num === 'pi') {
            _this.oneFormBatch[0].image = img[0].sattDir;
          }
        },
        tit,
        'content',
      );
    },
    handleSubmitUp() {
      // this.currentTab --
      if (this.currentTab-- < 0) this.currentTab = 0;
    },
    handleSubmitNest(name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          if (this.currentTab++ > 2) this.currentTab = 0;
        } else {
          if (
            !this.formValidate.store_name ||
            !this.formValidate.cate_id ||
            !this.formValidate.keyword ||
            !this.formValidate.unit_name ||
            !this.formValidate.store_info ||
            !this.formValidate.image ||
            !this.formValidate.slider_image
          ) {
            this.$message.warning('请填写完整商品信息！');
          }
        }
      });
    },
    // 提交
    handleSubmit(name) {
      if (this.formValidate.specType && this.formValidate.attr.length < 1)
        return this.$message.warning('请填写多规格属性！');
      this.formValidate.cateId = this.formValidate.cateIds.join(',');
      this.formValidate.keyword = this.labelarr.join(',');
      this.formValidate.sliderImage = JSON.stringify(this.formValidate.sliderImages);
      if (!this.isShowGroup) this.formValidate.guaranteeIds = this.formValidate.guaranteeIdsList.join(',');
      if (this.formValidate.specType) {
        this.formValidate.attrValue = this.ManyAttrValue;
        this.formValidate.attr = this.formValidate.attr.map((item) => {
          return {
            attrName: item.attrName,
            id: item.id,
            attrValue: item.attrValue,
            attrValues: item.attrValue.join(','),
          };
        });
        for (var i = 0; i < this.formValidate.attrValue.length; i++) {
          this.$set(this.formValidate.attrValue[i], 'id', 0);
          this.$set(this.formValidate.attrValue[i], 'productId', 0);
          this.$set(
            this.formValidate.attrValue[i],
            'attrValue',
            JSON.stringify(this.formValidate.attrValue[i].attrValue),
          ); //
          delete this.formValidate.attrValue[i].value0;
        }
      } else {
        this.formValidate.attr = [
          {
            attrName: '规格',
            attrValues: '默认',
            id: parseFloat(this.$route.params.id) > 0 ? this.formValidate.attr[0].id : 0,
          },
        ];
        this.OneattrValue.map((item) => {
          this.$set(item, 'attrValue', JSON.stringify({ 规格: '默认' }));
          this.$set(item, 'productId', 0);
        });
        this.formValidate.attrValue = this.OneattrValue;
      }
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.loadingBtn = true;
          parseFloat(this.$route.params.id) > 0
            ? productUpdateApi(this.formValidate)
                .then(async (res) => {
                  this.$message.success('编辑成功');
                  setTimeout(() => {
                    this.$router.push({ path: '/product/list' });
                  }, 500);
                  this.closeSelectedTag();
                  this.loadingBtn = false;
                })
                .catch((res) => {
                  this.loadingBtn = false;
                })
            : productCreateApi(this.formValidate)
                .then(async (res) => {
                  this.$message.success('新增成功');
                  setTimeout(() => {
                    this.$router.push({ path: '/product/list' });
                  }, 500);
                  this.closeSelectedTag();
                  this.loadingBtn = false;
                })
                .catch((res) => {
                  this.loadingBtn = false;
                });
        } else {
          if (
            !this.formValidate.name ||
            !this.formValidate.cateId ||
            !this.formValidate.keyword ||
            !this.formValidate.unitName ||
            !this.formValidate.intro ||
            !this.formValidate.image ||
            !this.formValidate.sliderImages
          ) {
            this.$message.warning('请填写完整商品信息！');
          }
        }
      });
    },
    isActive(route) {
      return route.path === this.$route.path;
    },
    closeSelectedTag() {
      let that = this;
      that.$store.dispatch('tagsView/delView', that.$route).then(({ visitedViews }) => {
        if (that.isActive(that.$route)) {
          that.toLastView(visitedViews, that.$route);
        }
      });
    },
    toLastView(visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0];
      if (latestView) {
        this.$router.push(latestView.fullPath);
      } else {
        // now the default is to redirect to the home page if there is no tags-view,
        // you can adjust it according to your needs.
        if (view.name === 'Dashboard') {
          // to reload home page
          this.$router.replace({ path: '/redirect' + view.fullPath });
        } else {
          this.$router.push('/');
        }
      }
    },
    // 表单验证
    validate(prop, status, error) {
      if (status === false) {
        this.$message.warning(error);
      }
    },
    // 移动
    handleDragStart(e, item) {
      if (!this.isDisabled) this.dragging = item;
    },
    handleDragEnd(e, item) {
      if (!this.isDisabled) this.dragging = null;
    },
    handleDragOver(e) {
      if (!this.isDisabled) e.dataTransfer.dropEffect = 'move';
    },
    handleDragEnter(e, item) {
      if (!this.isDisabled) {
        e.dataTransfer.effectAllowed = 'move';
        if (item === this.dragging) {
          return;
        }
        const newItems = [...this.formValidate.sliderImages];
        const src = newItems.indexOf(this.dragging);
        const dst = newItems.indexOf(item);
        newItems.splice(dst, 0, ...newItems.splice(src, 1));
        this.formValidate.sliderImages = newItems;
      }
    },
    getFileType(fileName) {
      // 后缀获取
      let suffix = '';
      // 获取类型结果
      let result = '';
      try {
        const flieArr = fileName.split('.');
        suffix = flieArr[flieArr.length - 1];
      } catch (err) {
        suffix = '';
      }
      // fileName无后缀返回 false
      if (!suffix) {
        return false;
      }
      suffix = suffix.toLocaleLowerCase();
      // 图片格式
      const imglist = ['png', 'jpg', 'jpeg', 'bmp', 'gif'];
      // 进行图片匹配
      result = imglist.find((item) => item === suffix);
      if (result) {
        return 'image';
      }
      // 匹配 视频
      const videolist = ['mp4', 'm2v', 'mkv', 'rmvb', 'wmv', 'avi', 'flv', 'mov', 'm4v'];
      result = videolist.find((item) => item === suffix);
      if (result) {
        return 'video';
      }
      // 其他 文件类型
      return 'other';
    },
  },
};
</script>
<style scoped lang="scss">
.line-ht {
  line-height: 28px;
}
.el-icon-warning {
  position: relative;
  top: 26px;
  left: 9px;
}
.disLabel {
  ::v-deepel-form-item__label {
    margin-left: 36px !important;
  }
}
.disLabelmoren {
  ::v-deepel-form-item__label {
    margin-left: 120px !important;
  }
}
.priamry_border {
  border: 1px solid #1890ff;
  color: #1890ff;
}
.color-item {
  height: 30px;
  line-height: 30px;
  padding: 0 10px;
  color: #fff;
  margin-right: 10px;
}
.color-list .color-item.blue {
  background-color: #1e9fff;
}
.color-list .color-item.yellow {
  background-color: rgb(254, 185, 0);
}
.color-list .color-item.green {
  background-color: #009688;
}
.color-list .color-item.red {
  background-color: #ed4014;
}
.proCoupon {
  ::v-deepel-form-item__content {
    margin-top: 5px;
  }
}
.tabPic {
  width: 40px !important;
  height: 40px !important;
  img {
    width: 100%;
    height: 100%;
  }
}
.noLeft {
  ::v-deepel-form-item__content {
    margin-left: 0 !important;
  }
}
.tabNumWidth {
  ::v-deepel-input-number--medium {
    width: 121px !important;
  }
  ::v-deepel-input-number__increase {
    width: 20px !important;
    font-size: 12px !important;
  }
  ::v-deepel-input-number__decrease {
    width: 20px !important;
    font-size: 12px !important;
  }
  ::v-deepel-input-number--medium .el-input__inner {
    padding-left: 25px !important;
    padding-right: 25px !important;
  }
  ::v-deep thead {
    line-height: normal !important;
  }
  ::v-deep .el-table .cell {
    line-height: normal !important;
  }
}
.selWidth {
  width: 100%;
}
.selWidthd {
  width: 300px;
}
.button-new-tag {
  height: 28px;
  line-height: 26px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
.pictrue {
  width: 60px;
  height: 60px;
  border: 1px dotted rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  position: relative;
  cursor: pointer;
  img {
    width: 100%;
    height: 100%;
  }
  video {
    width: 100%;
    height: 100%;
  }
}
.btndel {
  position: absolute;
  z-index: 1;
  width: 20px !important;
  height: 20px !important;
  left: 46px;
  top: -4px;
}
.labeltop {
  ::v-deepel-form-item__label {
    float: none !important;
    display: inline-block !important;
    width: auto !important;
  }
}
.iview-video-style {
  width: 300px;
  height: 180px;
  border-radius: 10px;
  background-color: #707070;
  margin: 0 120px 20px;
  position: relative;
  overflow: hidden;
}

.iview-video-style .iconv {
  color: #fff;
  line-height: 180px;
  width: 50px;
  height: 50px;
  display: inherit;
  font-size: 26px;
  position: absolute;
  top: -74px;
  left: 50%;
  margin-left: -25px;
}

.iview-video-style .mark {
  position: absolute;
  width: 100%;
  height: 30px;
  top: 0;
  background-color: rgba(0, 0, 0, 0.5);
  text-align: center;
}
</style>
