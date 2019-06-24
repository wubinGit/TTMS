package com.ttms.service.ProductManage.ServiceImpl;

import com.ttms.Entity.ProProduct;
import com.ttms.Entity.ProProductCat;
import com.ttms.Entity.SysUser;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.ProProductCatMapper;
import com.ttms.Mapper.ProProductMapper;
import com.ttms.service.ProductManage.IProductCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class ProductCatService implements IProductCatService {
    @Autowired
    private ProProductCatMapper proProductCatMapper;
    @Autowired
    private ProProductMapper productMapper;

    @Override
    public ProProductCat getProductCatById(Integer parentid) {
        ProProductCat productCat = proProductCatMapper.selectByPrimaryKey(parentid);
        if(productCat == null){
            throw new TTMSException(ExceptionEnum.PRODUCTCATID_NOT_FOUND);
        }
        return productCat;
    }

    public List<ProProductCat> getProductCatByIds(List<Integer> ids){
        List<ProProductCat> proProductCats = proProductCatMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(proProductCats)) {
            throw new TTMSException(ExceptionEnum.PRODUCTCATID_NOT_FOUND);
        }
        return proProductCats;
    }

    /**
     * 功能描述: <br>
     * 〈〉根据id查询分类
     * @Param: [catId]
     * @Return: java.util.List<com.ttms.Entity.ProProductCat>
     * @Author: 吴彬
     * @Date: 11:15 11:15
     */
    @Override
    public List<ProProductCat> queryCatById(Integer catId) {
        Example example=new Example(ProProductCat.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentid", catId);
        List<ProProductCat> list = this.proProductCatMapper.selectByExample(example);
        if(org.apache.commons.collections.CollectionUtils.isEmpty(list)){
            throw new TTMSException(ExceptionEnum.PRODUCT_CAT_NOT_FOUNDF);
        }
        return list;
    }

    @Override
    @Transactional
    public Void addProductCat(Integer parentId, String name, SysUser user, String note) {
        //添加内容分类补全数据
        ProProductCat productCat=new ProProductCat();
        productCat.setCreatetime(new Date());
        productCat.setCreateuserid(user.getId());
        productCat.setIsParent((byte)0);
        productCat.setParentid(parentId);
        productCat.setUpdatetime(null);
        productCat.setProductcatname(name);
        productCat.setNote(note);
        int i = this.proProductCatMapper.insert(productCat);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.PRODUCT_CAT_ADD_FAIL);
        }
        //如果根据父id为主键查询不存在的话
        ProProductCat proProductCat = proProductCatMapper.selectByPrimaryKey(parentId);
        //证明添加的是一级分类可以结束方法
        if(proProductCat==null){
            productCat.setIsParent((byte)1);
            int update = this.proProductCatMapper.updateByPrimaryKeySelective(productCat);
            if(update!=1){
                throw new TTMSException(ExceptionEnum.PRODUCT_CAT_ADD_FAIL);
            }
            return null;
        }
        if(proProductCat.getIsParent()!=1){
            proProductCat.setIsParent((byte) 1);
            int key = this.proProductCatMapper.updateByPrimaryKey(proProductCat);
        }
        return null;
    }

    /*
    * 功能描述: <br>
    * 〈〉删除分类
    * @Param: [productId]
    * @Return: java.lang.Void
    * @Author: 吴彬
    * @Date: 21:36 21:36
     */
    @Override
    @Transactional
    public Void deleteProProductCat(Integer productCatId) {
        //判断该分类下有没产品存在 有提示删除失败
        Example example=new Example(ProProduct.class);
        example.createCriteria().andEqualTo("productcatid1", productCatId);
        List<ProProduct> proProducts = this.productMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(proProducts)){
            throw new TTMSException(ExceptionEnum.PRODUCT_CAT_EXISTENCE_PRODUCE);
        }
        //删除节点
        deleteProductCat(productCatId);
        //先查询是否id是否存在子节点
        return null;
    }

    /**
     * 功能描述: <br>
     * 〈〉修改分类
     * @Param: [productId]
     * @Return: java.lang.Void
     * @Author: 吴彬
     * @Date: 21:56 21:56
     */
    @Override
    @Transactional
    public Void updateProProductCat(Integer productId, String name, SysUser user, String note) {
        ProProductCat productCat=new ProProductCat();
        productCat.setUpdateuserid(user.getId());
        productCat.setId(productId);
        productCat.setUpdatetime(new Date());
        productCat.setProductcatname(name);
        productCat.setNote(note);
        int i = this.proProductCatMapper.updateByPrimaryKeySelective(productCat);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.PRODUCT_CAT_UPDATE_FAIL);
        }
        return null;
    }




    /**
    * 功能描述: <br>
    * 〈〉递归删除
    * @Param: [productCatId]
    * @Return: void
    * @Author: 吴彬
    * @Date: 22:01 22:01
     */
    @Transactional
    public void deleteProductCat(Integer productCatId) {
//        Example example=new Example(ProProductCat.class);
//        example
        ProProductCat productCat = this.proProductCatMapper.selectByPrimaryKey(productCatId);
        if(productCat.getIsParent()==1){
            Example example=new Example(ProProductCat.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("parentid", productCat.getId());
            List<ProProductCat> productCats = this.proProductCatMapper.selectByExample(example);
            for (ProProductCat cat : productCats) {
                Integer id = cat.getId();
                deleteProductCat(id);
            }
        }
            int i = this.proProductCatMapper.deleteByPrimaryKey(productCatId);

    }
}
