package com.example.miniLinkedin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker

public class WebSocketConfig implements  WebSocketMessageBrokerConfigurer {
	
	 @Override
	    public void configureMessageBroker(MessageBrokerRegistry config) {
		 
	        // Préfixe des topics auxquels les clients s'abonnent
	        config.enableSimpleBroker("/topic", "/queue");
	        // Préfixe des messages envoyés par les clients vers le serveur
	        config.setApplicationDestinationPrefixes("/app");
	        // Préfixe pour les messages ciblés vers un utilisateur spécifique
	        config.setUserDestinationPrefix("/user");
	    }

	    @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	    	
	        // Endpoint de connexion WebSocket
	        registry.addEndpoint("/ws")
	                .setAllowedOriginPatterns("*")
	                // SockJS = fallback si WebSocket non supporté par le navigateur
	                .withSockJS();
	    }
	

}
