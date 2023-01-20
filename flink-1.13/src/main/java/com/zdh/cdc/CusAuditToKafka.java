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
 * @date 2022-05-24 16:17
 * @Version 1.0
 */
public class CusAuditToKafka {
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
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(5);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
//                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        //source端表创建
        final String sourceSql = "CREATE TABLE IF NOT EXISTS crm_cust_crm_audit(\n" +
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
                // "    'scan.startup.mode' = '" + startupMode + "',\n" +
                "    'server-time-zone' = 'Asia/Shanghai',\n" +
                "    'table-name' = 'bigdata_customer_crm_audit'\n" +

                ")";

        //sink端表创建
        final String sinkSql = "CREATE TABLE IF NOT EXISTS crm_cust_crm_audit_sink_mysql(\n" +
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
                " 'connector' = 'upsert-kafka',\n" +
                " 'topic' = 'bigdata_audit',\n" +
                " 'properties.bootstrap.servers' = '10.20.2.2:9092,10.20.2.5:9092,10.20.2.6:9092',\n" +
                " 'key.format' = 'json',\n" +
                " 'value.format' = 'json'\n" +
                ")";
        //执行插入操作
        final String querySql = "insert into crm_cust_crm_audit_sink_mysql\n" +
                "select Id, `BusinessId`,`CorpId`,`OpenId`,`ContactsId`,`ContactsIsOfficial`," +
                "ContactsName,Mobile,`Tel`,`Duty`,`Email`,`Avatar`,`CustomerId`,`CustomerName`,`RegisterType`," +
                "`InvitorId`,`AuditType`,`Auditor`,`AuditTime`,`DataCreateTime`,DataUpdateTime,`DataStatus` from " +
                "crm_cust_crm_audit";

        tableEnv.executeSql(sourceSql);
        tableEnv.executeSql(sinkSql);
        tableEnv.executeSql(querySql);
    }
}
