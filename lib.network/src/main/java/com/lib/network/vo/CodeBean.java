package com.lib.network.vo;

/**
 * Created by ywr on 2020/8/31 13:18
 */
public class CodeBean {

    /**
     * authCode : 0092B8A0112141A0821C7113C90FBFD7
     * channelId : 1061037519337951232
     * channelNo : weixin
     * codeUrl : weixin://wxpay/bizpayurl?pr=olAu887
     * collectionAccount : 1251418601
     * createTime : 1598854686994
     * deviceId : 1082066874704891904
     * deviceSN : 000001420020b667264e
     * discountFee : 0
     * feeType : CNY
     * orderId : 1126282099990241280
     * orderMark : 咪咕音乐续费一年
     * orderStatus : 0
     * orderType : 0
     * payFee : 0.01
     * refrenceId : 1126282099990241280
     * renewalMethod : 0
     * serviceId : 1061081411009646592
     * serviceNo : migu
     * totalFee : 0.01
     */

    private String authCode;
    private long channelId;
    private String channelNo;
    private String codeUrl;
    private String collectionAccount;
    private long createTime;
    private long deviceId;
    private String deviceSN;
    private int discountFee;
    private String feeType;
    private long orderId;
    private String orderMark;
    private int orderStatus;
    private int orderType;
    private double payFee;
    private long refrenceId;
    private int renewalMethod;
    private long serviceId;
    private String serviceNo;
    private double totalFee;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public String getCollectionAccount() {
        return collectionAccount;
    }

    public void setCollectionAccount(String collectionAccount) {
        this.collectionAccount = collectionAccount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public int getDiscountFee() {
        return discountFee;
    }

    public void setDiscountFee(int discountFee) {
        this.discountFee = discountFee;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderMark() {
        return orderMark;
    }

    public void setOrderMark(String orderMark) {
        this.orderMark = orderMark;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public double getPayFee() {
        return payFee;
    }

    public void setPayFee(double payFee) {
        this.payFee = payFee;
    }

    public long getRefrenceId() {
        return refrenceId;
    }

    public void setRefrenceId(long refrenceId) {
        this.refrenceId = refrenceId;
    }

    public int getRenewalMethod() {
        return renewalMethod;
    }

    public void setRenewalMethod(int renewalMethod) {
        this.renewalMethod = renewalMethod;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }
}
