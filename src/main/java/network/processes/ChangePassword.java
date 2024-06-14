package network.processes;

import network.Classification;
import network.Payload;
import network.Processing;

import org.json.JSONObject;
import org.json.JSONException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePassword extends Processing {

    public ChangePassword(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - changePassword");

        // 변수
        String newPassword = (String)payload.get("password");

        try { // 쿼리 실행
            exeQuery(conn, "CALL USER_PASSWORD_CHANGE(?, ?)", sqlData[3], newPassword);
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error");
        }

        return returnSeq("[알림]: 처리 중", "success");
    }

    ResultSet exeQuery(Connection conn, String query, String ... str) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        for(int i=0; i<str.length; i++){
            ps.setString(i + 1, str[i]);
        }
        return ps.executeQuery();
    } // 쿼리 실행 및 결과 반환 함수

    String returnSeq(String MSG, String type) throws JSONException {
        System.out.println(MSG);

        JSONObject obj = new JSONObject();
        obj.put("data", new JSONObject());
        obj.put("status", type);

        classification.setValue(4, obj.toString());
        String msg = classification.toString();
        System.out.println("[전송]: " + msg);

        return msg;
    } // 리턴 메세지 출력 및 반환
}
