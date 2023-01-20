package com.zdh.model;

/**
 * @author zdh
 * @date 2022-05-25 11:34
 * @Version 1.0
 */
public class Customer {
    private String BusinessId;
    private String NameCode;
    private String SerialNo;
    private Long rowTime;

    public String getBusinessId() {
        return BusinessId;
    }

    public void setBusinessId(String businessId) {
        BusinessId = businessId;
    }

    public String getNameCode() {
        return NameCode;
    }

    public void setNameCode(String nameCode) {
        NameCode = nameCode;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public Long getRowTime() {
        return rowTime;
    }

    public void setRowTime(Long rowTime) {
        this.rowTime = rowTime;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "BusinessId='" + BusinessId + '\'' +
                ", NameCode='" + NameCode + '\'' +
                ", SerialNo='" + SerialNo + '\'' +
                ", rowTime=" + rowTime +
                '}';
    }
}
