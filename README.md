![Java CI](https://github.com/Kevded/example-reactive-spring-kafka-consumer-and-producer/workflows/Java%20CI/badge.svg)

# Reactive Kafka consumer and producer Spring Boot

Sample project to show how to implement Reactive kafka consumer and producer in Spring Boot. With Spring Kafka.

Spring Boot version 2.3.9
## Config
- [ReactiveKafkaConsumerConfig](src/main/java/com/example/reactivekafkaconsumerandproducer/config/ReactiveKafkaConsumerConfig.java)
- [ReactiveKafkaProducerConfig](src/main/java/com/example/reactivekafkaconsumerandproducer/config/ReactiveKafkaProducerConfig.java)
- [application.properties](src/main/resources/application.properties)
- [pom.xml](pom.xml)

```java
package com.example.reactivekafkaconsumerandproducer.config;

import com.example.reactivekafkaconsumerandproducer.dto.FakeConsumerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
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
```

```java
package com.example.reactivekafkaconsumerandproducer.config;

import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
public class ReactiveKafkaProducerConfig {
    @Bean
    public ReactiveKafkaProducerTemplate<String, FakeProducerDTO> reactiveKafkaProducerTemplate(
            KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<String, FakeProducerDTO>(SenderOptions.create(props));
    }
}
```

```properties
# your ip for kafka server
spring.kafka.bootstrap-servers=10.8.0.1:9092
# producer
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
# consumer
spring.kafka.consumer.group-id=reactivekafkaconsumerandproducer

spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=*
# json deserializer config
spring.kafka.properties.spring.json.trusted.packages=*
spring.kafka.consumer.properties.spring.json.use.type.headers=false
spring.kafka.consumer.properties.spring.json.value.default.type=com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO

# topic
FAKE_PRODUCER_DTO_TOPIC=fake_consumer_dto_topic
FAKE_CONSUMER_DTO_TOPIC=fake_consumer_dto_topic
```

```xml
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>io.projectreactor.kafka</groupId>
            <artifactId>reactor-kafka</artifactId>
            <version>${reactor.kafka.version}</version>
        </dependency>
```

## Service
- [ReactiveConsumerService](src/main/java/com/example/reactivekafkaconsumerandproducer/service/ReactiveConsumerService.java)
- [ReactiveProducerService](src/main/java/com/example/reactivekafkaconsumerandproducer/service/ReactiveProducerService.java)

```java

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
```

```java
@Service
public class ReactiveProducerService {

    private final Logger log = LoggerFactory.getLogger(ReactiveProducerService.class);
    private final ReactiveKafkaProducerTemplate<String, FakeProducerDTO> reactiveKafkaProducerTemplate;

    @Value(value = "${FAKE_PRODUCER_DTO_TOPIC}")
    private String topic;

    public ReactiveProducerService(ReactiveKafkaProducerTemplate<String, FakeProducerDTO> reactiveKafkaProducerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    public void send(FakeProducerDTO fakeProducerDTO) {
        log.info("send to topic={}, {}={},", topic, FakeProducerDTO.class.getSimpleName(), fakeProducerDTO);
        reactiveKafkaProducerTemplate.send(topic, fakeProducerDTO)
                .doOnSuccess(senderResult -> log.info("sent {} offset : {}", fakeProducerDTO, senderResult.recordMetadata().offset()))
                .doOnError(throwable -> log.info("sent {} offset : {}", fakeProducerDTO, throwable.getMessage()))
                .subscribe(voidSenderResult -> log.info(voidSenderResult.recordMetadata().toString()));
    }
}
```

# Articles

- [gitbook.deddy.me/reactive-kafka-consumer-producer-spring-boot](https://gitbook.deddy.me/reactive-kafka-consumer-producer-spring-boot)
