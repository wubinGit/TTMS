package com.ttms.service.ProductManage;

import com.ttms.Entity.SysDepartment;
import com.ttms.Entity.SysRoles;
import com.ttms.Entity.SysUser;

import java.util.List;

public interface IPubNotifyService  {
    Void publicMsg(Integer messageType,Integer toid ,Integer valid , String messageTitle, String messageContent, SysUser user);

    List<SysDepartment> getDepartmentByPid(Integer pid);

    List<SysRoles> getRolesByDepartmentId(Integer did);

    List<SysUser> getSysuserByRoleId(Integer rid);
}
