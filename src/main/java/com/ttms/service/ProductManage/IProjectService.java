package com.ttms.service.ProductManage;

import com.ttms.Entity.ProProject;
import com.ttms.Entity.SysDepartment;
import com.ttms.Entity.SysUser;
import com.ttms.Vo.PageResult;

import java.util.Date;
import java.util.List;

public interface IProjectService {

    //新增项目 表单提交的数据有 项目编号 . 名称 起始日期--结束日期. 部门(部门需要根据输入的)，备注 没有的数据自己设置
    Void addProject(ProProject project, SysUser user, Integer departmentId);

    //类似新增
    Void editProject(ProProject project, SysUser user,Integer departName);


    PageResult<ProProject> getAllProjectByPage(String projectNumber, String projectName, int departmentid,
                                               Date startTime, Date endTime, int valid, int page, int rows);

    List<SysDepartment> getSubdepartmentProductDepartment();

    Boolean judge(String username);

    void ValidOrInvalidProject(Integer pid);

    ProProject findProjectById(Integer pid);
}

