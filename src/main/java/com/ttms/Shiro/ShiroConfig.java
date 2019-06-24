package com.ttms.Shiro;

import com.ttms.service.SystemManage.SysMenusService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
@Configuration
@Slf4j
public class ShiroConfig {
   /*创建ShrioFilterFactoryBean*/
    @Bean
    public ShiroFilterFactoryBean getShrioFilterFactoryBean(@Qualifier("webSecurityManager")DefaultWebSecurityManager securityManager ,
                                                            @Autowired  SysMenusService sysMenusService){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        /*设置安全管理器*/
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/anon/unlogin");
        shiroFilterFactoryBean.setUnauthorizedUrl("/anon/unauthority");
          /**
         * Shiro内置过滤器
         *   常用过滤器:
         *      anon:无序认证可以访问
         *      authc：必须认证才可以访问
         *      user:必须rememberme才能访问
         *      perms: 该资源必须得到授权
         *      role:  该资源必须得到角色权限
           *          正常情况下URL路径的拦截设置如下:
           *       /admins/user/**=roles[admin]
           *      参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，当有多个参数时，例如/admins/user/**=roles["admin,guest"]
           *     但是这个设置方法是需要每个参数满足才算通过，相当于hasAllRoles()方法。
         */
          /*关闭拦截器*/
        Map<String,String> permissionMap = sysMenusService.getUrlPermissionMapping();
        //登陆无须拦截
        //过滤路径映射
       for (Map.Entry entry:permissionMap.entrySet()) {
           log.info("Url:"+entry.getKey()+"   permission:"+entry.getValue());
      }
     //   HashMap<String,String> permissionMap = new HashMap();
        permissionMap.put("/login","anon");
        permissionMap.put("/anon/unlogin","anon");
        permissionMap.put("/anon/unahthority","anon");
        permissionMap.put("/getCuruser","anon");
        //分销商相关页面不拦截
        permissionMap.put("/distributor/**","anon");
        /*授权拦截后 ， 自动跳到为授权拦截页面*/
       shiroFilterFactoryBean.setFilterChainDefinitionMap(permissionMap);
        return shiroFilterFactoryBean;
    }
    /*创建DefaultWebSecurityManager*/
    @Bean("webSecurityManager")
    public DefaultWebSecurityManager getDefaultSecurityManager(@Qualifier("userRealm")UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        return securityManager;
    }
    /*创建Realm*/
    @Bean("userRealm")
    public UserRealm getRealm(){
        return new UserRealm();
    }

  /*  @Bean("authorizationFilter")
    public CustomRolesAuthorizationFilter getCustomRolesAuthorizationFilter(){
        return new CustomRolesAuthorizationFilter();
    }*/
}
