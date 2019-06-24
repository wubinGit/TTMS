package com.ttms.service.ProductManage;

import com.ttms.Entity.ProProductCat;
import com.ttms.Entity.SysUser;

import java.util.List;

public interface IProductCatService {
    ProProductCat getProductCatById(Integer parentid);

    List<ProProductCat> getProductCatByIds(List<Integer> ids);

    List<ProProductCat> queryCatById(Integer catId);

    Void addProductCat(Integer parentId, String name, SysUser user, String note);

    Void deleteProProductCat(Integer productId);

    Void updateProProductCat(Integer productId, String name, SysUser user, String note);
}
