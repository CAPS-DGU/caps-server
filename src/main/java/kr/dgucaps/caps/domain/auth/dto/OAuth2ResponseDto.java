package kr.dgucaps.caps.domain.auth.dto;

public interface OAuth2ResponseDto {

    String getProviderId();

    String getName();

    String getEmail();

    String getProfileImageUrl();
}
