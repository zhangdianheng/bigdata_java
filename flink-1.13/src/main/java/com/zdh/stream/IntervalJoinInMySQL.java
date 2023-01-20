package com.zdh.stream;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jayway.jsonpath.JsonPath;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @author zdh
 * @date 2022-05-24 11:10
 * @Version 1.0
 */
public class IntervalJoinInMySQL {
    public static void main(String[] args) throws Exception {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("mysql-bigdata.hdsaas.facehand.cn")
                .port(3306)
                .databaseList("saas_ods_dev")
                .tableList("saas_ods_dev.bigdata_customer_crm_audit", "saas_ods_dev.bigdata_customer_baseinfo")
                .serverTimeZone("Asia/Shanghai")
                .username("refactor")
                .password("dN4xeKhYKHOQZDw5")
//                .startupOptions(StartupOptions.latest())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStateBackend(new EmbeddedRocksDBStateBackend(true));
        env.enableCheckpointing(300000);
        env.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage("hdfs://10.20.2.17:4007/flink/checkpoint"));
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(6000);
        env.getCheckpointConfig().setCheckpointTimeout(6000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(5);
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        SingleOutputStreamOperator<String> source = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "watermark_window");
        SingleOutputStreamOperator<JSONObject> bigdata_customer_baseinfo = source.filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_baseinfo", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s -> JsonPath.read(JSONUtil.parseObj(s), "$.after"));
        SingleOutputStreamOperator<JSONObject> bigdata_customer_crm_audit = source.filter(s -> StringUtils.equalsIgnoreCase("bigdata_customer_crm_audit", JsonPath.read(JSONUtil.parseObj(s), "$.source.table")))
                .map(s ->JsonPath.read(JSONUtil.parseObj(s), "$.after"));
        bigdata_customer_crm_audit
                .assignTimestampsAndWatermarks(WatermarkStrategy
                                .<JSONObject>forBoundedOutOfOrderness(Duration.ofMinutes(5))//水印策略
                                .withTimestampAssigner((record, ts) -> {
                                    DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                            LocalDateTime parse = LocalDateTime.parse(record.getString("@timestamp"), pattern).plusHours(8);
//                            return parse.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                                    if (record.getStr("@timestamp") == null) {
                                        System.out.println("++++++++++++zdh====="+record);
                                        System.out.println("========"+record.getStr("DataUpdateTime"));
                                        return DateUtil.current();
                                    }
                                    return DateUtils.parseStringToLong(record.getStr("@timestamp"), pattern, 8, ChronoUnit.HOURS);
                                })//解析事件时间
                                .withIdleness(Duration.ofMinutes(1))//对于很久不来的流（空闲流，即可能一段时间内某源没有流来数据）如何处置
                )
                .keyBy(s -> s.getStr("CustomerId"))
                .intervalJoin(bigdata_customer_baseinfo
                        .assignTimestampsAndWatermarks(WatermarkStrategy
                                        .<JSONObject>forBoundedOutOfOrderness(Duration.ofMinutes(5))//水印策略
                                        .withTimestampAssigner((record, ts) -> {
                                            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                            LocalDateTime parse = LocalDateTime.parse(record.getString("@timestamp"), pattern).plusHours(8);
//                            return parse.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                                            if (record.getStr("@timestamp") == null) {
                                                return DateUtil.current();
                                            }
                                            return DateUtils.parseStringToLong(record.getStr("@timestamp"), pattern, 8, ChronoUnit.HOURS);
                                        })//解析事件时间
                                        .withIdleness(Duration.ofMinutes(1))//对于很久不来的流（空闲流，即可能一段时间内某源没有流来数据）如何处置
                        )
                        .keyBy(s -> s.getStr("BusinessId")))
                //窗口区间范围前后1分钟
                .between(Time.minutes(-5), Time.minutes(5))
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
                        long timestamp = context.getTimestamp();
                        obj.putOpt("timestamp", timestamp);
                        collector.collect(obj);
                    }
                })
                .print();
        env.execute("watermark_window");


    }
}
