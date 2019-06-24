package com.ttms.Controller.ProduceManage;

import com.ttms.Entity.ProProject;
import com.ttms.Entity.SysDepartment;
import com.ttms.Entity.SysUser;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Vo.PageResult;
import com.ttms.service.ProductManage.IGroupService;
import com.ttms.service.ProductManage.IProjectService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

//----------产品管理->产品-项目信息管理
@RestController
@RequestMapping("/producemanage/project/projectinfomanage")
public class ProjectInfoController {


    @Autowired
    private IProjectService projectService;

    @Autowired
    private IGroupService groupService;


    /**
     * 功能描述: <br>
     * 〈〉分页条件查询项目
     * @Param: [projectNumber, projectName, departmentid, startTime, endTime, valid, page, rows]
     * @Return: org.springframework.http.ResponseEntity<com.ttms.Vo.PageResult<com.ttms.Entity.ProProject>>
     * @Author: 万少波
     * @Date: 2019/5/29 15:16
     */
    @RequestMapping("/page")
    public ResponseEntity<PageResult<ProProject>> getAllProjectByPage(@RequestParam(required = false) String projectNumber,
                                                                      @RequestParam(required = false) String projectName,
                                                                      @RequestParam(required = false , defaultValue = "-1")int departmentid,
                                                                      @RequestParam(required = false) Date startTime,
                                                                      @RequestParam(required = false) Date endTime,
                                                                      @RequestParam(required = false,defaultValue = "-1")int valid,
                                                                      @RequestParam(required = false,defaultValue = "1")int page ,
                                                                      @RequestParam(required = false,defaultValue = "5")int rows){
        return ResponseEntity.ok(projectService.getAllProjectByPage(projectNumber,projectName,departmentid,startTime,endTime,valid,page,rows));
    }


    /**
    * 功能描述: <br>
    * 〈〉新增项目
    * @Param: [projectnumber, projectname, starttime, endtime, note, departmentId]
    * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
    * @Author: 吴彬
    * @Date: 10:10 10:10
     */
    @PostMapping
    public ResponseEntity<Void> addProject(Integer pid,
                                            @RequestParam String projectnumber,
                                           @RequestParam String projectname,
                                           @RequestParam Date starttime,
                                           @RequestParam Date endtime,
                                           @RequestParam String note ,
                                           @RequestParam Integer departmentId){
        ProProject proProject = encapsulation(pid,projectnumber, projectname, starttime, endtime, note, departmentId);
        this.projectService.addProject(proProject, (SysUser) SecurityUtils.getSubject().getPrincipal(), departmentId);
        return ResponseEntity.ok().build();
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * @Param: [pid, projectnumber, projectname, starttime, endtime, note, departmentId]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 吴彬
     * @Date: 2019/6/12 10:04
     */
    @PutMapping("/{pid}")
    public ResponseEntity<Void> editProject(
            @PathVariable("pid") Integer pid,
            @RequestParam String projectnumber,
            @RequestParam String projectname,
            @RequestParam Date starttime,
            @RequestParam Date endtime,
            @RequestParam String note ,
            @RequestParam Integer departmentId){
        ProProject project = encapsulation(pid,projectnumber, projectname, starttime, endtime, note, departmentId);
        this.projectService.editProject(project,(SysUser) SecurityUtils.getSubject().getPrincipal(), departmentId);
        return ResponseEntity.ok().build();
    }
    /**
     * 功能描述: <br>
     * 〈〉查询产品部下面的子部门
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SysDepartment>>
     * @Author: 万少波
     * @Date: 2019/5/30 8:11
     */
    @GetMapping("/subDepOfProduction")
    public ResponseEntity<List<SysDepartment>> getSubdepartmentProductDepartment(){
        return ResponseEntity.ok(projectService.getSubdepartmentProductDepartment());
    }

    private ProProject encapsulation(Integer pid, String projectnumber, String projectname, Date starttime, Date endtime, String note, Integer departmentId){
        SysUser user=(SysUser) SecurityUtils.getSubject().getPrincipal();
        //判断角色是否为产品部的产品经理。。。。。。
        //1.先判断角色是否为产品经理
        if(!projectService.judge(user.getUsername())){
            throw new TTMSException(ExceptionEnum.NOT_OPERATION_AUTHORITY);
        }else if(StringUtils.isBlank(projectnumber)){
            throw new TTMSException(ExceptionEnum.PROJECT_CODE_NULL);
        }else if(StringUtils.isBlank(projectname)){
            throw new TTMSException(ExceptionEnum.PROJECT_NAME_NULL);
        }else if(departmentId == null){
            throw new TTMSException(ExceptionEnum.DEPATMENT_ID_NULL);
        }
        ProProject proProject=new ProProject();
        proProject.setProjectnumber(projectnumber);
        proProject.setProjectname(projectname);
        proProject.setStarttime(starttime);
        proProject.setEndtime(endtime);
        proProject.setNote(note);
        proProject.setId(pid);
        proProject.setDepartmentid(departmentId);
        return proProject;
    }

    @GetMapping("/validorinvalid/{pid}")
    public ResponseEntity<Void> ValidOrInvalidProject(@PathVariable("pid") Integer pid){
        this.projectService.ValidOrInvalidProject(pid);
        return  ResponseEntity.ok().build();

    }


}
