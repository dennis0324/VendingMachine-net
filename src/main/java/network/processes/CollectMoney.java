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

public class CollectMoney extends Processing {

    public CollectMoney(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - collectMoney (admin only)");

        // 변수
        String currVendingID = classification.getValue(2);            // 현재 작업 중인 자판기 ID
        String currTimeStamp = classification.getValue(3);            // 현재 작업 중인 자판기 동작 시간
        String[] moneyUnit   = {"10", "50", "100", "500", "1000"};          // 화폐 단위
        
        try { // 금고 초기화
            for(int i = 0; i < 5; i++) {
                String initMoneyQty = currVendingID + "|" + (i + 1) + "|" + moneyUnit[i] + "|10"; // value data
                exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "ADD", currVendingID, currTimeStamp, "NULL", initMoneyQty);
            }
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[알림]; 쿼리 실행 실패", "error");
        }

        return returnSeq("[알림]: 처리 중", "success");
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
        obj.put("data", new JSONObject());
        obj.put("status", type);

        classification.setValue(4, obj.toString());
        String msg = classification.toString();
        System.out.println("[전송]: " + msg);

        return msg;
    } // 리턴 메세지 출력 및 반환
}
