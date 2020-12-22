package vn.vnpay.notspringdemo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class GsonUtil {

    public static String toJson(Object obj) {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        Gson gson = gsonBuilder.create();
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Type type) {

        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

}
