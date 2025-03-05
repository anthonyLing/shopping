<template>
  <div class="mr10">
    <el-form
      ref="formValidate"
      v-loading="loading"
      class="formValidate"
      :model="formValidate"
      label-width="130px"
      @submit.native.prevent
      :rules="rules"
    >
      <el-row :gutter="24">
        <el-col :span="24" v-if="!stockEdit"> </el-col>
        <el-col :span="24" class="mt10"> </el-col>
      </el-row>
      <el-form-item v-if="!stockEdit" label="商户商品分类：" prop="cateIds">
        <el-cascader
          v-model="formValidate.cateIds"
          :options="merProductClassify"
          :props="props2"
          clearable
          class="selWidth"
          :show-all-levels="false"
        />
      </el-form-item>
      <el-form-item label="商品属性：" class="labeltop">
        <el-table :data="ManyAttrValue" border class="tabNumWidth" size="mini">
          <template v-if="manyTabDate">
            <el-table-column
              v-for="(item, iii) in manyTabDate"
              :key="iii"
              align="center"
              :label="manyTabTit[iii].title"
            >
              <template slot-scope="scope">
                <span class="priceBox" v-text="scope.row[iii]" />
              </template>
            </el-table-column>
          </template>
          <el-table-column align="center" label="图片" width="80">
            <template slot-scope="scope">
              <div class="upLoadPicBox">
                <div v-if="scope.row.image" class="pictrue tabPic"><img :src="scope.row.image" /></div>
              </div>
            </template>
          </el-table-column>
          <el-table-column v-for="(item, iii) in attrValue" :key="iii" :label="formThead[iii].title" align="center">
            <template slot-scope="scope">
              <el-input
                v-if="(formThead[iii].title === '售价（元）' || formThead[iii].title === '库存') && !stockEdit"
                v-model.trim="scope.row[iii]"
                type="number"
                :min="0"
                class="priceBox"
                @keyup.native="keyupEvent(iii, scope.row[iii], scope.$index)"
              />
              <span v-else>{{ scope.row[iii] }}</span>
            </template>
          </el-table-column>
          <el-table-column v-if="stockEdit" align="center" label="增加库存" min-width="120">
            <template slot-scope="scope">
              <el-form-item class="all">
                <el-input
                  v-model.trim="scope.row.stockAdd"
                  type="number"
                  :min="0"
                  class="priceBox"
                  @keyup.native="keyupEvent('stockAdd', scope.row.stockAdd, scope.$index)"
                />
              </el-form-item>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit('formValidate')">{{
          loadingBtn ? '提交中 ...' : '确 定'
        }}</el-button>
      </el-form-item>
    </el-form>
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

import { productDetailApi, productStockAddApi, productFreeEditApi } from '@/api/product';
import { mapGetters } from 'vuex';
const defaultObj = {
  cateIds: [], // 商品分类id
  cateId: null, // 商品分类id传值
  attrValue: [
    {
      image: '',
      price: 0,
      cost: 0,
      otPrice: 0,
      stock: 0,
      weight: 0,
      volume: 0,
      brokerage: 0,
      brokerageTwo: 0,
    },
  ],
  attr: [],
  specType: false,
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
  stockAdd: {
    title: '增加库存',
  },
};
export default {
  name: 'edit',
  props: {
    productId: {
      type: Number || String,
      default: 0 || '',
    },
    stockEdit: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      props2: {
        children: 'childList',
        label: 'name',
        value: 'id',
        multiple: true,
        emitPath: false,
        checkStrictly: true,
      },
      rules: {
        cateIds: [{ required: true, message: '请选择商户商品分类', trigger: 'change', type: 'array', min: '1' }],
        stockAdd: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
      },
      formValidate: Object.assign({}, defaultObj),
      formThead: Object.assign({}, objTitle),
      isAttr: false,
      OneattrValue: [
        {
          image: '',
          price: 0,
          cost: 0,
          otPrice: 0,
          stock: 0,
          weight: 0,
          volume: 0,
          stockAdd: 0,
        },
      ], // 单规格
      ManyAttrValue: [
        {
          image: '',
          price: 0,
          cost: 0,
          otPrice: 0,
          stock: 0,
          weight: 0,
          volume: 0,
          stockAdd: 0,
        },
      ], // 多规格
      manyTabTit: {},
      manyTabDate: {},
      loading: false,
      attrInfo: {},
      loadingBtn: false,
    };
  },
  computed: {
    ...mapGetters(['merProductClassify']),
    attrValue() {
      const obj = Object.assign({}, defaultObj.attrValue[0]);
      delete obj.image;
      return obj;
    },
  },
  watch: {
    'formValidate.attr': {
      handler: function (val) {
        if (this.formValidate.specType && this.isAttr) this.watCh(val); //重要！！！
      },
      immediate: false,
      deep: true,
    },
  },
  created() {
    this.tempRoute = Object.assign({}, this.$route);
    if (this.formValidate.specType) {
      this.$watch('formValidate.attr', this.watCh);
    }
  },
  mounted() {
    this.getInfo();
  },
  methods: {
    keyupEvent(key, val, index) {
      this.$forceUpdate();
      var re = /^\D*([0-9]\d*\.?\d{0,2})?.*$/;
      this.ManyAttrValue[index][key] =
        key === 'stock' || key === 'stockAdd'
          ? parseInt(val)
          : this.$set(this.ManyAttrValue[index], key, val.replace(re, '$1'));
    },
    onSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.stockEdit) {
            let data = [];
            this.formValidate.attrValue.forEach((val, index) => {
              data.push({
                id: val.id,
                stock: val.stockAdd ? val.stockAdd : 0,
              });
            });
            productStockAddApi({
              attrValueList: data,
              id: this.productId,
            })
              .then((res) => {
                this.$message.success('操作成功');
                this.$emit('subSuccess');
                this.loadingBtn = false;
              })
              .catch((res) => {
                this.loadingBtn = false;
              });
          } else {
            let data = [];
            this.formValidate.cateId = this.formValidate.cateIds.join(',');
            this.formValidate.attrValue.forEach((val, index) => {
              data.push({
                id: val.id,
                stock: val.stock,
                price: val.price,
              });
            });
            productFreeEditApi({
              attrValue: data,
              id: this.productId,
              cateId: this.formValidate.cateId,
            })
              .then((res) => {
                this.$message.success('操作成功');
                this.$emit('subSuccess');
                this.loadingBtn = false;
              })
              .catch((res) => {
                this.loadingBtn = false;
              });
          }
        } else {
          return false;
        }
      });
    },
    watCh(val) {
      const tmp = {};
      const tmpTab = {};
      this.formValidate.attr.forEach((o, i) => {
        // tmp['value' + i] = { title: o.attrName }
        // tmpTab['value' + i] = ''
        tmp[o.attrName] = { title: o.attrName };
        tmpTab[o.attrName] = '';
      });
      this.ManyAttrValue = this.attrFormat(val);
      this.ManyAttrValue.forEach((val, index) => {
        const key = Object.values(val.attrValue).sort().join('/');
        if (this.attrInfo[key]) this.ManyAttrValue[index] = this.attrInfo[key];
      });
      this.attrInfo = [];
      this.ManyAttrValue.forEach((val) => {
        this.attrInfo[Object.values(val.attrValue).sort().join('/')] = val;
      });
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
                      stockAdd: 0,
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
                stockAdd: 0,
                attrValue: { [v['attrName']]: vv },
              };
              // Object.values(res[kk].attrValue).forEach((v, i) => {
              //   res[kk]['value' + i] = v
              // })
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
    // 详情
    getInfo() {
      this.loading = true;
      productDetailApi(this.productId)
        .then(async (res) => {
          this.isAttr = true;
          let info = res;
          this.formValidate = {
            attr: info.attr,
            attrValue: info.attrValue,
            specType: info.specType,
            cateIds: info.cateId.split(','), // 商品分类id
            cateId: info.cateId, // 商品分类id传值
          };
          if (info.specType) {
            this.formValidate.attr = info.attr.map((item) => {
              return {
                attrName: item.attrName,
                attrValue: item.attrValues.split(','),
              };
            });
            info.attrValue.forEach((val) => {
              val.image = this.$selfUtil.setDomain(val.image);
              val.attrValue = JSON.parse(val.attrValue);
              //val.stockAdd = 0;
              this.attrInfo[Object.values(val.attrValue).sort().join('/')] = val;
            });
            this.ManyAttrValue = info.attrValue;
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
              // tmp['value' + i] = { title: o.attrName }
              // tmpTab['value' + i] = ''
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
            this.ManyAttrValue = info.attrValue;
          }
          this.loading = false;
        })
        .catch((res) => {
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped>
.pictrue {
  border: none;
}
</style>
