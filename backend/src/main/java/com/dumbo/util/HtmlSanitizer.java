package com.dumbo.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class HtmlSanitizer 
{
    private final String ALLOWED_IMAGE_DOMAIN = "http://localhost:5000/"; // 이건 properties 파일로 빼야 할 듯

    // HTML sanitize 하는 메소드
    // 가장 기본적인 보안 위협에 대한 처리만 함. 너무 빡빡하게 하지는 말자..
    public String sanitizeHtml(String rawHtml)
    {
        Safelist safelist = Safelist.relaxed(); // 기본적인 보안 위협 제거 (relaxed)
        safelist.addAttributes("img", "src");
        String cleanHtml = Jsoup.clean(rawHtml, safelist);
        Document doc = Jsoup.parse(cleanHtml);
        for (Iterator<Element> it = doc.select("img").iterator(); it.hasNext();)
        {
            Element img = it.next();
            String src = img.attr("src");
            if (!src.startsWith(ALLOWED_IMAGE_DOMAIN)) img.remove();
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
}
