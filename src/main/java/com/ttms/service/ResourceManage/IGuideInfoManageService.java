package com.ttms.service.ResourceManage;

import com.ttms.Entity.ResGuide;

import java.util.List;

public interface IGuideInfoManageService {
    List<ResGuide> getGuidesByProductId(Integer pid);

    List<ResGuide> queryGuidesByCriteria(List<Integer> productGuideIds, String guideName, String mobile,
                        String language, String nationality);
}
