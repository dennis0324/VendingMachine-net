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

public class Change extends Processing {

    public Change(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - change (Admin only)");

        JSONObject jo = new JSONObject();
        ArrayList<JSONObject> jsonArray = new ArrayList<>();

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        PreparedStatement ppst = conn.prepareStatement("CALL CHANGE_PRODUCT(?, ?)");
        ppst.setString(1, sqlData[3]);  // 어드민 확인
        ppst.setString(2, (String)classification.getValue(2));         // 자판기 번호
        //ppst.setString(3, changeTarget); // 변경할 제품 번호/이름/가격
        ResultSet rs = ppst.executeQuery();

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
        // System.out.println(jsonArray.toString());
        jo.put("status", "success");
        jo.put("data", jsonArray);

        classification.setValue(4, jo.toString());
        String receiveMSG = classification.toString();
        System.out.println("[전송]: " + receiveMSG);

        return receiveMSG;
    }
}
