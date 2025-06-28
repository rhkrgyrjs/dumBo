package com.dumbo.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class HtmlSanitizer 
{
    private final String ALLOWED_IMAGE_DOMAIN = "http://localhost:5000/"; // 이건 properties 파일로 빼야 할 듯

    // 안전한 CSS 속성만 필터링해서 남기는 함수
    private String filterSafeStyle(String style) 
    {
        StringBuilder safe = new StringBuilder();
        String[] declarations = style.split(";");
        for (String decl : declarations) {
            String[] parts = decl.split(":", 2);
            if (parts.length != 2) continue;

            String property = parts[0].trim().toLowerCase();
            String value = parts[1].trim().toLowerCase();

            // 금지 키워드 검사
            if (value.contains("expression") || value.contains("javascript:") || value.contains("url(")) {
                continue;
            }

            // 허용할 CSS 속성 추가 (font-family, font-size 포함)
            if (property.matches("^(color|background-color|text-align|font-weight|font-style|text-decoration|font-family|font-size)$")) {
                safe.append(property).append(":").append(value).append(";");
            }
        }
        return safe.toString();
    }

    // HTML sanitize 하는 메소드
    // 가장 기본적인 보안 위협에 대한 처리만 함. 너무 빡빡하게 하지는 말자..
    public String sanitizeHtml(String rawHtml) 
    {
        // 기본적으로 안전한 태그를 허용하는 relaxed 기반 화이트리스트
        Safelist safelist = Safelist.relaxed()
            .addTags("ins", "del", "u") // 추가 태그 허용
            .addAttributes("span", "style")
            .addAttributes("p", "style")
            .addAttributes("div", "style");

        // style 속성을 허용하지만, 내용을 필터링해야 하므로 먼저 clean
        String cleanHtml = Jsoup.clean(rawHtml, "", safelist, new Document.OutputSettings().prettyPrint(false));

        // 다시 DOM 파싱
        Document doc = Jsoup.parse(cleanHtml);

        // 1. 이미지 src 검사 (whitelist 기반)
        for (Iterator<Element> it = doc.select("img").iterator(); it.hasNext(); ) {
            Element img = it.next();
            String src = img.attr("src");
            if (!src.startsWith(ALLOWED_IMAGE_DOMAIN)) {
                img.remove(); // 출처 불명 이미지는 제거
            }
        }

        // 2. style 속성 필터링 (XSS 방지용)
        for (Element el : doc.select("[style]")) {
            String originalStyle = el.attr("style");
            String safeStyle = filterSafeStyle(originalStyle);
            if (safeStyle.isEmpty()) {
                el.removeAttr("style");
            } else {
                el.attr("style", safeStyle);
            }
        }

        return doc.body().html();
    }



    // HTML에서 텍스트만 뽑아내는 메소드
    public String extractText(String htmlContent) 
    {
        if (htmlContent == null || htmlContent.isEmpty()) return "";
        return Jsoup.parse(htmlContent).text();
    }

    // 썸네일 URL 뽑는 메소드
    public String extractThumbnailImageUrl(String htmlContent) 
    {
        if (htmlContent == null || htmlContent.isEmpty()) return null;

        Document doc = Jsoup.parse(htmlContent);
        for (Element img : doc.select("img")) 
        {
            String src = img.attr("src");
            if (src.startsWith(ALLOWED_IMAGE_DOMAIN)) return src;
        }
        return null;
    }


    public List<String> extractImageFileNames(String sanitizedHtml) 
    {
        List<String> imageNames = new ArrayList<>();
        if (sanitizedHtml == null || sanitizedHtml.isEmpty()) return imageNames;

        Document doc = Jsoup.parse(sanitizedHtml);
        for (Element img : doc.select("img")) {
            String src = img.attr("src");
            if (src.startsWith(ALLOWED_IMAGE_DOMAIN)) {
                // URL에서 마지막 '/' 이후의 문자열이 파일 이름
                String fileName = src.substring(src.lastIndexOf('/') + 1);
                if (!fileName.isEmpty()) {
                    imageNames.add(fileName);
                }
            }
        }

        return imageNames;
    }



}
