package com.utem.ftmk.ws2.arsclient.model.payment;

import com.utem.ftmk.ws2.arsclient.model.plan.Plan;

import java.io.Serializable;

public class Payment implements Serializable {

    private String id;
    private long paidDate;
    private String payer;
    private Plan plan;

    public Payment() {

    }

    public Payment(String id, long paidDate, String payer, Plan plan) {
        this.id = id;
        this.paidDate = paidDate;
        this.payer = payer;
        this.plan = plan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(long paidDate) {
        this.paidDate = paidDate;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }
}
