package com.example.schedify.Util;

import java.util.Arrays;

public class Checker {
    private static String[] invalidStringList = {"", "null"};

    public static boolean isContainNullElement (String[] arr) {
        if (arr == null)
            return true;

        for (String el : arr) {
            if (isNull(el))
                return true;
        }
        return false;
    }

    public static boolean isNull (String str) {
        if (str == null || Arrays.asList(invalidStringList).contains(str))
            return true;
        return false;
    }
}
