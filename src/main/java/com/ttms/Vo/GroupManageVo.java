package com.ttms.Vo;

import com.ttms.Entity.ProGroup;
import lombok.Data;

@Data
public class GroupManageVo {

    private ProGroup proGroup;
    //负责人姓名
    private String chargerName;

    //负责人电话号码
    private String chargerPhoneNumber;

}
