package com.ttms.service.ProductManage.ServiceImpl;

import com.ttms.Entity.ProPricepolicy;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.ProPricePolicyMapper;
import com.ttms.service.ProductManage.IPricePolicyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class PricePolicyService implements IPricePolicyService {
    @Autowired
    private ProPricePolicyMapper pricePolicyMapper;

    @Override
    public List<ProPricepolicy> getProPricepoliciesByProductId(Integer pid) {
        List<ProPricepolicy> pricepolicies = pricePolicyMapper.getProPricepoliciesByProductId(pid);
        if (CollectionUtils.isEmpty(pricepolicies)) {
            throw new TTMSException(ExceptionEnum.PRODUCT_PRICE_POLICY_NOT_FOUND);
        }
        return pricepolicies;
    }

    @Override
    public List<ProPricepolicy> queryPricePolicyByCriteria(List<Integer> pricePolicyIds,
                                    String pricePolicyName, Date startTime, Date endTime) {
        Example example = new Example(ProPricepolicy.class);
        Example.Criteria criteria = example.createCriteria();
        //封装条件
        if(!CollectionUtils.isEmpty(pricePolicyIds)){
            criteria.andNotIn("id",pricePolicyIds);
        }
        if(!StringUtils.isEmpty(pricePolicyName)){
            criteria.andLike("policyname","%"+pricePolicyName+"%");
        }
        if(startTime != null){
            criteria.andGreaterThan("starttime",startTime);
        }
        if(endTime != null){
            criteria.andLessThan("endtime",endTime);
        }
        //查询并返回
        List<ProPricepolicy> pricepolicies = pricePolicyMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(pricepolicies)) {
            throw new TTMSException(ExceptionEnum.PRODUCT_PRICE_POLICY_NOT_FOUND);
        }
        return pricepolicies;
    }

    @Override
    public List<ProPricepolicy> getPricePolicyByIds(List<Integer> pricepolicyIds) {
        List<ProPricepolicy> pricepolicies = pricePolicyMapper.selectByIdList(pricepolicyIds);
        if (CollectionUtils.isEmpty(pricepolicies)) {
            throw new TTMSException(ExceptionEnum.PRICE_POLICY_NOT_FOUND);
        }
        return pricepolicies;
    }

    @Override
    public List<ProPricepolicy> getPricePolicyByProductId(Integer productId) {
        List<ProPricepolicy> proPricepolicies = pricePolicyMapper.getPricePolicyByProductId(productId);
        if (CollectionUtils.isEmpty(proPricepolicies)) {
            throw new TTMSException(ExceptionEnum.PRICE_POLICY_NOT_FOUND);
        }
        return proPricepolicies;
    }

    /**
    * 功能描述: <br>
    * 〈〉查询当前产品可用的所有价格政策
    * @Param: [productId]
    * @Return: java.util.List<com.ttms.Entity.ProPricepolicy>
    * @Author: 吴彬
    * @Date: 19:29 19:29
     */
    @Override
    public List<ProPricepolicy> getPricePolicyByProductIdEnable(Integer productId) {
        List<ProPricepolicy> proPricepolicies = pricePolicyMapper.getPricePolicyByProductIdEnable(productId);
        if (CollectionUtils.isEmpty(proPricepolicies)) {
            throw new TTMSException(ExceptionEnum.PRICE_POLICY_NOT_FOUND);
        }
        return proPricepolicies;
    }
}
