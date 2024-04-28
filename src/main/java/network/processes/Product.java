package network.processes;

import network.Classification;
import network.Payload;
import network.Processing;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;
import java.util.ArrayList;

public class Product extends Processing {

    public Product(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - products");

        ArrayList<JSONObject> jsonArray = new ArrayList<>();
        JSONObject jo = new JSONObject();

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        PreparedStatement ppst = conn.prepareStatement("CALL GET_PRODUCT(?)");
        ppst.setString(1, (String)classification.getValue(2));
        ResultSet rs = ppst.executeQuery();

        // 결과 처리
        while (rs.next()) {
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            JSONObject jsonData = new JSONObject();

            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String data = rs.getMetaData().getColumnLabel(i);
                jsonData.put(data, rs.getString(i));
            }
            jsonArray.add(jsonData);
        }

        jo.put("data", jsonArray);
        classification.setValue(4, jo.toString());
        String receiveMSG = classification.toString();
        System.out.println("[전송]: " + receiveMSG);

        return receiveMSG;
    }
}
