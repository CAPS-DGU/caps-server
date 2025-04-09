package kr.dgucaps.caps.global.error.exception;

import kr.dgucaps.caps.global.error.ErrorCode;

public class InvalidValueException extends BusinessException {
    public InvalidValueException() {
        super(ErrorCode.BAD_REQUEST);
    }
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}