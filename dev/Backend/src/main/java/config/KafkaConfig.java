package config;

import groupId.DTO.Records.Events.SolveRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServer;

    private static final String SOLVE_REQUEST_TOPIC = "solve-requests";
    private static final int NUM_PARTITIONS = 4;

    @NonNull
    @Bean
    public NewTopic solveRequestTopic() {
        return TopicBuilder.name(SOLVE_REQUEST_TOPIC)
                .partitions(NUM_PARTITIONS)
                .replicas(1)
                .build();
    }


    /**
     * Configures and provides a Kafka {@link ProducerFactory} for producing messages.
     *
     * @return a {@link ProducerFactory} configured for producing messages with a key of type
     *         {@link String} and value of type {@link SolveRequest}.
     */
    @NonNull
    @Bean
    public ProducerFactory<String, SolveRequest> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        return new DefaultKafkaProducerFactory<>(config);
    }


    /**
     * Provides a KafkaTemplate bean configured for sending messages with a key of type {@link String}
     * and a value of type {@link SolveRequest}.
     *
     * @return a {@link KafkaTemplate} instance configured to send messages to Kafka using the
     *         {@link ProducerFactory} returned by {@code producerFactory}.
     */
    @NonNull
    @Bean
    public KafkaTemplate<String, SolveRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Configures and provides a Kafka {@link ConsumerFactory} for consuming messages.
     *
     * @return a {@link ConsumerFactory} configured for consuming messages from Kafka.
     */
    @NonNull
    @Bean
    public ConsumerFactory<String, SolveRequest> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "problem-solving-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config);
    }
/*    @Bean
    public ConsumerFactory<String, SolveRequest> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "problem-solving-group");
        return new DefaultKafkaConsumerFactory<>(config);
    }*/


    /**
     * Configures and provides a Kafka {@link ConcurrentKafkaListenerContainerFactory} bean,
     * enabling the creation of listener containers for consuming messages from Kafka topics.
     *
     * @return a {@link ConcurrentKafkaListenerContainerFactory} configured for processing Kafka messages.
     */
    @NonNull
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SolveRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SolveRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(NUM_PARTITIONS);
        return factory;
    }


}
