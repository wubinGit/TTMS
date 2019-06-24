package com.ttms.service.SupplyManage;

import com.ttms.Entity.SupDistributor;

import java.util.List;

public interface IDistributorManageService {
    List<SupDistributor> getAllDistributor();

    List<SupDistributor> getAllDistributorInfoNotInThisProduct(List<Integer> ids);
}
