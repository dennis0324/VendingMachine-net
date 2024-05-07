package network.processes;

import network.Classification;
import network.Payload;
import network.Processing;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertMoney extends Processing {

    public InsertMoney(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - insertMoney");

        JSONArray tmp = (JSONArray)payload.get();
        JSONObject jo = new JSONObject();
        int priceid = 1;
        String corrections = "0000|1|300|4";

        System.out.println(tmp);

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        PreparedStatement ppst = conn.prepareStatement("CALL MACHINE_MONEY(?, ?, ?, ?)");
        ppst.setString(1, "SET"); // SET | GET
        ppst.setString(2, (String) classification.getValue(2)); // 0000 ~
        ppst.setInt(3, priceid);    // 1 ~ 5
        ppst.setString(4, corrections);    // NULL 값 사용 가능
        ResultSet rs = ppst.executeQuery();

        // 결과 처리
        while (rs.next()) {
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            JSONObject jsonData = new JSONObject();

            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String data = rs.getMetaData().getColumnLabel(i);
                jsonData.put(data, rs.getString(i));
            }
            tmp.put(jsonData);

        } // price, qty 속성값 사용

        jo.put("status", "success");
        jo.put("data", tmp);

        classification.setValue(4, jo.toString());
        String receiveMSG = classification.toString();
        System.out.println("[전송]: " + receiveMSG);

        return receiveMSG;
    }
}
