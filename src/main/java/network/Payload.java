package network;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class Payload {

    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private String payloadData;

    Payload(String classData) { // 생성자
        try { // JSON 오브젝트 지정
            this.jsonObject = new JSONObject(classData);
        } catch (JSONException e) {
            try { // JSON 배열 지정
                this.jsonArray = new JSONArray(classData);
            } catch (JSONException ex) { // String 타입 지정
                this.payloadData = classData;
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