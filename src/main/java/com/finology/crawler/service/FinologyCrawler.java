package com.finology.crawler.service;

import com.finology.crawler.model.Product;
import com.finology.crawler.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
public class FinologyCrawler extends WebCrawler {
    private ProductRepository productRepository;

    public FinologyCrawler(
            @Value("${app.start-url}") String startUrl,
            @Value("${app.threads}") Integer numberOfThreads,
            ProductRepository productRepository) {
        super(startUrl, numberOfThreads);
        this.productRepository = productRepository;
    }

    @Override
    protected void visit(String url, Document document) {
        Element productMainInfo = document.select("div.product-info-main").first();
        if (productMainInfo != null) {
            Product product = getProduct(document, productMainInfo);
            log.info(url);
            log.info(product.toString());
            productRepository.save(product);
        }
    }

    private Product getProduct(Document document, Element productMainInfo) {
        Product product = new Product();
        setProductMainInfo(product, productMainInfo);
        setProductEtraInfo(product, document);
        return product;
    }

    @Override
    protected boolean shouldVisit(String url) {
        return url.endsWith(".html");
    }

    private void setProductMainInfo(Product product, Element productMainInfo) {
        Element productTitle = productMainInfo.select("div.page-title-wrapper").first();
        if (productTitle != null) {
            product.setTitle(productMainInfo.select("span.base").text());
        }
        Element productPrice = productMainInfo.select("div.product-info-price").first();
        if (productPrice != null) {
            product.setPrice(productPrice.select("span.price").text());
        }
    }

    private void setProductEtraInfo(Product product, Document document) {
        Element productExtraInfo = document.select("div[class = product data items]").first();
        if (productExtraInfo != null) {
            product.setDescription(productExtraInfo.getElementById("description").text());
            Element additionalElement = productExtraInfo.getElementById("additional");
            if (additionalElement != null) {
                Elements elementRow = additionalElement.select("tr");
                product.setExtraInfo(elementRow.stream()
                        .map(row -> row.select("th").text() + ": " + row.select("td").text())
                        .collect(Collectors.joining(" | ")));
            }
        }
    }
}
