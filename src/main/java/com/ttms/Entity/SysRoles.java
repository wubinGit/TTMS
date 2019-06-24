package com.ttms.Entity;

import com.ttms.Dto.TempRole;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name="sys_roles")
@Data
public class SysRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String note;

    private Integer departmentid;

    private Date createdtime;

    private Date modifiedtime;

    private Integer createduserid;

    private Integer modifieduserid;

    @Transient
    private SysMenus sysMenus;

    @Transient   //角色管理页面所需显示数据
    private TempRole tempRole;
}