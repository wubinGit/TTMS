package com.ttms.Vo;

import com.ttms.Entity.SupDistributor;
import lombok.Data;

import java.util.Date;

@Data
public class ProductVo extends SupDistributor {
    private String productName;

    private Date serverstarttime;

    private Date serverendtime;
}
