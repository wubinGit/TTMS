package com.ttms.service.ProductManage;

import com.ttms.Entity.ProPricepolicy;
import com.ttms.Entity.ProProduct;
import com.ttms.Entity.ResGuide;
import com.ttms.Entity.SupDistributor;
import com.ttms.Vo.PageResult;
import com.ttms.Vo.ProductVo;
import com.ttms.Vo.ResoAttachmentVo;

import java.util.Date;
import java.util.List;

public interface IProductListService {
    PageResult<ProProduct> queryProjectByPage(int status, int productCatId1, int productCatId2,
                                              int productCatId3, String projectName, String productNumber,
                                              String productName, Date serverStartTime, Date serverEndTime ,
                                              List<Integer> prodicuIdList, int page, int size);

    void updateproductStatus(Integer productId, Integer pstatus);

    List<ProductVo> getDistributorsByPid(Integer pid);

    void addProductDistribute(Integer pid, Integer distributorNumber, Date startTime, Date endTime);


    Void deleteProductDistribute(Integer pid, Integer productDistributorId);

    List<SupDistributor> getAllDistributorInfo();

    List<ResoAttachmentVo> getAttachmentsByPid(int pid);

    Void addAttachement( int pid , String fileName , String fileUrl,String attachmentname);

    ProProduct getProductById(int pid);

    boolean checkIsCharger(int productId);
    Integer selectProductCreateUser(Integer productId);


    //查询商品剩余的数量
    Integer selectProductLowestNumber(Integer pid);

    List<ResGuide> getGuidesByProductId(Integer pid);

    Void deleteProductGuide(Integer productId, Integer guideId);

    PageResult<ResGuide> queryGuidesNotInProduct(Integer productId ,

    String guideName, String mobile, String language, String nationality, int page, int rows);

    Void addProductGuide﻿(Integer productId, List<Integer> guideIds);

    List<ProPricepolicy> getPricePolicyByProductId(Integer pid);

    Void deleteProductPricePolicy(int productId, int pricePolicyId);

    PageResult<ProPricepolicy> getPolicyNotinProductByPage(Integer productId, String pricePolicyName, Date startTime, Date endTime , int page  , int rows);

    Void addProductPricePolicy(int productId, List<Integer> pricepolicyIds);

    //添加行程
    Void addRount(Integer productId, String name, String content, String stayMessage, String breakfast, String lunch, String supper);

    List<Integer> getProductIdsByDistributorId(Integer did);

    Void deleteAttachmentsByid(Integer pid);

    List<SupDistributor> getAllDistributorInfoNotInThisProduct(Integer pid);

    //为产品添加分销商之后产品的数量更改  修改产品的数量
   // Void updataProductNumber(Integer productId);


}
