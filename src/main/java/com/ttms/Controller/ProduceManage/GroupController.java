package com.ttms.Controller.ProduceManage;

import com.ttms.Entity.ProProject;
import com.ttms.Entity.SysUser;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Vo.GroupManageVo;
import com.ttms.Vo.PageResult;
import com.ttms.service.ProductManage.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/producemanage/group/groupmanage")
public class GroupController {

    @Autowired
    private IGroupService groupService;

    /**
     * 功能描述: 修改团信息
     * 〈〉
     * @Param: [groupId, groupName, belongProjectId, chargeUserId, groupNote]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: lhf
     * @Date: 2019/5/28 8:59
     */
    @PutMapping
    public ResponseEntity<Void> updateGroup(@RequestParam int groupId, @RequestParam String groupName, @RequestParam int belongProjectId, @RequestParam String groupNote){
        this.groupService.updateGroup(groupId,groupName,belongProjectId,groupNote);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * 功能描述: 启动禁用团状态
     * 〈〉
     * @Param: [pid]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: lhf
     * @Date: 2019/5/28 14:37
     */
    @PutMapping("/validorinvalid/{gid}")
    public ResponseEntity<Void> pathvariable(@PathVariable("gid") Integer gid){
        groupService.ValidOrInvalidGroup(gid);
        return ResponseEntity.ok().body(null);
    }

    /**
     * 功能描述: <br>
     * 〈〉分页条件查询团信息
     * @Param: [groupName, projectName, valid, page, rows]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Vo.GroupManageVo>>
     * @Author: 万少波
     * @Date: 2019/5/28 16:13
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<GroupManageVo>> getAllGroupsByConditionAndPage(@RequestParam(required = false) String groupName,
                                                                     @RequestParam(required = false) String projectName,
                                                                     @RequestParam(required = false ,defaultValue = "-1") int valid ,
                                                                     @RequestParam(required = false , defaultValue = "1")int page ,
                                                                     @RequestParam(required = false , defaultValue = "5")int  rows){
       return ResponseEntity.ok(groupService.getAllGroupsByConditionAndPage(groupName,projectName,valid,page,rows));
    }

    /*
     *功能描述：创建团功能
     *@author罗占
     *@Description
     *Date15:40 2019/5/28
     *Param
     *return
     **/
    @PostMapping
    public ResponseEntity<Void> createGroup(  String groupName, Integer belongProjectId,  String groupNote){
        this.groupService.createGroup(groupName,belongProjectId,groupNote);
        return ResponseEntity.ok().body(null);

    }

    /*
    *功能描述：根据项目id查找其部门下所有职员
    *@author罗占
    *@Description
    *Date9:47 2019/5/29
    *Param[projectId]
    *returnorg.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SysUser>>
    **/
    @GetMapping("/getAllStaffInDep")
    public ResponseEntity<List<SysUser>> getprojectinfo(@RequestParam(value = "projectId",required = true) Integer  projectId){
       List<SysUser> users  = this.groupService.getAllStaffInDep(projectId);
       return ResponseEntity.ok().body(users);
    }

    /**
     * 功能描述: 查询所有项目名
     * 〈〉
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.ProProject>>
     * @Author: lhf
     * @Date: 2019/5/30 15:11
     */
    @GetMapping("/getProjectInfo")
    public ResponseEntity<List<ProProject>> getprojectinfo(){
        List<ProProject> getproject = this.groupService.getprojectinfo();
        return ResponseEntity.ok().body(getproject);
    }
}
