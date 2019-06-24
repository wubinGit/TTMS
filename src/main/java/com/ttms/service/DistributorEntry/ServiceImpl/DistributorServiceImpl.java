package com.ttms.service.DistributorEntry.ServiceImpl;

import com.ttms.Entity.DisTourist;
import com.ttms.Entity.ProPricepolicy;
import com.ttms.Entity.ProProduct;
import com.ttms.Entity.SupDistributor;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.DisToruistMapper;
import com.ttms.Mapper.ProProductMapper;
import com.ttms.Mapper.SupDistributorMapper;
import com.ttms.service.DistributorEntry.IDistributorService;
import com.ttms.service.ProductManage.IPricePolicyService;
import com.ttms.service.ProductManage.IProductListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DistributorServiceImpl implements IDistributorService {
    @Autowired
    private SupDistributorMapper distributorMapper;
    @Autowired
    private DisToruistMapper disToruistMapper;
    @Autowired
    private IPricePolicyService pricePolicyService;
    @Autowired
    private IProductListService productService;
    @Autowired
    private ProProductMapper proProductMapper;

    @Override
    public SupDistributor login(String distributorname, String password, HttpServletRequest request) {
        Example example = new Example(SupDistributor.class);
        example.createCriteria().andEqualTo("loginname",distributorname).andEqualTo("loginpass",password);
        List<SupDistributor> supDistributors = distributorMapper.selectByExample(example);
        //登录失败
        if(supDistributors == null || supDistributors.size() !=1)
            throw new TTMSException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        //登录成功
        //将用户信息存session
        request.getSession().setAttribute("curdistributor",supDistributors.get(0));
        return supDistributors.get(0);
    }


    /**
     * 功能描述: <br>
     * 〈〉查询该分销商下该产品的报名游客的人
     * @Param: [productId]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.DisTourist>>
     * @Author: 吴彬
     * @Date: 9:35 9:35
     */
    @Override
    public List<DisTourist> getMySignUpTourist(Integer id, Integer productId) {
        Example example=new Example(DisTourist.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("distributorid", id).andEqualTo("productid", productId);
        List<DisTourist> disTourists = this.disToruistMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(disTourists)){
            throw new TTMSException(ExceptionEnum.TOURIST_RECORD_NOTFOUND);
        }
        //如果参与了活动  则显示活动名称
        Set<Integer> pricePolicyIdSet = disTourists.stream().map(
                DisTourist::getPricepolicyid).collect(Collectors.toSet());
       try{
           List<ProPricepolicy> pricePolicyList = pricePolicyService.
                   getPricePolicyByIds(new LinkedList<>(pricePolicyIdSet));
           Map<Integer, String> pricePolicymap = pricePolicyList.stream().
                   collect(Collectors.toMap(ProPricepolicy::getId, ProPricepolicy::getPolicyname));
           //遍历填充
           for (DisTourist disTourist : disTourists) {
               Integer pricePolicyId = disTourist.getPricepolicyid();
               if(pricePolicyId != null)
                   disTourist.setPricePolicyName(pricePolicymap.get(pricePolicyId));
           }
       }catch (TTMSException e){
            //列表中不存在价格政策
       }

        return disTourists;
    }

    @Override
    @Transactional
    public Void cancelSignUp(Integer touristId,Integer productId) {
        int i = this.disToruistMapper.deleteByPrimaryKey(touristId);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.CANCEL_SIGN_FAIL);
        }
        ProProduct proProduct = this.proProductMapper.selectByPrimaryKey(productId);
        proProduct.setUpdatetime(new Date());
        proProduct.setSellednumber(proProduct.getSellednumber()-1);
        proProduct.setLowestnumber(proProduct.getLowestnumber()+1);
        int j = this.proProductMapper.updateByPrimaryKeySelective(proProduct);
        if(j!=1){
            throw new TTMSException(ExceptionEnum.PRODUCT_EDIT_FAIL);
        }
        return null;
    }


    @Override
    public List<ProPricepolicy> getPricePolicyByProductId(Integer productId) {
  //SELECT * FROM pro_pricepolicy pp JOIN pro_product_pricepolicy ppp ON ppp.`pricePolicyId` = pp.`id` WHERE ppp.`productId` = 1
        return pricePolicyService.getPricePolicyByProductIdEnable(productId);
    }

    @Transactional
    @Override
    public Void signup(Integer pricePolicyId , String name, Byte sex, String idcard, String phone, String desc, Integer productId, Integer did) {
        DisTourist disTourist = new DisTourist();
        disTourist.setTName(name);
        disTourist.setSignuptime(new Date());
        disTourist.setDistributorid(did);
        disTourist.setTIdcard(idcard);
        disTourist.setTNote(desc);
        disTourist.setTPhone(phone);
        disTourist.setTSex(sex);
        disTourist.setProductid(productId);
        //查看当前的产品是否有该价格政策
        Map<Integer, ProPricepolicy> ProPricepolicieyMap = null;
        try{
            ProPricepolicieyMap = getPricePolicyByProductId(productId).stream().collect(Collectors.toMap(ProPricepolicy::getId, item -> item));
        }catch (TTMSException e){

        }
        ProProduct product = productService.getProductById(productId);
        //存在价格政策
        if(!CollectionUtils.isEmpty(ProPricepolicieyMap)){
            Set<Integer> ids = ProPricepolicieyMap.keySet();
            if(pricePolicyId != null && ids.contains(pricePolicyId)){
                disTourist.setPricepolicyid(pricePolicyId);
                disTourist.setAcutalpay(ProPricepolicieyMap.get(pricePolicyId).getPriceafterdiscount());
            }else{
                disTourist.setAcutalpay(product.getProductprice());
            }
        }else{
            disTourist.setAcutalpay(product.getProductprice());
        }

        //查看当前价格政策是否属于当前产品
        disToruistMapper.insert(disTourist);
        //减去剩余数量  增加已购数量
        product.setLowestnumber(product.getLowestnumber()-1);
        product.setSellednumber(product.getSellednumber()+1);
        int count = proProductMapper.updateByPrimaryKey(product);
        if(count != 1)
            throw new TTMSException(ExceptionEnum.TOURIST_SIGNUP_FAIL);
        return null;
    }



}
