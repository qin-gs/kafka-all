# Kafka

分布式的基于发布订阅的消息队列

- 异步通信
- 缓存 (削峰)
- 解耦



两种模式

- 点对点：消费者主动拉取，消息收到后清除

- 发布订阅：可以有多个 topic，消费者互相独立可以消费同一条消息，消费者消费后不删除



producer  -->  topic (分为多个 partition，每个分区有副本 (leader, follower))  -->  group (多个 consumer，每个消费者消费一个 partition)



- broker：一个 kafka 节点是一个 broke，多个 broker 组成一个 kafka 集群
- topic：对消息进行分类，发送的消息需要指定 topic
- producer
- consumer







