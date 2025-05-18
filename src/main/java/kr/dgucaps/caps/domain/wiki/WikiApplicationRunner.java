package kr.dgucaps.caps.domain.wiki;


import kr.dgucaps.caps.domain.wiki.service.WikiService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 배포 후 삭제
@Configuration
public class WikiApplicationRunner {

    @Bean
    public ApplicationRunner updateDataOnStartup(WikiService wikiService) {
        return args -> {
            wikiService.updateExistingDataJamo();
            System.out.println("기존 데이터 자모 업데이트 완료");
        };
    }
}
