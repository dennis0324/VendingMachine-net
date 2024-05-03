package network;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class Payload {

    private JSONObject jsonObject;
    private JSONArray jsonArray;

    Payload(String cmdData) { // 생성자
        try {
            this.jsonObject = new JSONObject(cmdData);
        } catch (JSONException e) {
            try {
                this.jsonArray = new JSONArray(cmdData);
            } catch (JSONException ex) {
                ex.printStackTrace();
                System.out.println("[에러]: 확인되지 않은 JSON 타입");
            }
        }
    }

    public Object get(String key) {
        try {
            return jsonObject.get(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public Object get() {
        return jsonArray;
    }
}

// change, purchase, supply can write