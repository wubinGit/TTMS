package com.ttms.Controller.AllowVisitor;

import com.ttms.Entity.SysMenus;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.service.SystemManage.SysMenusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//  此中方法shiro不能拦截
@RestController
@RequestMapping("/anon")
public class ErrorController {
    @Autowired
    SysMenusService sysMenusService;


    /**
     * 功能描述: <br>
     * 〈〉用户没有登陆 回复消息
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SysMenus>>
     * @Author: 万少波
     * @Date: 2019/5/26 15:25
     */
    @RequestMapping("/unlogin")
    public void unloginHandler(){
        throw new TTMSException(ExceptionEnum.USER_UNLOGIN);
    }

    /**
     * 功能描述: <br>
     * 〈〉未授权处理
     * @Param: []
     * @Return: void
     * @Author: 万少波
     * @Date: 2019/5/26 15:31
     */
    @RequestMapping("/unauthority")
    public void unauthorityHandler(){
        throw new TTMSException(ExceptionEnum.NOT_AUTHORITY);
    }
    
    
}