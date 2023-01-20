package com.zdh.stream;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.util.Properties;

/**
 * @author zdh
 * @date 2022-05-25 14:36
 * @Version 1.0
 */
public class IntervalJoinDemo {
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
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.20.2.2:9092,10.20.2.5:9092,10.20.2.6:9092");
        props.put("zookeeper.connect", "10.20.2.17:2181");
        props.put("group.id", "stream_intervalJoin_api");
        props.put("auto.offset.reset", "latest");
//        env.addSource(new FlinkKafkaConsumer<>("watermark_window", new SimpleStringSchema(), props))
//                .map(JSONUtil::parseObj).print();
        DataStreamSource<String> source = env.addSource(new FlinkKafkaConsumer<>("watermark_window", new SimpleStringSchema(), props));
        SingleOutputStreamOperator<JSONObject> bigdata_customer_baseinfo = source.filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_baseinfo", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s -> JsonPath.read(JSONUtil.parseObj(s), "$.after"));
        SingleOutputStreamOperator<JSONObject> bigdata_customer_crm_audit = source.filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_crm_audit", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s -> JsonPath.read(JSONUtil.parseObj(s), "$.after"));
        bigdata_customer_crm_audit.keyBy(s -> s.getStr("CustomerId"))
                //只要在窗口范围内，数据到达能够join上就直接下发，不会等待。
                //不在窗口范围内，数据还是会丢失。
                .intervalJoin(bigdata_customer_baseinfo.keyBy(s -> s.getStr("BusinessId")))
                //窗口区间范围前后1分钟
                .between(Time.minutes(-1), Time.minutes(1))
                .process(new ProcessJoinFunction<JSONObject, JSONObject, JSONObject>() {
            @Override
            public void processElement(JSONObject s1, JSONObject s2, Context context, Collector<JSONObject> collector) throws Exception {
                JSONObject obj = JSONUtil.createObj();
                obj.putOpt("BusinessId", s1.getStr("BusinessId"));
                obj.putOpt("CustomerId", s1.getStr("CustomerId"));
                obj.putOpt("CustomerName", s1.getStr("CustomerName"));
                obj.putOpt("DataUpdateTime", s1.getStr("DataUpdateTime"));
                obj.putOpt("Auditor", s1.getStr("Auditor"));
                obj.putOpt("OpenId", s1.getStr("OpenId"));
                obj.putOpt("NameCode", s2.getStr("NameCode"));
                obj.putOpt("SerialNo", s2.getStr("SerialNo"));
                collector.collect(obj);
            }
        })
                .print();

        env.execute("watermark_window");


    }
}
