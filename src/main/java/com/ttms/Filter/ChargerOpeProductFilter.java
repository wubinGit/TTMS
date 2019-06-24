package com.ttms.Filter;

import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.service.ProductManage.IProductListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述: <br>
 * 〈〉过滤器 只有产品的主管人能够操作产品
 * @Author: 万少波
 * @Date: 2019/6/1 15:34
 */
public class ChargerOpeProductFilter implements HandlerInterceptor {
    @Autowired
    private IProductListService productListService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int productId = 0;
        try{
             productId = Integer.parseInt(request.getParameter("productId"));
        }catch (Exception e){
                throw new TTMSException(ExceptionEnum.WRONG_OPERATION);
        }
        boolean haveprivilege = productListService.checkIsCharger(productId);
        if(haveprivilege)
            //有权限放行
             return true;
        else
            //没权限抛异常
            throw new TTMSException(ExceptionEnum.NOT_AUTHORITY);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
