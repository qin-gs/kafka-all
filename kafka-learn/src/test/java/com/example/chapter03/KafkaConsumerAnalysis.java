package com.example.chapter03;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaConsumerAnalysis {

    public static final String brokerList = "localhost:9092";
    public static final String topic = "topic-demo";
    /**
     * 消费组隶属的消费组名称
     */
    public static final String groupId = "group.demo";
    public static final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static void main(String[] args) {
        Properties properties = initConfig();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(topic));

        while (isRunning.get()) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("record.topic() = " + record.topic());
                System.out.println("record.partition() = " + record.partition());
                System.out.println("record.key() = " + record.key());
                System.out.println("record.value() = " + record.value());
            }
        }
    }

    private static Properties initConfig() {
        Properties properties = new Properties();
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
        properties.put("bootstrap.servers", brokerList);
        // 消费组隶属的消费组名称
        properties.put("group.id", groupId);
        // 消费对应的客户端 id (默认为 consumer-1 ...)
        properties.put("client.id", "producer.client.id.demo");
        return properties;
    }
}
