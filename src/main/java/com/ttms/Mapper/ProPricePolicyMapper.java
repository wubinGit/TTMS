package com.ttms.Mapper;

import com.ttms.Entity.ProPricepolicy;
import com.ttms.utils.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProPricePolicyMapper extends BaseMapper<ProPricepolicy> {

    @Select("SELECT pp.*,ppp.`priceAfterDiscount`  FROM pro_product_pricepolicy ppp " +
            "JOIN pro_pricepolicy pp ON ppp.`pricePolicyId` = pp.`id` WHERE ppp.`productId` = #{pid}")
    List<ProPricepolicy> getProPricepoliciesByProductId(@Param(value = "pid") Integer pid);

    @Select("SELECT * FROM pro_pricepolicy pp JOIN pro_product_pricepolicy ppp ON ppp.`pricePolicyId` = pp.`id` WHERE ppp.`productId` = #{productId}")
    List<ProPricepolicy> getPricePolicyByProductId(@Param("productId") Integer productId);

    //查询当前产品可用的所有价格政策
    @Select("SELECT * FROM pro_pricepolicy pp JOIN pro_product_pricepolicy ppp ON ppp.`pricePolicyId` = pp.`id` WHERE  pp.startTime<=(SELECT NOW()) AND pp.endTime>=(SELECT NOW())" +
            " AND pp.minNum>0 AND ppp.`productId` =#{productId}  ORDER BY pp.policyDiscount DESC ")
    List<ProPricepolicy> getPricePolicyByProductIdEnable(@Param("productId") Integer productId);
}
