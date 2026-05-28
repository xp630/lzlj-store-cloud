package com.lzlj.account.gateway.log;

/**
 * API访问日志数据
 */
public class ApiAccessLog {
    private Long apiKeyId;
    private String apiKey;
    private Long tenantId;
    private String method;
    private String path;
    private String requestBody;
    private String responseBody;
    private Integer statusCode;
    private Long duration;
    private String ip;
    private String userAgent;
    private String errorMsg;
    private Long timestamp;

    public ApiAccessLog() {
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public Long getApiKeyId() { return apiKeyId; }
    public void setApiKeyId(Long apiKeyId) { this.apiKeyId = apiKeyId; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }
    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "ApiAccessLog{" +
                "apiKeyId=" + apiKeyId +
                ", apiKey='" + apiKey + '\'' +
                ", tenantId=" + tenantId +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", statusCode=" + statusCode +
                ", duration=" + duration +
                ", ip='" + ip + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
