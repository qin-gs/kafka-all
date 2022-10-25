package com.example;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerFastStart {

    public static final String brokerList = "localhost:9092";
    public static final String topic = "topic-demo";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "hello, kafka");

        // 通过 get 阻塞等待响应，实现同步发送
        RecordMetadata metadata = producer.send(record).get();
        // 当前消息的主题、分区号、分区中的偏移量(offset)、时间戳
        System.out.println(metadata.topic() + "-" + metadata.partition() + ":" + metadata.offset());

        // 也可以通过 callback 获取返回结果
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (metadata != null) {
                    System.out.println(metadata.topic() + "-" + metadata.partition() + ":" + metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            }
        });

        // 回收资源，会阻塞等待所有的请求完成 (可以指定等待时间)
        producer.close();

    }
}
