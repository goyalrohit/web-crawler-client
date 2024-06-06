package com.rohit.app.webcrawlerclient.config;

import lombok.extern.log4j.Log4j2;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.util.ErrorHandler;
import jakarta.jms.Session;

@Log4j2
@Configuration
@EnableJms
public class ActiveMqConnectionFactoryConfig {

    @Value("${activemq.user}")
    private String userName;

    @Value("${activemq.password}")
    private String password;

    @Value("${activemq.broker-url}")
    private String brokerUrl;

    @Value("${activemq.concurrent.connections}")
    private String numOfInstances;


    @Bean
    @Primary
    public ActiveMQConnectionFactory receiverActiveMQConnectionFactory() {
        ActiveMQSslConnectionFactory activeMQConnectionFactory = new ActiveMQSslConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        activeMQConnectionFactory.setUserName(userName);
        activeMQConnectionFactory.setPassword(password);
        return activeMQConnectionFactory;
    }

    @Bean(name = "crawlerContainer")
    public DefaultJmsListenerContainerFactory jmsListenerBireContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(receiverActiveMQConnectionFactory());
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency(numOfInstances);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        factory.setPubSubDomain(Boolean.FALSE);
        factory.setRecoveryInterval(2000L);
        factory.setErrorHandler(getErrorHandler());
        return factory;
    }

    private ErrorHandler getErrorHandler() {
        return new ErrorHandler() {
            @Override
            public void handleError(Throwable throwable) {
                log.error(
                        "Threw a exception in ActiveMqConnectionFactoryConfig ::getErrorHandler, exception cause follows:",
                        throwable.getCause().getMessage());
                throwable.printStackTrace();
            }
        };
    }

}
