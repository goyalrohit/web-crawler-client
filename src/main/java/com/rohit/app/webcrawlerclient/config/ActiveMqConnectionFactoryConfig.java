package com.rohit.app.webcrawlerclient.config;

import lombok.extern.log4j.Log4j2;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.util.ErrorHandler;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Session;

@Log4j2
@Configuration
@EnableJms
public class ActiveMqConnectionFactoryConfig {

	@Value("${activemq.user}")
	private String userName;

	@Value("${activemq.password}")
	private String password;

	@Value("${activemq.params.trustStorePassword}")
	private String trustStorePassword;

	@Value("${activemq.params.keyStorePassword}")
	private String keyStorePassword;

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

		try {
			//activeMQConnectionFactory.setTrustStore(trustStorePath);
			activeMQConnectionFactory.setTrustStorePassword(trustStorePassword);
			//activeMQConnectionFactory.setKeyStore(keyStorePath);
			activeMQConnectionFactory.setKeyStorePassword(keyStorePassword);

			((ActiveMQConnectionFactory) activeMQConnectionFactory).setUseAsyncSend(true);		

		} catch (Exception e) {
			log.error(
					"JMS Connection Failed (Trust store or key store weren't found) in RMSE Manager- activeMqConnectionFactoryConfig ::receiverActiveMQConnectionFactory, message :",
					e.getMessage());
		}
		return activeMQConnectionFactory;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(receiverActiveMQConnectionFactory());
		factory.setPubSubDomain(Boolean.FALSE);
		factory.setMessageConverter(jacksonJmsMessageConverter());		
		factory.setErrorHandler(getErrorHandler());
		return factory;
	}

	/*
	 * Used for Receiving Message
	 */
	@Bean
	public JmsListenerContainerFactory<?> jsaFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setMessageConverter(jacksonJmsMessageConverter());
		configurer.configure(factory, receiverActiveMQConnectionFactory());
		return factory;
	}


	@Bean(name = "crawlerContainer")
	public DefaultJmsListenerContainerFactory jmsListenerBireContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(receiverActiveMQConnectionFactory());
		factory.setDestinationResolver(new DynamicDestinationResolver());
		factory.setConcurrency(numOfInstances);
		//factory.setClientId(bireClientId);
		//factory.setSubscriptionDurable(true);
		// factory.setSubscriptionShared(true);
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		// factory.setMessageConverter(jacksonJmsMessageConverter());
		factory.setPubSubDomain(Boolean.FALSE);
		//FixedBackOff fixedbackOff = new FixedBackOff(); // or ExponentialBackOff
		//fixedbackOff.setMaxAttempts(maxAttempts);
		//fixedbackOff.setInterval(5000);
		//factory.setBackOff(fixedbackOff);
		factory.setRecoveryInterval(2000L);
		factory.setErrorHandler(getErrorHandler());
		return factory;
	}



	@Bean // Serialize message content to json using TextMessage
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	@Bean
	public CachingConnectionFactory cachingConnectionFactory() {
		return new CachingConnectionFactory(receiverActiveMQConnectionFactory());
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(cachingConnectionFactory());
		jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
		jmsTemplate.setPubSubDomain(Boolean.FALSE);
		jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
		jmsTemplate.setDeliveryPersistent(true);
		return jmsTemplate;
	}

	private ErrorHandler getErrorHandler() {
		return new ErrorHandler() {
			@Override
			public void handleError(Throwable throwable) {
				log.error(
						"Threw a exception in ActiveMqConnectionFactoryConfig ::getErrorHandler, full exception cause follows:",
						throwable.getCause().getMessage());
				throwable.printStackTrace();
			}
		};
	}
	
}
