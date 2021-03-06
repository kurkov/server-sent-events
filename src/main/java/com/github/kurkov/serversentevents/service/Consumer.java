package com.github.kurkov.serversentevents.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalTime;

@Service
public class Consumer {

    private Logger logger = LoggerFactory.getLogger(Consumer.class);

    public void consumeServerSentEvent() {
        WebClient client = WebClient.create("http://localhost:8080");
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<ServerSentEvent<String>>() {
        };

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/stream-sse")
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(
                content -> logger.info("Time: {} - event: name[{}], id [{}], content[{}] ",
                        LocalTime.now(), content.event(), content.id(), content.data()),
                error -> logger.error("Error receiving SSE: {}", error),
                () -> logger.info("Completed!!!"));
    }
}
