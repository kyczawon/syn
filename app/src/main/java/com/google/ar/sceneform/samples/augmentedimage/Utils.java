package com.google.ar.sceneform.samples.augmentedimage;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {
    public static List<String> languageCodes = Arrays.asList("en","da","it", "pl", "es");

    public static Map<String, String> codeToLanguage = new HashMap<String, String>() {{
        put("en", "English");
        put("da", "Danish");
        put("de", "German");
        put("es", "Spanish");
        put("it", "Italian");
        put("pl", "Polish");
    }};

    public static Map<String, String> languageToCode = new HashMap<String, String>() {{
        put("English", "en");
        put("Danish", "da");
        put("Italian", "it");
        put("Polish", "pl");
        put("Spanish", "es");
    }};

    public static List<String> getLanguages() {
        return languageCodes.stream().map(code -> codeToLanguage.get(code)).collect(Collectors.toList());
    }

    public static String[] descriptions = new String[]{
            "Ulcer present but no infection. Please visit your doctor as quickly as possible for treatment",
            "1st degree burn present. Cool the burn with cool running water for at least 10 mins, and apply a thin layer of ointment. Cover the burn with a non-stick, sterile bandage.",
            "2nd degree burn present. Cool the burn with cool running water for at least 15 mins - 30 mins. Seek medical advice for treatment."
    };

    public static String[] translations =  descriptions;

    public static int[] colors =  new int[]{
            Integer.parseInt("C52233", 16),
            Integer.parseInt("7CC6FE", 16),
    };

    public static void setTranslations(String[] translations) {
        Utils.translations = translations;
    }

    public static String targetLanguage = "en";

    public static void setTargetLanguage(String targetLanguage) {
        Utils.targetLanguage = targetLanguage;
    }

    public static String stringEncode(String str) {
        return str.replaceAll("'", "&#39;");
    }
}