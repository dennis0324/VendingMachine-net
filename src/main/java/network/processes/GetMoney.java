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

public class GetMoney extends Processing {

    public GetMoney(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - getMoney");

        // 변수
        ResultSet rs;                                   // SQL 데이터 테이블 결과값의 저장을 위한 변수
        PreparedStatement ppst;                         // SQL 처리를 위한 오브젝트
        JSONArray moneyInfo = new JSONArray();          // JSON 타입 배열
        boolean type = (boolean)payload.get("all");     // true >>> execute SPE
        String currVendingID = classification.getValue(2);
        String currTimeStamp = classification.getValue(3);

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        try {
            if (type) {
                rs = exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "SPE", currVendingID, currTimeStamp, "NULL", "NULL");
            } else {
                rs = exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "GET", currVendingID, currTimeStamp, "%", "NULL");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error", new JSONArray());
        }

        // 결과 처리
        while(rs.next()) {
            JSONObject obj = new JSONObject();
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                obj.put(rs.getMetaData().getColumnLabel(i), rs.getString(i));
            }
            moneyInfo.put(obj); // JSON 배열에 JSON 오브젝트 삽입
        }

        return returnSeq("[알림]: 처리 중", "success", moneyInfo);
    }

    ResultSet exeQuery(Connection conn, String query, String ... str) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        for(int i=0; i<str.length; i++){
            ps.setString(i+1, str[i]);
        }
        return ps.executeQuery();
    } // 쿼리 실행 및 결과 반환 함수

    String returnSeq(String MSG, String type, JSONArray arr) throws JSONException {
        System.out.println(MSG);

        JSONObject obj = new JSONObject();
        obj.put("data", arr);
        obj.put("status", type);

        classification.setValue(4, obj.toString());
        String msg = classification.toString();
        System.out.println("[전송]: " + msg);

        return msg;
    } // 리턴 메세지 출력 및 반환
}
