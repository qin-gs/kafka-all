package com.example.chapter02;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * 拦截器：在消息发送之前做一些准备工作
 */
public class ProducerInterceptorPrefix implements ProducerInterceptor<String, String> {

    private volatile long sendSuccess = 0;
    private volatile long sendFailure = 0;

    /**
     * 在消息系列化 和 计算分区之前对消息进行定制化操作
     */
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        String modifiedValue = "prefix-" + record.value();
        return new ProducerRecord<>(record.topic(), record.partition(),
                record.timestamp(), record.key(),
                modifiedValue, record.headers());
    }

    /**
     * 在消息被应答之前 或 发送失败时调用该方法
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            sendSuccess++;
        } else {
            sendFailure++;
        }
    }

    /**
     * 关闭拦截器之前进行资源清理
     */
    @Override
    public void close() {
        double successRatio = (double) sendSuccess / (sendFailure + sendSuccess);
        System.out.println("发送成功率 = " + String.format("%.2f", successRatio * 100) + "%");
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
