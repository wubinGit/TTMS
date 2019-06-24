package com.ttms.Controller.ProduceManage;

import com.ttms.Entity.ProGroup;
import com.ttms.Entity.ProProductCat;
import com.ttms.Entity.SysUser;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.service.ProductManage.ICreateProductService;
import com.ttms.service.ProductManage.IGroupService;
import com.ttms.service.ProductManage.ServiceImpl.ProductCatService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


//----------产品管理->产品->创建产品------------
@RestController
@RequestMapping("/producemanage/product/createproduct")
public class CreateProductController {
    @Autowired
    private ICreateProductService createProductService;

    @Autowired
    private ProductCatService productCatService;

    @Autowired
    private IGroupService groupService;


    /*
     *功能描述：查询所有团号
     *@author罗占
     *@Description
     *Date17:04 2019/5/30
     *Param
     *return
     **/
    @GetMapping("/queryAllGroups")
    public ResponseEntity<List<ProGroup>> queryGroupByCuruser(){
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        List<ProGroup> groups = this.createProductService.queryGroupByCuruser(user);
        return ResponseEntity.ok().body(groups);
    }

    /**
     * 功能描述: 创建产品
     * 〈〉
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: lhf
     * @Date: 2019/5/31 10:40
     */
    @PostMapping("/createProduct")
    public ResponseEntity<Void> createProduct(@RequestParam(name = "groupId") Integer groupId,
                                              @RequestParam Integer productCatId1,
                                              @RequestParam Integer productCatId2,
                                              @RequestParam Integer productCatId3,
                                              @RequestParam String productName,
                                              @RequestParam Date serverStartTime,
                                              @RequestParam Date serverEndTime,
                                              @RequestParam Integer preSellNumber,
                                              @RequestParam Integer selledNumber,
                                              @RequestParam Integer lowestNumber,
                                              @RequestParam Date onsellTime,
                                              @RequestParam Integer productPrice,
                                              @RequestParam Date upsellTime,
                                              @RequestParam String hotTip,
                                              @RequestParam String productIntroduction){
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        if(user==null){
            throw new TTMSException(ExceptionEnum.USER_UNLOGIN);
        }
        createProductService.createProduct(groupId,productCatId1,productCatId2,
                                            productCatId3,productName,serverStartTime,
                                            serverEndTime,preSellNumber,selledNumber,lowestNumber,
                                            onsellTime,productPrice,upsellTime, hotTip,productIntroduction,user);
        return ResponseEntity.ok().body(null);
    }

    /**
    * 功能描述: <br>
    * 〈〉, @RequestParam(name = "groupId") Integer groupId,
    * @Param: [pid, productCatId1, productCatId2, productCatId3, productName, serverStartTime, serverEndTime, preSellNumber, selledNumber, lowestNumber, onsellTime, productPrice, upsellTime, hotTip, productIntroduction]
    * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
    * @Author: 吴彬
    * @Date: 16:51 16:51
     */
    @PostMapping("/privilege/{pid}")
    public ResponseEntity<Void> editProduct(
           @PathVariable("pid") Integer pid,
            @RequestParam Integer productCatId1,
            @RequestParam Integer productCatId2,
            @RequestParam(value = "productCatId3") Integer productCatId3,
            @RequestParam(value = "productName") String productName,
            @RequestParam(value = "serverStartTime") Date serverStartTime,
            @RequestParam(value = "serverEndTime") Date serverEndTime,
            @RequestParam(value = "preSellNumber") Integer preSellNumber,
            @RequestParam(value = "selledNumber") Integer selledNumber,
            @RequestParam(value = "lowestNumber") Integer lowestNumber,
            @RequestParam(value = "onsellTime") Date onsellTime,
            @RequestParam(value = "projectPrice") Integer productPrice,
            @RequestParam(value = "upsellTime") Date upsellTime,
            @RequestParam(value = "hotTip") String hotTip,
            @RequestParam(value = "productIntroduction") String productIntroduction){
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        createProductService.UpdateProduct(pid,null, productCatId1,productCatId2
        , productCatId3,productName, serverStartTime, serverEndTime, preSellNumber, selledNumber
        , lowestNumber, onsellTime, productPrice, upsellTime, hotTip, productIntroduction, user);
        return ResponseEntity.ok().body(null);
    }

    /**
     * 功能描述: <br>
     * 〈〉根据id查询所有
     * @Param: [catId]
     * @Return: java.util.List<com.ttms.Entity.ProProductCat>
     * @Author: 吴彬
     * @Date: 11:10 11:10
     */
    @GetMapping("queryCatById")
    public ResponseEntity<List<ProProductCat>> queryCatById(@RequestParam("catId") Integer catId){
        return ResponseEntity.ok(this.productCatService.queryCatById(catId));
    }

    /**
    * 功能描述: <br>
    * 〈〉查询用户是否和当前团的负责人一致
    * @Param: [groupId]
    * @Return: org.springframework.http.ResponseEntity<com.ttms.Entity.SysUser>
    * @Author: 吴彬
    * @Date: 13:31 13:31
     */
    @GetMapping("/AmIcharger/{groupId}")
    public ResponseEntity<SysUser> AmIcharger(@PathVariable("groupId") Integer groupId){
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        Boolean amIcharger=this.groupService.AmIcharger(groupId,user.getId());
        if(!amIcharger){
            throw new TTMSException(ExceptionEnum.USER_NOT_GROUPCHARGEUSER);
        }
        return ResponseEntity.ok(user);
    }

}
