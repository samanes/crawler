package com.finology.crawler;

import com.finology.crawler.service.FinologyCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CrawlerApplication implements CommandLineRunner {

	@Autowired
	private FinologyCrawler magnetoFinologyCrawler;

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
	}

	@Override
	public void run(String... args) {
		long startTime = System.currentTimeMillis();
		magnetoFinologyCrawler.start();
		long endTime = System.currentTimeMillis();
		long processTime = endTime - startTime;

		log.info(String.format("process time: %d.%d seconds", processTime / 1000, processTime % 1000));
	}
}
