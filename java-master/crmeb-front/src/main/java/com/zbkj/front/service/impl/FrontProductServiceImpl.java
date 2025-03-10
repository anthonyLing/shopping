package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttr;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.record.BrowseRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.front.service.FrontProductService;
import com.zbkj.front.service.SeckillService;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IndexServiceImpl 接口实现
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
@Service
public class FrontProductServiceImpl implements FrontProductService {

    private static final Logger logger = LoggerFactory.getLogger(FrontProductServiceImpl.class);

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductReplyService productReplyService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private ProductAttrService productAttrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private PayComponentProductService componentProductService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private ProductGuaranteeService productGuaranteeService;
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private ActivityStyleService activityStyleService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private BrowseRecordService browseRecordService;
    @Autowired
    private CouponService couponService;


    /**
     * 商品列表
     *
     * @return List<ProductFrontResponse>
     */
    @Override
    public PageInfo<ProductFrontResponse> getList(ProductFrontSearchRequest request, PageParamRequest pageRequest) {
        return productService.findH5List(request, pageRequest);
    }

    /**
     * 获取商品详情
     *
     * @param id   视频号商品id
     * @param type 空 = 正常 video = 视频号商品 seckill = 秒杀商品
     * @return 商品详情信息
     */
    @Override
    public ProductDetailResponse getDetail(Integer id, String type) {
        logger.info("商品详情参数:id={},type={}", id, type);
        if (StrUtil.isBlank(type) || type.equals("undefined")) {
            type = "normal";
        }
        if (!type.equals("video") && !type.equals("normal") && !type.equals("seckill")) {
            throw new CrmebException("商品类型未知");
        }
        if (type.equals("video")) {
            logger.info("商品详情 --》 加载视频号商品id={},", id);
            return componentProductService.getH5Detail(id);
        }
        if (type.equals("seckill")) {
            return seckillService.getProductDetail(id);
        }
        logger.info("商品详情 --》 加载普通商品详情");
        // 查询普通商品
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        Product product = productService.getH5Detail(id);
        productDetailResponse.setProductInfo(product);
        if (StrUtil.isNotBlank(product.getGuaranteeIds())) {
            productDetailResponse.setGuaranteeList(productGuaranteeService.findByIdList(CrmebUtil.stringToArray(product.getGuaranteeIds())));
        }
        // 获取商品规格
        List<ProductAttr> attrList = productAttrService.getListByProductIdAndType(product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
        // 根据制式设置attr属性
        productDetailResponse.setProductAttr(attrList);
        // 根据制式设置sku属性
        LinkedHashMap<String, ProductAttrValueResponse> skuMap = new LinkedHashMap<>();
        List<ProductAttrValue> productAttrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
        for (ProductAttrValue productAttrValue : productAttrValueList) {
            ProductAttrValueResponse atr = new ProductAttrValueResponse();
            BeanUtils.copyProperties(productAttrValue, atr);
            skuMap.put(atr.getSku(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

        // 获取商户信息
        Merchant merchant = merchantService.getById(product.getMerId());
        ProductMerchantResponse merchantResponse = new ProductMerchantResponse();
        BeanUtils.copyProperties(merchant, merchantResponse);
        merchantResponse.setCollectNum(userMerchantCollectService.getCountByMerId(merchant.getId()));
        // 获取商户推荐商品
        List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 6);
        merchantResponse.setProList(merchantProductResponseList);
        productDetailResponse.setMerchantInfo(merchantResponse);

        // 获取用户
        Integer userId = userService.getUserId();
        productDetailResponse.setUserCollect(false);
        // 用户收藏
        if (userId > 0) {
            // 查询用户是否收藏收藏
            productDetailResponse.setUserCollect(productRelationService.existCollectByUser(userId, product.getId()));
        }
        // 异步调用进行数据统计
        asyncService.productDetailStatistics(product.getId(), userId);
        // 添加活动背景
        productDetailResponse.getProductInfo().setActivityStyle(activityStyleService.makeActivityBackgroundStyle(productDetailResponse.getProductInfo()));

        // 获取优惠券（商户）数据2条
        productDetailResponse.setCouponList(couponService.findProductDetailLimit(product.getId(), product.getMerId(), 2));

        return productDetailResponse;
    }

    /**
     * 商品评论列表
     *
     * @param proId            商品编号
     * @param type             评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return List<ProductReplyResponse>
     */
    @Override
    public PageInfo<ProductReplyResponse> getReplyList(Integer proId, Integer type, PageParamRequest pageParamRequest) {
        return productReplyService.getH5List(proId, type, pageParamRequest);
    }

    /**
     * 产品评价数量和好评度
     *
     * @return StoreProductReplayCountResponse
     */
    @Override
    public ProductReplayCountResponse getReplyCount(Integer id) {
        return productReplyService.getH5Count(id);
    }

    /**
     * 商品列表转为首页商品格式
     *
     * @param productList 商品列表
     */
    private List<ProductCommonResponse> productToIndexProduct(List<Product> productList) {
        List<ProductCommonResponse> productResponseArrayList = new ArrayList<>();
        for (Product product : productList) {
            ProductCommonResponse productResponse = new ProductCommonResponse();
            BeanUtils.copyProperties(product, productResponse);
            // 评论总数
            Integer sumCount = productReplyService.getCountByScore(product.getId(), ProductConstants.PRODUCT_REPLY_TYPE_ALL);
            // 好评总数
            Integer goodCount = productReplyService.getCountByScore(product.getId(), ProductConstants.PRODUCT_REPLY_TYPE_GOOD);
            String replyChance = "0";
            if (sumCount > 0 && goodCount > 0) {
                replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
            }
            productResponse.setReplyNum(sumCount);
            productResponse.setPositiveRatio(replyChance);
            productResponseArrayList.add(productResponse);
        }
        return productResponseArrayList;
    }

    /**
     * 商品详情评论
     *
     * @param id 商品id
     * @return ProductDetailReplyResponse
     * 评论只有一条，图文
     * 评价总数
     * 好评率
     */
    @Override
    public ProductDetailReplyResponse getProductReply(Integer id) {
        return productReplyService.getH5ProductReply(id);
    }

    /**
     * 商户商品列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<ProductCommonResponse> getMerchantProList(MerchantProductSearchRequest request, PageParamRequest pageParamRequest) {
        PageInfo<Product> pageInfo = productService.findMerchantProH5List(request, pageParamRequest);
        List<Product> productList = pageInfo.getList();
        productList = activityStyleService.makeActivityBorderStyle(productList);
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<ProductCommonResponse> responseList = productToIndexProduct(productList);
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 优惠券商品列表
     *
     * @param request          搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductFrontResponse> getCouponProList(CouponProductSearchRequest request) {
        PageInfo<Product> pageInfo = productService.getCouponProList(request);
        List<Product> productList = pageInfo.getList();
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<ProductFrontResponse> frontResponseList = productList.stream().map(product -> {
            ProductFrontResponse response = new ProductFrontResponse();
            BeanUtils.copyProperties(product, response);
            Merchant merchant = merchantMap.get(product.getMerId());
            response.setMerName(merchant.getName());
            response.setMerCategoryId(merchant.getCategoryId());
            response.setMerTypeId(merchant.getTypeId());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, frontResponseList);
    }

    /**
     * 获取已购商品列表
     *
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<ProductSimpleResponse> findPurchasedList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        PageInfo<OrderDetail> pageInfo = orderDetailService.findPurchasedList(userId, pageParamRequest);
        List<OrderDetail> detailList = pageInfo.getList();
        if (CollUtil.isEmpty(detailList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<Integer> proIdList = detailList.stream().map(OrderDetail::getProductId).distinct().collect(Collectors.toList());
        Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
        List<ProductSimpleResponse> responseList = detailList.stream().map(detail -> {
            Product product = productMap.get(detail.getProductId());
            ProductSimpleResponse response = new ProductSimpleResponse();
            response.setProductId(product.getId());
            response.setName(product.getName());
            response.setImage(product.getImage());
            response.setPrice(product.getPrice());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 足迹商品列表
     *
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<ProductSimpleResponse> findBrowseList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        PageInfo<BrowseRecord> pageInfo = browseRecordService.findPageByUserId(userId, pageParamRequest);
        List<BrowseRecord> browseRecordList = pageInfo.getList();
        if (CollUtil.isEmpty(browseRecordList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<Integer> proIdList = browseRecordList.stream().map(BrowseRecord::getProductId).distinct().collect(Collectors.toList());
        Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
        List<ProductSimpleResponse> responseList = browseRecordList.stream().map(detail -> {
            Product product = productMap.get(detail.getProductId());
            ProductSimpleResponse response = new ProductSimpleResponse();
            response.setProductId(product.getId());
            response.setName(product.getName());
            response.setImage(product.getImage());
            response.setPrice(product.getPrice());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 系统优惠券商品列表
     *
     * @param request 搜索参数
     */
    @Override
    public PageInfo<ProductFrontResponse> findCouponProductList(SystemCouponProductSearchRequest request) {
        Coupon coupon = couponService.getById(request.getCouponId());
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException("优惠券不存在");
        }
        if (!coupon.getStatus()) {
            throw new CrmebException("请重新选择优惠券");
        }
        PageInfo<Product> pageInfo = productService.findCouponProductList(coupon.getId(), coupon.getCategory(), coupon.getLinkedData(), request);
        List<Product> productList = pageInfo.getList();
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<ProductFrontResponse> frontResponseList = productList.stream().map(product -> {
            ProductFrontResponse response = new ProductFrontResponse();
            BeanUtils.copyProperties(product, response);
            Merchant merchant = merchantMap.get(product.getMerId());
            response.setMerName(merchant.getName());
            response.setMerCategoryId(merchant.getCategoryId());
            response.setMerTypeId(merchant.getTypeId());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, frontResponseList);
    }


}

