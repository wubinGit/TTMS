package com.ttms.Entity;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "reso_attachment")
@Data
public class ResoAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String filename;

    private Integer productId;

    private String attachmenttitle;

    private Byte invalid;

    private String fileurl;

    private Date uploadtime;

    private Integer uploaduserid;

}