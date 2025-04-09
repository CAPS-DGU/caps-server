package kr.dgucaps.caps.global.error.exception;

import kr.dgucaps.caps.global.error.ErrorCode;

public class IllegalArgumentException extends BusinessException {
    public IllegalArgumentException() {
        super(ErrorCode.BAD_REQUEST);
    }
    public IllegalArgumentException(ErrorCode errorCode) {super(errorCode);}
}
