package com.finology.crawler.util;

import org.apache.commons.lang3.StringUtils;

public class UrlUtil {
    public static String toValidUrl(String url) {
        if(url.endsWith("/")) {
            url = StringUtils.substringBeforeLast(url, "/");
        }
        return StringUtils.substringBefore(
                StringUtils.substringBefore(url, "#"),
                "?");
    }
}
