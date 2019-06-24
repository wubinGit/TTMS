package com.ttms.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 功能描述: <br>
 * 〈〉Redis前缀
 * @Param:
 * @Return: 
 * @Author: 万少波
 * @Date: 2019/5/27 19:30
 */
@AllArgsConstructor
public enum RedisKeyPrefixEnum {
    SYSMENUS_TREE("ttms:sysmenus:tree","保存SpringBoot启动时加载的MenusTree"),
    USER_ONLINE_KEY("USER:ONLINE:LIST:","用户Id在线列表"),
    ;
    private String prefix;          //前缀
    private String description;   //该前缀的用途

    public String val(){
        return prefix;
    }
}
