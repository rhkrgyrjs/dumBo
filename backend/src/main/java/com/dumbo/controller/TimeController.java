package com.dumbo.controller;  // 이 컨트롤러가 속한 패키지

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;      // GET 요청 매핑 어노테이션
import org.springframework.web.bind.annotation.RestController; // REST 컨트롤러로 등록 (응답을 JSON으로 처리)

import java.time.ZonedDateTime;       // 타임존 포함한 날짜/시간 클래스
import java.time.format.DateTimeFormatter; // 날짜 포맷 지정용
import java.time.ZoneId;              // 타임존 ID (예: Asia/Seoul)
import java.util.HashMap;             // 응답을 위한 Map 객체
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController  // @Controller + @ResponseBody 를 합친 어노테이션 (응답을 JSON 형태로 반환)
public class TimeController {

    @GetMapping("/time")  // /time 경로에 GET 요청이 오면 아래 메서드를 실행
    public Map<String, String> getCurrentTime() {
        ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));  
        // 현재 시각을 KST 기준으로 가져옴

        String formatted = nowKST.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));  
        // 지정된 포맷으로 날짜/시간 문자열 생성

        Map<String, String> response = new HashMap<>();  
        response.put("time_kst", formatted);  
        // 응답 JSON의 키와 값 지정: { "time_kst": "..." }

        return response;  
        // 이 Map은 Jackson 라이브러리에 의해 자동으로 JSON으로 변환되어 응답됨
    }
}
