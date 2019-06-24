package com.ttms.Controller.SystemManage;

import com.ttms.Entity.SysDepartment;
import com.ttms.Entity.SysMenus;
import com.ttms.Entity.SysRoles;
import com.ttms.Entity.SysUser;
import com.ttms.Vo.PageResult;
import com.ttms.service.SystemManage.SysMenusService;
import com.ttms.utils.CodecUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

//系统管理->用户权限模块->用户管理
@RestController
@RequestMapping("/sysmanage/userauth/usermanage")    //类中所有方法访问都需要加上共享
public class UserManageController {

    @Autowired
    private SysMenusService sysMenusService;

    /**
    * 功能描述: <br>
    * 〈〉查询该子部门下的所有角色
    * @Param: [departmentId]fdsfd
    * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SysRoles>>
    * @Author: 吴彬
    * @Date: 10:56 10:56
     */
    @GetMapping("/getRolesByDepartmentId")
    public ResponseEntity<List<SysRoles>> getRolesByDepartmentId(@RequestParam Integer departmentId){
       return  ResponseEntity.ok(this
        .sysMenusService.getRolesByDepartmentId(departmentId));


    }



    /**
     * 功能描述: <br>
     * 〈〉根据父部门id查询所有子部门
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SysDepartment>>
     * @Author: 吴彬
     * @Date: 15:53 15:53
     */
    @GetMapping("/getDepartmentBypid")
    public ResponseEntity<List<SysDepartment>> queryAllDepartmentBypid(@RequestParam Integer pid){
        return ResponseEntity.ok(this.sysMenusService.queryAllDepartmentBypid(pid));
    }


    /*功能描述：分页查询已注册的用户 --------------ok
     *@author罗占
     *@Description
     *Date15:21 2019/5/26
     *Param
     *return
     **/
    @GetMapping("/page")
    public ResponseEntity<PageResult<SysUser>> queryUserByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                               @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                               @RequestParam(value = "name", required = false) String name){

        PageResult<SysUser> result = sysMenusService.queryUserByPage(page, rows, name);
        return ResponseEntity.ok().body(result);
    }

    /*
     *功能描述：根据id查询用户
     *@author罗占
     *@Description
     *Date15:38 2019/5/26
     *Param
     *return
     **/
    @GetMapping("/{id}")
    public ResponseEntity<SysUser> getUserById(@PathVariable("id")Integer id){
        SysUser user = sysMenusService.getUserById(id);
        return ResponseEntity.ok().body(user);
    }

    /**
     * @Description:    查询所有角色
     * @param
     * @Author:         吴彬
     * @UpdateRemark:   修改内容
     * @Version:        1.0
     */
    @GetMapping("/getAllRoles")
    public ResponseEntity<List<SysRoles>> getAllRoles(){
        List<SysRoles> rolesList = this.sysMenusService.getAllRoles();
        return ResponseEntity.ok(rolesList);
    }

    /**
     * 功能描述: 新增用户 -------------------
     * 〈〉
     * @Param: [username, image, password, mail, phonenumber]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: lhf
     * @Date: 2019/5/27 15:03
     */
    @PostMapping
    public ResponseEntity<Void> addSysUser(@RequestParam  String username, @RequestParam  String image,
                                           @RequestParam  String password, @RequestParam  String mail,
                                           @RequestParam  String  phonenumber , @RequestParam  Integer roleId){
        this.sysMenusService.addSysUser(username,image, password,mail,phonenumber,roleId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * 功能描述: 修改用户
     * 〈〉
     * @Param: [id, username, image, password, mail, phonenumber]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: lhf
     * @Date: 2019/5/27 15:03
     */
    @PutMapping("/{id}")
    public ResponseEntity<SysUser> updateUserById(@PathVariable("id") Integer id,
                                                   @RequestParam  String username,
                                                  @RequestParam  String image,
                                                  @RequestParam String password,
                                                  @RequestParam String mail,
                                                  @RequestParam  String phonenumber,
                                                  @RequestParam  Integer roleId){
        //更新用户表
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setImage(image);
        user.setSalt(CodecUtils.generateSalt());
        user.setPassword(CodecUtils.md5Hex(password,user.getSalt()));
        user.setValid((byte) 1);
        user.setEmail(mail);
        user.setRoleid(roleId);
        user.setMobile(phonenumber);
        user.setModifiedtime(new Date());
        SysUser curUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        user.setCreateduserid(curUser.getId());
        sysMenusService.updateUserById(user);
        //更新项目表
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    /**
     * 功能描述: 启用和禁用用户
     * 〈〉
     * @Param: [id]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: lhf
     * @Date: 2019/5/27 15:02
     */
    @PutMapping("/valid/{id}")
    public ResponseEntity<Void> validOrInvalid(@PathVariable("id") Integer id){
        sysMenusService.validOrInvalid(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
