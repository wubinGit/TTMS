package com.ttms.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name="sys_menus")
@Data
public class SysMenus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String url;

    private Integer type;

    private Integer sort;

    private String note;

    private Integer parentid;

    private String permission;

    private Date createdtime;

    private Date modifiedtime;

    private int createduserid;

    private int modifieduserid;

    @Transient
    private List<SysMenus> childMenus;
}