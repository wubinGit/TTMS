package com.ttms.Controller.SupplyManage;

import com.ttms.service.SupplyManage.IDistributorManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//-------------------供销管理-分销商-分销商管理
@RestController
@RequestMapping("/supplymanage/distributor/distributormanage")
public class DistributorManageController {
    @Autowired
    IDistributorManageService distributorManageService;

}
