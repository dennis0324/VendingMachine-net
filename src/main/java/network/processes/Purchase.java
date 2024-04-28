package network.processes;

import network.Classification;
import network.Payload;
import network.Processing;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Purchase extends Processing {

    public Purchase(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - products");

        // 쿼리 준비
        String receiveMSG = null;
        JSONObject jo = new JSONObject();
        String tmp = (String)classification.getValue(2);
        PreparedStatement ppst = conn.prepareStatement("CALL GET_PRODUCT(?)");
        ppst.setString(1, tmp);

        // 쿼리 실행 및 결과 가져오기
        ResultSet rs = ppst.executeQuery();
        ArrayList<JSONObject> jsonArray = new ArrayList<>();

        // 결과 처리
        while (rs.next()) {
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            JSONObject jsonData = new JSONObject();

            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String data = rs.getMetaData().getColumnLabel(i);
                jsonData.put(data, rs.getString(i));
            }
            // System.out.println(jsonType.toString());
            jsonArray.add(jsonData);

        }
        //System.out.println(jsonArray.toString());
        jo.put("data", jsonArray);
        classification.setValue(4, jo.toString());
        receiveMSG = classification.toString();
        System.out.println("[전송]: " + receiveMSG);
        return receiveMSG;
    }
}
