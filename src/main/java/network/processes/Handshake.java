package network.processes;

import network.Classification;
import network.Payload;
import network.Processing;
import org.json.JSONException;
import org.json.JSONObject;

public class Handshake extends Processing {

    public Handshake(Classification classification, String counter) {
        super(classification, counter);
    }

    @Override
    public String run(Payload payload) throws JSONException {
        System.out.println("[알림]: CMD 코드 - handshake");

        // 결과 처리
        JSONObject jo = new JSONObject();
        jo.put("status", "success");
        jo.put("data", "");

        classification.setValue(2, counter);
        classification.setValue(4, jo.toString());
        String receiveMSG = classification.toString();
        System.out.println("[전송]: " + receiveMSG);

        return receiveMSG;
    }
}
