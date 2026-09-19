package com.example.dvotesystem;

public class Language {
    private String name;
    private String code;
    private String flag;

    public Language(String name, String code, String flag) {
        this.name = name;
        this.code = code;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getFlag() {
        return flag;
    }
}
