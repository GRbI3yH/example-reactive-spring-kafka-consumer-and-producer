package com.example.reactivekafkaconsumerandproducer.config;

import com.example.reactivekafkaconsumerandproducer.dto.FakeConsumerDTO;
import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
public class ReactiveKafkaConsumerConfig {
    @Bean
    public ReceiverOptions<String, FakeProducerDTO> kafkaReceiverOptions(@Value(value = "${FAKE_CONSUMER_DTO_TOPIC}") String topic, KafkaProperties kafkaProperties) {
        ReceiverOptions<String, FakeProducerDTO> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, FakeProducerDTO> reactiveKafkaConsumerTemplate(ReceiverOptions<String, FakeProducerDTO> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<String, FakeProducerDTO>(kafkaReceiverOptions);
    }
}
