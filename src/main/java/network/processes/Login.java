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

        JSONObject jo = new JSONObject();   // JSON 오브젝트
        String status = "deny", data;       // 상태값, 문자열 변수

        PreparedStatement ppst;             // SQL 처리를 위한 오브젝트
        ResultSet rs;                       // SQL 데이터 테이블 결과값의 저장을 위한 변수

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        ppst = conn.prepareStatement("CALL GET_LOGIN_INFO(?)");
        ppst.setString(1, sqlData[3]);
        rs = ppst.executeQuery();

        // 결과 처리
        while (rs.next()) {
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            String original = rs.getString("password");
            String target = (String)payload.get("password");

            if(target == null) return null; // 비밀번호가 없을 경우
            if(target.equals(original)) {   // 두 비밀번호가 일치할 경우
                status = "success";
            }
        }

        jo.put("data", new JSONObject());   // 결과값 삽입
        jo.put("status", status);           // 상태값 삽입
        classification.setValue(4, jo.toString());
        data = classification.toString();
        System.out.println("[전송]: " + data);

        return data;
    }
}
