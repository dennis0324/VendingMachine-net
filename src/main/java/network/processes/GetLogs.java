package network.processes;

import network.Classification;
import network.Payload;
import network.Processing;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetLogs extends Processing {
    public GetLogs(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - getLogs");

        // 변수
        ResultSet rs;                                             // SQL 작업을 위한 데이터 테이블 변수
        JSONArray resultData  = new JSONArray();                  // 로그 저장용 Json ArrayID
        String[] list = {"year", "month", "day", "limit"};        //
        String[] currPayload = new String[list.length];           //
        String currVendingID  = classification.getValue(2); // 현재 동작중인 자판기

        for(int i = 0; i < list.length; i++) {
            currPayload[i] = String.valueOf(payload.get(list[i]));
        }

        try { // 로그 가져오기
            rs = exeQuery(conn, "CALL MACHINE_HIST_GET(?, ?, ?, ?, ?)", currVendingID, currPayload[0], currPayload[1], currPayload[2], currPayload[3]);
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error", new JSONArray());
        }
        
        while(rs.next()) { // 쿼리 실행 결과를 변환 및 저장
            JSONObject obj = new JSONObject();
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                obj.put(rs.getMetaData().getColumnLabel(i), rs.getString(i));
            }
            resultData.put(obj); // JSON 배열에 JSON 오브젝트 삽입
        }

        // 결과 처리
        return returnSeq("[알림]: 기록 중", "success", resultData);
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
