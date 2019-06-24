package com.ttms.service.ProductManage;

import com.ttms.Entity.ProGroup;
import com.ttms.Entity.ProProject;
import com.ttms.Entity.SysUser;
import com.ttms.Vo.GroupManageVo;
import com.ttms.Vo.PageResult;

import java.util.List;
import java.util.Set;

public interface IGroupService {
        ///修改团信息
        void updateGroup(int groupId ,String groupName, int belongProjectId, String groupNote);

        //启动禁用团状态
        void ValidOrInvalidGroup(Integer gid);

        //查询所有项目名
        List<ProProject> getprojectinfo();

        //创建团
    void createGroup(String groupName,Integer belongProjectId, String groupNote);
    //根据项目id查找其部门下所有职员
    List<SysUser> getAllStaffInDep(Integer projectId);

        PageResult<GroupManageVo> getAllGroupsByConditionAndPage(String groupName, String projectName, int valid, int page, int rows);

        //查询团号
        ProGroup getGroupById(Integer groupId);
        //查询用户是否和当前团的负责人一致
    Boolean AmIcharger(Integer groupId, Integer id);

    //更新团中的项目名（当修改项目信息后）
    Void updateRedundancyWordProjectName(Integer id, String projectname);

    List<ProGroup> getGroupsByIds(List<Integer> groupIdsList);
}
