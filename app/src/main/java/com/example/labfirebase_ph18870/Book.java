package com.example.labfirebase_ph18870;

public class Book {
    private String tenTruyen;
    private int gia;
    private String linkAnh;

    public Book() {
    }

    public Book(String tenTruyen, int gia, String linkAnh) {
        this.tenTruyen = tenTruyen;
        this.gia = gia;
        this.linkAnh = linkAnh;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public void setTenTruyen(String tenTruyen) {
        this.tenTruyen = tenTruyen;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(String linkAnh) {
        this.linkAnh = linkAnh;
    }
}
