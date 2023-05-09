package tn.esprit.spring.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import tn.esprit.spring.AppUser.AppUserService;
import tn.esprit.spring.Chat.ChatService;
import tn.esprit.spring.Chat.ChatWebSocketHandler;


@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    @Autowired
    private  ObjectMapper objectMapper;
    @Autowired
    private ChatService chatService;
    @Autowired
    private  AppUserService medicalStaffService;

    public WebSocketConfig(ObjectMapper objectMapper, AppUserService medicalStaffService) {
        this.objectMapper = objectMapper;
        this.medicalStaffService = medicalStaffService;
    }

    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Bean
    public ChatWebSocketHandler chatWebSocketHandler() {
        return new ChatWebSocketHandler(objectMapper, chatService, medicalStaffService);
    }

    @Autowired
    public WebSocketConfig(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/chat").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-app").withSockJS();
    }

    @Bean
    @Primary
    public SimpMessagingTemplate simpMessagingTemplate(MessageChannel clientOutboundChannel) {
        return new SimpMessagingTemplate(clientOutboundChannel);
    }

    @Bean
    public SimpMessagingTemplate brokerMessagingTemplate(MessageChannel brokerChannel) {
        return new SimpMessagingTemplate(brokerChannel);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
