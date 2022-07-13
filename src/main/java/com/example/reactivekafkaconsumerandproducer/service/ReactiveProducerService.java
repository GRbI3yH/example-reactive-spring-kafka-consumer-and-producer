package com.example.reactivekafkaconsumerandproducer.service;

import com.example.reactivekafkaconsumerandproducer.dto.MessageFromKafka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReactiveProducerService {

    private final Logger log = LoggerFactory.getLogger(ReactiveProducerService.class);
    private final ReactiveKafkaProducerTemplate<String, MessageFromKafka> reactiveKafkaProducerTemplate;

    @Value(value = "${FAKE_PRODUCER_DTO_TOPIC}")
    private String topic;

    public ReactiveProducerService(ReactiveKafkaProducerTemplate<String, MessageFromKafka> reactiveKafkaProducerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    public void send(MessageFromKafka messageFromKafka) {
        log.info("send to topic={}, {}={},", topic, MessageFromKafka.class.getSimpleName(), messageFromKafka);
        reactiveKafkaProducerTemplate.send(topic, messageFromKafka)
                .doOnSuccess(senderResult -> log.info("sent {} offset : {}", messageFromKafka, senderResult.recordMetadata().offset()))
                .doOnError(throwable -> log.info("sent {} offset : {}", messageFromKafka, throwable.getMessage()))
                .subscribe(voidSenderResult -> log.info(voidSenderResult.recordMetadata().toString()));
    }
}
