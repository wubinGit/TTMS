package com.ttms.Controller.News;

import com.ttms.Entity.SysDepartment;
import com.ttms.Entity.SysRoles;
import com.ttms.Entity.SysUser;
import com.ttms.service.ProductManage.IPubNotifyService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;

//----------------------消息中心-通知管理-发布通知------------------------
@RestController
@RequestMapping("/news/notifymanage/pubnotify")
public class PubNotifyController {

    @Autowired
    private IPubNotifyService iPubNotifyService;
    /**
    * 功能描述: <br>
    * 〈〉发布通知
    * @Param: [messageClassName, messageTitle, messageContent]
    * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
    * @Author: 吴彬
    * @Date: 10:55 10:55
     */
    @PostMapping("/pubmsg")
    public ResponseEntity<Void> publicMsg(@RequestParam Integer messageType,@RequestParam(required = false) Integer toid , @RequestParam Integer valid, @RequestParam  String messageTitle,@RequestParam  String messageContent
        ){
        SysUser user  = (SysUser) SecurityUtils.getSubject().getPrincipal();
        return ResponseEntity.ok(this.iPubNotifyService.publicMsg(messageType,toid,valid, messageTitle, messageContent, user));
    }

    /**
     * 功能描述: <br>
     * 〈〉根据父部们查询子部门
     * @Param: [pid]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SysDepartment>>
     * @Author: 万少波
     * @Date: 2019/6/18 9:58
     */
    @GetMapping("/querydepartmentbypid")
    public ResponseEntity<List<SysDepartment>> getDepartmentByPid(@RequestParam(required = false,defaultValue = "0") Integer pid){
        return ResponseEntity.ok(iPubNotifyService.getDepartmentByPid(pid));
    }

    /**
     * 功能描述: <br>
     * 〈〉查询部门下面的所有员工
     * @Param: [did]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SysUser>>
     * @Author: 万少波
     * @Date: 2019/6/18 10:03
     */
    @GetMapping("/queryrolesbydid")
    public ResponseEntity<List<SysRoles>> getRolesByDepartmentId(@RequestParam Integer did){
        return ResponseEntity.ok(iPubNotifyService.getRolesByDepartmentId(did));
    }

    /**
     * 功能描述: <br>
     * 〈〉查询角色下所有用户
     * @Param: [rid]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SysUser>>
     * @Author: 万少波
     * @Date: 2019/6/18 10:13
     */
    @GetMapping("/getusersbyrid")
    public ResponseEntity<List<SysUser>> getSysuserByRoleId(@RequestParam Integer rid){
        return ResponseEntity.ok(iPubNotifyService.getSysuserByRoleId(rid));
    }
}
