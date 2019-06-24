package com.ttms.Controller.ProduceManage;

import com.ttms.Entity.ProProductCat;
import com.ttms.Entity.SysUser;
import com.ttms.service.ProductManage.IProductCatService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//----------产品管理->产品->产品分类------------
@RestController
@RequestMapping("/producemanage/product/productcat")
public class ProductCatController {

    @Autowired
    private IProductCatService productCatService;


    /**
    * 功能描述: <br>
    * 〈〉添加分类
    * @Param: []
    * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
    * @Author: 吴彬
    * @Date: 20:42 20:42
     */
    @PostMapping("/add")
    public ResponseEntity<Void> addProProductCat(@RequestParam(defaultValue = "0",required = false) Integer parentId,
                                                 @RequestParam String name,@RequestParam String note){
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
         this.productCatService.addProductCat(parentId, name, user,note);
         return ResponseEntity.ok().build();
    }
    /**
    * 功能描述: <br>
    * 〈〉删除分类
    * @Param: [productId]
    * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
    * @Author: 吴彬
    * @Date: 21:33 21:33
     */
    @DeleteMapping("{productId}")
    public ResponseEntity<Void> deleteProProductCat(@PathVariable("productId") Integer productId){
        this.productCatService.deleteProProductCat(productId);
        return  ResponseEntity.ok().build();
    }
    
    /**
    * 功能描述: <br>
    * 〈〉修改分类
    * @Param: [productId]
    * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
    * @Author: 吴彬
    * @Date: 21:55 21:55
     */
    @PutMapping("{productId}")
    public ResponseEntity<Void> updateProProductCat(@PathVariable("productId") Integer productId,@RequestParam String name,@RequestParam String note){
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        this.productCatService.updateProProductCat(productId,name,user,note);
        return  ResponseEntity.ok().build();
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




}
