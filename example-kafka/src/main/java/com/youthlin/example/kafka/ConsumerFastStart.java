package com.youthlin.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * @author youthlin.chen
 * @date 2019-06-03 17:18
 */
public class ConsumerFastStart {
    public static final String GROUP_ID = "group.demo";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CompanySerializer.class.getName());
        properties.put("bootstrap.servers", ProducerFastStart.BROKER_LIST);
        properties.put("group.id", GROUP_ID);
        KafkaConsumer<String, Company> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(ProducerFastStart.TOPIC));
        while (true) {
            ConsumerRecords<String, Company> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, Company> record : records) {
                System.out.println(record);
            }
        }
    }
}
