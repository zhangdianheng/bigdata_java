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
}
