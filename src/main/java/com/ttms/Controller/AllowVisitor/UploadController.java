package com.ttms.Controller.AllowVisitor;

import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Vo.DownLoad;
import com.ttms.service.AllowVisitor.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(uploadService.uploadFile(file));
    }

    //http://114.115.204.56/group1/M00/00/00/rBAA3F0FtUOAPcmoAAA8K_c90Hg367.txt
    @GetMapping(value = "/download/{id}")
    public void downloadFilesWithFastdfs(@PathVariable Integer id, HttpServletResponse httpServletResponse) throws MalformedURLException {
        //操作数据库，读取文件上传时的信息
        try{
            DownLoad file = this.uploadService.downloadFile(id);
            if(file!=null) {
                httpServletResponse.reset();
                httpServletResponse.setContentType("application/x-download");
                httpServletResponse.addHeader("Content-Disposition", "attachment;filename=\"" + file.getFileName() + "\"");
                httpServletResponse.setHeader("Access-Control-Allow-Origin", "http://114.115.204.56:9090");
                httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpServletResponse.getOutputStream().write(file.getFilebyte());
                //uploadService.updateDownloadCount(id);
                httpServletResponse.getOutputStream().close();
            }
        }catch (Exception e){
            throw new TTMSException(ExceptionEnum.FILE_DOWNLOAD_FAIL);
        }
    }
}
