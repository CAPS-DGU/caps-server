package kr.dgucaps.caps.global.error.exception;

import kr.dgucaps.caps.global.error.ErrorCode;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}