package com.ttms.service.ResourceManage;

import com.ttms.Entity.ResoAttachment;
import com.ttms.Vo.ResoAttachmentVo;

import java.util.List;

public interface IAttachmentService {
    List<ResoAttachment> getAttachmentsByPid(int pid);

    Void addAttachment(Integer pid, String fileName, String fileUrl, String attachementName , Integer userId);

    ResoAttachment getResoAttachmentByproductId(Integer proudctId);

    List<ResoAttachmentVo> getResoAttachmentByproductIdAndUerName(Integer proudctId);

    ResoAttachment getResoAttchmentById(Integer id);

    Void deleteAttachmentsByid(Integer pid);
}
