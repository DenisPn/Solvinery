package config;

import groupId.DTO.Records.Events.SolveRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServer;

    /**
     * Configures and provides a Kafka {@link ProducerFactory} for producing messages.
     *
     * @return a {@link ProducerFactory} configured for producing messages with a key of type
     *         {@link String} and value of type {@link SolveRequest}.
     */
    @Bean
    public ProducerFactory<String, SolveRequest> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Provides a KafkaTemplate bean configured for sending messages with a key of type {@link String}
     * and a value of type {@link SolveRequest}.
     *
     * @return a {@link KafkaTemplate} instance configured to send messages to Kafka using the
     *         {@link ProducerFactory} returned by {@code producerFactory}.
     */
    @Bean
    public KafkaTemplate<String, SolveRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Configures and provides a Kafka {@link ConsumerFactory} for consuming messages.
     *
     * @return a {@link ConsumerFactory} configured for consuming messages from Kafka.
     */
    @Bean
    public ConsumerFactory<String, SolveRequest> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "problem-solving-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    /**
     * Configures and provides a Kafka {@link ConcurrentKafkaListenerContainerFactory} bean,
     * enabling the creation of listener containers for consuming messages from Kafka topics.
     *
     * @return a {@link ConcurrentKafkaListenerContainerFactory} configured for processing Kafka messages.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SolveRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SolveRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
