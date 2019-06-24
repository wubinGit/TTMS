package com.ttms.Controller.SystemManage;

import com.ttms.Entity.SysDepartment;
import com.ttms.Vo.PageResult;
import com.ttms.service.SystemManage.SysMenusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//系统管理->用户权限模块->组织机构管理
@RestController
@RequestMapping("/sysmanage/userauth/organmanage")    //类中所有方法访问都需要加上共享
public class OrganizeManageController {

    @Autowired
    private SysMenusService sysMenusService;



    /**
     * 功能描述: 新增部门
     * 〈〉
     * @Param: [sysdepartment]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: lhf
     * @Date: 2019/5/27 15:01
     */
    @PostMapping("/add/dartment")
    public ResponseEntity<Void> addDepartment(@RequestParam String departmentName, @RequestParam String departmentCode,
                                              @RequestParam String departmentNote, @RequestParam int parentId){
        this.sysMenusService.addDepartment(departmentName,departmentCode,departmentNote,parentId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /*
    *功能描述：分页查询所有部门
    *@author罗占
    *@Description
    *Date8:15 2019/5/30
    *Param
    *return
    **/
    @GetMapping("/page")
    public ResponseEntity<PageResult<SysDepartment>> queryDepartment(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                     @RequestParam(name = "rows", defaultValue = "5") Integer rows,
                                                                     @RequestParam(name = "departmentname", required = false) String  departmentname,
                                                                     @RequestParam(name = "valid",required = false ,defaultValue = "-1") Integer valid,
                                                                     @RequestParam(required = false , defaultValue = "0")Integer pid){
        PageResult<SysDepartment> result = this.sysMenusService.queryDepartment(page,rows,departmentname,valid,pid);
        return ResponseEntity.ok().body(result);
    }

    /*
    *功能描述：启用或禁用部门
    *@author罗占
    *@Description
    *Date9:04 2019/5/30
    *Param
    *return
    **/
    @PutMapping("/valid/{id}")
    public ResponseEntity<Void> updateDepartmentValidOrInvalid(@PathVariable("id") Integer id){
        this.sysMenusService.updateDepartmentValidOrInvalid(id);
        return ResponseEntity.ok().body(null);
    }

    /*
    *功能描述：修改部门
    *@author罗占
    *@Description
    *Date9:12 2019/5/30
    *Param
    *return
    **/
    @PutMapping("/update/dartment")
    public ResponseEntity<Void> updateDepartment(@RequestParam Integer id,
                                                 @RequestParam String departmentName,
                                                 @RequestParam String departmentCode,
                                                 @RequestParam String departmentNote){
        SysDepartment department = new SysDepartment();
        department.setId(id);
        department.setDepartmentname(departmentName);
        department.setDepartmentcode(departmentCode);
        department.setNote(departmentNote);
        this.sysMenusService.updateDepartment(department);
        return ResponseEntity.ok().body(null);
    }
}
