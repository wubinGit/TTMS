package com.ttms.Controller.News;

import com.ttms.Entity.MesMessage;
import com.ttms.Vo.PageResult;
import com.ttms.service.ProductManage.INotifyManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//----------------------消息中心-通知管理-通知管理------------------------
@RestController
@RequestMapping("/news/notifymanage/notifymanage")
public class NotifyManageController {

    @Autowired
    private INotifyManageService iNotifyManageService;


   /**
   * 功能描述: <br>
   * 〈〉消息类型，发布人，消息标题
   * @Param: [messageclassname, messagetitle, sendName]
   * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.MesMessage>>
   * @Author: 吴彬
   * @Date: 8:29 8:29
    */
    @GetMapping("/queryAllnew/page")
    public ResponseEntity<PageResult<MesMessage>> queryAllnewPage(@RequestParam(value = "page" ,defaultValue = "1")
                                                                        Integer page, @RequestParam(value = "rows" ,defaultValue = "5")
                                                                Integer rows, @RequestParam(required = false) Integer sendtype,
                                                                  @RequestParam(required = false) String messagetitle,
                                                                  @RequestParam(required = false) String sendName
                                                            ){
        return ResponseEntity.ok(this.iNotifyManageService.queryAllnewPage
                (page,rows,sendtype,messagetitle,sendName));
    }



    @GetMapping("{mid}")
    public ResponseEntity<Void> updateState(@PathVariable("mid") Integer mid){
        this.iNotifyManageService.updateState(mid);
        return ResponseEntity.ok().build();
    }

}
