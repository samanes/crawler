package com.finology.crawler.util;

import com.finology.crawler.util.UrlUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlUtilTest {
    @Test()
    public void toValidUrl_removeSharp_test() {
        String validUrl = UrlUtil.toValidUrl("http://url#test");
        assertThat(validUrl)
                .doesNotContain("#")
                .isEqualTo("http://url");
    }

    @Test()
    public void toValidUrl_removeQueryString_test() {
        String validUrl = UrlUtil.toValidUrl("http://url?test=value");
        assertThat(validUrl)
                .doesNotContain("?")
                .isEqualTo("http://url");
    }

    @Test()
    public void toValidUrl_removeQueryString2_test() {
        String validUrl = UrlUtil.toValidUrl("https://magento-test.finology.com.my/women/tops-women/hoodies-and-sweatshirts-women.html?style_general=128");
        assertThat(validUrl)
                .doesNotContain("?")
                .isEqualTo("https://magento-test.finology.com.my/women/tops-women/hoodies-and-sweatshirts-women.html");
    }
}
