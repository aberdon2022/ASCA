package org.chatproject.ascp.config;

import jakarta.servlet.http.HttpSession;
import org.chatproject.ascp.utils.RSAKeyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                System.out.println("Processing STOMP command: " + accessor.getCommand());
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Principal principal = accessor.getUser();
                    System.out.println("WebSocket CONNECT, principal: " + (principal != null ? principal.getName() : "null"));
                    if (principal instanceof Authentication) {
                        Authentication auth = (Authentication) principal;
                        System.out.println("Authenticated user: " + auth.getName() + ", isAuthenticated: " + auth.isAuthenticated());
                        if (auth.isAuthenticated()) {
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            return message;
                        }
                    }
                    System.out.println("Authentication failed for WebSocket CONNECT");
                    throw new AuthenticationCredentialsNotFoundException("Not authenticated");
                } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    System.out.println("SUBSCRIBE to: " + accessor.getDestination() + ", user: " + (accessor.getUser() != null ? accessor.getUser().getName() : "null"));
                } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    System.out.println("WebSocket DISCONNECT received");
                }
                return message;
            }
        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config ) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/queue")
                .setTaskScheduler(taskScheduler())
                .setHeartbeatValue(new long[]{10000, 10000});
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:8080")
                .withSockJS();
    }
}
