package com.zdh.cdc;

import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author zdh
 * @date 2022-05-24 16:20
 * @Version 1.0
 */
public class CusBaseInfoToKafka {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(6000);
        env.setStateBackend(new EmbeddedRocksDBStateBackend(true));
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
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);

        //source端表创建
        final String sourceCusInfoSql = "CREATE TABLE IF NOT EXISTS crm_cust_customer_baseinfo(\n" +
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
                "PRIMARY KEY (Id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'mysql-bigdata.hdsaas.facehand.cn',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'refactor',\n" +
                "    'password' = 'dN4xeKhYKHOQZDw5',\n" +
                "    'database-name' = 'saas_ods_dev',\n" +
                // "    'scan.startup.mode' = '" + startupMode + "',\n" +
                "    'server-time-zone' = 'Asia/Shanghai',\n" +
                "    'table-name' = 'bigdata_customer_baseinfo'\n" +

                ")";


        final String sinkCusInfoSql = "CREATE TABLE IF NOT EXISTS crm_cust_customer_baseinfo_sink_mysql(\n" +
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
                "PRIMARY KEY (Id) NOT ENFORCED\n" +
                ") WITH (\n" +
                " 'connector' = 'upsert-kafka',\n" +
                " 'topic' = 'bigdata_customer',\n" +
                " 'properties.bootstrap.servers' = '10.20.2.2:9092,10.20.2.5:9092,10.20.2.6:9092',\n" +
                " 'key.format' = 'json',\n" +
                " 'value.format' = 'json'\n" +
                ")";
        final String queryCusInfoSql = "insert into crm_cust_customer_baseinfo_sink_mysql " +
                "select `Id`,`BusinessId`,`CorpId`,`SerialNo`,`Name`,`NameCode`,`CustomerKey`,`ErpName`,`ErpCategoryKey`," +
                "`TypeId`,`CustomerCategory`,`OrderCustomerType`,`IsTemp`,`PriceLevelId`,`DepotId`,`IsArrearsShipped`," +
                "`IsPriceTracks`,`LogisticsId`,`DeliveryId`,`SourceType`,`CreatorId`,`ReviserId`,`DataCreateTime`,`DataUpdateTime`," +
                "`DataStatus`,`Version` from " +
                "crm_cust_customer_baseinfo";

        tableEnv.executeSql(sourceCusInfoSql);
        tableEnv.executeSql(sinkCusInfoSql);
        tableEnv.executeSql(queryCusInfoSql);
    }
}
