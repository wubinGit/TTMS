package com.ttms.Entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "pro_route")
@Data
public class ProRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productid;

    private String name;

    private String content;

    private String staymessage;

    private String breakfast;

    private String lunch;

    private String supper;

    private Date createtime;

    private Date updatetime;
}