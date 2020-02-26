package com.nvish.awsCSConnector.core.constants;

import java.util.HashMap;
import java.util.Map;

public enum LanguageList {
    English("en"),
    Arabic("ar"),
    Armenian("hy"),
    Basque("eu"),
    Bulgarian("bg"),
    Catalan("ca"),
    ChineseSimplified("zh-Hans"),
    ChineseTraditional("zh-Hant"),
    Czech("cs"),
    Danish("da"),
    Dutch("nl"),
    Finnish("fi"),
    French("fr"),
    Galician("gl"),
    German("de"),
    Greek("el"),
    Hebrew("he"),
    Hindi("hi"),
    Hungarian("hu"),
    Indonesian("id"),
    Irish("ga"),
    Italian("it"),
    Japanese("ja"),
    Korean("ko"),
    Latvian("lv"),
    Norwegian("no"),
    Persian("fa"),
    Portuguese("pr"),
    Romanian("ro"),
    Russian("ru"),
    Spanish("es"),
    Swedish("sv"),
    Thai("th"),
    Turkish("tr");

    private String value;

    LanguageList(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static String getEnumByString(String code){
        for(LanguageList e : LanguageList.values()){

            if(code.equalsIgnoreCase(e.value)) return e.name();
        }
        return null;
    }
    private static final Map lookup =   new HashMap();
    static {
        //Create reverse lookup hash map
        for(LanguageList d : LanguageList.values())
            lookup.put(d.value(), d);
    }


    private static final Map<String, LanguageList> map = new HashMap<>(values().length, 1);

    static {
        for (LanguageList c : values()) map.put(c.value, c);
    }



    public static LanguageList of(String name) {
        LanguageList result = map.get(name);
        if (result == null) {
            throw new IllegalArgumentException("Invalid category name: " + name);
        }
        return result;
    }
}
