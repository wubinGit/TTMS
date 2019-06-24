package com.ttms.service.ProductManage.ServiceImpl;

import com.ttms.Entity.ProGroup;
import com.ttms.Entity.ProProduct;
import com.ttms.Entity.ProProductCat;
import com.ttms.Entity.SysUser;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.ProGroupMapper;
import com.ttms.Mapper.ProProductMapper;
import com.ttms.service.ProductManage.ICreateProductService;
import com.ttms.service.ProductManage.IGroupService;
import com.ttms.service.ProductManage.IProductCatService;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class CreateProductService implements ICreateProductService {
    @Autowired
    private IGroupService groupService;
    @Autowired
    private ProGroupMapper groupMapper;

    @Autowired
    private IProductCatService productCatService;
    @Autowired
    private ProProductMapper proProductMapper;



    /**
    * 功能描述: <br>
    * 〈〉创建产品完善
    * @Param: [groupId, productCatId1, productCatId2, productCatId3, productName, serverStartTime, serverEndTime, preSellNumber, selledNumber, lowestNumber, onsellTime, productPrice, upsellTime, hotTip, productIntroduction, user]
    * @Return: void
    * @Author: 吴彬
    * @Date: 15:29 15:29
     */
    @Override
    @Transactional
    public void createProduct(Integer groupId, Integer productCatId1, Integer productCatId2,
                              Integer productCatId3, String productName, Date serverStartTime,
                              Date serverEndTime, Integer preSellNumber, Integer selledNumber,
                              Integer lowestNumber, Date onsellTime, Integer productPrice, Date upsellTime,
                              String hotTip, String productIntroduction, SysUser user) {
        ProProduct proProduct = new ProProduct();
        //查询团号是否存在
        ProGroup proGroup = groupService.getGroupById(groupId);
        proProduct.setCreateuserid(user.getId());
        //当前用户是否和团负责人一致
        if(!user.getId().equals(proGroup.getCreateuserid())){
            throw new TTMSException(ExceptionEnum.USER_NOT_GRUOPCHARGEUSER);
        }
        //团下面是否有产品，有，则不创建
        //查询改用户是否有创建了产品
        ProProduct product=new ProProduct();
        product.setCreateuserid(user.getId());
        List<ProProduct> productList = this.proProductMapper.select(product);
        if(!CollectionUtils.isEmpty(productList)){
            throw new TTMSException(ExceptionEnum.YOU_CREATE_PRODUCT);
        }

        proProduct.setGroupid(groupId);

        //查询三级分类是否是父子关系 和是否存在
        ProProductCat proProductCat3 = productCatService.getProductCatById(productCatId3);
        if(!proProductCat3.getParentid().equals(productCatId2)) {
            throw new TTMSException(ExceptionEnum.NOT_FOUND_PARENTID);
        }
        ProProductCat proProductCat2 = productCatService.getProductCatById(productCatId2);
        if(!proProductCat2.getParentid().equals(productCatId1)) {
            throw new TTMSException(ExceptionEnum.NOT_FOUND_PARENTID);
        }
        proProduct.setCreateuserid(user.getId());
        proProduct.setProductcatid1(productCatId1);
        proProduct.setProductcatid2(productCatId2);
        proProduct.setProductcatid3(productCatId3);
        proProduct.setProductname(productName);
        proProduct.setProductnumber(productName(productCatId1,productCatId2,productCatId3));
        //根据groupId查询补全参数
        //ProGroup group = this.groupMapper.selectByPrimaryKey(groupId);
        ProGroup groupById = this.groupService.getGroupById(groupId);
        proProduct.setProjectid(groupById.getProjectid());
        // proProduct.setProductname(null);
        proProduct.setProjectname(groupById.getProjectname());
        // proProduct.setPresellnumber(0);
        proProduct.setServerstarttime(serverStartTime);
        proProduct.setServerendtime(serverEndTime);
        proProduct.setOnselltime(onsellTime);
        proProduct.setUpselltime(upsellTime);
        proProduct.setPresellnumber(preSellNumber);
        proProduct.setSellednumber(selledNumber);
        proProduct.setLowestnumber(lowestNumber);
        proProduct.setProductprice(productPrice);
        proProduct.setHottip(hotTip);
        proProduct.setProductintroduction(productIntroduction);
        proProduct.setProductstatus(1);
        proProduct.setCreatetime(new Date());
        int i = this.proProductMapper.insert(proProduct);
        if (i != 1) {
            throw new TTMSException(ExceptionEnum.PRODUCT_ADD_FAIL);
        }
        return;
    }

    /**
    * 功能描述: <br>
    * 〈〉查询属于当前用户的所有团
    * @Param: [user]
    * @Return: java.util.List<com.ttms.Entity.ProGroup>
    * @Author: 吴彬
    * @Date: 15:57 15:57
     */
    @Override
    public List<ProGroup> queryGroupByCuruser(SysUser user) {
        Example example=new Example(ProGroup.class);
        example.createCriteria().andEqualTo("chargeuserid", user.getId());
        List<ProGroup> proGroups = this.groupMapper.selectByExample(example);
        return proGroups;
    }
    /**
    * 功能描述: <br>
    * 〈〉生成产品编号
    * @Param: []
    * @Return: java.lang.String
    * @Author: 吴彬
    * @Date: 17:44 17:44
     */
    private String getProductName(){
        String dataString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(52);
            String msg = dataString.substring(index, index + 1);
            buffer.append(msg);
        }
        return buffer.toString().toUpperCase();
    }

    private String GetTime(){
        SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        return format;
    }
    /**
    * 功能描述: <br>
    * 〈〉
    * @Param: [len]
    * @Return: java.lang.String
    * @Author: 吴彬
    * @Date: 17:45 17:45
     */
    private  String generateCode(int len){
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = new Random().nextInt(Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0,len);
    }

    /**
    * 功能描述: <br>
    * 〈〉编号命名前缀
    * @Param: [chinese]
    * @Return: java.lang.String
    * @Author: 吴彬
    * @Date: 15:47 15:47
     */
    public  String getPrefix(String chinese) {
        if(cn2FirstSpell(chinese).equals("GNY")) {
            return "TPCN-CHN";
        }
        return "TLCN";
    }

    /**
    * 功能描述: <br>
    * 〈〉中文转换器
    * @Param: [chinese]
    * @Return: java.lang.String
    * @Author: 吴彬
    * @Date: 15:47 15:47
     */
    public  String cn2FirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (_t != null) {
                        pybf.append(_t[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim().toUpperCase();
    }
    /**
    * 功能描述: <br>
    * 〈〉生成产品编号
    * @Param: []
    * @Return: java.lang.String
    * @Author: 吴彬
    * @Date: 17:50 17:50
     * @param productCatId1
     * @param productCatId2
     * @param productCatId3
     */
    private String productName(Integer productCatId1, Integer productCatId2, Integer productCatId3){
        StringBuilder sb=new StringBuilder();
        StringBuilder sB=new StringBuilder();
        sB.append(getPrefix(productCatService.getProductCatById(productCatId1).getProductcatname())).append("-").append(cn2FirstSpell(productCatService.getProductCatById(productCatId2).getProductcatname())).append("-")
                .append( GetTime()).append("-").append(cn2FirstSpell(productCatService.getProductCatById(productCatId3).getProductcatname())).append("-")
                .append("00").append(generateCode(1));
        return sB.toString();
    }

    /**
    * 功能描述: <br>
    * 〈〉修改产品
    * @Param: [groupId, productCatId1, productCatId2, productCatId3, productName, serverStartTime, serverEndTime, preSellNumber, selledNumber, lowestNumber, onsellTime, productPrice, upsellTime, hotTip, productIntroduction, user]
    * @Return: void
    * @Author: 吴彬
    * @Date: 16:25 16:25
     */
    @Override
    @Transactional
    public void UpdateProduct(Integer pid,Integer groupId, Integer productCatId1, Integer productCatId2,
                              Integer productCatId3, String productName, Date serverStartTime,
                              Date serverEndTime, Integer preSellNumber, Integer selledNumber,
                              Integer lowestNumber, Date onsellTime, Integer productPrice,
                              Date upsellTime, String hotTip, String productIntroduction, SysUser user) {
        ProProduct proProduct=new ProProduct();
        proProduct.setId(pid);
        proProduct.setProductcatid1(productCatId1);
        proProduct.setProductcatid2(productCatId2);
        proProduct.setProductcatid3(productCatId3);
        proProduct.setProductcatnames(productName);
        proProduct.setServerstarttime(serverStartTime);
        proProduct.setServerendtime(serverEndTime);
        proProduct.setOnselltime(onsellTime);
        proProduct.setUpselltime(upsellTime);
        proProduct.setPresellnumber(preSellNumber);
        proProduct.setSellednumber(selledNumber);
        proProduct.setLowestnumber(lowestNumber);
        proProduct.setProductprice(productPrice);
        proProduct.setHottip(hotTip);
        proProduct.setProductintroduction(productIntroduction);
        proProduct.setUpdatetime(new Date());
        proProduct.setUpdateuserid(user.getId());
        int i = this.proProductMapper.updateByPrimaryKeySelective(proProduct);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.PRODUCT_EDIT_FAIL);
        }
        return;
    }

    /**
     * 功能描述: 修改项目时更新产品中的项目名
     * 〈〉
     * @Param: [id, projectname]
     * @Return: java.lang.Void
     * @Author: lhf
     * @Date: 2019/6/3 15:59
     */
    @Override
    public Void updateRedundancyWordProjectNameProduct(Integer id, String projectname) {
        proProductMapper.updateRedundancyWordProjectNameProduct(id,projectname);
        return null;
    }


}
