package kr.ac.dankook.VettAIRecordService.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapSevers;

    private final KafkaTemplate<String,String> kafkaTemplate;
    private static final String DLT_SUFFIX = ".dlt";

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(){
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record,ex) -> new TopicPartition(record.topic()+DLT_SUFFIX,record.partition()));
    }

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer){

        // 지수 백오프 ( 100ms 시작, 2배씩, 최대 10초)
        var backoff = new ExponentialBackOffWithMaxRetries(5);
        backoff.setInitialInterval(100);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(10_000);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer,backoff);

        handler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                SerializationException.class,
                DeserializationException.class,
                JsonProcessingException.class
        );
        return handler;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootStrapSevers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
