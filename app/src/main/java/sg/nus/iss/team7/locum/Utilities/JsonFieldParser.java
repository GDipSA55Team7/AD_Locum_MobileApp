package sg.nus.iss.team7.locum.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonFieldParser {

    public static String getField(String jsonString, String fieldName) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        JsonElement element = jsonObject.get(fieldName);
        if (element == null) {
            return null;
        }
        return element.getAsString();
    }
}

