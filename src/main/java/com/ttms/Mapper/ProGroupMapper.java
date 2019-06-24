package com.ttms.Mapper;

import com.ttms.Entity.ProGroup;
import com.ttms.Vo.GroupManageVo;
import com.ttms.utils.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProGroupMapper extends BaseMapper<ProGroup> {
    @Update("update pro_group pg set pg.projectName = #{projectname} where pg.projectId =#{id}")
    int updateRedundancyWordProjectName(@Param("id")Integer id,@Param("projectname") String projectname);
}
