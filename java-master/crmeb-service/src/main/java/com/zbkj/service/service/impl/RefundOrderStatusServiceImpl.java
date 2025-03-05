package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.order.RefundOrderStatus;
import com.zbkj.service.dao.RefundOrderStatusDao;
import com.zbkj.service.service.RefundOrderStatusService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
*  RefundOrderStatusServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class RefundOrderStatusServiceImpl extends ServiceImpl<RefundOrderStatusDao, RefundOrderStatus> implements RefundOrderStatusService {

    @Resource
    private RefundOrderStatusDao dao;

}

