package com.malalaoshi.android.entity;

import java.util.List;

/**
 * Created by kang on 16/11/8.
 */

public class Charge {

    /**
     * id : ch_PuHG84GqL8SOarj5iPuv5q9O
     * object : charge
     * created : 1458186741
     * livemode : true
     * paid : false
     * refunded : false
     * app : app_1Gqj58ynP0mHeX1q
     * channel : alipay_qr
     * order_no : 123456789
     * client_ip : 127.0.0.1
     * amount : 100
     * amount_settle : 100
     * currency : cny
     * subject : Your Subject
     * body : Your Body
     * extra : {}
     * time_paid : null
     * time_expire : 1458273141
     * time_settle : null
     * transaction_no : null
     * refunds : {"object":"list","url":"/v1/charges/ch_PuHG84GqL8SOarj5iPuv5q9O/refunds","has_more":false,"data":[]}
     * amount_refunded : 0
     * failure_code : null
     * failure_msg : null
     * metadata : {}
     * credential : {"object":"credential","alipay_qr":"https://qr.alipay.com/tax09784bfcqujv2upup2070"}
     * description : Your Description
     */

    private String id;
    private String object;
    private int created;
    private boolean livemode;
    private boolean paid;
    private boolean refunded;
    private String app;
    private String channel;
    private String order_no;
    private String client_ip;
    private int amount;
    private int amount_settle;
    private String currency;
    private String subject;
    private String body;
    private Object time_paid;
    private int time_expire;
    private Object time_settle;
    private Object transaction_no;
    /**
     * object : list
     * url : /v1/charges/ch_PuHG84GqL8SOarj5iPuv5q9O/refunds
     * has_more : false
     * data : []
     */

    private RefundsBean refunds;
    private int amount_refunded;
    private Object failure_code;
    private Object failure_msg;
    /**
     * object : credential
     * alipay_qr : https://qr.alipay.com/tax09784bfcqujv2upup2070
     */

    private CredentialBean credential;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public boolean isLivemode() {
        return livemode;
    }

    public void setLivemode(boolean livemode) {
        this.livemode = livemode;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public void setRefunded(boolean refunded) {
        this.refunded = refunded;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount_settle() {
        return amount_settle;
    }

    public void setAmount_settle(int amount_settle) {
        this.amount_settle = amount_settle;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Object getTime_paid() {
        return time_paid;
    }

    public void setTime_paid(Object time_paid) {
        this.time_paid = time_paid;
    }

    public int getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(int time_expire) {
        this.time_expire = time_expire;
    }

    public Object getTime_settle() {
        return time_settle;
    }

    public void setTime_settle(Object time_settle) {
        this.time_settle = time_settle;
    }

    public Object getTransaction_no() {
        return transaction_no;
    }

    public void setTransaction_no(Object transaction_no) {
        this.transaction_no = transaction_no;
    }

    public RefundsBean getRefunds() {
        return refunds;
    }

    public void setRefunds(RefundsBean refunds) {
        this.refunds = refunds;
    }

    public int getAmount_refunded() {
        return amount_refunded;
    }

    public void setAmount_refunded(int amount_refunded) {
        this.amount_refunded = amount_refunded;
    }

    public Object getFailure_code() {
        return failure_code;
    }

    public void setFailure_code(Object failure_code) {
        this.failure_code = failure_code;
    }

    public Object getFailure_msg() {
        return failure_msg;
    }

    public void setFailure_msg(Object failure_msg) {
        this.failure_msg = failure_msg;
    }

    public CredentialBean getCredential() {
        return credential;
    }

    public void setCredential(CredentialBean credential) {
        this.credential = credential;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class RefundsBean {
        private String object;
        private String url;
        private boolean has_more;
        private List<?> data;

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isHas_more() {
            return has_more;
        }

        public void setHas_more(boolean has_more) {
            this.has_more = has_more;
        }

        public List<?> getData() {
            return data;
        }

        public void setData(List<?> data) {
            this.data = data;
        }
    }

    public static class CredentialBean {
        private String object;
        private String alipay_qr;
        private String wx_pub_qr;

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public String getAlipay_qr() {
            return alipay_qr;
        }

        public void setAlipay_qr(String alipay_qr) {
            this.alipay_qr = alipay_qr;
        }

        public String getWx_pub_qr() {
            return wx_pub_qr;
        }

        public void setWx_pub_qr(String wx_pub_qr) {
            this.wx_pub_qr = wx_pub_qr;
        }
    }
}
