package com.zdh.stream;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zdh.model.Audit;
import com.zdh.model.Customer;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;
import java.util.Properties;

import static org.apache.flink.table.api.Expressions.*;

/**
 * @author zdh
 * @date 2022-05-23 15:36
 * @Version 1.0
 */
public class JoinWindowTableApi {
    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "root");
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
//                .useBlinkPlanner()
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
        DataStreamSource<String> source = env.addSource(new FlinkKafkaConsumer<>("watermark_window", new SimpleStringSchema(), props));
        SingleOutputStreamOperator<Customer> bigdata_customer_baseinfo = source
                .filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_baseinfo", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s->{
                    Object read = JsonPath.read(JSONUtil.parseObj(s), "$.after");
                    JSONObject jsonObject = JSONUtil.parseObj(read);
                    jsonObject.putOpt("rowTime",DateUtil.current());
                    return jsonObject;
                })
                .map(jsonObject -> JSONUtil.toBean(jsonObject, Customer.class))
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Customer>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        //
                        .withTimestampAssigner((customer, l) -> customer.getRowTime())
                        //空闲窗口触发时间，在多并发任务中，会存在有窗口中没有数据，窗口即使watermark到达了触发边界，barren没对齐，窗口也不会触发计算，所以针对这种情况即为空闲窗口，设置空闲时间进行窗口触发
                        .withIdleness(Duration.ofSeconds(10)));
        SingleOutputStreamOperator<Audit> bigdata_customer_crm_audit = source
                .filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_crm_audit", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s->{
                    Object read = JsonPath.read(JSONUtil.parseObj(s), "$.after");
                    JSONObject jsonObject = JSONUtil.parseObj(read);
                    jsonObject.putOpt("rowTime",DateUtil.current());
                    return jsonObject;
                })
                .map(jsonObject -> JSONUtil.toBean(jsonObject, Audit.class))
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Audit>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((audit, l) -> audit.getRowTime())
                        .withIdleness(Duration.ofSeconds(10)));
        Table customer = tableEnv.fromDataStream(bigdata_customer_baseinfo)
                .select($("BusinessId"), $("NameCode"), $("SerialNo"),$("rowTime").as("time"));
        Table audit = tableEnv.fromDataStream(bigdata_customer_crm_audit).select($("CustomerId"), $("CustomerName"),
                $("DataUpdateTime"), $("Auditor"), $("OpenId"),$("rowTime").as("b_time"));
        Table select = audit.leftOuterJoin(customer)
                .where($("CustomerId").isEqual($("BusinessId")))
                .window(Tumble.over($("1.minutes")).on($("a.time")).as($("w")))
//                .window(Slide.over($("1.minutes")).every($("2.minutes")).on($("row_time")).as($("w")))
//                .window(Session.withGap($("1.minutes")).on($("row_time")).as($("w")))
                .groupBy($("w"),$("CustomerId"))
                .select($("CustomerId").count().as("counts"),$("CustomerId"),$("w").rowtime());
//        audit.execute().print();
//        customer.execute().print();
        select.execute().print();

        env.execute("watermark_window");


    }
}
