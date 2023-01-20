package com.zdh.stream;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zdh.model.Audit;
import com.zdh.model.Customer;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.Properties;

import static org.apache.flink.table.api.Expressions.*;

/**
 * @author zdh
 * @date 2022-05-23 15:36
 * @Version 1.0
 */
public class StateBackendTableApi {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStateBackend(new EmbeddedRocksDBStateBackend(true));
        env.enableCheckpointing(300000);
        env.setParallelism(1);
        env.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage("hdfs://10.20.2.17:4007/flink/checkpoint"));
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(6000);
        env.getCheckpointConfig().setCheckpointTimeout(6000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(5);
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.20.2.2:9092,10.20.2.5:9092,10.20.2.6:9092");
        props.put("zookeeper.connect", "10.20.2.17:2181");
        props.put("group.id", "stream_table_api");
        props.put("auto.offset.reset", "latest");
//        env.addSource(new FlinkKafkaConsumer<>("watermark_window", new SimpleStringSchema(), props))
//                .map(JSONUtil::parseObj).print();
        DataStreamSource<String> source = env.addSource(new FlinkKafkaConsumer<>("watermark_window", new SimpleStringSchema(), props))
                ;
        SingleOutputStreamOperator<Customer> bigdata_customer_baseinfo = source
                .filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_baseinfo", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s -> JSONUtil.toBean((JSONObject) JsonPath.read(JSONUtil.parseObj(s), "$.after"), Customer.class));
        SingleOutputStreamOperator<Audit> bigdata_customer_crm_audit = source
                .filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_crm_audit", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s -> JSONUtil.toBean((JSONObject) JsonPath.read(JSONUtil.parseObj(s), "$.after"), Audit.class));
        Table customer = tableEnv.fromDataStream(bigdata_customer_baseinfo)
                .select($("BusinessId"), $("NameCode"), $("SerialNo"));
        Table audit = tableEnv.fromDataStream(bigdata_customer_crm_audit).select($("CustomerId"), $("CustomerName"),
                $("DataUpdateTime"), $("Auditor"), $("OpenId"));
        Table select = audit.leftOuterJoin(customer)
                .where($("CustomerId").isEqual($("BusinessId")))
                .select($("CustomerId"), $("CustomerName"), $("DataUpdateTime"),
                        $("Auditor"), $("OpenId"), $("NameCode"), $("SerialNo"));
        select.execute().print();

        env.execute("watermark_window");


    }
}
