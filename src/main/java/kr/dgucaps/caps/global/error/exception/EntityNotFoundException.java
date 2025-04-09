package kr.dgucaps.caps.global.error.exception;

import kr.dgucaps.caps.global.error.ErrorCode;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);
    }
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}