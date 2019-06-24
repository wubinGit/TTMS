package com.ttms.Entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name="res_tour_guide")
@Data
public class ResGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String guidenumber;

    private String name;

    private String englishname;

    private Byte  sex;

    private String mail;

    private String language;

    private String nationality;

    private String mobile;

    private String note;

    private Date createtime;

    private Date updatetime;
}
