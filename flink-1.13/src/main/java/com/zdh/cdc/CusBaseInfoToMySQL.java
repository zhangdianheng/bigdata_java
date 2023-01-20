package com.zdh.cdc;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author zdh
 * @date 2022-05-24 16:20
 * @Version 1.0
 */
public class CusBaseInfoToMySQL {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
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
                "    'hostname' = 'mysql.fat.hdsaas.facehand.cn',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'refactor',\n" +
                "    'password' = 'dN4xeKhYKHOQZDw5',\n" +
                "    'database-name' = 'saas_customer',\n" +
                // "    'scan.startup.mode' = '" + startupMode + "',\n" +
                "    'server-time-zone' = 'Asia/Shanghai',\n" +
                "    'table-name' = 'customer_baseinfo'\n" +

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
                "   'connector' = 'jdbc',\n" +
                //sink端连接地址url
                "   'url' = 'jdbc:mysql://mysql-bigdata.hdsaas.facehand.cn:3306/saas_ods_dev?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai',\n" +
                //sink端写入数据表名
                "   'table-name' = 'bigdata_customer_baseinfo',\n" +
                //sink端用户名
                "   'username' = 'refactor',\n" +
                //密码
                "   'password' = 'dN4xeKhYKHOQZDw5',\n" +
                //实时批量写入参数
                "   'sink.buffer-flush.max-rows' = '50', \n" +
                "   'sink.buffer-flush.interval' = '1000' \n" +
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
