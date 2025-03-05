<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="acea-row">
        <div class="tab_view">
          <div
            class="cell_item"
            :class="{ tab_active: listActive === index }"
            v-for="(item, index) in tabList"
            :key="index"
            @click="ProductNavTab(index)"
          >
            {{ item }}
          </div>
        </div>
        <div v-if="listActive === 0" class="rightModel acea-row">
          <div class="leftModel">
            <div class="current_home">
              <div class="model_header">
                <iframe id="iframe" class="iframe-box" :src="frontDomain" frameborder="0" ref="iframe"></iframe>
              </div>
              <div class="mask"></div>
            </div>
          </div>
          <div style="width: 100%">
            <el-row>
              <el-col v-bind="grid">
                <div class="flex_between">
                  <el-button
                    v-hasPermi="['platform:pagediy:update', 'platform:pagediy:getdefault', 'platform:pagediy:info']"
                    type="primary"
                    @click="handlerDiyHomePage()"
                    size="small"
                    class="mb35"
                    style="font-size: 12px"
                    >首页装修</el-button
                  >
                  <el-card body-style="background-color: #F9F9F9;" class="mb20 Qrcode-card">
                    <el-row>
                      <el-col v-bind="grid2">
                        <div class="acea-row row-between-wrapper Qrcode-box">
                          <div>
                            <div class="title mb20">微信小程序</div>
                            <div class="tips">扫描右侧二维码查看</div>
                          </div>
                        </div>
                      </el-col>
                      <el-col v-bind="grid2">
                        <div style="text-align: right">
                          <el-image :src="Qrcode" class="Qrcode_img"></el-image>
                        </div>
                      </el-col>
                    </el-row>
                  </el-card>
                  <el-card body-style="background-color: #F9F9F9;" class="Qrcode-card">
                    <el-row>
                      <el-col v-bind="grid2">
                        <div class="acea-row row-between-wrapper Qrcode-box">
                          <div>
                            <div class="title mb20">微信公众号</div>
                            <div class="tips">扫描右侧二维码查看</div>
                          </div>
                        </div>
                      </el-col>
                      <el-col v-bind="grid2">
                        <div class="acea-row" style="justify-content: right">
                          <div id="diyQrcode"></div>
                        </div>
                      </el-col>
                    </el-row>
                  </el-card>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
        <div v-else class="rightModel">
          <div slot="header" class="clearfix mb20">
            <div class="container" v-hasPermi="['platform:pagediy:list']">
              <el-form size="small" inline @submit.native.prevent>
                <el-form-item label="模板名称：">
                  <el-input v-model.trim="name" placeholder="请输入模板名称" style="width: 350px" clearable>
                    <el-button slot="append" icon="el-icon-search" size="small" @click="getList(1)" />
                  </el-input>
                </el-form-item>
              </el-form>
            </div>
            <el-button
              type="primary"
              v-hasPermi="['platform:pagediy:save']"
              @click="handlerEditDiyPage(0)"
              size="small"
              style="font-size: 12px"
              ><i class="el-icon-plus" style="margin-right: 4px" />添加</el-button
            >
            <el-button v-hasPermi="['platform:pagediy:list']" @click="getList(1)" size="small" style="font-size: 12px"
              >刷新</el-button
            >
          </div>
          <el-table
            v-loading="listLoading"
            :data="tableData.data"
            class="table"
            @row-dblclick="handleName"
            highlight-current-row
            :cell-class-name="tableCellClassName"
            :header-cell-style="{ fontWeight: 'bold' }"
          >
            <el-table-column prop="id" label="ID" width="50" />
            <el-table-column label="模板名称" min-width="200" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <div class="acea-row">
                  <el-tag v-show="scope.row.isDefault === 1" effect="plain" class="mr5">首页</el-tag>
                  <!--v-if去判断双击的是不是当前单元格-->
                  <el-input
                    @blur="hideInput(scope.row)"
                    size="mini"
                    maxlength="15"
                    :ref="scope.row.index + ',' + scope.column.index"
                    v-model="scope.row.name"
                    v-if="
                      scope.row.index + ',' + scope.column.index == currentCell &&
                      checkPermi(['platform:pagediy:updatename'])
                    "
                  >
                  </el-input>
                  <span v-else style="cursor: pointer">{{ scope.row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="addTime" label="添加时间" min-width="180" />
            <el-table-column prop="updateTime" label="更新时间" min-width="180" />
            <el-table-column label="操作" min-width="190" fixed="right" align="center">
              <template slot-scope="scope">
                <el-button
                  v-hasPermi="['platform:pagediy:update', 'platform:pagediy:info']"
                  type="text"
                  size="small"
                  @click="handlerEditDiyPage(scope.row.id)"
                  >设计</el-button
                >
                <el-button
                  type="text"
                  size="small"
                  v-if="scope.row.isDefault !== 1 && checkPermi(['platform:pagediy:setdefault'])"
                  @click="setHomepage(scope.row.id)"
                  >设为首页</el-button
                >
                <el-button
                  v-if="scope.row.isDefault !== 1 && checkPermi(['platform:pagediy:delete'])"
                  type="text"
                  size="small"
                  @click="handleDelete(scope.row.id, scope.$index)"
                  >删除</el-button
                >
                <el-button slot="reference" type="text" size="small" @click="previewProtol(scope.row.id)"
                  >预览</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <div class="block">
            <el-pagination
              :page-sizes="[10, 20, 30]"
              :page-size="tableForm.limit"
              :current-page="tableForm.page"
              layout="total, sizes, prev, pager, next, jumper"
              :total="tableData.total"
              @size-change="handleSizeChange"
              @current-change="pageChange"
            />
          </div>
        </div>
      </div>
    </el-card>

    <!--    列表预览弹窗-->
    <el-dialog
      :visible.sync="perViewDia.visible"
      title="DIY 模版预览"
      width="430px"
      top="1"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <iframe
        v-if="perViewDia.visible"
        id="ifPerviewShop"
        :src="perViewDia.perViewUrl"
        style="width: 390px; height: 800px"
        frameborder="0"
      />
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
  pagediyListApi,
  pagediySetdefaultApi,
  pagediyDeleteApi,
  pagediyGetSetHome,
  wechatQrcodeApi,
  pagediyUpdatenameApi,
} from '@/api/devise';
import QRcode from 'qrcodejs2';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'index',
  data() {
    return {
      grid: {
        xl: 16,
        lg: 20,
        md: 20,
        sm: 24,
        xs: 24,
      },
      grid2: {
        xl: 12,
        lg: 12,
        md: 20,
        sm: 24,
        xs: 24,
      },
      listActive: 0,
      tabList: ['商城首页', '自定义页面'],
      tableForm: {
        page: 1,
        limit: 10,
        name: '',
      },
      name: '',
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      frontDomain: '',
      perViewDia: {
        // 列表二维码预览
        visible: false,
        perViewUrl: '',
      },
      Qrcode: '', //小程序二维码
      // 用一个字符串来保存当前双击的是哪一个单元格
      currentCell: null,
    };
  },
  mounted() {
    //判断移动端配置地址中是否包含https://，http://
    let url = ['https://', 'http://'];
    let frontDomain = localStorage.getItem('frontDomain');
    let contains = url.some((item) => frontDomain.includes(item));
    this.frontDomain = contains ? frontDomain : `https://${frontDomain}`;

    if (checkPermi(['platform:pagediy:list'])) this.getList();
    this.getWechatQrcode();
    this.getQRcode();
  },
  methods: {
    checkPermi,
    // 给单元格绑定横向和竖向的index，这样就能确定是哪一个单元格
    tableCellClassName({ row, column, rowIndex, columnIndex }) {
      row.index = rowIndex;
      column.index = columnIndex;
    },
    // 获得当前双击的单元格的横竖index，然后拼接成一个唯一字符串用于判断，并赋给currentCell
    // 拼接后类似这样："1,0","1,1",
    handleName(row, column) {
      this.currentCell = row.index + ',' + column.index;
      // 这里必须要setTimeout，因为在点击的时候，input才刚被v-if显示出来，不然拿不到dom
      setTimeout(() => {
        // 双击后自动获得焦点
        this.$refs[row.index + ',' + column.index].focus();
      });
    },
    // 当input失去焦点的时候，隐藏input
    hideInput(item) {
      if (!item.name) {
        return this.$message.warning('模板名称不能为空');
      } else {
        this.currentCell = null;
      }
      pagediyUpdatenameApi({
        id: item.id,
        name: item.name,
      }).then((res) => {
        this.$message.success('编辑成功');
      });
    },
    //微信二维码
    getQRcode() {
      document.getElementById('diyQrcode').innerHTML = '';
      new QRcode('diyQrcode', { width: 120, height: 120, text: this.frontDomain });
    },
    //小程序二维码
    getWechatQrcode() {
      wechatQrcodeApi({
        scene: 'id=0',
        path: 'pages/index/index',
        env_version: 'trial',
      }).then((res) => {
        this.Qrcode = res.code;
      });
    },
    //点击左侧菜单
    ProductNavTab(index) {
      this.listActive = index;
    },
    //预览
    previewProtol(id) {
      if (!id) return;
      this.perViewDia.perViewUrl = this.frontDomain + '?id=' + id;
      this.perViewDia.visible = true;
    },
    // 使用模板
    setHomepage(id) {
      this.$modalSure('把该模板设为首页').then(() => {
        pagediySetdefaultApi(id).then((res) => {
          this.$message.success('操作成功');
          this.getList();
        });
      });
    },
    // DIY 编辑首页
    handlerDiyHomePage() {
      pagediyGetSetHome().then((homePageId) => {
        this.handlerEditDiyPage(homePageId);
      });
    },
    // 添加
    handlerEditDiyPage(id) {
      const { href } = this.$router.resolve({
        path: `/page/design/creatDevise/${id}`,
      });
      window.open(href);
    },
    getList(num) {
      this.listLoading = true;
      this.tableForm.page = num ? num : this.tableForm.page;
      this.tableForm.name = encodeURIComponent(this.name);
      pagediyListApi(this.tableForm)
        .then((res) => {
          this.listLoading = false;
          this.tableData.data = res.list;
          this.tableData.total = res.total;
        })
        .then((res) => {
          this.listLoading = false;
        });
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure('删除模板吗').then(() => {
        pagediyDeleteApi({ id: id }).then((res) => {
          this.$message.success('删除成功');
          if (this.tableData.data.length === 1 && this.tableForm.page > 1)
            this.tableForm.page = this.tableForm.page - 1;
          this.getList();
        });
      });
    },
    handleSizeChange(val) {
      this.tableForm.limit = val;
      this.getList();
    },
    pageChange(val) {
      this.tableForm.page = val;
      this.getList();
    },
  },
};
</script>

<style scoped lang="scss">
.current_home {
  position: relative;
}
.mask {
  position: absolute;
  left: 0;
  width: 100%;
  top: 0;
  height: 100%;
  background-color: transparent;
}
.Qrcode-box {
  width: 100%;
  height: 120px;
}
.Qrcode_img {
  width: 120px;
}
.flex_between {
  margin-left: 60px;
  width: 100%;
  border-radius: 2px 2px 2px 2px;
  opacity: 1;
  border: 1px solid #eeeeee;
  position: relative;
  padding: 20px;

  &:before {
    content: '';
    width: 0px;
    height: 0px;
    border-right: 10px solid #eeeeee;
    border-top: 10px solid transparent;
    border-bottom: 10px solid transparent;
    position: absolute;
    top: 23px;
    left: -10px;
  }
  &:after {
    content: '';
    width: 0px;
    height: 0px;
    border-right: 9px solid #ffffff;
    border-top: 9px solid transparent;
    border-bottom: 9px solid transparent;
    position: absolute;
    top: 24px;
    left: -9px;
  }
}
.title {
  font-size: 20px;
  font-weight: 800;
  color: #303133;
}
.tips {
  font-size: 14px;
  color: #909399;
}
.tab_view {
  width: 200px;
  border-right: 1px solid #eee;
  margin-right: 40px;
  display: flex;
  flex-direction: column;
  .cell_item {
    height: 50px;
    font-size: 14px;
    line-height: 50px;
    text-align: left;
    padding-left: 30px;
    cursor: pointer;
  }
}
.tab_active {
  background: #f6fbff;
  color: #1890ff;
  border-right: 1px solid #1890ff;
}
.rightModel {
  width: 100%;
  min-width: 700px;
}
.acea-row {
  flex-wrap: nowrap !important;
}
.model_header {
  width: 350px;
}
.iframe-box {
  width: 350px;
  height: 669px;
  border-radius: 10px;
  border: 1px solid #eee;
}
</style>
