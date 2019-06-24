package com.ttms.Entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name="pro_product_pricepolicy")
@Data
public class ProProductPricepolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productid;

    private Integer pricepolicyid;

    private Integer priceafterdiscount;

    private Date createtime;

    private Date updatetime;
}
