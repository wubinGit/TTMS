package com.ttms.service.ProductManage;

import com.ttms.Entity.MesMessage;
import com.ttms.Entity.SysUser;
import com.ttms.Vo.PageResult;

import java.util.List;

public interface INotifyManageService {

    //查询所有通知
    public List<MesMessage> queryAllnew(int size);

    //查询所有有关自己的通知
    List<MesMessage> querybyUser(SysUser user);

    Void updateState(Integer mid);

    PageResult<MesMessage> queryAllnewPage(Integer page, Integer rows, Integer messageclassname, String messagetitle, String sendName);
}
