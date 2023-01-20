package com.zdh.sql;

import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

/**
 * @author zdh
 * @date 2022-05-25 9:59
 * @Version 1.0
 */
public class RegularJoinSql {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setStateBackend(new EmbeddedRocksDBStateBackend(true));
        env.setParallelism(1);
        env.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage("hdfs://10.20.2.17:4007/flink/checkpoint"));
        env.enableCheckpointing(60000);
        // 设置 语义 模式
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 设置 checkpoint 最小间隔 1000 ms
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(6000);
        // 设置 checkpoint 必须在1分钟内完成，否则会被丢弃
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // 设置 checkpoint 的并发度为 1
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        //checkpoint状态信息清除策略
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //checkpoint连续失败次数
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(5);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        //设置状态后端ttl
        tableEnv.getConfig().setIdleStateRetention(Duration.ofMinutes(10));
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
                "PRIMARY KEY (Id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'mysql-bigdata.hdsaas.facehand.cn',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'refactor',\n" +
                "    'password' = 'dN4xeKhYKHOQZDw5',\n" +
                "    'database-name' = 'saas_ods_dev',\n" +
//                 "    'scan.startup.mode' = 'latest-offset',\n" +
                "    'scan.incremental.snapshot.enabled' = 'false',\n" +
                "    'server-time-zone' = 'Asia/Shanghai',\n" +
                "    'table-name' = 'bigdata_customer_crm_audit'\n" +

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
                "PRIMARY KEY (Id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'mysql-bigdata.hdsaas.facehand.cn',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'refactor',\n" +
                "    'password' = 'dN4xeKhYKHOQZDw5',\n" +
                "    'database-name' = 'saas_ods_dev',\n" +
//                 "    'scan.startup.mode' = 'latest-offset',\n" +
                "    'scan.incremental.snapshot.enabled' = 'false',\n" +
                "    'server-time-zone' = 'Asia/Shanghai',\n" +
                "    'table-name' = 'bigdata_customer_baseinfo'\n" +

                ")";

        final String query = "select a.BusinessId,a.CustomerId,a.CustomerName,a.DataUpdateTime,a.Auditor,a.OpenId,b.NameCode,b.SerialNo " +
                "from bigdata_customer_crm_audit a left JOIN bigdata_customer_baseinfo b on a.CustomerId=b.BusinessId ";

        tableEnv.executeSql(sourceSql);
        tableEnv.executeSql(sourceCusInfoSql);
        tableEnv.executeSql(query).print();

    }

}
