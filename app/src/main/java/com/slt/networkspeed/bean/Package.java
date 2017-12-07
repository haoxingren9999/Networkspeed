package com.slt.networkspeed.bean;

public class Package {
    private String name;
    private String version;
    private String packageName;


    private int uid;
    private long  rxByte=0;
    private long rxBytesAll=0;

    public long getRxByte() {
        return rxByte;
    }

    public void setRxByte(long rxByte) {
        this.rxByte = rxByte;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getRxBytesAll() {
        return rxBytesAll;
    }

    public void setRxBytesAll(long rxBytesAll) {
        this.rxBytesAll = rxBytesAll;
    }
}