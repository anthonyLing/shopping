package com.zbkj.front.controller;


import com.zbkj.common.request.CitySearchRequest;
import com.zbkj.common.response.CityResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.CityVo;
import com.zbkj.service.service.CityRegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 城市服务
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
@RequestMapping("api/front/city")
@Api(tags = "移动端城市控制器")
public class CityController {

    @Autowired
    private CityRegionService cityRegionService;

    @ApiOperation(value = "获取城市区域tree结构的列表")
    @RequestMapping(value = "/list/tree", method = RequestMethod.GET)
    public CommonResult<List<CityVo>> getListTree() {
        return CommonResult.success(cityRegionService.getRegionListTree());
    }

    @ApiOperation(value = "获取城市区域分级列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<CityResponse>> getList(@ModelAttribute @Validated CitySearchRequest request) {
        return CommonResult.success(cityRegionService.getCityRegionList(request));
    }
}



