package com.malalaoshi.android.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Create course order entity
 * Created by tianwei on 2/27/16.
 */
public class CreateCourseOrderResultEntity extends CreateCourseOrderEntity {
    private Integer orderType;
    private long live_class;
    private String order_id;
    private String parent;
    private String id;
    private String total;
    private String price;
    private String status;
    private long to_pay;

    private boolean ok;
    private int code;

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public long getLive_class() {
        return live_class;
    }

    public void setLive_class(long live_class) {
        this.live_class = live_class;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTo_pay() {
        return to_pay;
    }

    public void setTo_pay(long to_pay) {
        this.to_pay = to_pay;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static CreateCourseOrderResultEntity parse(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(obj.toString(), CreateCourseOrderResultEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "CreateCourseOrderResultEntity{" +
                "orderType=" + orderType +
                ", live_class=" + live_class +
                ", order_id='" + order_id + '\'' +
                ", parent='" + parent + '\'' +
                ", id='" + id + '\'' +
                ", total='" + total + '\'' +
                ", price='" + price + '\'' +
                ", status='" + status + '\'' +
                ", to_pay=" + to_pay +
                ", ok=" + ok +
                ", code=" + code +
                '}';
    }
}
