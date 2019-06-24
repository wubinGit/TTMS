package com.ttms.Mapper;

import com.ttms.Dto.TempRole;
import com.ttms.Entity.SysMenus;
import com.ttms.Entity.SysRoleMenus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysRoleMenusMapper extends Mapper<SysRoleMenus> {

    @Select("SELECT sm.* FROM sys_role_menus srm JOIN sys_menus sm ON srm.`menu_id`= sm.`id` WHERE srm.`role_id`=#{id}")
    List<SysMenus> getMenusListByRoleId(@Param("id") int id);


}
