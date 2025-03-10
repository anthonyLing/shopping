package com.zbkj.service.service;

import com.alibaba.fastjson.JSONException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.SimpleProductVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ProductService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface ProductService extends IService<Product> {

    // ==================================================================================
    // ==================           商户端                   =============================
    // ==================================================================================

    /**
     * 获取产品列表Admin
     * @param request 筛选参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<AdminProductListResponse> getAdminList(ProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增商品
     * @param request 商品请求对象
     * @return Boolean
     */
    Boolean save(ProductAddRequest request);

    /**
     * 商品回收/删除
     * @param request 删除参数
     * @return Boolean
     */
    Boolean deleteProduct(ProductDeleteRequest request);

    /**
     * 恢复已删除商品
     * @param productId 商品id
     * @return 恢复结果
     */
    Boolean restoreProduct(Integer productId);

    /**
     * 更新商品信息
     * @param productRequest 商品参数
     * @return 更新结果
     */
    Boolean update(ProductAddRequest productRequest);

    /**
     * 获取tabsHeader对应数量
     * @return List
     */
    List<ProductTabsHeaderResponse> getTabsHeader();

    /**
     * 下架
     * @param id 商品id
     */
    Boolean offShelf(Integer id);

    /**
     * 上架
     * @param id 商品id
     * @return Boolean
     */
    Boolean putOnShelf(Integer id);

    /**
     * 快捷添加库存
     * @param request 添加库存参数
     * @return Boolean
     */
    Boolean quickAddStock(ProductAddStockRequest request);

    /**
     * 商品免审编辑
     * @param request 商品免审编辑参数
     * @return Boolean
     */
    Boolean reviewFreeEdit(ProductReviewFreeEditRequest request);

    /**
     * 是否有商品使用对应的商户商品分类
     * @param id 商户商品分类id
     * @return Boolean
     */
    Boolean isExistStoreCategory(Integer id);

    /**
     * 是否有商品使用运费模板
     * @return Boolean
     */
    Boolean isUseShippingTemplateId(Integer templateId);

    /**
     * 商品提审
     * @param id 商品ID
     * @return Boolean
     */
    Boolean submitAudit(Integer id);


    // ==================================================================================
    // ==================           平台端                   =============================
    // ==================================================================================

    /**
     * 平台端商品分页列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<PlatformProductListResponse> getPlatformPageList(ProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据id集合查询对应商品列表
     * @param ids 商品id集合 Integer 对象集合
     * @return 商品列表
     */
    List<PlatformProductListResponse> getPlatformListForIds(List<String> ids);

    /**
     * 根据id集合以及活动上限加载商品数据
     * @param ids id集合
     * @return 平台商品列表
     */
    List<PlatformProductListResponse> getPlatformListForIdsByLimit(List<String> ids);

    /**
     * 商品审核
     * @param request 审核参数
     * @return Boolean
     */
    Boolean audit(ProductAuditRequest request);

    /**
     * 判断商品是否使用品牌
     * @param brandId 品牌id
     * @return Boolean
     */
    Boolean isUseBrand(Integer brandId);

    /**
     * 判断商品是否使用平台分类
     * @param categoryId 平台分类id
     * @return Boolean
     */
    Boolean isUsePlatformCategory(Integer categoryId);

    /**
     * 查询使用服务保障的商品列表
     * @param gid 服务保障id
     * @return List
     */
    List<Product> findUseGuarantee(Integer gid);

    /**
     * 判断商品是否使用服务保障
     * @param gid 服务保障id
     * @return Boolean
     */
    Boolean isUseGuarantee(Integer gid);

    /**
     * 平台端获取商品表头数量
     * @return List
     */
    List<ProductTabsHeaderResponse> getPlatformTabsHeader();

    /**
     * 平台端商品编辑
     * @param request 商品编辑参数
     * @return Boolean
     */
    Boolean platUpdate(ProductPlatUpdateRequest request);

    /**
     * 强制下架商户所有商品
     * @param merchantId 商户ID
     * @return Boolean
     */
    Boolean forcedRemovalAll(Integer merchantId);


    // ==================================================================================
    // ==================           公用部分                 =============================
    // ==================================================================================

    /**
     * 商品详情（管理端）
     * @param id 商品id
     * @return ProductInfoResponse
     */
    ProductInfoResponse getInfo(Integer id);

    /**
     * 添加/扣减库存
     * @param id 商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationStock(Integer id, Integer num, String type);

    // ==================================================================================
    // ==================           移动端                   =============================
    // ==================================================================================

    /**
     * 根据id集合获取商品简单信息
     * @param productIds id集合
     * @return 商品信息
     */
    List<SimpleProductVo> getSimpleListInIds(List<Integer> productIds);

    /**
     * 根据其他平台url导入产品信息
     * @param url 待倒入平台的url
     * @param tag 待导入平台标识
     * @return 待导入的商品信息
     */
    ProductRequest importProductFromUrl(String url, int tag) throws IOException, JSONException;

    /**
     * 根据其他平台url导入产品信息
     * @param url 待倒入平台的url
     * @param tag 待导入平台标识
     * @return 待导入的商品信息
     */
    ProductResponseForCopyProduct importProductFrom99Api(String url, int tag) throws IOException, JSONException;

    /**
     * 首页商品列表
     * @param pageParamRequest 分页参数
     * @param cid 一级商品分类id，全部传0
     * @return CommonPage
     */
    PageInfo<Product> getIndexProduct(Integer cid, PageParamRequest pageParamRequest);

    /**
     * 获取商品移动端列表
     * @param request 筛选参数
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<ProductFrontResponse> findH5List(ProductFrontSearchRequest request, PageParamRequest pageRequest);

    /**
     * 获取移动端商品详情
     * @param id 商品id
     * @return Product
     */
    Product getH5Detail(Integer id);

    /**
     * 获取购物车商品信息
     * @param productId 商品编号
     * @return Product
     */
    Product getCartByProId(Integer productId);

    /**
     * 根据日期获取新增商品数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getNewProductByDate(String date);

    /**
     * 获取所有未删除的商品
     * @return List<Product>
     */
    List<Product> findAllProductByNotDelete();

    /**
     * 模糊搜索商品名称
     * @param productName 商品名称
     * @param merId 商户Id
     * @return List
     */
    List<Product> likeProductName(String productName, Integer merId);

    /**
     * 销售中（上架）商品数量
     * @return Integer
     */
    Integer getOnSaleNum(Integer merId);

    /**
     * 强制下加商品
     * @param request 商品id参数
     * @return Boolean
     */
    Boolean forceDown(ProductForceDownRequest request);

    /**
     * 商品增加浏览量
     * @param proId 商品id
     * @return Boolean
     */
    Boolean addBrowse(Integer proId);

    /**
     * 获取商户推荐商品
     * @param merId 商户id
     * @param num 查询商品数量
     * @return List
     */
    List<ProMerchantProductResponse> getRecommendedProductsByMerId(Integer merId, Integer num);

    /**
     * 商户商品列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    PageInfo<Product> findMerchantProH5List(MerchantProductSearchRequest request, PageParamRequest pageParamRequest);


    /**
     * 获取待审核商品数量
     */
    Integer getAwaitAuditNum(Integer merId);

    /**
     * 下架商品商品
     * @param merId 商户id
     */
    Boolean downByMerId(Integer merId);

    /**
     * 优惠券商品列表
     * @param request 搜索参数
     */
    PageInfo<Product> getCouponProList(CouponProductSearchRequest request);

    /**
     * 获取复制商品配置
     * @return copyType 复制类型：1：一号通
     *         copyNum 复制条数(一号通类型下有值)
     */
    MyRecord copyConfig();

    /**
     * 复制平台商品
     * @param url 商品链接
     * @return MyRecord
     */
    MyRecord copyProduct(String url);

    /**
     * 获取商品Map
     * @param proIdList 商品id列表
     * @return Map
     */
    Map<Integer, Product> getMapByIdList(List<Integer> proIdList);

    /**
     * 商品搜索分页列表（活动）
     * @param request 搜索参数
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<ProductActivityResponse> getActivitySearchPage(ProductActivitySearchRequest request, PageParamRequest pageRequest);

    /**
     * 商品搜索分页列表（活动）商户端
     * @param request 搜索参数
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<ProductActivityResponse> getActivitySearchPageByMerchant(ProductActivitySearchRequest request, PageParamRequest pageRequest);

    /**
     * 秒杀回滚库存
     * @param id 商品ID
     * @param num 数量
     * @param sales 销量
     */
    Boolean seckillRollBack(Integer id, Integer num, Integer sales);

    /**
     * 活动操作库存
     * @param id 商品ID
     * @param num 数量
     * @param sales 销量
     * @param type 类型
     */
    Boolean activityOperationStock(Integer id, Integer num, Integer sales, String type);

    /**
     * 把商品列表转换为 平台商品商品列表格式
     * @param productList 商品列表
     * @return 平台商品列表格式
     */
    List<PlatformProductListResponse> productListToPlatFromProductListResponse(List<Product> productList);

    /**
     * 领券中心优惠券商品列表
     * @param couponCategory 优惠券类型：1-商家券, 2-商品券, 3-通用券，4-品类券，5-品牌券，6-跨店券
     * @param pidList 商品ID列表
     * @param linkedData 优惠券关联参数
     * @param pcIdList 商品分类ID列表（3级）
     */
    List<SimpleProductVo> findCouponListLimit3(Integer couponCategory, List<Integer> pidList, String linkedData, List<Integer> pcIdList);

    /**
     * 系统优惠券商品列表
     * @param couponId 优惠券ID
     * @param couponCategory 优惠券分类
     * @param couponLinkedDate 优惠券关联参数
     * @param pageParamRequest 分页参数
     */
    PageInfo<Product> findCouponProductList(Integer couponId, Integer couponCategory, String couponLinkedDate, SystemCouponProductSearchRequest pageParamRequest);

    /**
     * 通过ID获取商品列表
     * @param proIdsList 商品ID列表
     */
    List<Product> findByIds(List<Integer> proIdsList);
}
