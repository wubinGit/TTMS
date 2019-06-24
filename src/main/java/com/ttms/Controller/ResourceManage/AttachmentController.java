package com.ttms.Controller.ResourceManage;

import com.ttms.Entity.SysUser;
import com.ttms.service.ResourceManage.IAttachmentService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//---------------------旅游管理-附件-附件管理
@RestController
@RequestMapping("/resourcemanage/attachment/attachmanage")
public class AttachmentController {

    @Autowired
    private IAttachmentService attachmentService;

    @PostMapping("/add")
    public ResponseEntity<Void> addAttachmanage(@RequestParam(value = "fileName") String fileName,
    @RequestParam(value = "productId") Integer productId,@RequestParam(value = "fileUrl") String fileUrl
                                             ){
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        return ResponseEntity.ok(attachmentService.addAttachment(productId, fileName, fileUrl
        , fileName, sysUser.getId()));
    }//


}
