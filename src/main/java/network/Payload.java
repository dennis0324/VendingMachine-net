package network;

import org.json.JSONException;
import org.json.JSONObject;

public class Payload {

    private JSONObject jsonObject;

    Payload(String cmdData) throws JSONException { // 생성자
        this.jsonObject = new JSONObject(cmdData);
    }

    public Object get(String key) {
        try {
            return jsonObject.get(key);
        } catch (JSONException e) {
            return null;
        }
    }
}
