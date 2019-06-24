package com.ttms.Mapper;

import com.ttms.Entity.ResGuide;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ResGuideMapper extends Mapper<ResGuide> {

    @Select("SELECT rtg.* FROM pro_product_guide  ppg JOIN res_tour_guide rtg ON ppg.`guideId` = rtg.`id` WHERE ppg.`productId` = #{pid} ")
    List<ResGuide> getGuidesByProductId(@Param("pid") Integer id);
}
