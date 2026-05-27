package com.lzlj.account.common.core.exception;

import com.lzlj.account.common.core.result.ResultCode;
import lombok.Getter;

/**
 * 权限异常
 */
@Getter
public class PermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer code;
    private final String message;

    public PermissionException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public PermissionException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
