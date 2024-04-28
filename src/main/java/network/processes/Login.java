package network.processes;

import network.Classification;
import network.Payload;
import network.Processing;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;

public class Login extends Processing {

    public Login(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - login");

        JSONObject jo = new JSONObject();
        String status = "deny";
        String receiveMSG = null;

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        PreparedStatement ppst = conn.prepareStatement("CALL GET_LOGIN_INFO(?)");
        ppst.setString(1, sqlData[3]);
        ResultSet rs = ppst.executeQuery();

        // 결과 처리
        while (rs.next()) {
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            String original = rs.getString("password");
            String target = (String)payload.get("password");

            if(target == null) return null;
            if(target.equals(original)) {
                status = "success";
            }
            jo.put("status", status);
            jo.put("data", "");

            classification.setValue(4, jo.toString());
            receiveMSG = classification.toString();
            System.out.println("[전송]: " + receiveMSG);

            return receiveMSG;
        }
        jo.put("status", status);
        jo.put("data", "");
        classification.setValue(4, jo.toString());

        return receiveMSG;
    }
}
