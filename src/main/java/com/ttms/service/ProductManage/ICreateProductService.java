package com.ttms.service.ProductManage;

import com.ttms.Entity.ProGroup;
import com.ttms.Entity.SysUser;

import java.util.Date;
import java.util.List;


public interface ICreateProductService {
    //创建产品
    void createProduct(Integer groupId, Integer productCatId1, Integer productCatId2, Integer productCatId3, String productName,
                       Date serverStartTime, Date serverEndTime, Integer preSellNumber , Integer selledNumber, Integer lowestNumber,
                       Date onsellTime , Integer productPrice, Date upsellTime, String hotTip, String productIntroduction, SysUser user);

    List<ProGroup> queryGroupByCuruser(SysUser user);

    //修改产品
    void UpdateProduct(Integer pid,Integer groupId, Integer productCatId1, Integer productCatId2, Integer productCatId3,String productName,
                       Date serverStartTime, Date serverEndTime, Integer preSellNumber , Integer selledNumber, Integer lowestNumber,
                       Date onsellTime , Integer productPrice, Date upsellTime, String hotTip, String productIntroduction, SysUser user);

    //更新产品中的项目名（当修改项目信息后）
    Void updateRedundancyWordProjectNameProduct(Integer id, String projectname);

}

