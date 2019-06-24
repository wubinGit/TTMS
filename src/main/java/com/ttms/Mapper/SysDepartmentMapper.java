package com.ttms.Mapper;

import com.ttms.Entity.SysDepartment;
import com.ttms.Entity.SysRoles;
import com.ttms.utils.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {
    //查询部门下的所有成员
    @Select("SELECT su.id FROM sys_user su WHERE su.`roleId` IN  " +
            "(SELECT sr.id FROM sys_department sd JOIN sys_roles sr  ON sd.id " +
            "= sr.departmentId WHERE sd.id = #{departmentId})")
    public List<Integer> getAllStaffIdsOfDepartment(@Param("departmentId") Integer departmentId);

    //判断该用户是否属于产品部门
    @Select("SELECT depart.departmentname FROM sys_department depart WHERE  depart.id IN" +
            "( SELECT r.departmentId FROM sys_user u, sys_roles r,sys_user_roles s WHERE u.id=s.user_id AND s.role_id=r.id AND u.id=#{Uid})")
    public SysDepartment IsBelongProduceManager(@Param("Uid")Integer Uid);

    //查询该部门下的所有角色
    @Select("SELECT r.* FROM sys_roles r,sys_department d WHERE r.`departmentId`=d.`id` AND d.`isparent`=0 AND d.`id`=#{departmentId}")
    List<SysRoles> getRolesByDepartmentId(@Param("departmentId") Integer departmentId);

    @Select(("SELECT  sr.departmentId FROM sys_roles sr  WHERE sr.id  =( SELECT su.roleId FROM sys_user su  WHERE su.id=#{userId})"))
    Integer getDepartmentId(@Param("userId") Integer userId);

    @Select(("SELECT s.departmentname  FROM sys_user u,sys_roles r,sys_department s WHERE u.`roleId`=r.`id` AND s.`id`=r.`departmentId` AND u.id=#{userId}"))
    String selectUserDepartment(Integer userId );

    @Select("SELECT  s.name FROM sys_roles s WHERE s.id=( SELECT su.roleId  FROM sys_user su  WHERE su.id=#{userId})")
    String judgeUserIsProductManager(@Param("userId") Integer userId);

}
