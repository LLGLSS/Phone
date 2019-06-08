package com.example.phone;

public class AddressPeople {

    //电话
    private String phone;
    //姓名
    private String name;
    //地址
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //拼音
    private String pinyin;
    //拼音首字母
    private String headerWord;

    public AddressPeople(String name,String phone,String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.pinyin =PinYinUtils.getPinyin(name);
        headerWord = pinyin.substring(0, 1);
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public  void setPhone (String phone){
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeaderWord() {
        return headerWord;
    }
}

