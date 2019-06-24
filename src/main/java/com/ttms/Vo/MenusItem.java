package com.ttms.Vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class MenusItem {
    @JsonIgnore
    private int menuId;

    //子菜单标题
    private String title;

    //子菜单路径
    private String path;

}
