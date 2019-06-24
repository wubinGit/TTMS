package com.ttms.Entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sys_department")
@Data
public class SysDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String departmentname;

    private String departmentcode;

    private Integer parentid;

    private Byte isparent;

    private Byte valid;

    private String note;

    private Date modifiytime;

    private Date createtime;

    private Integer modifyuserid;

    private Integer createuserid;

}