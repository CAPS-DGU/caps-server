package kr.dgucaps.caps.global.error.exception;

import kr.dgucaps.caps.global.error.ErrorCode;

public class InternalServerException extends BusinessException {
    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}