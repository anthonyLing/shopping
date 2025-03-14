package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zbkj.admin.filter.TokenComponent;
import com.zbkj.admin.service.AdminLoginService;
import com.zbkj.admin.service.ValidateCodeService;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.admin.SystemMenu;
import com.zbkj.common.model.admin.SystemPermissions;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.request.LoginAdminUpdateRequest;
import com.zbkj.common.request.SystemAdminLoginRequest;
import com.zbkj.common.response.AdminLoginPicResponse;
import com.zbkj.common.response.LoginAdminResponse;
import com.zbkj.common.response.MenusResponse;
import com.zbkj.common.response.SystemLoginResponse;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.common.vo.MenuTree;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 管理端登录服务实现类
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
public class AdminLoginServiceImpl implements AdminLoginService {

    @Resource
    private TokenComponent tokenComponent;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemMenuService systemMenuService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private MerchantService merchantService;


    /**
     * PC登录
     *
     * @param request   请求信息
     * @param adminType 管理员类型
     * @param ip        ip
     */
    private SystemLoginResponse login(SystemAdminLoginRequest request, Integer adminType, String ip) {
        // 校验验证码
        checkCaptcha(request);
        // 用户验证
        Authentication authentication = null;
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        try {
            String principal = request.getAccount() + adminType;
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(principal, request.getPwd()));
        } catch (AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                throw new CrmebException("用户不存在或密码错误");
            }
            throw new CrmebException(e.getMessage());
        }
        LoginUserVo loginUser = (LoginUserVo) authentication.getPrincipal();
        SystemAdmin systemAdmin = loginUser.getUser();

        String token = tokenComponent.createToken(loginUser);
        SystemLoginResponse systemAdminResponse = new SystemLoginResponse();
        systemAdminResponse.setToken(token);
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);

        //更新最后登录信息
        systemAdmin.setUpdateTime(DateUtil.date());
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        systemAdmin.setLastIp(ip);
        systemAdminService.updateById(systemAdmin);

        // 返回后台LOGO图标
        if (adminType.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            systemAdminResponse.setLeftTopLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE));
        } else {
            systemAdminResponse.setLeftTopLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_LOGO_SQUARE));
        }
        return systemAdminResponse;
    }

    /**
     * 平台端登录
     */
    @Override
    public SystemLoginResponse platformLogin(SystemAdminLoginRequest request, String ip) {
        SystemLoginResponse loginResponse = login(request, RoleEnum.PLATFORM_ADMIN.getValue(), ip);
        asyncService.installStatistics();
        return loginResponse;
    }

    /**
     * 商户端登录
     */
    @Override
    public SystemLoginResponse merchantLogin(SystemAdminLoginRequest request, String ip) {
        return login(request, RoleEnum.MERCHANT_ADMIN.getValue(), ip);
    }

    /**
     * 用户登出
     */
    @Override
    public Boolean logout() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        if (ObjectUtil.isNotNull(loginUserVo)) {
            // 删除用户缓存记录
            tokenComponent.delLoginUser(loginUserVo);
        }
        return true;
    }

    /**
     * 获取登录页图片
     *
     * @return AdminLoginPicResponse
     */
    @Override
    public AdminLoginPicResponse getLoginPic() {
        AdminLoginPicResponse loginPicResponse = new AdminLoginPicResponse();
        loginPicResponse.setBackgroundImage(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE));
        loginPicResponse.setLoginLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LOGIN));
        loginPicResponse.setLeftLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LEFT_LOGO));
        return loginPicResponse;
    }

    /**
     * 获取商户端登录页图片
     *
     * @return AdminLoginPicResponse
     */
    @Override
    public AdminLoginPicResponse getMerchantLoginPic() {
        AdminLoginPicResponse loginPicResponse = new AdminLoginPicResponse();
        loginPicResponse.setBackgroundImage(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_BACKGROUND_IMAGE));
        loginPicResponse.setLoginLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LOGIN));
        loginPicResponse.setLeftLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LEFT_LOGO));
        return loginPicResponse;
    }

    /**
     * 获取管理员可访问目录
     *
     * @return List<MenusResponse>
     */
    @Override
    public List<MenusResponse> getMenus() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        List<String> roleList = Stream.of(loginUserVo.getUser().getRoles().split(",")).collect(Collectors.toList());
        List<SystemMenu> menuList;
        if (roleList.contains("1")) {// 超管
            menuList = systemMenuService.findAllCatalogue(RoleEnum.PLATFORM_ADMIN.getValue());
        } else if (roleList.contains("2")) {// 商户主
            menuList = systemMenuService.findAllCatalogue(RoleEnum.MERCHANT_ADMIN.getValue());
        } else {
            menuList = systemMenuService.getMenusByUserId(loginUserVo.getUser().getId());
        }
        // 组装前端对象
        List<MenusResponse> responseList = menuList.stream().map(e -> {
            MenusResponse response = new MenusResponse();
            BeanUtils.copyProperties(e, response);
            return response;
        }).collect(Collectors.toList());

        MenuTree menuTree = new MenuTree(responseList);
        return menuTree.buildTree();
    }

    /**
     * 根据Token获取对应用户信息
     */
    @Override
    public LoginAdminResponse getInfoByToken() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        SystemAdmin systemAdmin = loginUserVo.getUser();
        LoginAdminResponse loginAdminResponse = new LoginAdminResponse();
        BeanUtils.copyProperties(systemAdmin, loginAdminResponse);
        List<String> roleList = Stream.of(systemAdmin.getRoles().split(",")).collect(Collectors.toList());
        List<String> permList = CollUtil.newArrayList();
        if ((roleList.contains("1") &&
                (systemAdmin.getType().equals(RoleEnum.PLATFORM_ADMIN.getValue())) || systemAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue()))) {
            permList.add("*:*:*");
        } else if ((roleList.contains("2") &&
                (systemAdmin.getType().equals(RoleEnum.MERCHANT_ADMIN.getValue())) || systemAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue()))) {
            permList.add("*:*:*");
        } else {
            permList = loginUserVo.getPermissions().stream().map(SystemPermissions::getPath).collect(Collectors.toList());
        }
        loginAdminResponse.setPermissionsList(permList);

        if (systemAdmin.getMerId() > 0) {
            Merchant merchant = merchantService.getById(systemAdmin.getMerId());
            loginAdminResponse.setMerStarLevel(merchant.getStarLevel());
        }
        return loginAdminResponse;
    }

    /**
     * 修改登录用户信息
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean loginAdminUpdate(LoginAdminUpdateRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        SystemAdmin systemAdmin = new SystemAdmin();
        systemAdmin.setId(admin.getId());
        systemAdmin.setRealName(request.getRealName());
        String pwd = CrmebUtil.encryptPassword(request.getPassword(), admin.getAccount());
        systemAdmin.setPwd(pwd);
        return systemAdminService.updateById(systemAdmin);
    }

    /**
     * 校验验证码是否正确
     *
     * @param request 登录参数
     */
    private void checkCaptcha(SystemAdminLoginRequest request) {
        // 判断验证码
        boolean codeCheckResult = validateCodeService.check(request.getKey(), request.getCode());
        if (!codeCheckResult) throw new CrmebException("验证码不正确");
    }
}
