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
        ResultSet rs;                                                     //
        JSONArray insertedMoney = (JSONArray)payload.get();               // 삽입된 화페 정보
        JSONArray certainMoney  = new JSONArray();                        // 보유 중인 화페 정보
        String currVendingID    = classification.getValue(2);       // 현재 작업 중인 자판기 ID
        String currTimeStamp    = classification.getValue(3);       // 현재 작업 중인 자판기 동작 시간
        int[] paperOrder        = {10, 50, 100, 500, 1000};               // 화페 단위 정렬 순서
        int total = 0;
        
        System.out.println(insertedMoney); // 디버그
        //        {"use":5,"price":10},
        //        {"use":0,"price":50},
        //        {"use":0,"price":100},
        //        {"use":0,"price":500},
        //        {"use":0,"price":1000}

        // 돈 삽입 전, 기존 삽입된 화페 정보 호출 및 저장
        rs = exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "GET", currVendingID, currTimeStamp, "%", "NULL");
        while (rs.next()) {
            JSONObject j = new JSONObject();
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String s = rs.getMetaData().getColumnLabel(i);
                j.put(s, rs.getString(i));
            }
            certainMoney.put(j); // JSON 배열에 JSON 오브젝트 삽입
        }

        // 입력된 화페 처리
        for (int i = 0; i < insertedMoney.length(); i++) {
            if(insertedMoney.getJSONObject(i).getInt("use") != 0) { // 삽입된 화페의 총액 계산
                total += insertedMoney.getJSONObject(i).getInt("use") * insertedMoney.getJSONObject(i).getInt("price");
            }


        }

        // 현재 화페 보유액 변경
        exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?", "SET", currVendingID, currTimeStamp, "NULL", (rs.getString("id") + "|" + currVendingID + "|" + total));
        
        // 결과 처리
        return returnSeq("", "success");
    }

    ResultSet exeQuery(Connection conn, String query, String ... str) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        for(int i=0; i<str.length; i++){
            ps.setString(i+1, str[i]);
        }
        return ps.executeQuery();
    } // 쿼리 실행 및 결과 반환 함수

    String returnSeq(String errMSG, String type) throws JSONException {
        System.out.println(errMSG);

        JSONObject obj = new JSONObject();
        obj.put("status", type);
        obj.put("data", new JSONObject());

        classification.setValue(4, obj.toString());
        String msg = classification.toString();
        System.out.println("[전송]: " + msg);

        return msg;
    } // 리턴 메세지 출력 및 반환
}
