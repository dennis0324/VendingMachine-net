package network.processes;

import network.Classification;
import network.Payload;
import network.Processing;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

public class Purchase extends Processing {

    public Purchase(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - purchase");

        // 변수
        ResultSet rs;                                                                      // SQL 데이터 테이블 결과값의 저장을 위한 변수
        JSONArray requestOrder = (JSONArray)payload.get();                                 // payload 의 JSON ARRAY 타입 데이터 저장 변수
        String currVendingID   = classification.getValue(2);                         // 현재 작업 중인 자판기 ID
        String currTimeStamp   = classification.getValue(3);                         // 현재 작업 중인 자판기 동작 시간
        String[] productOrder  = {"id", "productId", "name", "price", "qty", "qty_limit"}; // JSON Object 키값 정렬 순서(product)
        StringBuilder concatProduct = new StringBuilder();                                 // 쿼리문에 사용할 value data 변수
        int payment = 0; // 지불 금액

        // 구매 전 DB 에서 제품 리스트 불러오기
        rs = exeQuery(conn, "CALL MACHINE_PRODUCT(?, ?, ?, ?, ?)", "GET", currVendingID, currTimeStamp, "%", "NULL");

        // 요청 사항과 테이블 데이터 비교 후 쿼리에 보낼 value data 작성
        while (rs.next()) { // 각 행에서 모든 열의 데이터를 가져와서 출력

            // 변수
            String productId = rs.getString("productId");   // product id
            int qty = rs.getInt("qty");                     // qty
            JSONObject tmpJson = new JSONObject();                     // JSON temp

            for(int i = 0; i < requestOrder.length(); i++) { // JSON 배열에 저장된 구매 목록과 제품 테이블 비교

                // JSON 배열에서 비교할 JSON Object 추출
                ArrayList<String> productInfoTmp = new ArrayList<>();
                JSONObject compareTarget = requestOrder.getJSONObject(i);
                Iterator keys = compareTarget.keys();
                while(keys.hasNext()) {
                    String key = (String)keys.next();
                    tmpJson.put(key, compareTarget.getString(key));
                } // Iterator 타입 배열에 비교할 JSON Object 의 key 값을 삽입

                for(String s : productOrder) productInfoTmp.add((String)tmpJson.get(s));
                // ArrayList를 JsonOrder 값에 맞게 정렬

                if(!Objects.equals(compareTarget.getString("productId"), productId))  continue;
                // 서로의 productID 값이 다를 경우 continue

                if(compareTarget.getInt("qty") > qty) {
                    return returnSeq("[경고]: 주문 대응 불가 - 재고 부족(" + compareTarget.getString("name") + ")\"", "deny");
                } else { // 요청한 제품의 개수가 DB 에서 보유한 제품 개수보다 많을 경우 실패
                    payment += compareTarget.getInt("qty") * compareTarget.getInt("price");
                    productInfoTmp.set(4, String.valueOf(qty - compareTarget.getInt("qty")));
                } // 제품 테이블의 수량에서 요청한 제품의 개수만큼 차감

                concatProduct.append(String.join("|", productInfoTmp)); // 속성값 별 구분자 추가
                concatProduct.append("=");                                      // 요청 제품 별 구분자 추가
            }
        }
        concatProduct.deleteCharAt(concatProduct.length()-1);             // 마지막 제품 구분자 삭제
        
        try { // 물건 판매 전 보유 잔액 확인 후 차감
            rs = exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "SPE", currVendingID, currTimeStamp, "NULL", "NULL");
            while(rs.next()) {
                if(rs.getInt("price") < payment) {
                    return returnSeq("[경고]: 주문 대응 불가 - 금액 부족(" + (payment - rs.getInt("price")) + "원 부족)", "deny");
                } else {
                    exeQuery(conn, "CALL MACHINE_MONEY(?, ?, ?, ?, ?)", "SET", currVendingID, currTimeStamp,"NULL", (rs.getString("id") + "|" + currVendingID + "|" + payment));
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error");
        }

        try { // 화페 차감 후 물건 차감 쿼리 실행
            exeQuery(conn, "CALL MACHINE_PRODUCT(?, ?, ?, ?, ?)", "SUB", currVendingID, currTimeStamp, "NULL", String.valueOf(concatProduct));
        } catch (SQLException e) {
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error");
        }
        // 결과 처리
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