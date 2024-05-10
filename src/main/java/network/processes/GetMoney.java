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
        JSONArray ja = new JSONArray();     // JSON 타입 배열
        JSONObject jo = new JSONObject();   // JSON 오브젝트

        String data;                        // 반환 메세지

        PreparedStatement ppst;             // SQL 처리를 위한 오브젝트
        ResultSet rs;                       // SQL 데이터 테이블 결과값의 저장을 위한 변수

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        ppst = conn.prepareStatement("CALL MACHINE_MONEY(?, ?, ?, ?, ?)");
        ppst.setString(1, "GET");
        ppst.setString(2, classification.getValue(2));
        ppst.setString(3, classification.getValue(3));
        ppst.setString(4, "%");
        ppst.setString(5, "NULL");

        rs = ppst.executeQuery();

        // 결과 처리
        while (rs.next()) {
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                data = rs.getMetaData().getColumnLabel(i);
                jo.put(data, rs.getString(i));
            }
            ja.put(jo); // JSON 배열에 JSON 오브젝트 삽입
        }

        jo = new JSONObject();              // 초기화
        jo.put("data", ja);              // 결과값 삽입
        jo.put("status", "success");  // 상태값 삽입
        classification.setValue(4, jo.toString());
        data = classification.toString();
        System.out.println("[전송]: " + data);

        return data;
    }
}
