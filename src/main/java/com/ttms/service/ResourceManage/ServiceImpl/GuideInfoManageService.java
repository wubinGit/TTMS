package com.ttms.service.ResourceManage.ServiceImpl;

import com.ttms.Entity.ResGuide;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.ResGuideMapper;
import com.ttms.service.ResourceManage.IGuideInfoManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;

@Service
public class GuideInfoManageService implements IGuideInfoManageService {
    @Autowired
    private ResGuideMapper resGuideMapper;


    //根据productId查询于其关联的guide
    @Override
    public List<ResGuide> getGuidesByProductId(Integer pid) {
        List<ResGuide> resGuides = resGuideMapper.getGuidesByProductId(pid);
        if (CollectionUtils.isEmpty(resGuides)) {
            throw new TTMSException(ExceptionEnum.RESGUIDE_NOT_FOUND);
        }
        return resGuides;
    }

    @Override
    public List<ResGuide> queryGuidesByCriteria(List<Integer> productGuideIds,
                            String guideName, String mobile, String language,
                               String nationality) {
        Example example = new Example(ResGuide.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(productGuideIds)){
            criteria.andNotIn("id",productGuideIds);
        }
        if(!StringUtil.isEmpty(guideName)){
            criteria.andLike("name","%"+guideName+"%");
        }
        if(!StringUtil.isEmpty(mobile)){
            criteria.andLike("mobile","%"+mobile+"%");
        }
        if(!StringUtil.isEmpty(language)){
            criteria.andLike("language","%"+language+"%");
        }
        if(!StringUtil.isEmpty(nationality)){
            criteria.andLike("nationality","%"+nationality+"%");
        }
        List<ResGuide> resGuides = resGuideMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(resGuides)) {
            throw new TTMSException(ExceptionEnum.RESGUIDE_NOT_FOUND);
        }
        return resGuides;
    }
}
