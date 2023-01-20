package com.zdh.sql;

import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author zdh
 * @date 2022-05-30 16:56
 * @Version 1.0
 */
public class WindowTVFJoinDemo {
    public static void main(String[] args) {
        System.setProperty("HADOOP_USER_NAME", "root");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(6000);
        env.setParallelism(1);
        env.setStateBackend(new EmbeddedRocksDBStateBackend());
        env.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage("hdfs://10.20.2.17:4007/flink/checkpoint"));
        // 设置 语义 模式
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 设置 checkpoint 最小间隔 1000 ms
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(6000);
        // 设置 checkpoint 必须在1分钟内完成，否则会被丢弃
        env.getCheckpointConfig().setCheckpointTimeout(6000);
        // 设置 checkpoint 的并发度为 1
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        //checkpoint状态信息清除策略
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //checkpoint连续失败次数
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(5);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        final String sourceSql = "CREATE TABLE IF NOT EXISTS bigdata_customer_crm_audit(\n" +
                "`Id` DECIMAL(20, 0),\n" +
                "`BusinessId` DECIMAL(20, 0),\n" +
                "`CorpId` DECIMAL(20, 0),\n" +
                "`OpenId` STRING,\n" +
                "`ContactsId` DECIMAL(20, 0),\n" +
                "`ContactsIsOfficial` INT,\n" +
                "`ContactsName` STRING,\n" +
                "`Mobile` STRING,\n" +
                "`Tel` STRING,\n" +
                "`Duty` STRING,\n" +
                "`Email` STRING,\n" +
                "`Avatar` STRING,\n" +
                "`CustomerId` DECIMAL(20, 0),\n" +
                "`CustomerName` STRING,\n" +
                "`RegisterType` SMALLINT,\n" +
                "`InvitorId` DECIMAL(20, 0),\n" +
                "`AuditType` SMALLINT,\n" +
                "`Auditor` DECIMAL(20, 0),\n" +
                "`AuditTime` DECIMAL(20, 0),\n" +
                "`DataCreateTime` DECIMAL(20, 0),\n" +
                "`DataUpdateTime` TIMESTAMP(6),\n" +
                "`DataStatus` SMALLINT,\n" +
                "row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n" +
                "WATERMARK FOR row_time AS row_time -INTERVAL '1' MINUTES\n" +
                ") WITH (\n" +
                "  'connector' = 'kafka',\n" +
                "  'topic' = 'bigdata_audit',\n" +
                "  'properties.bootstrap.servers' = '10.20.2.2:9092,10.20.2.5:9092,10.20.2.6:9092',\n" +
                "  'properties.group.id' = 'sql_join_www',\n" +
                "  'scan.startup.mode' = 'latest-offset',\n" +
                "  'format' = 'json',\n" +
                "  'json.fail-on-missing-field' = 'true',\n" +
                "  'json.ignore-parse-errors' = 'false'\n" +
                ")";

        final String sourceCusInfoSql = "CREATE TABLE IF NOT EXISTS bigdata_customer_baseinfo(\n" +
                "`Id` DECIMAL(20, 0),\n" +
                "`BusinessId` DECIMAL(20, 0),\n" +
                "`CorpId` DECIMAL(20, 0),\n" +
                "`SerialNo` STRING,\n" +
                "`Name` STRING,\n" +
                "`NameCode` STRING,\n" +
                "`CustomerKey` STRING,\n" +
                "`ErpName` STRING,\n" +
                "`ErpCategoryKey` STRING,\n" +
                "`TypeId` DECIMAL(20, 0),\n" +
                "`CustomerCategory` SMALLINT,\n" +
                "`OrderCustomerType` SMALLINT,\n" +
                "`IsTemp` SMALLINT,\n" +
                "`PriceLevelId` DECIMAL(20, 0),\n" +
                "`DepotId` DECIMAL(20, 0),\n" +
                "`IsArrearsShipped` SMALLINT,\n" +
                "`IsPriceTracks` SMALLINT,\n" +
                "`LogisticsId` DECIMAL(20, 0),\n" +
                "`DeliveryId` DECIMAL(20, 0),\n" +
                "`SourceType` SMALLINT,\n" +
                "`CreatorId` DECIMAL(20, 0),\n" +
                "`ReviserId` DECIMAL(20, 0),\n" +
                "`DataCreateTime` DECIMAL(20, 0),\n" +
                "`DataUpdateTime` TIMESTAMP(6),\n" +
                "`DataStatus` SMALLINT,\n" +
                "`Version` DECIMAL(20, 0),\n" +
                "row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n" +
                "WATERMARK FOR row_time AS row_time -INTERVAL '1' MINUTES\n" +
                ") WITH (\n" +
                "  'connector' = 'kafka',\n" +
                "  'topic' = 'bigdata_customer',\n" +
                "  'properties.bootstrap.servers' = '10.20.2.2:9092,10.20.2.5:9092,10.20.2.6:9092',\n" +
                "  'properties.group.id' = 'sql_join_cus',\n" +
                "  'scan.startup.mode' = 'latest-offset',\n" +
                "  'format' = 'json',\n" +
                "  'json.fail-on-missing-field' = 'true',\n" +
                "  'json.ignore-parse-errors' = 'false'\n" +
                ")";

        /**
         * @Author zdh
         * @Description //TODO window join 处理模式
         * 通过窗口join表数据聚合操作，状态存储处理窗口内的数据，这里使用的是TUMBLE 滚动窗口
         * @Date 10:10 2022-06-09
         * @Param [args]
         * @return void
         **/
        final String query = "SELECT a.BusinessId,a.CustomerId,a.CustomerName,a.DataUpdateTime,a.Auditor,a.OpenId,b.NameCode,b.SerialNo,a.window_start,a.window_end\n" +
                "           FROM (\n" +
                "               select * from TABLE(TUMBLE(TABLE bigdata_customer_crm_audit , DESCRIPTOR(row_time), INTERVAL '1' MINUTES))\n" +
                "           ) a\n" +
                "            LEFT JOIN (\n" +
                "               select * from TABLE(TUMBLE(TABLE bigdata_customer_baseinfo , DESCRIPTOR(row_time), INTERVAL '1' MINUTES))\n" +
                "           ) b\n" +
                "           ON  a.CustomerId=b.BusinessId AND a.window_start = b.window_start AND a.window_end = b.window_end";


        /**
         * @Author zdh
         * @Description //TODO Window TVF 1.14版本处理方式
         * 单表的窗口处理模式 目前这里展示的是TUMBLE 滚动窗口，还有滑动窗口，支持的窗口有： HOP(TABLE Bid, DESCRIPTOR(bidtime), INTERVAL '5' MINUTES, INTERVAL '10' MINUTES));
         *      CUMULATE(TABLE Bid, DESCRIPTOR(bidtime), INTERVAL '2' MINUTES, INTERVAL '10' MINUTES));（累积窗口(cumulate window)）
         * @Date 10:09 2022-06-09
         * @Param [args]
         * @return void
         **/
//        final String auditQuery = "select count(CustomerId),CustomerId from TABLE(TUMBLE(TABLE bigdata_customer_crm_audit , DESCRIPTOR(row_time), INTERVAL '1' MINUTES)) group by CustomerId,window_start,window_end";
//        final String cusQuery = "select count(SerialNo),SerialNo from TABLE(TUMBLE(TABLE bigdata_customer_baseinfo, DESCRIPTOR(row_time), INTERVAL '1' MINUTES)) group by SerialNo,window_start,window_end";

        tableEnv.executeSql(sourceSql);
        tableEnv.executeSql(sourceCusInfoSql);
        tableEnv.executeSql(query).print();
//        tableEnv.executeSql(auditQuery).print();
//        tableEnv.executeSql(cusQuery).print();
    }
}
