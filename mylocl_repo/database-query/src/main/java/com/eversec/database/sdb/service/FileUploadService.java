
package com.eversec.database.sdb.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eversec.database.sdb.constant.Constant;

/**
 * 文件上传到服务器指定目录
 *
 * @author Jony
 */
@Service
public class FileUploadService {
    public String fileupload(MultipartFile pfile, String fileName)
            throws IllegalStateException, IOException {
        String path = Constant.getConfigItem("imprtFilePath") + fileName;
        File file = new File(path);
        pfile.transferTo(file);
        return path;
    }
}
