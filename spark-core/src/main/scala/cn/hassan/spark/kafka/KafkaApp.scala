package cn.hassan.spark.kafka

import java.util.{Collections, Properties}

import cn.hassan.spark.kafka.interceptor.ProducerPrefixInterceptor
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

import scala.collection.JavaConversions._

object KafkaApp {
  def main(args: Array[String]): Unit = {
    //producer()
    consumer()
  }

  def consumer(): Unit ={
    val props = new Properties()
    props.put("bootstrap.servers","47.99.217.209:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", "kafka-test")
    props.put("auto.offset.reset","earliest")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")

    val consumer = new KafkaConsumer[String,String](props)
    consumer.subscribe(Collections.singletonList("kafka-test"))

    //val partitionInfoes = consumer.partitionsFor("kafka-test")

    while (true) {
      val records = consumer.poll(100)
      for (record <- records){
        println(record.offset() +"--" +record.key() +"--" +record.value())
      }
    }
    consumer.close()
  }

  def producer(): Unit ={
    val brokers_list = "47.99.217.209:9092"
    val topic = "kafka-test"
    val properties = new Properties()
    properties.put("group.id", "kafka-test")
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,brokers_list)
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName) //key的序列化;
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)//value的序列化;
    properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,classOf[ProducerPrefixInterceptor].getName)//添加拦截器
    val producer = new KafkaProducer[String, String](properties)
    var num = 0
    for(i<- 1 to 5){
      producer.send(new ProducerRecord(topic,"topic"+i),new Callback(){
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          if(exception != null){
            exception.printStackTrace()
          }else{
            println(metadata.topic() + ":" + metadata.partition() + ":" + metadata.hasOffset)
          }
        }
      })
    }
    producer.close()
  }
}
