package com.ttms.service.ResourceManage.ServiceImpl;

import com.ttms.Entity.ResoAttachment;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.ResoAttachMentMapper;
import com.ttms.Vo.ResoAttachmentVo;
import com.ttms.service.ProductManage.IProductListService;
import com.ttms.service.ResourceManage.IAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class AttachmentService implements IAttachmentService {
    @Autowired
    private ResoAttachMentMapper resoAttachMentMapper;

    @Autowired
    private IProductListService productListService;

    public List<ResoAttachment> getAttachmentsByPid(int pid){
        ResoAttachment resoAttachment = new ResoAttachment();
        resoAttachment.setProductId(pid);
        List<ResoAttachment> resoAttachments = resoAttachMentMapper.select(resoAttachment);
        if(CollectionUtils.isEmpty(resoAttachments)){
            throw new TTMSException(ExceptionEnum.PRODUCT_ATTACHMENT_NOT_FOUND);
        }
        return resoAttachments;
    }



    public Void addAttachment(Integer pid, String fileName, String fileUrl, String attachementName , Integer userId) {
        ResoAttachment attachment = new ResoAttachment();
        attachment.setProductId(pid);
        attachment.setAttachmenttitle(attachementName);
        attachment.setFileurl(fileUrl);
        attachment.setFilename(fileName);
        attachment.setInvalid((byte) 1);
        attachment.setUploadtime(new Date());
        attachment.setUploaduserid(userId);
        int i = resoAttachMentMapper.insert(attachment);
        if (i != 1) {
            throw new AssertionError(ExceptionEnum.ATTACHMENT_INSERT_FAIL);
        }
        return null;
    }

    @Override
    public ResoAttachment getResoAttachmentByproductId(Integer proudctId) {
        ResoAttachment resoAttachment = this.resoAttachMentMapper.getResoAttachmentByproductId(proudctId);
        if(resoAttachment==null){
            return null;
            //throw new TTMSException(ExceptionEnum.ATTACHMENT_INSERT_FAIL);
        }
        return resoAttachment;
    }

    @Override
    public List<ResoAttachmentVo> getResoAttachmentByproductIdAndUerName(Integer proudctId) {
        List<ResoAttachmentVo> resoAttachmentByproductIdAndUerName = this.resoAttachMentMapper.getResoAttachmentByproductIdAndUerName(proudctId);
        return resoAttachmentByproductIdAndUerName;
    }

    /**
    * 功能描述: <br>
    * 〈〉根据主键查询附件下载
    * @Param: [id]
    * @Return: com.ttms.Entity.ResoAttachment
    * @Author: 吴彬
    * @Date: 10:59 10:59
     */
    @Override
    public ResoAttachment getResoAttchmentById(Integer id) {
        ResoAttachment resoAttachment = this.resoAttachMentMapper.selectByPrimaryKey(id);
        return resoAttachment;
    }

    @Override
    public Void deleteAttachmentsByid(Integer pid) {
        ResoAttachment attachment=new ResoAttachment();
        attachment.setId(pid);
        attachment.setInvalid((byte)0);
        int i = this.resoAttachMentMapper.updateByPrimaryKeySelective(attachment);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.PROJECT_NOT_EXIST);
        }
        return null;
    }

}
