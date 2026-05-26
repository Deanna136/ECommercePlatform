package com.example.ecommerceplatform.common.utils;

import com.qcloud.cos.model.CannedAccessControlList;
import com.example.ecommerceplatform.common.Exception.CosException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.properties.TencentCosProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
public class TencentCosUtil {
    @Autowired
    private TencentCosProperties cosProperties;

    // 1.创建cos客户端
    private COSClient getCosClient() {
        System.out.println(cosProperties.getSecretId() + "," + cosProperties.getSecretKey());
        COSCredentials cosCredentials = new BasicCOSCredentials(
                cosProperties.getSecretId(),
                cosProperties.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(cosProperties.getRegion()));
        return new COSClient(cosCredentials, clientConfig);
    }

    // 2.上传图片
    public String upload(MultipartFile file) {
        COSClient cosClient = getCosClient();
        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            String key = "upload/" + fileName;

            PutObjectRequest request = new PutObjectRequest(
                    cosProperties.getBucketName(),
                    key,
                    file.getInputStream(),
                    null);
            request.setCannedAcl(CannedAccessControlList.PublicRead);
            cosClient.putObject(request);

            // 直接使用你配置的 baseUrl 拼接
            return cosProperties.getBaseUrl() + key;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new CosException(ErrorCode.FILE_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("腾讯云COS上传异常", e);
            throw new CosException(ErrorCode.COS_ERROR);
        } finally {
            cosClient.shutdown();
        }
    }

    // 3.下载图片
    public InputStream getFileInputStream(String fileUrl) {
        COSClient cosClient = getCosClient();
        try {
            String key = fileUrl.replace(cosProperties.getBaseUrl(), "");
            COSObject cosObject = cosClient.getObject(new GetObjectRequest(cosProperties.getBucketName(), key));
            return cosObject.getObjectContent();
        } catch (Exception e) {
            log.error("腾讯云COS下载异常, fileUrl={}", fileUrl, e);
            throw new CosException(ErrorCode.FILE_DOWNLOAD_FAILED);
        }
    }

    // 4.删除图片
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new CosException(ErrorCode.URL_NULL);
        }

        COSClient cosClient = getCosClient();
        try {
            String key = fileUrl.replace(cosProperties.getBaseUrl(), "");
            cosClient.deleteObject(cosProperties.getBucketName(), key);
        } catch (Exception e) {
            log.error("腾讯云COS删除异常, fileUrl={}", fileUrl, e);
            throw new CosException(ErrorCode.FILE_DELETE_FAILED);
        } finally {
            cosClient.shutdown();
        }
    }
}
