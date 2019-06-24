package com.ttms.Config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述: <br>
 * 〈〉定义了菜单Id到perms的映射   SpringBoot启动时初始化
 *        1:sysmanage                  比如角色绑定的一个Menus id为1 则其中perms为sysmanage:*所有的菜单都能访问
 *       2:logmanage                                    id为3时 对应perms为sysmanage:logmanage:rolemanage
 *   3:rolemanage 4:usernamage
 * @Author: 万少波
 * @Date: 2019/5/27 13:06
 */
@Component
public class MenuIdPermsMap {
    private Map map = new HashMap<Integer,String>();

    public void put(int id , String perms){
        map.put(id,perms);
    }

    public String get(int id){
       return (String) map.get(id);
    }
}
