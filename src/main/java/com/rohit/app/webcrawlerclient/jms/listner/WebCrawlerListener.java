package com.rohit.app.webcrawlerclient.jms.listner;

import com.rohit.app.webcrawlerclient.service.SitemapGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class WebCrawlerListener {

	@Autowired
	private SitemapGenerator sitemapGenerator;

	@JmsListener(destination = "${activemq.destination.subscriber.crawler}", subscription = "${activemq.destination.subscriber.crawler}", containerFactory = "crawlerContainer")
	public void receive(String message) {
		log.debug("Received response from Server ='{}'", message);
		try {
			sitemapGenerator.createSiteMap(message);
		} catch (Exception e) {
			log.error("Error in WebCrawlerListener :: receive");
			throw e;
		}
		
	}

}
