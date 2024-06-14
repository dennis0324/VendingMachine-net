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

        // 변수
        JSONArray insertedMoney = (JSONArray)payload.get();               // 삽입된 화페 정보
        String currVendingID    = classification.getValue(2);       // 현재 작업 중인 자판기 ID
        StringBuilder concatMoney = new StringBuilder();                  // value data

        // 쿼리에 사용할 value data 작성
        for (int i = 0; i < insertedMoney.length(); i++) {
            int stored = insertedMoney.getJSONObject(i).getInt("use");
            if(stored != 0) {
                concatMoney.append(stored).append("|");
            } else {
                concatMoney.append("|");
            }
        } concatMoney.deleteCharAt(concatMoney.length()-1); // 마지막 구분자 삭제

        try { // 쿼리 실행
            exeQuery(conn, "CALL MACHINE_MONEY_INSERT(?, ?)", currVendingID, String.valueOf(concatMoney));
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error");
        }

        return returnSeq("[알림]: 처리 중", "success"); // 결과 처리
    }

    ResultSet exeQuery(Connection conn, String query, String ... str) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        for(int i=0; i<str.length; i++){
            ps.setString(i+1, str[i]);
        }
        return ps.executeQuery();
    } // 쿼리 실행 및 결과 반환 함수

    String returnSeq(String MSG, String type) throws JSONException {
        System.out.println(MSG);

        JSONObject obj = new JSONObject();
        obj.put("status", type);
        obj.put("data", new JSONObject());

        classification.setValue(4, obj.toString());
        String msg = classification.toString();
        System.out.println("[전송]: " + msg);

        return msg;
    } // 리턴 메세지 출력 및 반환
}
