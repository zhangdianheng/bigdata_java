package com.zdh.kafka;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.time.Duration;
import java.util.Properties;

/**
 * @author zdh
 * @date 2022-05-24 11:10
 * @Version 1.0
 */
public class ProducerData {
    public static void main(String[] args) throws Exception {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("mysql-bigdata.hdsaas.facehand.cn")
                .port(3306)
                .databaseList("saas_ods_dev")
                .tableList("saas_ods_dev.bigdata_customer_crm_audit","saas_ods_dev.bigdata_customer_baseinfo")
                .username("refactor")
                .password("dN4xeKhYKHOQZDw5")
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(60000);
        env.setParallelism(1);
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.20.2.2:9092,10.20.2.5:9092,10.20.2.6:9092");
        SingleOutputStreamOperator<String> source = env.fromSource(mySqlSource, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(5)), "watermark_window");
        source.addSink(new FlinkKafkaProducer<String>("watermark_window", new SimpleStringSchema(),props));
        env.execute("watermark_window");


    }
}
