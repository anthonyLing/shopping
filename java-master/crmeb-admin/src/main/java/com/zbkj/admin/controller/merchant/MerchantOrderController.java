package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.MerchantOrderPageResponse;
import com.zbkj.common.response.OrderAdminDetailResponse;
import com.zbkj.common.response.OrderCountItemResponse;
import com.zbkj.common.response.OrderInvoiceResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.service.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户侧订单控制器
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
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/order")
@Api(tags = "商户侧订单控制器") //配合swagger使用
public class MerchantOrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAuthority('merchant:order:page:list')")
    @ApiOperation(value = "商户端订单分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantOrderPageResponse>> getList(@Validated OrderSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(orderService.getMerchantAdminPage(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:order:status:num')")
    @ApiOperation(value = "获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<OrderCountItemResponse> getOrderStatusNum(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit) {
        return CommonResult.success(orderService.getMerchantOrderStatusNum(dateLimit));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "商户删除订单")
    @PreAuthorize("hasAuthority('merchant:order:delete')")
    @ApiOperation(value = "订单删除")
    @RequestMapping(value = "/delete/{orderNo}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(name = "orderNo") String orderNo) {
        if (orderService.merchantDeleteByOrderNo(orderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户备注订单")
    @PreAuthorize("hasAuthority('merchant:order:mark')")
    @ApiOperation(value = "商户备注订单")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestBody @Validated OrderRemarkRequest request) {
        if (orderService.merchantMark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:order:info')")
    @ApiOperation(value = "订单详情")
    @RequestMapping(value = "/info/{orderNo}", method = RequestMethod.GET)
    public CommonResult<OrderAdminDetailResponse> info(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(orderService.adminDetail(orderNo));
    }

    @PreAuthorize("hasAuthority('merchant:order:detail:list')")
    @ApiOperation(value = "订单细节详情列表（发货使用）")
    @RequestMapping(value = "/{orderNo}/detail/list", method = RequestMethod.GET)
    public CommonResult<List<OrderDetail>> getDetailList(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(orderService.getDetailList(orderNo));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "订单发货")
    @PreAuthorize("hasAuthority('merchant:order:send')")
    @ApiOperation(value = "订单发货")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public CommonResult<Boolean> send(@RequestBody @Validated OrderSendRequest request) {
        if (orderService.send(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:order:invoice:list')")
    @ApiOperation(value = "获取订单发货单列表")
    @RequestMapping(value = "/{orderNo}/invoice/list", method = RequestMethod.GET)
    public CommonResult<List<OrderInvoiceResponse>> getInvoiceList(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(orderService.getInvoiceList(orderNo));
    }

    @PreAuthorize("hasAuthority('merchant:order:logistics:info')")
    @ApiOperation(value = "订单物流详情")
    @RequestMapping(value = "/get/{invoiceId}/logistics/info", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@PathVariable(value = "invoiceId") Integer invoiceId) {
        return CommonResult.success(orderService.getLogisticsInfo(invoiceId));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "核销码核销订单")
    @PreAuthorize("hasAuthority('merchant:order:verification')")
    @ApiOperation(value = "核销码核销订单")
    @RequestMapping(value = "/verification", method = RequestMethod.POST)
    public CommonResult<Object> verificationOrder(@RequestBody @Validated OrderVerificationRequest request) {
        return CommonResult.success(orderService.verificationOrderByCode(request.getVerifyCode()));
    }

}



