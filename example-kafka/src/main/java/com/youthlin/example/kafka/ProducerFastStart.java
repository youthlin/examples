package com.youthlin.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @author youthlin.chen
 * @date 2019-06-03 17:15
 */
public class ProducerFastStart {
    public static final String BROKER_LIST = "localhost:9092";
    public static final String TOPIC = "topic-demo";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CompanySerializer.class.getName());
        properties.put("bootstrap.servers", BROKER_LIST);
        KafkaProducer<String, Company> producer = new KafkaProducer<>(properties);
        Company company = new Company();
        company.setName("Qunar");
        company.setAddress("北京海淀");
        ProducerRecord<String, Company> record = new ProducerRecord<>(TOPIC, company);
        producer.send(record);
        producer.close();
    }

}
