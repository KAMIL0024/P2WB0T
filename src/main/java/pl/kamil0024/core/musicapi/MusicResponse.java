package pl.kamil0024.core.musicapi;

import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import pl.kamil0024.api.Response;

public class MusicResponse {

    public JSONObject json;

    public MusicResponse(JSONObject json) {
        this.json = json;
        try {
            this.json = json.getJSONObject("map");
        } catch (Exception ignored) {}

    }

    public boolean isError() {
        return !json.getBoolean("success");
    }

    @Nullable
    public Response.Error getError() {
        if (!isError()) return null;
        JSONObject tak = json.getJSONObject("error");
        return new Response.Error(tak.getString("body"), tak.getString("description"));
    }

    @Override
    public String toString() {
        return json.toString();
    }

}
