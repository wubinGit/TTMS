package com.ttms.Config;

import com.ttms.Entity.SysUser;
import org.springframework.stereotype.Component;

@Component
public class MyThreadLocal {
    private ThreadLocal<SysUser> userThreadLocal = new ThreadLocal<>();

    public void setTempUser(SysUser sysUser){
        userThreadLocal.set(sysUser);
    }

    public SysUser getTempUser(){
       return userThreadLocal.get();
    }

    public void removeTempUser(){
        userThreadLocal.remove();
    }
}
