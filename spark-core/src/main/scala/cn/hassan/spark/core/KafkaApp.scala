package cn.hassan.spark.core


import java.util.{Collections, Properties}

import com.google.gson.Gson
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
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
    val producer = new KafkaProducer[String, String](properties)
    var num = 0
    for(i<- 1 to 10){
      producer.send(new ProducerRecord(topic,"topic"+i))
    }
    producer.close()
  }
}
