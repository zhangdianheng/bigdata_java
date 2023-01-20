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
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * @author zdh
 * @date 2022-05-23 15:36
 * @Version 1.0
 */
public class StateBackendDemo {
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
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.20.2.2:9092,10.20.2.5:9092,10.20.2.6:9092");
        props.put("zookeeper.connect", "10.20.2.17:2181");
        props.put("group.id", "stream_api");
        props.put("auto.offset.reset", "latest");
//        env.addSource(new FlinkKafkaConsumer<>("watermark_window", new SimpleStringSchema(), props))
//                .map(JSONUtil::parseObj).print();
        DataStreamSource<String> source = env.addSource(new FlinkKafkaConsumer<>("watermark_window", new SimpleStringSchema(), props));
        SingleOutputStreamOperator<JSONObject> bigdata_customer_baseinfo = source.filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_baseinfo", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s -> JsonPath.read(JSONUtil.parseObj(s), "$.after"));
        SingleOutputStreamOperator<JSONObject> bigdata_customer_crm_audit = source.filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_crm_audit", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s -> JsonPath.read(JSONUtil.parseObj(s), "$.after"));
        bigdata_customer_crm_audit.join(bigdata_customer_baseinfo)
                .where(s1->s1.getStr("CustomerId"))
                .equalTo(s2->s2.getStr("BusinessId"))
                //会话窗口，会根据会话窗口时间执行，设置1分钟，则执行apply聚合操作则在1分钟之后执行
                .window(ProcessingTimeSessionWindows.withGap(Time.minutes(1)))
                //滑动窗口
//                .window(SlidingProcessingTimeWindows.of(Time.seconds(4),Time.seconds(5)))
                //滚动窗口
//                .window(TumblingProcessingTimeWindows.of(Time.seconds(3)))
                .apply((s1,s2)->{
                    JSONObject obj = JSONUtil.createObj();
                    obj.putOpt("BusinessId",s1.getStr("BusinessId"));
                    obj.putOpt("CustomerId",s1.getStr("CustomerId"));
                    obj.putOpt("CustomerName",s1.getStr("CustomerName"));
                    obj.putOpt("DataUpdateTime",s1.getStr("DataUpdateTime"));
                    obj.putOpt("Auditor",s1.getStr("Auditor"));
                    obj.putOpt("OpenId",s1.getStr("OpenId"));
                    obj.putOpt("NameCode",s2.getStr("NameCode"));
                    obj.putOpt("SerialNo",s2.getStr("SerialNo"));
                    return obj;
                })
                .print();

        env.execute("watermark_window");


    }
}
