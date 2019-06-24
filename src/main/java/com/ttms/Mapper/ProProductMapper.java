package com.ttms.Mapper;

import com.ttms.Entity.ProProduct;
import com.ttms.Vo.ProductVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProProductMapper extends Mapper<ProProduct> {
    //查询所有分销此产品的分销商
   @Select("SELECT d.`id` , p.`productName`,p.`serverStartTime`,p.`serverEndTime` ,sd.`distributorName` , sd.`distributorPhone` , sd.`distributorAddress` FROM pro_product p,pro_product_distributor d," +
           "sup_distributor sd WHERE d.`productId`=p.`id` AND sd.`id`=d.`distributorId` AND d.`productId`=#{projectId}")
//            "SELECT p.`productName`,p.`serverStartTime`,p.`serverEndTime` ,sd.* FROM pro_product p,pro_product_distributor d,sup_distributor sd WHERE d.`productId`=p.`id` AND sd.`id`=d.`distributorId` AND d.`productId`=#{projectId}")
    List<ProductVo> getDistributorsByPid(@Param("projectId") Integer parjectId);

    //更新产品中的项目名（当修改项目信息后）
    @Update("update pro_product pp set pp.projectName = #{projectname} where pp.projectId =#{id}")
    int updateRedundancyWordProjectNameProduct(@Param("id")Integer id,@Param("projectname") String projectname);
}
