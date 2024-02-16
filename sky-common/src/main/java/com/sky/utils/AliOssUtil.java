package com.sky.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "sky.alioss")
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 文件上传
     *
     * @param fileName
     * @param inputStream
     * @return
     */
    public String upload(String fileName, InputStream inputStream) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        String objectName = "images/" + new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + "/" + System.currentTimeMillis() +"-"+fileName;
        try {
            //解决图片预览问题（默认是下载）
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(fileName.substring(fileName.lastIndexOf("."))));

            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, inputStream,metadata);
        } catch (OSSException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);

        log.info("文件上传到:{}", stringBuilder.toString());

        return stringBuilder.toString();
    }

    private String getContentType(String filenameExtension) {
        if (filenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (filenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (filenameExtension.equalsIgnoreCase(".jpeg") ||
                filenameExtension.equalsIgnoreCase(".jpg") ||
                filenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        return "image/jpg";
    }
}
