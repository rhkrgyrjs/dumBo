package com.dumbo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ImageServiceTemp {

    private final WebClient webClient;

    public ImageServiceTemp(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:5000").build();
    }

    public void deleteImages(List<String> imageIds) {
        webClient.post()  // POST로 변경 (DELETE는 바디 지원이 제한적이어서 POST로 처리하는 게 일반적)
            .uri("/delete-images")
            .bodyValue(new DeleteImagesRequest(imageIds))
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    // 내부 DTO 클래스 (JSON 바디로 보낼 객체)
    private static class DeleteImagesRequest {
        private List<String> filenames;

        public DeleteImagesRequest(List<String> filenames) {
            this.filenames = filenames;
        }

        public List<String> getFilenames() {
            return filenames;
        }

        public void setFilenames(List<String> filenames) {
            this.filenames = filenames;
        }
    }
}
