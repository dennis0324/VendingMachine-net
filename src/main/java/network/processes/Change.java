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

import java.util.Objects;

public class Change extends Processing {

    public Change(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - change (Admin only)");

        // 변수
        ResultSet rs;
        JSONArray changeInfo  = (JSONArray)payload.get();            // 변경할 제품 정보
        JSONObject concatData = new JSONObject();                    // 통합 반환 데이터
        JSONArray beforeData  = new JSONArray();                     // 클라이언트에게 반환할 payload before 데이터
        StringBuilder value   = new StringBuilder();                 // 쿼리에서 사용할 value data
        String[] productOrder = {"id", "productId", "name", "price", "qty", "qty_limit"}; // JSON Object 키값 정렬 순서(product)
        String currVendingID  = classification.getValue(2);    //
        String currTimeStamp  = classification.getValue(3);    //

        try { // 제품 테이블 내용 불러오기
            rs = exeQuery(conn, "CALL MACHINE_PRODUCT(?, ?, ?, ?, ?)", "GET", currVendingID, currTimeStamp, "%", "NULL");
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error", new JSONObject());
        }

        while (rs.next()) {
            // 변수
            String productID = rs.getString("productId");
            String[] tmpInfo = new String[productOrder.length];
            for (int i = 0; i < changeInfo.length(); i++) {
                if (Objects.equals(changeInfo.getJSONObject(i).getString("productId"), productID)) {
                    JSONObject obj = new JSONObject();
                    // 각 행에서 모든 열의 데이터를 가져와서 출력
                    for(int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
                        obj.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
                    }
                    beforeData.put(obj); // JSON before 배열에 JSON 오브젝트 삽입

                    tmpInfo[0] = currVendingID;
                    tmpInfo[1] = productID;
                    tmpInfo[2] = changeInfo.getJSONObject(i).getString("name");
                    tmpInfo[3] = changeInfo.getJSONObject(i).getString("price");
                    tmpInfo[4] = changeInfo.getJSONObject(i).getString("qty");
                    tmpInfo[5] = "6";

                    for (String s : tmpInfo) { value.append(s).append("|"); }
                    value.deleteCharAt(value.length()-1); // 마지막 제품 구분자 삭제
                    value.append("=");
                }
            }
        }
        value.deleteCharAt(value.length()-1); // 마지막 제품 구분자 삭제

        System.out.println(value); // 디버그

        try { // 결과 처리
            exeQuery(conn, "CALL MACHINE_PRODUCT(?, ?, ?, ?, ?)", "ADD", currVendingID, currTimeStamp, "NULL", value.toString());
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error", new JSONObject());
        }

        concatData.put("before", beforeData);
        concatData.put("after",changeInfo);

        return returnSeq("[알림]: 처리 중", "success", concatData);
    }

    ResultSet exeQuery(Connection conn, String query, String ... str) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        for(int i=0; i<str.length; i++){
            ps.setString(i+1, str[i]);
        }
        return ps.executeQuery();
    } // 쿼리 실행 및 결과 반환 함수

    String returnSeq(String MSG, String type, JSONObject data) throws JSONException {
        System.out.println(MSG);

        JSONObject obj = new JSONObject();
        obj.put("status", type);
        obj.put("data", data);

        classification.setValue(4, obj.toString());
        String msg = classification.toString();
        System.out.println("[전송]: " + msg);

        return msg;
    } // 리턴 메세지 출력 및 반환
}
