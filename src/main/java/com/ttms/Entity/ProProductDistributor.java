package com.ttms.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "pro_product_distributor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProProductDistributor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productid;

    private Integer distributorid;

    private Date starttime;

    private Date endtime;

    private Date createtime;

    private Date updatetime;

}