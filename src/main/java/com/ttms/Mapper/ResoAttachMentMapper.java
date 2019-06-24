package com.ttms.Mapper;

import com.ttms.Entity.ResoAttachment;
import com.ttms.Vo.ResoAttachmentVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ResoAttachMentMapper extends Mapper<ResoAttachment> {

    /**
    * 功能描述: <br>
    * 〈〉获取最近的一个文件
    * @Param: [productId]
    * @Return: com.ttms.Entity.ResoAttachment
    * @Author: 吴彬
    * @Date: 11:43 11:43
     */
    @Select("select f.* from reso_attachment f where f.`product_id`=#{productId} order by f.`uploadTime` desc limit 1")
    public ResoAttachment getResoAttachmentByproductId(@Param("productId") Integer productId);

    @Select("SELECT r.*,s.`username` FROM reso_attachment r ,sys_user s WHERE s.`id`=r.`uploadUserId` AND r.`product_id`=#{productId} AND r.`invalid`='1'")
    public List<ResoAttachmentVo> getResoAttachmentByproductIdAndUerName(@Param("productId") Integer productId);
}
