package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.zbkj.common.constants.AlipayConfig;
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.alipay.AliPayInfo;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Optional;

/**
 * 支付宝支付 Service impl
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
public class AliPayServiceImpl implements AliPayService {

    private static final Logger logger = LoggerFactory.getLogger(AliPayServiceImpl.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;

//    @Autowired
//    private UserRechargeService userRechargeService;
//    @Autowired
//    private RechargePayService rechargePayService;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private AliPayInfoService aliPayInfoService;

//    /**
//     * 查询支付结果
//     * @param orderNo 订单编号
//     * @return Boolean
//     */
//    @Override
//    public Boolean queryPayResult(String orderNo) {
//        if (StrUtil.isBlank(orderNo)) {
//            throw new CrmebException("订单编号不能为空");
//        }
//        // 切割字符串，判断是支付订单还是充值订单
//        String pre = StrUtil.subPre(orderNo, 5);
//        if (pre.equals("order")) {// 支付订单
//            StoreOrder storeOrder = storeOrderService.getByOderId(orderNo);
//            if (ObjectUtil.isNull(storeOrder)) {
//                throw new CrmebException("订单不存在");
//            }
//            if (storeOrder.getIsDel()) {
//                throw new CrmebException("订单已被删除");
//            }
//            if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
//                throw new CrmebException("不是微信支付类型订单，请重新选择支付方式");
//            }
//            if (storeOrder.getPaid()) {
//                return Boolean.TRUE;
//            }
//
//            User user = userService.getById(storeOrder.getUid());
//            if (ObjectUtil.isNull(user)) throw new CrmebException("用户不存在");
//
//            // 商户订单号，商户网站订单系统中唯一订单号，必填
//            String out_trade_no = orderNo;
//            //支付宝交易号
//            // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
//            String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
//            String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
//            String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY_2);
//            AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
//            AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
//
//            AlipayTradeQueryModel model=new AlipayTradeQueryModel();
//            model.setOutTradeNo(out_trade_no);
////            model.setTradeNo(trade_no);
//            alipay_request.setBizModel(model);
//            logger.info("alipay_request = " + alipay_request);
//
//            AlipayTradeQueryResponse alipay_response = null;
//            try {
//                alipay_response = client.execute(alipay_request);
//            } catch (AlipayApiException e) {
//                e.printStackTrace();
//                logger.error("支付宝支付查询异常，" + e.getMessage());
//                throw new CrmebException("支付宝支付查询异常");
//            }
//            logger.info("alipay_response = ");
//            if (!alipay_response.getTradeStatus().equals("TRADE_SUCCESS")) {
//                logger.error("支付宝支付结果异常，tradeStatus = " + alipay_response.getTradeStatus());
//                throw new CrmebException("支付宝支付结果异常");
//            }
//
//            Boolean execute = transactionTemplate.execute(e -> {
//                storeOrderService.updatePaid(orderNo);
//                if (storeOrder.getUseIntegral() > 0) {
//                    userService.updateIntegral(user, storeOrder.getUseIntegral(), "sub");
//                }
//                // 处理拼团
//                if (storeOrder.getCombinationId() > 0) {
//                    // 判断拼团团长是否存在
//                    StorePink headPink = new StorePink();
//                    Integer pinkId = storeOrder.getPinkId();
//                    if (pinkId > 0) {
//                        headPink = storePinkService.getById(pinkId);
//                        if (ObjectUtil.isNull(headPink) || headPink.getIsRefund().equals(true) || headPink.getStatus() == 3) {
//                            pinkId = 0;
//                        }
//                    }
//                    StoreCombination storeCombination = storeCombinationService.getById(storeOrder.getCombinationId());
//                    // 如果拼团人数已满，重新开团
//                    if (pinkId > 0) {
//                        Integer count = storePinkService.getCountByKid(pinkId);
//                        if (count >= storeCombination.getPeople()) {
//                            pinkId = 0;
//                        }
//                    }
//                    // 生成拼团表数据
//                    StorePink storePink = new StorePink();
//                    storePink.setUid(user.getUid());
//                    storePink.setAvatar(user.getAvatar());
//                    storePink.setNickname(user.getNickname());
//                    storePink.setOrderId(storeOrder.getOrderId());
//                    storePink.setOrderIdKey(storeOrder.getId());
//                    storePink.setTotalNum(storeOrder.getTotalNum());
//                    storePink.setTotalPrice(storeOrder.getTotalPrice());
//                    storePink.setCid(storeCombination.getId());
//                    storePink.setPid(storeCombination.getProductId());
//                    storePink.setPeople(storeCombination.getPeople());
//                    storePink.setPrice(storeCombination.getPrice());
//                    Integer effectiveTime = storeCombination.getEffectiveTime();// 有效小时数
//                    DateTime dateTime = cn.hutool.core.date.DateUtil.date();
//                    storePink.setAddTime(dateTime.getTime());
//                    if (pinkId > 0) {
//                        storePink.setStopTime(headPink.getStopTime());
//                    } else {
//                        DateTime hourTime = cn.hutool.core.date.DateUtil.offsetHour(dateTime, effectiveTime);
//                        long stopTime =  hourTime.getTime();
//                        if (stopTime > storeCombination.getStopTime()) {
//                            stopTime = storeCombination.getStopTime();
//                        }
//                        storePink.setStopTime(stopTime);
//                    }
//                    storePink.setKId(pinkId);
//                    storePink.setIsTpl(false);
//                    storePink.setIsRefund(false);
//                    storePink.setStatus(1);
//                    storePinkService.save(storePink);
//                    // 如果是开团，需要更新订单数据
//                    storeOrder.setPinkId(storePink.getId());
//                    storeOrderService.updateById(storeOrder);
//                }
//                return Boolean.TRUE;
//            });
//
//            if (!execute) {
//                throw new CrmebException("支付成功更新订单失败");
//            }
//            // 添加支付成功task
//            redisUtil.lPush(TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER, orderNo);
//            return Boolean.TRUE;
//        }
//        // 充值订单
//        UserRecharge userRecharge = new UserRecharge();
//        userRecharge.setOrderId(orderNo);
//        userRecharge = userRechargeService.getInfoByEntity(userRecharge);
//        if(ObjectUtil.isNull(userRecharge)){
//            throw new CrmebException("没有找到订单信息");
//        }
//        if(userRecharge.getPaid()){
//            return Boolean.TRUE;
//        }
//        // 查询订单
//        // 商户订单号，商户网站订单系统中唯一订单号，必填
//        String out_trade_no = orderNo;
//        //支付宝交易号
////            String trade_no = new String("1111");
//        /**********************/
//        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
//        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
//        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
//        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY_2);
//        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
//        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
//
//        AlipayTradeQueryModel model=new AlipayTradeQueryModel();
//        model.setOutTradeNo(out_trade_no);
////            model.setTradeNo(trade_no);
//        alipay_request.setBizModel(model);
//        logger.info("alipay_request = " + alipay_request);
//
//        AlipayTradeQueryResponse alipay_response = null;
//        try {
//            alipay_response = client.execute(alipay_request);
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//            logger.error("支付宝支付查询异常，" + e.getMessage());
//            throw new CrmebException("支付宝支付查询异常");
//        }
//        logger.info("alipay_response = ");
//        if (!alipay_response.getTradeStatus().equals("TRADE_SUCCESS")) {
//            logger.error("支付宝支付结果异常，tradeStatus = " + alipay_response.getTradeStatus());
//            throw new CrmebException("支付宝支付结果异常");
//        }
//        // 支付成功处理
//        Boolean rechargePayAfter = rechargePayService.paySuccess(userRecharge);
//        if (!rechargePayAfter) {
//            throw new CrmebException("wechat pay error : 数据保存失败==》" + orderNo);
//        }
//        return rechargePayAfter;
//    }

    /**
     * 支付宝退款
     * @param outTradeNo 支付宝交易号
     * @param refundOrderNo 退款单号
     * @param refundReasonWapExplain 退款说明
     * @param refundPrice 退款金额
     */
    @Override
    public void refund(String outTradeNo, String refundOrderNo, String refundReasonWapExplain, BigDecimal refundPrice) {

        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
        //商户订单号，和支付宝交易号二选一
        String out_trade_no = outTradeNo;
        //支付宝交易号，和商户订单号二选一
//        String trade_no = outTradeNo;
        //退款金额，不能大于订单总金额
        String refund_amount = refundPrice.toString();
        //退款的原因说明
        String refund_reason = refundReasonWapExplain;
        //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
        String out_request_no = refundOrderNo;
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
        AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();

        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(out_trade_no);
//        model.setTradeNo(trade_no);
        model.setRefundAmount(refund_amount);
        model.setRefundReason(refund_reason);
        model.setOutRequestNo(out_request_no);
        alipay_request.setBizModel(model);
        logger.info("alipay_request = " + alipay_request);

        AlipayTradeRefundResponse alipay_response = null;
        try {
            alipay_response = client.execute(alipay_request);
        } catch (AlipayApiException e) {
            logger.error("支付宝退款申请异常，alipay_response = " + alipay_response);
            logger.error(e.getMessage());
            throw new CrmebException("支付宝支付结果异常");
        }
        logger.info("alipay_response = " + alipay_response.getBody());
    }

    /**
     * 支付宝支付
     * @param orderNo 订单号
     * @param price 支付金额
     * @param orderType 订单类型：order - 商品订单，recharge - 充值订单
     * @param payChannel 支付渠道：alipayApp - 支付宝app支付, alipay - 支付宝支付
     * @return 支付宝调用结果
     */
    @Override
    public String pay(String orderNo, BigDecimal price, String orderType, String payChannel) {
        if (payChannel.equals(PayConstants.PAY_CHANNEL_ALI_APP_PAY)) {// APP 支付
            return appPay(orderNo, price, orderType);
        }
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = orderNo;
        //付款金额，必填
        String total_amount = price.toString();
        //订单名称，必填
        String subject = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        // 商品描述，可空
        // String body = "用户订购商品个数：1";
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "30m";

        //获得初始化的AlipayClient
        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
        //设置请求参数
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        alipayRequest.setReturnUrl(systemConfigService.getValueByKey(orderType.equals("order") ? AlipayConfig.return_url : AlipayConfig.recharge_return_url));
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);
        alipayRequest.setNotifyUrl(apiDomain + PayConstants.ALI_PAY_NOTIFY_API_URI);

        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(out_trade_no);
        model.setSubject(subject);
        model.setTotalAmount(total_amount);
//            model.setBody(body);
        model.setTimeoutExpress(timeout_express);
        model.setProductCode("QUICK_WAP_PAY");
        model.setQuitUrl(systemConfigService.getValueByKey(orderType.equals("order") ? AlipayConfig.quit_url : AlipayConfig.recharge_quit_url));

        String encode = "type=" + (orderType.equals("order") ? PayConstants.PAY_SERVICE_TYPE_ORDER : PayConstants.PAY_SERVICE_TYPE_RECHARGE);
        try {
            encode = URLEncoder.encode(encode, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new CrmebException("支付宝参数UrlEncode异常");
        }
        model.setPassbackParams(encode);

        alipayRequest.setBizModel(model);
        logger.info("alipayRequest = " + alipayRequest);
        //请求
        String result;
        AlipayTradeWapPayResponse response;
        try {
            response = alipayClient.pageExecute(alipayRequest);
            result = response.getBody();
        } catch (AlipayApiException e) {
            logger.error("支付宝订单生成失败," + e.getErrMsg());
            throw new CrmebException(e.getErrMsg());
        }
        logger.info("result = " + result);

        // 保存支付宝订单信息
        AliPayInfo aliPayInfo = new AliPayInfo();
        aliPayInfo.setAppId(aliPayAppid);
        aliPayInfo.setTimestamp(DateUtil.now());
        aliPayInfo.setBody("");
        aliPayInfo.setSubject(subject);
        aliPayInfo.setOutTradeNo(out_trade_no);
        aliPayInfo.setTimeoutExpress(timeout_express);
        aliPayInfo.setTotalAmount(total_amount);
        aliPayInfo.setPassbackParams(model.getPassbackParams());
        aliPayInfo.setNotifyUrl(alipayRequest.getNotifyUrl());
        aliPayInfo.setCode(Optional.ofNullable(response.getCode()).orElse(""));
        aliPayInfo.setMsg(Optional.ofNullable(response.getMsg()).orElse(""));
        aliPayInfo.setSubCode(Optional.ofNullable(response.getSubCode()).orElse(""));
        aliPayInfo.setSubMsg(Optional.ofNullable(response.getSubMsg()).orElse(""));
        aliPayInfo.setTradeNo(Optional.ofNullable(response.getTradeNo()).orElse(""));
        aliPayInfo.setSellerId(Optional.ofNullable(response.getSellerId()).orElse(""));
        aliPayInfoService.save(aliPayInfo);
        return result;
    }

    /**
     * 支付宝App支付
     * @param orderNo 订单号
     * @param price 支付金额
     * @param orderType 订单类型：order - 商品订单，recharge - 充值订单
     */
    private String appPay(String orderNo, BigDecimal price, String orderType) {
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = orderNo;
        //付款金额，必填
        String total_amount = price.toString();
        //订单名称，必填
        String subject = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        //商品描述，可空
//            String body = "用户订购商品个数：1";

        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "30m";

        //获得初始化的AlipayClient
        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
//                model.setBody("我是测试数据");
        model.setSubject(subject);
        model.setOutTradeNo(out_trade_no);
        model.setTimeoutExpress(timeout_express);
        model.setTotalAmount(total_amount);
        model.setProductCode("QUICK_MSECURITY_PAY");

        String encode = "type=" + (orderType.equals("order") ? PayConstants.PAY_SERVICE_TYPE_ORDER : PayConstants.PAY_SERVICE_TYPE_RECHARGE);
        try {
            encode = URLEncoder.encode(encode, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new CrmebException("支付宝参数UrlEncode异常");
        }
        model.setPassbackParams(encode);

        request.setBizModel(model);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);
        request.setNotifyUrl(apiDomain + PayConstants.ALI_PAY_NOTIFY_API_URI);

        //请求
        String result;
        AlipayTradeAppPayResponse response;
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            response = alipayClient.sdkExecute(request);
            result = response.getBody();
        } catch (AlipayApiException e) {
            logger.error("生成支付宝app支付请求异常," + e.getErrMsg());
            throw new CrmebException(e.getErrMsg());
        }
        logger.info("支付宝app result = " + result);
        // 保存支付宝订单信息
        AliPayInfo aliPayInfo = new AliPayInfo();
        aliPayInfo.setAppId(aliPayAppid);
        aliPayInfo.setTimestamp(DateUtil.now());
        aliPayInfo.setBody("");
        aliPayInfo.setSubject(subject);
        aliPayInfo.setOutTradeNo(out_trade_no);
        aliPayInfo.setTimeoutExpress(timeout_express);
        aliPayInfo.setTotalAmount(total_amount);
        aliPayInfo.setPassbackParams(model.getPassbackParams());
        aliPayInfo.setNotifyUrl(request.getNotifyUrl());
        aliPayInfo.setCode(Optional.ofNullable(response.getCode()).orElse(""));
        aliPayInfo.setMsg(Optional.ofNullable(response.getMsg()).orElse(""));
        aliPayInfo.setSubCode(Optional.ofNullable(response.getSubCode()).orElse(""));
        aliPayInfo.setSubMsg(Optional.ofNullable(response.getSubMsg()).orElse(""));
        aliPayInfo.setTradeNo(Optional.ofNullable(response.getTradeNo()).orElse(""));
        aliPayInfo.setSellerId(Optional.ofNullable(response.getSellerId()).orElse(""));
        aliPayInfoService.save(aliPayInfo);
        return result;


    }

//    /**
//     * 查询退款
//     * @param orderNo 订单编号
//     */
//    @Override
//    public Boolean queryRefund(String orderNo) {
//        StoreOrder storeOrder = orderService.getByOderId(orderNo);
//        if (ObjectUtil.isNull(storeOrder)) {
//            throw new CrmebException("订单不存在");
//        }
//        if (storeOrder.getIsDel()) {
//            throw new CrmebException("订单已被删除");
//        }
//        if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
//            throw new CrmebException("不是支付宝支付类型订单");
//        }
//        if (!storeOrder.getPaid()) {
//            throw new CrmebException("订单未支付");
//        }
//        if (!storeOrder.getRefundStatus().equals(3)) {
//            throw new CrmebException("订单已不在退款中状态");
//        }
//
//        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
//        //商户订单号，和支付宝交易号二选一
//        String out_trade_no = orderNo;
//        //支付宝交易号，和商户订单号二选一
//        //        String trade_no = new String(request.getParameter("WIDtrade_no").getBytes("ISO-8859-1"),"UTF-8");
//        //请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号
//        //        String out_request_no = new String(request.getParameter("WIDout_request_no").getBytes("ISO-8859-1"),"UTF-8");
//        //        String out_request_no = new String("WIDout_request_no");
//        /**********************/
//        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
//        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
//        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
//        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY);
//        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
//
//        AlipayTradeFastpayRefundQueryRequest alipay_request = new AlipayTradeFastpayRefundQueryRequest();
//
//        AlipayTradeFastpayRefundQueryModel model=new AlipayTradeFastpayRefundQueryModel();
//        model.setOutTradeNo(out_trade_no);
////        model.setTradeNo(trade_no);
////        model.setOutRequestNo(out_request_no);
//        alipay_request.setBizModel(model);
//        logger.info("alipay_request = " + alipay_request);
//        AlipayTradeFastpayRefundQueryResponse alipay_response= null;
//        try {
//            alipay_response = client.execute(alipay_request);
//        } catch (AlipayApiException e) {
//            logger.error("支付宝退款查询异常，alipay_response = " + alipay_response);
//            throw new CrmebException("支付宝退款查询异常");
//        }
//        logger.info("alipay_response = " + alipay_response.getBody());
//
//        if (alipay_response.getRefundStatus().equals("REFUND_PROCESSING")) {
//            throw new CrmebException("退款处理中");
//        }
//        if (alipay_response.getRefundStatus().equals("REFUND_FAIL")) {
//            throw new CrmebException("支付宝退款失败");
//        }
//
//        storeOrder.setRefundStatus(2);
//        boolean update = orderService.updateById(storeOrder);
//        if (update) {
//            // 退款task
//            redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, storeOrder.getId());
//        } else {
//            logger.warn("支付宝退款订单更新失败==>" + alipay_response);
//        }
//        return update;
//    }
}
