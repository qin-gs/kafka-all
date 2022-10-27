# Kafka



## 一. 初始 kafka

分布式的基于发布订阅的消息队列

- 消息系统：解耦、冗余存储、流量削峰、缓冲、异步通信、扩展性、可恢复

- 存储系统
- 流式处理平台



### 1.1 基本概念

- consumer：消费者
- producer：生产者
- broker：服务代理节点 (服务器)
- topic：主题 (有多个分区，可以横跨多个 broker)
- partition：分区 (一个日志文件，通过 offset 保证消息在分区的有序性)
- replica：副本 (leader, follower)



4 个 broker，某个主题有 3 个分区，每个分区有 1 个 leader 和 2 个 follower

<img src="../img/kafka多副本架构.png" alt="kafka多副本架构" style="zoom:50%;" />

- AR (assigned replicas)：所有副本

- ISR (in-sync replicas)：与 leader 保持一定程度同步的副本

- OSR (out-of-sync replicas)：与 leader 滞后过多的副本

​		$AR = ISR + OSR (通常为 0)$

- HW (high watermark)：高水位，表示一个特定的消息偏移量，消费者只能拉取到该偏移量之前 (防止消息还没同步到副本就被消费了)

- LEO (log end offset)：当前日志文件中下一条待写入消息的偏移量

​		ISR 集合中最小的 LEO 就是分区的 HW

​		同步复制要求所有能工作的 follower 副本都复制完，这条消息才会被确认为已成功提交



### 1.2 安装配置



### 1.3 生产与消费

```sh
# zookeeper 2181
# kafka 9092

# 创建一个分区数为 4，副本因子为 3 的主题 topic-demo
kafka-topics.sh --zookeeper localhost:2181/kafka --create --topic topic-demo --replication-factor 3 --partitions 4

# 展示主题信息
kafka-topics.sh --zookeeper localhost:2181/kafka --describe --topic topic-demo

# 订阅主题
kafka-console-consumer.sh --bootstrap-server localhost:9092 --ttopic topic-demo

# 发送消息
kafka-console-producer.sh --broker-list localhost:9092 --topic topic-demo

```



### 1.4 服务端参数配置



## 二. 生产者



### 2.1 客户端开发



**一些能配置的参数**

`ProducerConfig`

- bootstrap.servers：生产者连接 kafka 集群需要的 broker 地址
- key/value.serializer：broker 接收到消息必须以字节数组的形式存在
- client.id：生产者对应的客户端 id

producer 是线程安全的



**消息发送**

`ProducerRecord`

- topic
- partition
- headers
- key：可以用来计算分区号把消息发往特定的分区
- value：消息体
- timestamp：消息的时间戳 (消息创建时间、消息追加到日志文件的时间)



发消息的三种模式

- 发后即忘 (fire-and-forget)
- 同步 (sync)
- 异步 (async)



发消息中的异常

- 可重试异常：可以配置重试次数
- 不可重试异常



**序列化**

生产者将消息转换成字节数组后通过网络发送给 kafka



**分区器**

如果 ProducerRecord 中指定了 partition，就不需要分区器

默认：`org.apache.kafka.clients.producer.internals.DefaultPartitioner`



**生产者拦截器**

在消息发送之前做一些准备工作 (过滤)



### 2.2 原理分析



**整体架构**

主线程经过 (拦截器，序列化器，分区器) 将消息发送到 RecordAccmulator

RecordAccmulator (每个分区一个双端队列 `Deque<ProducerBatch>`) 用来缓存消息批量发送

sender 线程从 RecordAccmulator 中取出消息放到 InFlightRequests (保存已经发出去还没有收到响应的请求) 中



**元数据的更新**



### 2.3 生产者参数

- acks：指定有多少副本收到消息才会认为消息成功写入
- max.request.size：限制生产者客户端能发送消息的最大值 (1MB)
- reties / retry.backoff.ms：重试次数 / 两次重试的时间间隔
- compression.type：消息压缩方式
- connections.max.idle.ms：关闭限制的连接的时间
- linger.ms：ProducerRecord 满了之后生产者的等待时间
- receive.buffer.bytes：socket 接收消息缓冲区的大小
- send.buffer.bytes：socket 发送消息缓冲区的大小
- request.timeout.ms：Producer 等待请求的最长时间 (超时后能重试)





## 三. 消费者



### 3.1 消费者 与 消费组

每个分区只能被一个消费组中的一个消费者消费

消息投递模式

- 点对点：基于队列，可以把所有的消费组放到一个消费组中来实现
- 发布 / 订阅：通过订阅主题，可以把每个消费者都放到不同消费者组进行会进行广播



### 3.2 客户端开发

1. 配置消费者客户端参数 及 创建相应的消费者实例
2. 订阅主题
3. 拉取消息并消费
4. 提交消费位移
5. 关闭消费者实例



**订阅主题 与 分区**

通过集合订阅主题，也可以采用正则











