package com.ttms.service.AllowVisitor;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.ttms.Entity.ResoAttachment;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Properties.UploadProperties;
import com.ttms.Vo.DownLoad;
import com.ttms.service.ResourceManage.IAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
@EnableConfigurationProperties(UploadProperties.class)
@Slf4j
public class UploadService {
    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UploadProperties uploadProperties;

    @Autowired
    private IAttachmentService attachmentService;

    public String uploadFile(MultipartFile file) {
        try{
            //判断文件大小
            // 文件不大于100M
            boolean normalSize = checkFileSize(file.getSize(), uploadProperties.getMaxsize(), uploadProperties.getUnit());
            if(!normalSize){
                throw new TTMSException(ExceptionEnum.FILE_SIZE_TOO_LARGE);
            }

            //储存文件
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            return uploadProperties.getBaseUrl()+"/"+storePath.getFullPath();
        }catch(IOException e){
                log.error("[文件上传] 文件上传异常");
                throw new TTMSException(ExceptionEnum.FILE_UPLOAD_FAIL);
        }
    }

    //http://114.115.204.56/group1/M00/00/00/rBAA3F0FtUOAPcmoAAA8K_c90Hg367.txt
    public DownLoad downloadFile(Integer attachmentId){
        ResoAttachment attachment= attachmentService.getResoAttchmentById(attachmentId);
        DownLoad downLoad=null;
        if(attachment!=null) {
            try {
                downLoad=new DownLoad();
                String fileName = URLEncoder.encode(attachment.getFilename(), "UTF8");
                String fileUrl = attachment.getFileurl();
                String filepath = fileUrl.substring(fileUrl.lastIndexOf("group1/") + 7);
                log.info("文件名称："+filepath);
                DownloadByteArray callback = new DownloadByteArray();
                byte[] b = storageClient.downloadFile("group1", filepath, callback);
                downLoad.setFileName(fileName);
                downLoad.setFilebyte(b);
                return downLoad;
            } catch (UnsupportedEncodingException e) {
                throw new TTMSException(ExceptionEnum.FILE_DOWNLOAD_FAIL);
            }
        }
        return downLoad;
    }





    /**
     * 判断文件大小
     *
     * @param len
     *            文件长度
     * @param size
     *            限制大小
     * @param unit
     *            限制单位（B,K,M,G）
     * @return
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
//        long len = file.length();
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }
}
