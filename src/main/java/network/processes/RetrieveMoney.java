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

public class RetrieveMoney extends Processing {

    public RetrieveMoney(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - retrieveMoney");
        
        // 변수
        ResultSet rs;                                                         // SQL 데이터 테이블 결과값의 저장을 위한 변수
        JSONArray currMoneyInfo = new JSONArray();                            // 현재 작업 중인 자판기 화폐 별 보윺량
        JSONArray returnData    = new JSONArray();                            // 클라이언트에게 반환할 payload
        String currVendingID    = classification.getValue(2);           // 현재 작업 중인 자판기 ID
        String currTimeStamp    = classification.getValue(3);           // 현재 작업 중인 자판기 동작 시간
        StringBuilder valueData = new StringBuilder();                        // 쿼리에서 사용할 value data
        int[] retrieveMoneyUnitQty = new int[5];                              // 거스를 화페 단위 별 수량
        int[] moneyUnit = {10, 50, 100, 500, 1000};                           // 화폐 단위
        int remainMoney = 0;                                                  // 거스름돈
        int TBL_id = 0;                                                       // machinecertainmoneyTBL 고유 번호

        try { // 사용자의 잔여금 불러오기
            rs = exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "SPE", currVendingID, currTimeStamp, "NULL", "NULL");
            while(rs.next()) {
                remainMoney = rs.getInt("price");
                TBL_id = rs.getInt("id");
            }
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error", new JSONArray());
        }

        try { // 자판기의 화폐 보유 정보 불러오기
            rs = exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "GET", currVendingID, currTimeStamp, "%", "NULL");
            while(rs.next()) {
                JSONObject obj = new JSONObject();
                // 각 행에서 모든 열의 데이터를 가져와서 출력
                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    obj.put(rs.getMetaData().getColumnLabel(i), rs.getString(i));
                }
                currMoneyInfo.put(obj); // JSON 배열에 JSON 오브젝트 삽입
            }
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error", new JSONArray());
        }

        // 거스를 화폐의 개수 확인 및 반환 데이터 생성
        for(int i = 4; i >= 0; i--) {
            if(remainMoney / moneyUnit[i] > currMoneyInfo.getJSONObject(i).getInt("qty")) {
                retrieveMoneyUnitQty[i] = currMoneyInfo.getJSONObject(i).getInt("qty");
            } else {
                retrieveMoneyUnitQty[i] = remainMoney / moneyUnit[i];
            }
            remainMoney -= moneyUnit[i] * retrieveMoneyUnitQty[i];
            
            // 반환할 payload 데이터 생성
            JSONObject tmpJson = new JSONObject();
            tmpJson.put("qty", retrieveMoneyUnitQty[i]);
            tmpJson.put("price", moneyUnit[i]);
            returnData.put(tmpJson);
        }

        if (remainMoney != 0) { // 계산이 올바른지 확인
            returnSeq("[에러]: 거스를 화폐가 부족/계산 오류", "deny", new JSONArray());
        }

        for (int i = 0; i < 5; i++) { // 쿼리에 적용할 value data 작성
            valueData.append(currVendingID).append("|");
            valueData.append(i+1).append("|");
            valueData.append(moneyUnit[i]).append("|");
            valueData.append(currMoneyInfo.getJSONObject(i).getInt("qty")-retrieveMoneyUnitQty[i]);
            valueData.append("=");
        } valueData.deleteCharAt(valueData.length()-1); // 마지막 제품 구분자 삭제

        try { // 각 테이블에 쿼리 적용
            exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "SET", currVendingID, currTimeStamp, "NULL", (TBL_id + "|" + currVendingID + "|" + remainMoney));
            exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "SUB", currVendingID, currTimeStamp, "NULL", String.valueOf(valueData));
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error", new JSONArray());
        }
    
        // 결과 반환
        return returnSeq("[알림]: 처리 중", "success", returnData);
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
