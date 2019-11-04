package com.changgou.file.controller;

import com.changgou.file.pojo.FastDFSFile;
import com.changgou.file.util.FastdfsClientUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.file.controller *
 * @since 1.0
 */
@RestController
@CrossOrigin
//注解用于支持跨域.
public class FileController {


    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            //1.获取字节数组
            byte[] bytes = file.getBytes();
            //2.调用fastdfs的api 存储到fastdfs上
            FastDFSFile fastdfsfile = new FastDFSFile(
                    file.getOriginalFilename(),//原始的文件名  1231232131.jpg
                    bytes,//字节数组 文件本身
                    StringUtils.getFilenameExtension(file.getOriginalFilename())// jpg
            );
            String[] upload = FastdfsClientUtil.upload(fastdfsfile);//
            //upload[0]= group1
            //upload[1]= M00/00/00/wKjThF1-ASaAGXNyAANdC6JX9KA351.jpg
            //3.获取到文件的URL
            //http://192.168.211.132:8080/group1/M00/00/00/wKjThF1-ASaAGXNyAANdC6JX9KA351.jpg

            String realPath = FastdfsClientUtil.getTrackerUrl()+"/" + upload[0] + "/" + upload[1];
            //4.返回文件的URL
            return realPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "默认的路径";
        }
    }

}
