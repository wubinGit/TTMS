package com.ttms.Filter;

import com.ttms.Entity.SupDistributor;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DistributorAuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SupDistributor curdistributor = (SupDistributor) request.getSession().getAttribute("curdistributor");
        if(curdistributor == null){
            throw new TTMSException(ExceptionEnum.USER_UNLOGIN);
        }
        return true;
    }
}
