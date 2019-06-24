package com.ttms.Vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

//返回菜单时的模块名
@Data
public class ModulesVo {
    @JsonIgnore
    private int menuId;
    //模块名
    private String moduleName;
    //模块名称
    private String moduleIcon;
    //模块路径
    private String path;
    //该模块下的菜单
    private List<Menus> menus ;
}
