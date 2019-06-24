package com.ttms.Vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

//每个模块下的菜单项
@Data
public class Menus {
    @JsonIgnore
    private int menuId;

    //菜单图标
    private String action;

    //菜单标题
    private String title;

    //菜单路径
    private String path;

    //子菜单条目
    private List<MenusItem> menusItems;
}
