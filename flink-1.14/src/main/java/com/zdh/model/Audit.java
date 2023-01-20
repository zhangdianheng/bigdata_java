package com.zdh.model;

/**
 * @author zdh
 * @date 2022-05-25 11:34
 * @Version 1.0
 */
public class Audit {
    private String CustomerId;
    private String CustomerName;
    private String DataUpdateTime;
    private String Auditor;
    private String OpenId;
    private Long rowTime;

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getDataUpdateTime() {
        return DataUpdateTime;
    }

    public void setDataUpdateTime(String dataUpdateTime) {
        DataUpdateTime = dataUpdateTime;
    }

    public String getAuditor() {
        return Auditor;
    }

    public void setAuditor(String auditor) {
        Auditor = auditor;
    }

    public String getOpenId() {
        return OpenId;
    }

    public void setOpenId(String openId) {
        OpenId = openId;
    }

    public Long getRowTime() {
        return rowTime;
    }

    public void setRowTime(Long rowTime) {
        this.rowTime = rowTime;
    }

    @Override
    public String toString() {
        return "Audit{" +
                "CustomerId='" + CustomerId + '\'' +
                ", CustomerName='" + CustomerName + '\'' +
                ", DataUpdateTime='" + DataUpdateTime + '\'' +
                ", Auditor='" + Auditor + '\'' +
                ", OpenId='" + OpenId + '\'' +
                ", rowTime=" + rowTime +
                '}';
    }
}

