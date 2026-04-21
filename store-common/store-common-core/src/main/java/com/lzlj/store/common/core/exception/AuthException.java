package com.lzlj.store.common.core.exception;

import com.lzlj.store.common.core.result.ResultCode;
import lombok.Getter;

/**
 * 认证异常
 */
@Getter
public class AuthException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer code;
    private final String message;

    public AuthException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public AuthException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
