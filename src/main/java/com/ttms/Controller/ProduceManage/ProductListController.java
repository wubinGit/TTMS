package com.ttms.Controller.ProduceManage;

import com.ttms.Entity.*;
import com.ttms.Vo.PageResult;
import com.ttms.Vo.ProductVo;
import com.ttms.Vo.ResoAttachmentVo;
import com.ttms.service.ProductManage.IProductCatService;
import com.ttms.service.ProductManage.IProductListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

//----------产品管理->产品->产品列表------------
@RestController
@RequestMapping("/producemanage/product/productlist")
public class ProductListController {
    @Autowired
    private IProductListService productListService;

    @Autowired
    private IProductCatService productCatService;

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
     * 〈〉更新产品的状态【上架，下架，代售】
     * @Param: [productId, pstatus]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 吴彬
     * @Date: 17:18 17:18
     */
    @PutMapping("/privilege/updateproductStatus")
    public ResponseEntity<Void> updateproductStatus(@RequestParam Integer productId ,@RequestParam Integer pstatus){
        productListService.updateproductStatus(productId,pstatus);
        return  ResponseEntity.ok().build();
    }

    /**
     * 功能描述: <br>
     * 〈〉查询该产品所有的分销商
     * @Param: [pid]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Vo.ProductVo>>
     * @Author: 吴彬
     * @Date: 17:17 17:17
     */
    @GetMapping("/distributor/{pid}")
    public ResponseEntity<List<ProductVo>> getDistributorsByPid(@PathVariable("pid") Integer pid){
        return ResponseEntity.ok(this.productListService.getDistributorsByPid(pid));
    }

    /**
     * 功能描述: <br>
     * 〈〉为产品添加分销商
     * @Param: [pid, distributorId, distributorNumber, startTime, endTime]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 吴彬
     * @Date: 17:29 17:29
     */
    @PostMapping("/privilege/distributor")
    public ResponseEntity<Void> addProductDistribute(Integer productId ,Integer distributorId ,
                                                      Date startTime, Date endTime){

        // 没有完成需要查询产品的数量是否够分销  需要加一次判断
        this.productListService.addProductDistribute(productId,distributorId,startTime,endTime);
        return   ResponseEntity.ok().build();
    }

    /**
     * 功能描述: <br>
     * 〈〉分页查询项目
     * @Param: [status, productCatId1, productCatId2, productCatId3, projectName, productNumber, productName, serverStartTime, serverEndTime, page, size]
     * @Return: org.springframework.http.ResponseEntity<com.ttms.Vo.PageResult>
     * @Author: 万少波
     * @Date: 2019/5/31 14:15
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<ProProduct>>  queryProjectByPage(
            @RequestParam(required = false,defaultValue = "-1") int status,
            @RequestParam(required = false,defaultValue = "-1") int productCatId1,
            @RequestParam(required = false,defaultValue = "-1") int productCatId2,
            @RequestParam(required = false,defaultValue = "-1") int productCatId3,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String productNumber,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Date serverStartTime,
            @RequestParam(required = false) Date serverEndTime ,
            @RequestParam(required = false ,defaultValue = "1") int page ,
            @RequestParam(required = false , defaultValue = "5") int size
    ){

        return ResponseEntity.ok(productListService.queryProjectByPage(status,productCatId1,
                productCatId2,productCatId3,projectName,productNumber,productName,serverStartTime,
                serverEndTime,null,page,size));
    }

    /**
     * 功能描述: <br>
     * 〈〉删除为产品分销的分销商
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/1 13:55
     */
    @DeleteMapping("/privilege/distributor")
    public ResponseEntity<Void> deleteProductDistribute(@RequestParam Integer productId ,@RequestParam Integer productDistributorId){
        return ResponseEntity.ok(productListService.deleteProductDistribute(productId,productDistributorId));
    }

    /**
     * 功能描述: <br>
     * 〈〉获取该产品中没有添加的分销商
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SupDistributor>>
     * @Author: 万少波
     * @Date: 2019/6/1 14:39
     */
    @GetMapping("/distributors")
    public ResponseEntity<List<SupDistributor>> getAllDistributorInfoNotInThisProduct(@RequestParam Integer pid){
        return ResponseEntity.ok(productListService.getAllDistributorInfoNotInThisProduct(pid));
    }


    /**
     * 功能描述: <br>
     * 〈〉查询当前产品下所有附件
     * @Param: [pid]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.ResoAttachment>>
     * @Author: 万少波
     * @Date: 2019/6/1 14:49
     */
    @GetMapping("/allAttachment/{pid}")
    public ResponseEntity<List<ResoAttachmentVo>> getAttachmentsByPid(@PathVariable int pid){
        return ResponseEntity.ok(productListService.getAttachmentsByPid(pid));
    }

    @DeleteMapping("/del/Attachment/{pid}")
    public ResponseEntity<Void> deleteAttachmentsByPid(@PathVariable Integer pid){
        return ResponseEntity.ok(productListService.deleteAttachmentsByid(pid));
    }

    /**
     * 功能描述: <br>
     * 〈〉新增附件
     * @Param: [pid, fileName, attachmentname, attachmentUrl]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/1 14:58
     */
    @PostMapping("/privilege/attachment")
    public ResponseEntity<Void> addAttachement(@RequestParam int productId  ,
                                               @RequestParam String fileName ,
                                               @RequestParam String fileUrl,
                                               @RequestParam String attachmentname){
        return ResponseEntity.ok(productListService.addAttachement(productId ,fileName ,fileUrl ,attachmentname));
    }

    /**
     * 功能描述: <br>
     * 〈〉获取和产品关联的所有导游
     * @Param: [pid]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.ResGuide>>
     * @Author: 万少波
     * @Date: 2019/6/1 20:29
     */
    @GetMapping("/guide/{pid}")
    public ResponseEntity<List<ResGuide>> getGuidesByProductId(@PathVariable Integer pid){
        return ResponseEntity.ok(productListService.getGuidesByProductId(pid));
    }

    /**
     * 功能描述: <br>
     * 〈〉删除产品下面的导游
     * @Param: [productId, guideId]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/1 20:35
     */
    @DeleteMapping("/privilege/guide")
    public ResponseEntity<Void> deleteProductGuide(@RequestParam Integer productId  , @RequestParam Integer guideId){
        return ResponseEntity.ok(productListService.deleteProductGuide(productId , guideId));
    }
    
    /**
     * 功能描述: <br>
     * 〈〉分页查询所有我的项目中未添加的导游
     * @Param: [guideName, mobile, language, nationality, page, rows]
     * @Return: org.springframework.http.ResponseEntity<com.ttms.Vo.PageResult<com.ttms.Entity.ProProductGuide>>
     * @Author: 万少波
     * @Date: 2019/6/1 21:19
     */
    @GetMapping("/guide/page")
    public ResponseEntity<PageResult<ResGuide>>
    queryGuidesNotInProduct(
                           @RequestParam  Integer productId,
                           @RequestParam(required = false)  String guideName ,
                           @RequestParam(required = false) String mobile,
                           @RequestParam(required = false) String language ,
                           @RequestParam(required = false) String nationality,
                           @RequestParam(required = false,defaultValue = "1") int page ,
                           @RequestParam(required = false,defaultValue = "5") int rows){
        return ResponseEntity.ok(productListService.queryGuidesNotInProduct(productId,guideName,mobile,language,nationality,page,rows));
    }

    /**
     * 功能描述: <br>
     * 〈〉为产品添加导游
     * @Param: [productId, guideIds]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/2 9:08
     */
    @PostMapping("/privilege/guide")
    public ResponseEntity<Void> addProductGuide﻿(@RequestParam Integer productId , @RequestParam @RequestBody List<Integer> guideIds){
        return ResponseEntity.ok(productListService.addProductGuide﻿(productId,guideIds));
    }

    /**
     * 功能描述: <br>
     * 〈〉查询当前产品加入的价格政策
     * @Param: [pid]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.ResGuide>>
     * @Author: 万少波
     * @Date: 2019/6/2 9:31
     */
    @GetMapping("/pricepolicy/{pid}")
    public ResponseEntity<List<ProPricepolicy>> getPricePolicyByProductId(@PathVariable Integer pid){
        return ResponseEntity.ok(productListService.getPricePolicyByProductId(pid));
    }

    /**
     * 功能描述: <br>
     * 〈〉删除产品下面的价格政策
     * @Param: [productId, pricePolicyId]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/2 10:17
     */
    @DeleteMapping("/privilege/pricepolicy")
    public ResponseEntity<Void> deleteProductPricePolicy(@RequestParam  int productId, @RequestParam  int pricePolicyId){
        return ResponseEntity.ok(productListService.deleteProductPricePolicy(productId,pricePolicyId));
    }

    /**
     * 功能描述: <br>
     * 〈〉分页查询产品下关联的价格政策
     * @Param: [productId, pricePolicyName, startTime, endTime, page, rows]
     * @Return: org.springframework.http.ResponseEntity<com.ttms.Vo.PageResult<com.ttms.Entity.ProPricepolicy>>
     * @Author: 万少波
     * @Date: 2019/6/2 11:09
     */
    @GetMapping("/pricepolicy/page")
    public ResponseEntity<PageResult<ProPricepolicy>> getPolicyNotinProductByPage(@RequestParam Integer productId,
                                                                  @RequestParam(required = false) String pricePolicyName ,
                                                                  @RequestParam(required = false) Date startTime ,
                                                                  @RequestParam(required = false) Date endTime,
                                                                  @RequestParam(required = false,defaultValue = "1") int page,
                                                                  @RequestParam(required = false,defaultValue = "5") int rows
                                                                  ){
        return ResponseEntity.ok(productListService.getPolicyNotinProductByPage(productId, pricePolicyName ,startTime ,endTime ,page,rows ));
    }

    /**
     * 功能描述: <br>
     * 〈〉为产品添加价格政策
     * @Param: [productId, pricespolicyIds]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/2 11:12
     */
    @PostMapping("/privilege/pricepolicy")
    public ResponseEntity<Void> addProductPricePolicy(@RequestParam int productId ,
                                                      @RequestParam @RequestBody List<Integer> pricespolicyIds){
        return ResponseEntity.ok(productListService.addProductPricePolicy(productId,pricespolicyIds));
    }
    @PostMapping("/privilege/route")
    public ResponseEntity<Void> addRount(Integer productId,String name,
                                                      String content,String stayMessage,String breakfast,String lunch,String supper){

    return ResponseEntity.ok(productListService.addRount(productId,name,content,stayMessage,breakfast,lunch,supper));
    }
 }
