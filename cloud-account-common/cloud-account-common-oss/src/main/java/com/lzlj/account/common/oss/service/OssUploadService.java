package com.lzlj.account.common.oss.service;

import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.common.oss.config.OssProperties;
import com.lzlj.account.common.oss.dto.PresignedUrlRequest;
import com.lzlj.account.common.oss.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;

/**
 * OSS 上传服务 - 预签名URL生成
 * 仅在 oss.enabled=true 时加载
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "oss.enabled", havingValue = "true")
public class OssUploadService {

    private final OssProperties props;

    /**
     * 支持的图片类型白名单
     */
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    /**
     * 生成预签名上传URL
     */
    public PresignedUrlResponse generateUploadUrl(PresignedUrlRequest request, Long userId, Long tenantId) {
        // 1. 校验 content-type
        if (!ALLOWED_CONTENT_TYPES.contains(request.getContentType())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "不支持的文件类型");
        }

        // 2. 校验文件大小
        if (request.getSize() != null && request.getSize() > props.getMaxFileSize()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件大小超出限制");
        }

        // 3. 生成文件路径: {type}/{tenantId}/{userId}/{uuid}.{ext}
        String ext = getExtension(request.getFilename());
        String objectName = buildObjectName(request.getType(), tenantId, userId, ext);

        // 4. 生成预签名URL
        String endpoint = props.getEndpoint().replace("https://", "").replace("http://", "");
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(props.getAccessKeyId(), props.getAccessKeySecret());

        com.aliyun.oss.OSS ossClient = new com.aliyun.oss.OSSClientBuilder().build(
                "https://" + endpoint, credentialsProvider);

        try {
            int expireSeconds = props.getExpireSeconds() != null ? props.getExpireSeconds() : 300;
            Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000L);

            GeneratePresignedUrlRequest generateRequest = new GeneratePresignedUrlRequest(props.getBucket(), objectName);
            generateRequest.setExpiration(expiration);
            generateRequest.setContentType(request.getContentType());
            generateRequest.setMethod(com.aliyun.oss.HttpMethod.PUT);

            URL signedUrl = ossClient.generatePresignedUrl(generateRequest);

            // 5. 构建响应
            PresignedUrlResponse response = new PresignedUrlResponse();
            response.setUploadUrl(signedUrl.toString());
            response.setFileUrl(buildFileUrl(objectName));
            response.setExpireSeconds(expireSeconds);
            response.setObjectName(objectName);

            log.info("生成预签名URL成功, objectName={}, userId={}, tenantId={}", objectName, userId, tenantId);
            return response;

        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 构建对象存储路径: {type}/{tenantId}/{userId}/{uuid}.{ext}
     */
    private String buildObjectName(String type, Long tenantId, Long userId, String ext) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s/%d/%d/%s.%s",
                type != null ? type : "file",
                tenantId != null ? tenantId : 0,
                userId != null ? userId : 0,
                uuid,
                ext);
    }

    /**
     * 构建文件访问URL
     */
    private String buildFileUrl(String objectName) {
        if (props.getCdnDomain() != null && !props.getCdnDomain().isEmpty()) {
            return props.getCdnDomain().replace("/$", "") + "/" + objectName;
        }
        return "https://" + props.getBucket() + "." + props.getEndpoint() + "/" + objectName;
    }

    /**
     * 从文件名获取扩展名
     */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
