package com.ttms.Controller.DistributorEntry;

import com.ttms.Entity.DisTourist;
import com.ttms.Entity.ProPricepolicy;
import com.ttms.Entity.ProProduct;
import com.ttms.Entity.SupDistributor;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Vo.PageResult;
import com.ttms.Vo.ResoAttachmentVo;
import com.ttms.service.DistributorEntry.IDistributorService;
import com.ttms.service.ProductManage.IProductListService;
import com.ttms.service.ResourceManage.IAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


/*----------------------------------------分销商入口---------------------------------------------------*/
@RestController
@RequestMapping("/distributorEntry")
public class DistributorController {

    @Autowired
    private IProductListService productListService;

    @Autowired
    private IDistributorService distributorService;

    @Autowired
    private IAttachmentService attachmentService;

    /**
     * 功能描述: <br>
     * 〈〉万少波
     * @Param: [distributorname, password, request]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/12 9:46
     */
    @PostMapping("/login")
    public ResponseEntity<SupDistributor> distributorLogin(
                            @RequestParam String distributorname,
                             @RequestParam String password,
                             HttpServletRequest request){
        return ResponseEntity.ok(distributorService.login(distributorname,password,request));
    }

    /**
    * 功能描述: <br>
    * 〈〉查询所有可用的产品
    * @Param: []
    * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.ProProduct>>
    * @Author: 吴彬
    * @Date: 8:38 8:38
     */
    @GetMapping("/auth/getAvailableProducts")
    public ResponseEntity<PageResult<ProProduct>> getAvailableProducts(
            @RequestParam Integer did,
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
        //查询出该分销商有权报名的所有产品id
        List<Integer> idList = null;
        idList = productListService.getProductIdsByDistributorId(did);
        return ResponseEntity.ok(this.productListService.queryProjectByPage(status,productCatId1,
                productCatId2,productCatId3,projectName,productNumber,productName,serverStartTime,
                serverEndTime,idList,page,size));
    }

  /**
  * 功能描述: <br>
  * 〈〉查询该分销商下 的所有报名 的游客
  * @Param: []
  * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.DisTourist>>
  * @Author: 吴彬
  * @Date: 9:14 9:14
   */
    @GetMapping("/auth/showMySignUpTourist")
    public ResponseEntity<List<DisTourist>> getMySignUpTourist() {
//  /**
//  * 功能描述: <br>
//  * 〈〉查询该分销商下 的所有报名 的游客
//  * @Param: []
//  * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.DisTourist>>
//  * @Author: 吴彬
//  * @Date: 9:14 9:14
//   */
//    @GetMapping("/showMySignUpTourist")
//    public ResponseEntity<List<DisTourist>> getMySignUpTourist(){
//        SupDistributor supDistributor = (SupDistributor) SecurityUtils.getSubject().getPrincipal();
//        return ResponseEntity.ok(this.distributorService.getMySignUpTourist(supDistributor.getId()));
//    }
        return null;
    }

    /**
    * 功能描述: <br>
    * 〈〉查询该分销商下该产品的报名游客的人
    * @Param: [productId]
    * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.DisTourist>>
    * @Author: 吴彬
    * @Date: 9:35 9:35
     */
    @GetMapping("auth/getAvailableProducts/signUptourist")
    public ResponseEntity<List<DisTourist>> getMySignUpTourist(@RequestParam(name = "productId") Integer productId,HttpServletRequest req){
        SupDistributor curdistributor = (SupDistributor) req.getSession().getAttribute("curdistributor");
        if(curdistributor == null ){
            throw new TTMSException(ExceptionEnum.USER_UNLOGIN);
        }
        return ResponseEntity.ok(this.distributorService.getMySignUpTourist(curdistributor.getId(), productId));
    }

    /**
    * 功能描述: <br>
    * 〈〉分销商退出
    * @Param: []
    * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
    * @Author: 吴彬
    * @Date: 9:26 9:26
     */
    @GetMapping("/loginout")
    public ResponseEntity<Void> loginout(HttpServletRequest request){
        request.getSession().setAttribute("curdistributor",null);
        return ResponseEntity.ok(null);
    }

    /**
     * 功能描述: <br>
     * 〈〉获得当前登录的用户信息
     * @Param: [request]
     * @Return: org.springframework.http.ResponseEntity<com.ttms.Entity.SupDistributor>
     * @Author: 万少波
     * @Date: 2019/6/12 9:53
     */
    @GetMapping("/getCurDistributor")
    public ResponseEntity<SupDistributor> getCurDistributor(HttpServletRequest request){
        SupDistributor curdistributor = (SupDistributor) request.getSession().getAttribute("curdistributor");
        if(curdistributor == null ){
            throw new TTMSException(ExceptionEnum.USER_UNLOGIN);
        }
        return ResponseEntity.ok(curdistributor);
    }

    /**
     * 功能描述: <br>
     * 〈〉获取产品下的价格政策
     * @Param: [productId]
     * @Return: org.springframework.http.ResponseEntity<com.ttms.Entity.ProPricepolicy>
     * @Author: 万少波
     * @Date: 2019/6/12 15:48
     */
    @GetMapping("/auth/getPricePolicyByProductId/{productId}")
    public ResponseEntity<List<ProPricepolicy>> getPricePolicyByProductId(@PathVariable Integer productId){
        return ResponseEntity.ok(distributorService.getPricePolicyByProductId(productId));
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * @Param: 报名
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/12 17:24
     */
    @PostMapping("/auth/signup")
    public ResponseEntity<Void> signup(@RequestParam(required = false) Integer pricePolicy,
                                       @RequestParam String name,
                                       @RequestParam Byte sex,
                                       @RequestParam String idcard,
                                       @RequestParam String phone,
                                       @RequestParam String desc,
                                       @RequestParam Integer productId,
                                       HttpServletRequest request){
        //获取当前分销商id
        SupDistributor curdistributor = (SupDistributor) request.getSession().getAttribute("curdistributor");
        return ResponseEntity.ok(distributorService.signup(pricePolicy,name,sex,idcard,phone,desc,productId,curdistributor.getId()));
    }

    /**
    * 功能描述: <br>
    * 〈〉取消报名
    * @Param: [productId]
    * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
    * @Author: 吴彬
    * @Date: 14:48 14:48
     */
    @GetMapping("/auth/cancelSign")
    public ResponseEntity<Void> cancelSignUp(@RequestParam(name = "touristId") Integer touristId,@RequestParam(name = "productId") Integer productId){
        return ResponseEntity.ok(this.distributorService.cancelSignUp(touristId,productId));
    }

    /**
    * 功能描述: <br>
    * 〈〉查询所有附件
    * @Param: [pid]
    * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.ResoAttachment>>
    * @Author: 吴彬
    * @Date: 10:35 10:35
     */
    @GetMapping("/allAttachment/{pid}")
    public ResponseEntity<List<ResoAttachmentVo>> getAttachmentsByPid(@PathVariable(value = "pid") int pid){
        return ResponseEntity.ok(this.attachmentService.getResoAttachmentByproductIdAndUerName(pid));
    }

}
