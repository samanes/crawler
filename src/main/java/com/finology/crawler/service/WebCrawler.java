package com.finology.crawler.service;

import com.finology.crawler.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class WebCrawler {
    Set<String> visitedUrls;
    private String startUrl;
    int numberOfThreads;
    ThreadPoolExecutor executor;

    public WebCrawler(String startUrl, int numberOfThreads) {
        visitedUrls = new HashSet<>();
        this.startUrl = startUrl;
        this.numberOfThreads = numberOfThreads;
        executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
    }

    public void start() {
        try {
            crawlUrls(Arrays.asList(startUrl));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void crawlUrls(List<String> allUrls) {
        List<String> urlsToVisit = new ArrayList<>(allUrls);
        while (urlsToVisit.size() > 0) {
            urlsToVisit = new ArrayList<>(visitAndGetUrls(urlsToVisit));
        }
        executor.shutdown();
    }

    private List<String> visitAndGetUrls(List<String> urls) {
        List<String> urlsToVisit = new ArrayList<>();
        for (Future<List<String>> future : visitAndGetUrlsThread(urls)) {
            try {
                urlsToVisit.addAll(future.get());
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        urlsToVisit.removeAll(visitedUrls);
        return urlsToVisit.stream().distinct().collect(Collectors.toList());
    }

    private List<Future<List<String>>> visitAndGetUrlsThread(List<String> urls) {
        List<Future<List<String>>> futureList = new ArrayList<>();
        for (String url : urls) {
            futureList.add(executor.submit(() -> visitAndGetLinks(url)));
        }
        return futureList;
    }

    private List<String> visitAndGetLinks(String url) {
        List<String> links = new ArrayList<>();
        try {
            Document document = parseUrl(url);
            synchronized (visitedUrls) {
                visit(url, document);
                visitedUrls.add(url);
            }
            links.addAll(getValidAbsUrls(document).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return links;
        }
    }

    public Document parseUrl(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    private Stream<String> getValidAbsUrls(Document document) {
        return document.select("a[href]")
                .stream()
                .map(element -> element.absUrl("href"))
                .map(UrlUtil::toValidUrl)
                .filter(a -> !visitedUrls.contains(a) && shouldVisit(a))
                .distinct();
    }

    protected abstract void visit(String url, Document document);

    protected abstract boolean shouldVisit(String url);
}
