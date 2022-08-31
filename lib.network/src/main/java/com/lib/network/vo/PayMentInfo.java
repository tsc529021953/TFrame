package com.lib.network.vo;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by ywr on 2020/8/28 17:24
 */
public class PayMentInfo {

    private List<ServiceBean> service;
    private List<PaymentBean> payment;

    public List<ServiceBean> getService() {
        return service;
    }

    public void setService(List<ServiceBean> service) {
        this.service = service;

    }

    public List<PaymentBean> getPayment() {
        return payment;
    }

    public void setPayment(List<PaymentBean> payment) {
        this.payment = payment;
    }

    public static class ServiceBean {
        /**
         * refrenceId : 1061081411009646592
         * serviceIcon : http://192.168.2.9:8080/multipart/open/image/2020/03/20200304/1061081401195008000.png
         * serviceMark : xx音乐续费一年
         * serviceName : xx音乐续费服务
         * serviceNo : xx
         * servicePrice : 0.01
         */

        private long serviceId;
        private String serviceIcon;
        private String serviceMark;
        private String serviceName;
        private String serviceNo;
        private double servicePrice;
        private List<PricesBean>  prices;
        private int serviceStatus;

        public int getServiceStatus() {
            return serviceStatus;
        }

        public void setServiceStatus(int serviceStatus) {
            this.serviceStatus = serviceStatus;
        }


        public List<PricesBean>  getPrices(){return prices;}

        public void setPrices(List<PricesBean> prices){this.prices = prices;}

        public long getServiceId() {
            return serviceId;
        }

        public void setServiceId(long serviceId) {
            this.serviceId = serviceId;
        }

        public String getServiceIcon() {
            return serviceIcon;
        }

        public void setServiceIcon(String serviceIcon) {
            this.serviceIcon = serviceIcon;
        }

        public String getServiceMark() {
            return serviceMark;
        }

        public void setServiceMark(String serviceMark) {
            this.serviceMark = serviceMark;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceNo() {
            return serviceNo;
        }

        public void setServiceNo(String serviceNo) {
            this.serviceNo = serviceNo;
        }

        public double getServicePrice() {
            return servicePrice;
        }

        public void setServicePrice(double servicePrice) {
            this.servicePrice = servicePrice;
        }
    }

    public static class PricesBean{
        private long refrenceId;
        private int  serviceOrder;
        private String discountRemarks;
        private String serviceTitle;
        private int servicePrice;
        private String serviceRemarks;
        private Boolean deleteRemarks;

        public long getRefrenceId() {
            return refrenceId;
        }

        public void setRefrenceId(long refrenceId) {
            this.refrenceId = refrenceId;
        }

        public int getServiceOrder() {
            return serviceOrder;
        }

        public void setServiceOrder(int serviceOrder) {
            this.serviceOrder = serviceOrder;
        }

        public String getDiscountRemarks() {
            return discountRemarks;
        }

        public void setDiscountRemarks(String discountRemarks) {
            this.discountRemarks = discountRemarks;
        }

        public String getServiceTitle() {
            return serviceTitle;
        }

        public void setServiceTitle(String serviceTitle) {
            this.serviceTitle = serviceTitle;
        }

        public int getServicePrice() {
            return servicePrice;
        }

        public void setServicePrice(int servicePrice) {
            this.servicePrice = servicePrice;
        }

        public String getServiceRemarks() {
            return serviceRemarks;
        }

        public void setServiceRemarks(String serviceRemarks) {
            this.serviceRemarks = serviceRemarks;
        }

        public Boolean getDeleteRemarks() {
            return deleteRemarks;
        }

        public void setDeleteRemarks(Boolean deleteRemarks) {
            this.deleteRemarks = deleteRemarks;
        }
    }

    public static class PaymentBean {
        /**
         * channelIcon : http://192.168.2.9:8080/multipart/open/image/2020/03/20200304/1061034587787923456.jpg
         * channelMark : 银联支付
         * channelName : 银联
         * channelNo : card
         * channelOrder : 10
         * refrenceId : 1061034622839689216
         */

        private String channelIcon;
        private String channelMark;
        private String channelName;
        private String channelNo;
        private int channelOrder;
        private long channelId;

        public String getChannelIcon() {
            return channelIcon;
        }

        public void setChannelIcon(String channelIcon) {
            this.channelIcon = channelIcon;
        }

        public String getChannelMark() {
            return channelMark;
        }

        public void setChannelMark(String channelMark) {
            this.channelMark = channelMark;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getChannelNo() {
            return channelNo;
        }

        public void setChannelNo(String channelNo) {
            this.channelNo = channelNo;
        }

        public int getChannelOrder() {
            return channelOrder;
        }

        public void setChannelOrder(int channelOrder) {
            this.channelOrder = channelOrder;
        }

        public long getChannelId() {
            return channelId;
        }

        public void setChannelId(long channelId) {
            this.channelId = channelId;
        }
    }
}
