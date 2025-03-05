<template>
  <el-dialog
    v-if="dialogVisible"
    title="用户等级"
    :visible.sync="dialogVisible"
    width="500px"
    :before-close="handleClose"
  >
    <el-form :model="user" :rules="rules" ref="user" label-width="100px" class="demo-ruleForm" v-loading="loading">
      <el-form-item label="等级名称" prop="name">
        <el-input v-model.trim="user.name" maxlength="10" placeholder="请输入等级名称"></el-input>
      </el-form-item>
      <el-form-item label="等级级别" prop="grade">
        <el-select v-model="user.grade" placeholder="请选择" :disabled="user.grade === 0">
          <el-option
            v-for="item in levelData"
            :key="item.val"
            :label="item.name"
            :value="item.val"
            :disabled="item.disabled"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所需成长值" prop="experience">
        <el-input-number
          :disabled="user.grade === 0"
          v-model.number="user.experience"
          placeholder="请输入成长值"
          :min="0"
          :max="99999"
          step-strictly
        ></el-input-number>
      </el-form-item>
      <el-form-item label="文字颜色" prop="backColor">
        <el-input v-model.trim="user.backColor" placeholder="请输入文字颜色(例：#455A93)"></el-input>
      </el-form-item>
      <el-form-item label="图标" prop="icon">
        <div class="upLoadPicBox" @click="modalPicTap('1', 'icon')">
          <div v-if="user.icon" class="pictrue"><img :src="user.icon" /></div>
          <div v-else-if="formValidate.icon" class="pictrue"><img :src="formValidate.icon" /></div>
          <div v-else class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
          <p class="desc mt10">尺寸：180*180px</p>
        </div>
      </el-form-item>
      <el-form-item label="背景图" prop="backImage">
        <div class="upLoadPicBox" @click="modalPicTap('1', 'backImage')">
          <div v-if="user.backImage" class="pictrue"><img :src="user.backImage" /></div>
          <div v-else-if="formValidate.backImage" class="pictrue"><img :src="formValidate.backImage" /></div>
          <div v-else class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
        </div>
        <p class="desc mt10">尺寸：660*300px</p>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="resetForm('user')">取 消</el-button>
      <el-button
        type="primary"
        @click="submitForm('formValidate')"
        v-hasPermi="['platform:system:user:level:save', 'platform:system:user:level:update']"
        >确 定</el-button
      >
    </span>
  </el-dialog>
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
import { levelSaveApi, levelInfoApi, levelUpdateApi } from '@/api/user';
import { Debounce } from '@/utils/validate';
const obj = {
  name: '',
  grade: 1,
  icon: '',
  backImage: '',
  id: null,
  backColor: '',
  experience: '',
};
export default {
  name: 'CreatGrade',
  props: {
    user: Object,
    levelNumData: {
      type: Array,
      default: [],
    },
  },
  watch: {
    levelNumData: {
      handler: function (val) {
        this.levelNumDataNew = val;
      },
      immediate: false,
      deep: true,
    },
  },
  data() {
    return {
      dialogVisible: false,
      formValidate: Object.assign({}, obj),
      loading: false,
      rules: {
        name: [{ required: true, message: '请输入等级名称', trigger: 'blur' }],
        grade: [
          { required: true, message: '请输入等级', trigger: 'blur' },
          { type: 'number', message: '等级必须为数字值' },
        ],
        backColor: [{ required: true, message: '请输入文字颜色', trigger: 'blur' }],
        discount: [{ message: '请输入折扣', trigger: 'blur' }],
        experience: [
          { required: true, message: '请输入经验', trigger: 'blur' },
          { type: 'number', message: '经验必须为数字值' },
        ],
        icon: [{ required: true, message: '请上传图标', trigger: 'change' }],
        backImage: [{ required: true, message: '请上传背景图', trigger: 'change' }],
      },
      levelNum: [],
      levelNumDataNew: this.levelNumData,
      //levelData: []
    };
  },
  mounted() {
    for (let i = 0; i < 100; i++) {
      this.levelNum.push({
        val: i,
        name: i,
      });
    }
  },
  computed: {
    levelData() {
      this.levelNum.map((i) => {
        if (this.levelNumDataNew.some((item) => item === i.val)) {
          i.disabled = true;
        } else {
          i.disabled = false;
        }
      });
      return this.levelNum;
    },
  },
  methods: {
    // 点击商品图
    modalPicTap(tit, num) {
      const _this = this;
      this.$modalUpload(
        function (img) {
          if (tit === '1' && num === 'icon') {
            _this.formValidate.icon = img[0].sattDir;
            this.$set(_this.user, 'icon', _this.formValidate.icon);
          } else {
            _this.formValidate.backImage = img[0].sattDir;
            this.$set(_this.user, 'backImage', _this.formValidate.backImage);
          }
          this.$set(_this.user, 'isShow', false);
        },
        tit,
        'user',
      );
    },
    info(id) {
      this.loading = true;
      levelInfoApi({ id: id })
        .then((res) => {
          this.formValidate = res;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    handleClose() {
      this.dialogVisible = false;
      // this.user = Object.assign({}, '')
    },
    submitForm: Debounce(function (formName) {
      this.$refs.user.validate((valid) => {
        if (valid) {
          this.loading = true;
          let data = {
            discount: this.user.discount,
            experience: this.user.experience,
            grade: this.user.grade,
            icon: this.user.icon,
            id: this.user.id,
            isShow: this.user.isShow,
            name: this.user.name,
            backColor: this.user.backColor,
            backImage: this.user.backImage,
          };
          this.user.id
            ? levelUpdateApi(data)
                .then((res) => {
                  this.$message.success('编辑成功');
                  this.loading = false;
                  this.handleClose();
                  this.formValidate = Object.assign({}, obj);
                  this.$parent.getList();
                })
                .catch(() => {
                  this.loading = false;
                })
            : levelSaveApi(this.user)
                .then((res) => {
                  this.$message.success('添加成功');
                  this.loading = false;
                  this.handleClose();
                  this.formValidate = Object.assign({}, obj);
                  this.$parent.getList();
                })
                .catch(() => {
                  this.loading = false;
                  this.formValidate = Object.assign({}, obj);
                });
        } else {
          return false;
        }
      });
    }),
    resetForm(formName) {
      this.dialogVisible = false;
      this[formName] = {};
      //  this.$nextTick(() => {
      //   this.$refs.formData.resetFields();
      // })
    },
  },
};
</script>

<style scoped>
.desc {
  color: #999;
  font-size: 12px;
  line-height: 16px;
}
</style>
