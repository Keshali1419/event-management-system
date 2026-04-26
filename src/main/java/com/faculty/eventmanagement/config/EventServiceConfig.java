package com.faculty.eventmanagement.config;

import com.faculty.eventmanagement.decorator.IEventService;
import com.faculty.eventmanagement.decorator.LoggingEventServiceDecorator;
import com.faculty.eventmanagement.decorator.ValidationEventServiceDecorator;
import com.faculty.eventmanagement.services.EventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EventServiceConfig {

    @Bean
    @Primary
    public IEventService decoratedEventService(EventService eventService) {
        return new LoggingEventServiceDecorator(
                new ValidationEventServiceDecorator(eventService));
    }
}