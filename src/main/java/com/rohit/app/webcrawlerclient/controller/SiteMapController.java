package com.rohit.app.webcrawlerclient.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Log4j2
@RestController
public class SiteMapController {

    @Value("${app.crawlerServerUrl}")
    private String crawlerServerUrl ;

    private final static String RESP = "Sitemap Requested Succesfully. Please check Command Line for Sitemap";

    @GetMapping("/sitemap")
    public String getsiteMap(@RequestParam String url) {
        log.info("Getting Sitemap for URL {}", url);
        RestTemplate restTemplate = new RestTemplate();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            Date d1 = new Date();
                restTemplate.getForObject(crawlerServerUrl + "?url="+url, String.class);
            Date d2 = new Date();
            float seconds = ((d2.getTime()-d1.getTime())/1000);
            System.out.println("Seconds: " + (seconds/60));
        });
        return RESP;
    }


}