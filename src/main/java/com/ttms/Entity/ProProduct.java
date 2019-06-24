package com.ttms.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "pro_product")
@Data
public class ProProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productnumber;

    private Integer groupid;

    @Transient
    private String groupname;

    private Integer productcatid1;

    private Integer productcatid2;

    private Integer productcatid3;

    //三级分类的名称
    @Transient
    private String productcatnames;


    private Integer projectid;

    private  String projectname;

    private String productname;

    private Integer productstatus;

    private Date serverstarttime;

    private Date serverendtime;

    private Integer presellnumber;

    private Integer sellednumber;

    private Integer lowestnumber;

    private Date onselltime;

    private Integer productprice;

    private Date upselltime;

    private String hottip;

    private String productintroduction;

    private Date createtime;

    private Date updatetime;

    private Integer createuserid;

    @Transient
    private String createusername;

    private Integer updateuserid;

}