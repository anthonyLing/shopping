package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 首页商户列表
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
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="IndexMerchantResponse对象", description="首页商户列表")
public class IndexMerchantResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商户ID")
    private Integer id;

    @ApiModelProperty(value = "商户名称")
    private String name;

    @ApiModelProperty(value = "商户logo（横）")
    private String rectangleLogo;

    @ApiModelProperty(value = "商户街背景图")
    private String streetBackImage;

    @ApiModelProperty(value = "商户封面图")
    private String coverImage;

    @ApiModelProperty(value = "商户分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "商户类型ID")
    private Integer typeId;

    @ApiModelProperty(value = "店铺关注数量")
    private Integer followerNum;

    @ApiModelProperty(value = "是否自营：0-自营，1-非自营")
    private Boolean isSelf;

    @ApiModelProperty(value = "商户对应推荐商品")
    private List<ProMerchantProductResponse> proList;

}
