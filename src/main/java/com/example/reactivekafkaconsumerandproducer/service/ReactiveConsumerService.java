package com.example.reactivekafkaconsumerandproducer.service;

import com.example.reactivekafkaconsumerandproducer.dto.FakeConsumerDTO;
import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
public class ReactiveConsumerService implements CommandLineRunner {
    Logger log = LoggerFactory.getLogger(ReactiveConsumerService.class);

    private final ReactiveKafkaConsumerTemplate<String, FakeProducerDTO> reactiveKafkaConsumerTemplate;

    public ReactiveConsumerService(ReactiveKafkaConsumerTemplate<String, FakeProducerDTO> reactiveKafkaConsumerTemplate) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
    }

    private Flux<FakeProducerDTO> consumeFakeConsumerDTO() {
        return reactiveKafkaConsumerTemplate
                .receiveAutoAck()
                // .delayElements(Duration.ofSeconds(2L)) // BACKPRESSURE
                .doOnNext(consumerRecord -> log.info("received key={}, value={} from topic={}, offset={}",
                        consumerRecord.key(),
                        consumerRecord.value(),
                        consumerRecord.topic(),
                        consumerRecord.offset())
                )
                .map(ConsumerRecord::value)
                .doOnNext(fakeConsumerDTO -> log.info("successfully consumed {}={}", FakeProducerDTO.class.getSimpleName(), fakeConsumerDTO))
                .doOnError(throwable -> log.error("something bad happened while consuming : {}", throwable.getMessage()));
    }

    @Override
    public void run(String... args) {
        // we have to trigger consumption
        consumeFakeConsumerDTO().subscribe();
    }
}
