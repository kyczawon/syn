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
            "Sushi set - special of the chief famous salmon sushi",
            "French fries and bacon and Cheddar burger",
            "pizza pepperoni",
            "chief's special rice"
    };

    public static String[] allergens = new String[]{
            "Fish, Sesame Seeds, Milk, Soybeans",
            "Wheat, Soy, Egg, Milk",
            "Milk, Gluten",
            "No allergens"
    };

    public static String[] translations =  descriptions;

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

    public static void setAllergens(String[] allergens) {
        Utils.allergens = allergens;
    }
}