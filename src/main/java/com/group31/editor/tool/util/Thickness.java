package com.group31.editor.tool.util;

public class Thickness {
    private static volatile int instance = 10;

    public Thickness(int a) {
        instance = a;
    }

    public static void setActiveThickness(int a) {
        instance = a;
    }

    public static int getActiveThickness(){
        return instance;
    }
}
