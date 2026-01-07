package kr.dgucaps.caps.domain.report.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    INFO_ERROR("정보 오류"),
    ACCOUNT_MANAGEMENT("계정 관리"),
    SUGGESTION("건의사항"),
    USER_REPORT_AND_SECURITY_REPORT("유저 신고 및 보안 제보"),
    ETC("기타");
    private final String title;

    public String toJson() {
        return title;
    }
}
