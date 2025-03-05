<template>
  <div>
    <el-row :gutter="30">
      <el-col v-bind="grid" v-hasPermi="['platform:category:list:tree']">
        <div class="Nav">
          <div class="input">
            <el-input
              v-model.trim="filterText"
              placeholder="选择分类"
              prefix-icon="el-icon-search"
              style="width: 100%"
              clearable
            />
          </div>
          <div class="trees-coadd">
            <div class="scollhide">
              <div class="trees" :style="{ maxHeight: !pictureType ? '500px' : '700px' }">
                <el-tree
                  ref="tree"
                  :data="treeData2"
                  :filter-node-method="filterNode"
                  :props="defaultProps"
                  highlight-current
                >
                  <div slot-scope="{ node, data }" class="custom-tree-node" @click.stop="handleNodeClick(data)">
                    <div>
                      <span class="custom-tree-node-label" :title="node.label">{{ node.label }}</span>
                      <span v-if="data.space_property_name" style="font-size: 11px; color: #3889b1"
                        >（{{ data.name }}）</span
                      >
                    </div>
                    <span class="el-ic">
                      <el-dropdown>
                        <span class="el-dropdown-link">
                          <i class="el-icon-more"></i>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item @click.native="onAdd(data.id)" v-if="checkPermi(['platform:category:save'])"
                            >添加分类</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="node.label !== '全部图片' && checkPermi(['platform:category:update'])"
                            @click.native="onEdit(data.id)"
                            >编辑分类</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="node.label !== '全部图片' && checkPermi(['platform:category:delete'])"
                            @click.native="handleDelete(data.id)"
                            >删除分类</el-dropdown-item
                          >
                        </el-dropdown-menu>
                      </el-dropdown>
                    </span>
                  </div>
                </el-tree>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col v-bind="grid2" class="colLeft">
        <div class="conter mb15 relative">
          <div class="bnt">
            <el-button size="small" type="primary" class="mr15 mb20" @click="checkPics" v-if="!pictureType"
              >使用选中图片
            </el-button>
            <!--弹窗组件-->
            <div class="mb20" v-show="!pictureType">
              <el-tooltip class="item" effect="dark" :content="uploadName" placement="top-start">
                <el-button
                  v-if="typeDate === 'pic'"
                  icon="el-icon-upload2"
                  size="small"
                  class="mr15"
                  @click="handleChangeImage"
                ></el-button>
                <el-upload
                  v-else
                  class="upload-demo"
                  action
                  :http-request="handleUploadForm"
                  :on-change="imgSaveToUrl"
                  :headers="myHeaders"
                  :show-file-list="false"
                  multiple
                >
                  <el-button icon="el-icon-upload2" size="small" class="mr15"></el-button>
                </el-upload>
              </el-tooltip>
              <el-tooltip class="item" effect="dark" content="删除图片" placement="top-start">
                <el-button
                  icon="el-icon-delete"
                  class="mr15"
                  type="danger"
                  size="small"
                  @click.stop="editPicList('图片')"
                  v-hasPermi="['platform:attachment:delete']"
                ></el-button>
              </el-tooltip>
            </div>
            <!--素材管理-->
            <div class="acea-row" v-if="pictureType">
              <div v-if="typeDate === 'pic' && checkPermi(['platform:upload:image', 'platform:upload:file'])">
                <el-button class="mr10" type="primary" @click="handleChangeImage">上传 </el-button>
              </div>

              <el-upload
                v-if="typeDate !== 'pic' && checkPermi(['platform:upload:image', 'platform:upload:file'])"
                class="upload-demo"
                action
                :http-request="handleUploadForm"
                :on-change="imgSaveToUrl"
                :headers="myHeaders"
                :show-file-list="false"
                multiple
              >
                <el-button class="mr10" type="primary">上传</el-button>
              </el-upload>
              <div v-hasPermi="['platform:attachment:delete']">
                <el-button class="mr10" type="danger" @click.stop="editPicList('图片')">删除图片 </el-button>
              </div>
            </div>
            <el-select
              v-hasPermi="['platform:attachment:move']"
              v-model="sleOptions.attachment_category_name"
              placeholder="图片移动至"
              class="mb20"
              :size="pictureType ? '' : 'small'"
            >
              <el-option
                class="demo"
                :label="sleOptions.attachment_category_name"
                :value="sleOptions.attachment_category_id"
                style="max-width: 560px; height: 200px; overflow: auto; background-color: #fff"
              >
                <el-tree
                  ref="tree2"
                  :data="treeData2"
                  :filter-node-method="filterNode"
                  :props="defaultProps"
                  highlight-current
                  @node-click="handleSelClick"
                />
              </el-option>
            </el-select>
            <div class="abs_video">
              <el-radio-group v-model="typeDate" @change="radioChange" size="small">
                <el-radio-button label="pic">图片</el-radio-button>
                <el-radio-button label="video">视频</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="pictrueList acea-row" v-loading="loadingPic">
            <div v-show="isShowPic" class="imagesNo">
              <i class="el-icon-picture" style="font-size: 60px; color: rgb(219, 219, 219)" />
              <span class="imagesNo_sp">视频为空</span>
            </div>
            <div class="conters scrollbarAll" :style="{ maxHeight: !pictureType ? '500px' : '700px' }">
              <div v-for="(item, index) in pictrueList.list" :key="index" class="gridPic">
                <span class="num_badge" v-if="item.num > 0">{{ item.num }}</span>
                <img
                  style="object-fit: cover"
                  v-lazy="item.sattDir ? item.sattDir : localImg"
                  :class="item.isSelect ? 'on' : ''"
                  @click="changImage(item, index, pictrueList.list)"
                  v-if="item.attType !== 'video/mp4'"
                />
                <video
                  :src="item.sattDir"
                  :class="item.isSelect ? 'on' : ''"
                  @click="changImage(item, index, pictrueList.list)"
                  v-if="item.attType == 'video/mp4'"
                ></video>
              </div>
            </div>
          </div>
        </div>
        <div class="block">
          <el-pagination
            :page-sizes="!pictureType ? [20, 40, 60, 80] : [30, 60, 90, 120]"
            :page-size="tableData.limit"
            :current-page="tableData.page"
            :pager-count="5"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pictrueList.total"
            @size-change="handleSizeChange"
            @current-change="pageChange"
          />
        </div>
      </el-col>
    </el-row>
    <el-dialog
      :title="bizTitle"
      :visible.sync="visible"
      destroy-on-close
      :close-on-click-modal="false"
      :modal="modals"
      @close="closeModel"
    >
      <el-form ref="editPram" :model="editPram" label-width="100px" v-loading="loading">
        <el-form-item
          label="上级分类"
          prop="pid"
          :rules="[
            {
              type: 'number',
              required: true,
              message: '请选择上级分类',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-cascader v-model="editPram.pid" :options="treeData2" :props="categoryProps" style="width: 100%" />
        </el-form-item>
        <el-form-item
          label="分类名称"
          prop="name"
          :rules="[
            {
              required: true,
              message: '请输入分类名称',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-input v-model.trim="editPram.name" placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model.trim="editPram.sort" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlerSubmit('editPram')">确定 </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <el-dialog
      title="上传图片"
      :visible.sync="visiblePic"
      @close="onCallback(false)"
      class="handleDialog"
      :fullscreen="true"
      :append-to-body="true"
    >
      <div class="wrap">
        <div class="main">
          <div class="cropperContent">
            <div class="mb35">
              <div class="title">原始图片</div>
              <div class="cropper mr20">
                <vue-cropper
                  ref="cropper"
                  :img="option.img"
                  :output-size="option.size"
                  :output-type="option.outputType"
                  :info="true"
                  :full="option.full"
                  :fixed="fixed"
                  :fixed-number="fixedNumber"
                  :can-move="option.canMove"
                  :can-move-box="option.canMoveBox"
                  :fixed-box="option.fixedBox"
                  :original="option.original"
                  :auto-crop="option.autoCrop"
                  :auto-crop-width="option.autoCropWidth"
                  :auto-crop-height="option.autoCropHeight"
                  :center-box="option.centerBox"
                  @real-time="realTime"
                  :high="option.high"
                  @img-load="imgLoad"
                  mode="contain"
                  :max-img-size="option.max"
                ></vue-cropper>
              </div>
              <div class="scopeButton mt20 mb20">
                <el-button-group>
                  <el-button type="primary">
                    <label class="localButton" for="uploads">选择本地图片</label>
                    <input
                      type="file"
                      id="uploads"
                      class="inputFile"
                      accept="image/png, image/jpeg, image/gif, image/jpg"
                      @change="uploadImg($event)"
                  /></el-button>
                  <el-button type="primary" v-if="!crap" @click="startCrop">图片裁剪 </el-button>
                  <el-button type="primary" @click="stopCrop" v-else>关闭裁剪 </el-button>
                  <el-button type="primary" @click="clearCrop">清除裁剪 </el-button>
                  <el-button type="primary" @click="changeScale(1)" icon="el-icon-plus"></el-button>
                  <el-button type="primary" @click="changeScale(-1)" icon="el-icon-minus"></el-button>
                  <el-button type="primary" @click="rotateLeft" icon="el-icon-refresh-left"></el-button>
                  <el-button type="primary" @click="rotateRight" icon="el-icon-refresh-right"></el-button>
                </el-button-group>
              </div>
              <div class="uploadButton acea-row">
                <el-button-group>
                  <el-button @click="down('blob')" type="primary" icon="el-icon-download">下载 </el-button>
                  <el-button @click="uploadNewPic" type="primary" icon="el-icon-upload2">上传至图片库 </el-button>
                  <el-button type="primary">
                    <el-upload
                      action
                      :http-request="handleUploadForm"
                      :on-change="imgSaveToUrl"
                      :headers="myHeaders"
                      :show-file-list="false"
                      multiple
                    >
                      <el-button icon="el-icon-upload2" type="primary" style="padding: 9px; border-right: none"
                        >多图上传</el-button
                      >
                    </el-upload></el-button
                  >
                </el-button-group>
              </div>
            </div>
            <div class="previewBox">
              <div class="title">实时预览</div>
              <div
                class="show-preview"
                :style="{ width: previews.w + 'px', height: previews.h + 'px', overflow: 'hidden', margin: '5px' }"
              >
                <div :style="previews.div">
                  <img :src="previews.url" :style="previews.img" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
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

import { addCategroy, treeCategroy, infoCategroy, updateCategroy, deleteCategroy } from '@/api/categoryApi';
import { fileImageApi, fileListApi, fileDeleteApi, attachmentMoveApi } from '@/api/systemSetting';
import { getToken } from '@/utils/auth';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { VueCropper } from 'vue-cropper';

export default {
  name: 'uploadPicture',
  props: {
    pictureType: {
      type: String,
      default: '',
    },
    isMore: {
      type: String,
      default: '1',
    },
    modelName: {
      type: String,
      default: '',
    },
    checkedMore: {
      type: Array,
      default: () => [],
    },
    labelName: {
      type: String,
      default: '',
    },
  },
  components: {
    VueCropper,
  },
  data() {
    return {
      uploadName: '上传',
      fixed: false,
      fixedNumber: [16, 9],
      option: {
        img: '',
        size: 1,
        full: false,
        outputType: 'png',
        canMove: true,
        fixedBox: false,
        original: false,
        canMoveBox: true,
        autoCrop: false,
        // 只有自动截图开启 宽度高度才生效
        // autoCropWidth: 160,
        // autoCropHeight: 150,
        centerBox: false,
        high: true,
        max: 99999,
      },
      picId: '',
      newPicId: '',
      crap: false,
      previews: {},
      visiblePic: false,
      loadingPic: false,
      loading: false,
      modals: false,
      allTreeList: [],
      categoryProps: {
        value: 'id',
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: true,
        emitPath: false,
      },
      editPram: {
        pid: 0,
        name: '',
        type: 2,
        sort: 1,
        status: 0,
        url: 'url',
        id: 0,
      },
      visible: false,
      bizTitle: '',
      sleOptions: {
        attrId: '',
        pid: '',
      },
      list: [],
      grid: {
        xl: 7,
        lg: 7,
        md: 7,
        sm: 7,
        xs: 24,
      },
      grid2: {
        xl: 17,
        lg: 17,
        md: 17,
        sm: 17,
        xs: 24,
      },
      filterText: '',
      treeData: [],
      treeData2: [],
      defaultProps: {
        children: 'child',
        label: 'name',
      },
      tableData: {
        page: 1,
        limit: 20,
        pid: 0,
        attType: 'jpg,jpeg,gif,png,bmp,PNG,JPG',
      },
      classifyId: 0,
      myHeaders: { 'X-Token': getToken() },
      treeFrom: {
        status: -1,
        type: 2,
      },
      pictrueList: {
        list: [],
        total: 0,
      },
      isShowPic: false,
      checkPicList: [],
      ids: [],
      listPram: {
        pid: 0,
        type: 2,
        status: 0,
        name: '',
        page: 1,
        limit: 9999,
      },
      localImg: '',
      videoStatus: false,
      typeDate: 'pic',
    };
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
  },
  mounted() {
    if (this.labelName) {
      this.option.fixedBox = true;
      this.option.autoCropWidth = 200;
      this.option.autoCropHeight = 100;
      let str = this.labelName;
      let regex = /\((.+?)\)/g; // 中括号
      let regexs = /（(.+?)）/g; // 中文小括号
      // if(str.match(regex)){
      //   str = str.match(regex)[0].replace("(", "").replace(")", "");
      // }else{
      //   str = str.match(regexs)[0].replace("（", "").replace("）", "");
      // }
      // console.log(typeof str);
    }
    this.pictureType ? (this.tableData.limit = 30) : (this.tableData.limit = 20);
    if (this.$route && this.$route.query.field === 'dialog') import('../js/internal.js');
    if (checkPermi(['platform:category:list:tree'])) this.getList();
    if (checkPermi(['platform:attachment:list'])) this.getFileList();
  },
  methods: {
    checkPermi,
    // 选取图片后自动回调，里面可以获取到文件
    imgSaveToUrl(event) {
      // 也可以用file
      this.localFile = event.raw; // 或者 this.localFile=file.raw

      // 转换操作可以不放到这个函数里面，
      // 因为这个函数会被多次触发，上传时触发，上传成功也触发
      let reader = new FileReader();
      reader.readAsDataURL(this.localFile); // 这里也可以直接写参数event.raw

      // 转换成功后的操作，reader.result即为转换后的DataURL ，
      // 它不需要自己定义，你可以console.integralLog(reader.result)看一下
      reader.onload = () => {
        // console.integralLog(reader.result)
      };

      /* 另外一种本地预览方法 */
      let URL = window.URL || window.webkitURL;
      this.localImg = URL.createObjectURL(event.raw);
      // 转换后的地址为 blob:http://xxx/7bf54338-74bb-47b9-9a7f-7a7093c716b5
    },
    closeModel() {
      this.$refs['editPram'].resetFields();
    },
    handlerSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.editPram.pid > 0) {
            if (this.editPram.pid === this.editPram.id) return this.$message.warning('上级分类不能选当前分类');
          }
          this.bizTitle === '添加分类'
            ? addCategroy(this.editPram).then((data) => {
                this.$message.success('创建成功');
                this.visible = false;
                this.getList();
              })
            : updateCategroy(this.editPram).then((data) => {
                this.$message.success('编辑成功');
                this.visible = false;
                this.getList();
              });
        } else {
          return false;
        }
      });
    },
    // 表单分类
    handlerGetList() {
      let datas = {
        name: '全部图片',
        id: '',
      };
      treeCategroy(this.treeFrom).then((data) => {
        this.allTreeList = data;
        this.allTreeLis.unshift(datas);
      });
    },
    // 搜索分类
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    // 所有分类
    getList() {
      const data = {
        name: '全部图片',
        id: 0,
      };
      treeCategroy(this.treeFrom).then((res) => {
        this.treeData = res;
        this.treeData.unshift(data);
        this.treeData2 = [...this.treeData];
      });
    },
    // 添加分类
    onAdd(id) {
      this.tableData.pid = id;
      this.bizTitle = '添加分类';
      this.visible = true;
      if (id)
        this.editPram = {
          pid: id,
          name: '',
          type: 2,
          sort: 1,
          status: 0,
          url: 'url',
          id: 0,
        };
    },
    // 编辑
    onEdit(id) {
      this.bizTitle = '编辑分类';
      this.loading = true;
      infoCategroy(id).then((res) => {
        this.editPram = res;
        this.loading = false;
      });
      this.visible = true;
    },
    // 删除
    handleDelete(id) {
      this.$modalSure().then(() => {
        deleteCategroy(id).then(() => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
    handleNodeClick(data) {
      this.checkPicList = [];
      this.pictrueList.list = [];
      this.tableData.pid = data.id;
      this.tableData.page = 1;
      this.getFileList();
    },
    // 上传
    handleUploadForm(param) {
      const formData = new FormData();
      const data = {
        model: 'product',
        pid: this.tableData.pid ? this.tableData.pid : 0,
      };
      formData.append('multipart', param.file);
      this.uploadPic(formData, data);
    },
    uploadPic(formData, data) {
      let loading = this.$loading({
        lock: true,
        text: '上传中，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
      });
      fileImageApi(formData, data)
        .then((res) => {
          loading.close();
          this.$message.success('上传成功');
          this.tableData.page = 1;
          this.getFileList();
          this.onCallback();
        })
        .catch((res) => {
          loading.close();
        });
    },
    // 文件列表
    getFileList() {
      if (this.tableData.pid === 0) this.tableData.pid = null;
      this.loadingPic = true;
      fileListApi(this.tableData)
        .then(async (res) => {
          this.pictrueList.list = res.list;
          if (this.tableData.page === 1 && this.pictrueList.list.length > 0)
            this.pictrueList.list[0].localImg = this.localImg;
          if (this.pictrueList.list.length) {
            this.isShowPic = false;
          } else {
            this.isShowPic = true;
          }
          this.pictrueList.total = res.total;
          this.checkPicList = [];
          this.loadingPic = false;
        })
        .catch(() => {
          this.loadingPic = false;
        });
    },
    pageChange(page) {
      this.tableData.page = page;
      this.checkPicList = [];
      this.getFileList();
    },
    handleSizeChange(val) {
      this.tableData.limit = val;
      this.getFileList();
    },
    // 选中图片
    changImage(item, index, row) {
      let activeIndex = 0;
      if (!item.isSelect) {
        this.$set(item, 'isSelect', true);
        this.checkPicList.push(item);
      } else {
        this.$set(item, 'isSelect', !item.isSelect);
        this.checkPicList.map((el, index) => {
          if (el.attId == item.attId) {
            activeIndex = index;
          }
        });
        this.checkPicList.splice(activeIndex, 1);
      }
      this.ids = [];
      this.checkPicList.map((item, i) => {
        this.ids.push(item.attId);
      });
      if (this.checkPicList.length > 0) {
        this.uploadName = '裁剪图片';
      } else {
        this.uploadName = '上传';
      }

      this.pictrueList.list.map((el, i) => {
        if (el.isSelect) {
          this.checkPicList.filter((el2, j) => {
            if (el.attId == el2.attId) {
              el.num = j + 1;
              this.$nextTick(() => {
                this.pictrueList.list;
              });
            }
          });
        } else {
          el.num = 0;
        }
      });
    },
    // 点击使用选中图片
    checkPics() {
      if (!this.checkPicList.length) return this.$message.warning('请先选择图片');
      if (this.$route && this.$route.query.field === 'dialog') {
        let str = '';
        for (let i = 0; i < this.checkPicList.length; i++) {
          str += '<img src="' + this.checkPicList[i].sattDir + '">';
        }
        /* eslint-disable */
        nowEditor.dialog.close(true);
        nowEditor.editor.setContent(str, true);
      } else {
        if (this.isMore === '1' && this.checkPicList.length > 1) {
          return this.$message.warning('最多只能选一张图片');
        }

        this.$emit('getImage', [...this.checkedMore, ...this.checkPicList]);
      }
    },
    // 删除图片
    editPicList(tit) {
      if (!this.checkPicList.length) return this.$message.warning('请先选择图片');
      this.$modalSure().then(() => {
        fileDeleteApi(this.ids.join(',')).then(() => {
          this.$message.success('刪除成功');
          this.tableData.page = 1;
          this.getFileList();
          this.checkPicList = [];
          this.uploadName = '上传';
        });
      });
    },
    // 移动分类点击
    handleSelClick(node) {
      if (this.ids.length) {
        this.sleOptions = {
          attrId: this.ids.join(','),
          pid: node.id,
        };
        this.getMove();
      } else {
        this.$message.warning('请先选择图片');
      }
    },
    getMove() {
      attachmentMoveApi(this.sleOptions)
        .then(async (res) => {
          this.$message.success('操作成功');
          this.clearBoth();
          this.tableData.page = 1;
          this.getFileList();
        })
        .catch((res) => {
          this.clearBoth();
        });
    },
    clearBoth() {
      this.sleOptions = {
        attrId: '',
        pid: '',
      };
      this.checkPicList = [];
      this.ids = [];
    },
    videoChange(val) {
      if (val == false) {
        this.$set(this.tableData, 'attType', 'jpg,jpeg,gif,png,bmp,PNG,JPG');
        this.tableData.page = 1;
        this.getFileList();
      } else {
        this.$set(this.tableData, 'attType', 'video/mp4');
        this.tableData.page = 1;
        this.getFileList();
      }
    },
    radioChange(val) {
      if (val === 'video') {
        this.videoChange(true);
      } else {
        this.videoChange(false);
      }
    },
    /**
     * 裁剪图片
     * @returns {ElMessageComponent}
     */
    startCrop() {
      this.crap = true;
      this.$refs.cropper.startCrop();
    },
    stopCrop() {
      //  stop
      this.crap = false;
      this.$refs.cropper.stopCrop();
    },
    clearCrop() {
      // clear
      this.$refs.cropper.clearCrop();
    },
    handleChangeImage() {
      if (this.checkPicList.length > 1) {
        return this.$message.warning('最多只能选一张图片');
      }
      this.visiblePic = true;
      if (this.checkPicList.length > 0) this.imageToBase64(this.checkPicList[0].sattDir);
    },
    saveNewPic() {
      // if (!this.newPicId) {
      //   return this.$message.warning("请上传裁剪后的图片");
      // }
      this.$refs.cropper.getCropBlob((data) => {
        this.downImg = window.URL.createObjectURL(data);
        // aLink.download = this.downImg;
        this.$emit('getImage', [
          {
            sattDir: this.downImg,
          },
        ]);
      });
    },
    onCallback() {
      this.visiblePic = false;
      this.option.img = '';
      this.stopCrop();
      this.clearCrop();
    },
    changeScale(num) {
      num = num || 1;
      this.$refs.cropper.changeScale(num);
    },
    rotateLeft() {
      this.$refs.cropper.rotateLeft();
    },
    rotateRight() {
      this.$refs.cropper.rotateRight();
    },
    // 实时预览函数
    realTime(data) {
      this.previews = data;
    },
    //image转Base64
    imageToBase64(file) {
      //一定要设置为let，不然图片不显示
      let image = new Image();
      //解决跨域问题
      image.setAttribute('crossOrigin', 'anonymous');
      let imageUrl = file;
      image.src = imageUrl;
      //image.onload为异步加载
      image.onload = () => {
        var canvas = document.createElement('canvas');
        canvas.width = image.width;
        canvas.height = image.height;
        var context = canvas.getContext('2d');
        context.drawImage(image, 0, 0, image.width, image.height);

        var quality = 0.8;
        //这里的dataurl就是base64类型
        var dataURL = canvas.toDataURL('image/jpeg', quality); //使用toDataUrl将图片转换成jpeg的格式,不要把图片压缩成png，因为压缩成png后base64的字符串可能比不转换前的长！
        this.option.img = dataURL;
      };
    },
    // 将base64转换为文件 百度随便找的 看需求使用
    dataURLtoFile(dataurl, filename) {
      var arr = dataurl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n);
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
      }
      return new File([u8arr], filename, { type: mime });
    },
    uploadNewPic() {
      this.$refs.cropper.getCropData((data) => {
        let name = new Date().getTime();
        let file = this.dataURLtoFile(data, `${name}.png`);
        const datas = {
          model: this.modelName ? this.modelName : 'product',
          pid: this.tableData.pid ? this.tableData.pid : 0,
        };
        let formData = new FormData();
        formData.append('multipart', file);
        this.uploadPic(formData, datas);
        // this.onCallback();
      });
    },
    down(type) {
      // event.preventDefault()
      var aLink = document.createElement('a');
      aLink.download = 'author-img';
      // 输出
      if (type === 'blob') {
        this.$refs.cropper.getCropBlob((data) => {
          this.downImg = window.URL.createObjectURL(data);
          // aLink.download = this.downImg;
          aLink.href = window.URL.createObjectURL(data);
          aLink.click();
        });
      } else {
        this.$refs.cropper.getCropData((data) => {
          this.downImg = data;
          aLink.href = data;
          aLink.click();
        });
      }
    },
    uploadImg(e) {
      //上传图片
      var file = e.target.files[0];
      if (!/\.(gif|jpg|jpeg|png|bmp|GIF|JPG|PNG)$/.test(e.target.value)) {
        this.$message.warning('图片类型必须是.gif,jpeg,jpg,png,bmp中的一种');
        return false;
      }
      var reader = new FileReader();
      reader.onload = (e) => {
        let data;
        if (typeof e.target.result === 'object') {
          // 把Array Buffer转化为blob 如果是base64不需要
          data = window.URL.createObjectURL(new Blob([e.target.result]));
        } else {
          data = e.target.result;
        }
        this.option.img = data;
      };
      // 转化为base64
      // reader.readAsDataURL(file)
      // 转化为blob
      reader.readAsArrayBuffer(file);
    },
    imgLoad(msg) {
      console.log(msg, 'msg');
    },
  },
};
</script>

<style scoped lang="scss">
.cut {
  width: 500px;
  height: 500px;
  margin: 30px auto;
}

.handleDialog {
  z-index: 999999 !important;
  .cropperContent {
    display: flex;
    padding-left: 20px;
    flex-wrap: wrap;
    .cropper {
      width: 1200px;
      height: 500px;
      border: 1px solid #ddd;
    }
    .title {
      font-size: 18px;
      height: 36px;
      margin-bottom: 20px;
    }
    .previewBox {
      flex: 1;
      display: flex;
      flex-direction: column;
      .showPreview {
        flex: 1;
        display: flex;
        justify-content: center;
        .preview {
          overflow: hidden;
          background: #eeeeee;
          border: 1px solid #eeeeee;
        }
        img {
          width: 100%;
          height: 100%;
        }
      }
    }
  }
  .scopeButton {
    width: 600px;
    display: flex;
    justify-content: space-between;
    ::v-deep.el-button--primary:first-child {
      padding: 0;
    }
  }
  .uploadButton {
    ::v-deep.el-button--primary:last-child {
      padding: 0;
      border-right-color: none;
    }
  }
  .localButton {
    cursor: pointer;
    padding: 10px 15px;
    border-radius: 3px;
    appearance: none;
    display: flex;
    align-self: center;
    font-weight: normal;
  }
  .inputFile {
    width: 180px;
    position: absolute;
    clip: rect(0 0 0 0);
  }
}

.demo::-webkit-scrollbar {
  display: none; /* Chrome Safari */
}

.demo {
  scrollbar-width: none; /* firefox */
  -ms-overflow-style: none; /* IE 10+ */
  overflow-x: hidden;
  overflow-y: auto;
}

.temp {
  height: 0;
  margin-bottom: 0;
  border: none;
  padding: 0;
}

.selectTreeClass {
  background: #d5e8fc;
}

.treeBox {
  width: 100%;
  height: 100%;
}

.upload-demo {
  display: inline-block !important;
  float: left;
}

.tree_w {
  padding: 20px 30px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  padding-right: 8px;
  color: #4386c6;
}

.custom-tree-node-label {
  display: block;
  width: 125px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.el-ic {
  display: none;
  i,
  span {
    /*padding: 0 14px;*/
    font-size: 18px;
    font-weight: 600;
  }
  .svg-icon {
    color: #4386c6;
  }
}

.el-tree-node__content {
  height: 38px;
}

.el-tree-node__expand-icon {
  color: #428bca;
  /*padding: 10px 10px 0px 10px !important;*/
}

.el-tree-node__content:hover .el-ic {
  color: #428bca !important;
  display: inline-block;
}

.el-tree-node__content:hover {
  font-weight: bold;
}

.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content :hover {
  .el-tree-node__expand-icon.is-leaf {
    color: transparent;
    cursor: default;
  }
  /*background-color: #3998d9;*/
  .custom-tree-node {
    font-weight: bold;
  }
  .el-tree-node__expand-icon {
    font-weight: bold;
  }
}

.el-dialog__body {
  .upload-container .image-preview .image-preview-wrapper img {
    height: 100px;
  }
  .el-dialog .el-collapse-item__wrap {
    padding-top: 0px;
  }
  .spatial_img {
    .el-collapse-item__wrap {
      margin-bottom: 0;
      padding-top: 0px;
    }
  }
  .upload-container .image-preview .image-preview-wrapper {
    width: 120px;
  }
  .upload-container .image-preview .image-preview-action {
    line-height: 100px;
    height: 100px;
  }
}

.trees-coadd {
  width: 100%;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  .scollhide {
    overflow-x: hidden;
    overflow-y: scroll;
    padding: 10px 0 10px 0;
    box-sizing: border-box;
    .trees {
      width: 100%;
    }
  }
  .scollhide::-webkit-scrollbar {
    display: none;
  }
}

.conters {
  display: flex;
  flex-wrap: wrap;
  /*max-height: 296px;*/
  overflow: auto;
}

.conters:after {
  content: '';
  width: 410px !important;
}

.gridPic {
  margin-right: 15px;
  margin-bottom: 10px;
  width: 110px;
  height: 110px;
  position: relative;
  cursor: pointer;
  img {
    width: 100%;
    height: 100%;
    display: block;
  }
  video {
    width: 100%;
    height: 100%;
    display: block;
  }
  .num_badge {
    position: absolute;
    bottom: 6px;
    right: 6px;
    display: inline-block;
    min-width: 20px;
    height: 20px;
    border-radius: 10px;
    background: #1890ff;
    font-size: 14px;
    text-align: center;
    color: #fff;
    line-height: 20px;
  }
}

.conter {
  width: 99%;
  height: 100%;
  .bnt {
    width: 100%;
    padding: 0 13px 10px 7px;
    box-sizing: border-box;
    display: flex;
    ::v-deep.el-button + .el-button {
      margin-left: 0;
    }
  }
  .pictrueList {
    /*padding-left: 15px;*/
    width: 100%;
    el-image {
      width: 100%;
      border: 2px solid #fff;
    }
    .on {
      border: 2px solid #1890ff;
    }
  }
  .el-image {
    width: 110px;
    height: 110px;
    cursor: pointer;
  }
  .imagesNo {
    width: 100%;
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    margin: 65px 0;
    .imagesNo_sp {
      font-size: 13px;
      color: #dbdbdb;
      line-height: 3;
    }
  }
}

.relative {
  position: relative;
}

.abs_video {
  position: absolute;
  right: 20px;
  top: 0;
}
</style>
