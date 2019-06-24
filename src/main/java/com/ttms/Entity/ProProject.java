package com.ttms.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "pro_project")
@Data
public class ProProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String projectnumber;

    private String projectname;

    private Integer departmentid;

    private Date starttime;

    private Date endtime;

    private Byte valid;

    private String note;

    private Date createtime;

    private Date updatetime;

    private Integer createuserid;

    private Integer updateuserid;

    //部门名临时数据
    @Transient
    private String departmentName;
}