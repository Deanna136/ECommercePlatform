package com.example.ecommerceplatform.server.controller.seller;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.common.utils.TencentCosUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/seller")
@Api(tags = "卖家COS腾讯云相关接口")
public class SellerFileController {
    @Autowired
    TencentCosUtil cosUtils;
    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @PostMapping("/file/upload")
    @ApiOperation("上传图片")
    public Result<String> upload(MultipartFile file){
        String url = cosUtils.upload(file);
        return Result.success(url);
    }

    /**
     * 获取图片
     *
     * @param fileUrl
     * @return
     */
    @GetMapping("/file/download")
    @ApiOperation("获取图片")
    public ResponseEntity<byte[]> download(@RequestParam String fileUrl) {
        try (InputStream inputStream = cosUtils.getFileInputStream(fileUrl)) {
            // Java 1.8 兼容方式读取输入流
            java.io.ByteArrayOutputStream result = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            byte[] data = result.toByteArray();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (BusinessException e) {
            // 工具类抛出的业务异常（如文件不存在、参数为空）
            log.error("下载失败: code={}, message={}", e.getCode(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // 其他未知异常
            log.error("下载异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 删除图片
     *
     * @param fileUrl
     * @return
     */
    @PostMapping("/file/delete")
    @ApiOperation("删除图片")
    public Result<Void> delete(String fileUrl){
        cosUtils.delete(fileUrl);
        return Result.success();
    }
}
