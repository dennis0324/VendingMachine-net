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

public class Purchase extends Processing {

    public Purchase(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - purchase");

        // 변수
        PreparedStatement ppst;                             // SQL 처리를 위한 오브젝트
        ResultSet rs;                                       // SQL 데이터 테이블 결과값의 저장을 위한 변수

        JSONArray list  = new JSONArray();                  // 제품 리스트를 저장할 JSON ARRAY
        JSONArray arr   = (JSONArray)payload.get();         // payload 의 JSON ARRAY 타입 데이터 저장 변수
        JSONObject obj1 = new JSONObject();                 // JSON OBJECT 임시 변수
        JSONObject obj2 = new JSONObject();                 // JSON OBJECT 임시 변수

        String currVendingID = classification.getValue(2); // 현재 작업 중인 자판기 ID
        String tmp, receiveMSG;                             // 임시 변수, 반환 메세지

        // 구매 전 제품 리스트 확보
        ppst = conn.prepareStatement("CALL GET_PRODUCT(?)");
        ppst.setString(1, currVendingID);
        rs = ppst.executeQuery();

        // 제품 리스트를 배열에 저장
        while (rs.next()) { // 각 행에서 모든 열의 데이터를 가져와서 출력
            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                tmp = rs.getMetaData().getColumnLabel(i);
                obj1.put(tmp, rs.getString(i));
            }
            list.put(obj1);
        }

        // System.out.println(arr); // 디버그
        obj1 = new JSONObject();    // 초기화

        // 구매를 요청한 제품의 ID가 현재 제공되는 제품의 정보와 일치한지 비교
        for(int i = 0; i < list.length(); i++) {                                // 입고된 제품 리스트 반복 비교
            obj1 = (JSONObject) list.get(i);
            for(int j = 0; j < arr.length(); j++) {                             // 구매 요청된 제품 리스트 반복 비교
                obj2 = (JSONObject) arr.get(j);
                if(obj1.get("productId") == obj2.get("productId")) {            // 동일 제품 ID일 경우
                    System.out.println("pass id");
                    if(obj1.get("name") == obj2.get("name")) {                  // 같은 제품인지 (change 에 의한 제품 변경 고려)
                        System.out.println("pass name");
                        if(obj1.getInt("qty") >= obj2.getInt("qty")) {    // 제품 발주량과 재고량 비교 후, 쿼리 작성 및 실행
                            System.out.println("pass qty");
                            try { // cmd, vending id, product id, tbl name, value data
                                tmp = currVendingID + "|" +    // 테이블 수정 사항 작성 예시: 0000|1|고급 커피|700|3|6
                                      obj2.get("productId") + "|" +
                                      obj2.get("name") + "|" +
                                      obj2.get("price") + "|" +
                                      ((int)obj1.get("qty") - (int)obj2.get("qty")) + "|" + // 재고량에서 발주량만큼 차감
                                      obj2.get("qty_limit");

                                System.out.println(tmp); // 디버그

                                ppst = conn.prepareStatement("CALL EXE_PRODUCT(?, ?, ?, ?, ?)");
                                ppst.setString(1, "SET");                    // SET | GET
                                ppst.setString(2, currVendingID);               // vending ID
                                ppst.setInt   (3, (int)obj1.get("productId"));  // 1 ~ 6
                                ppst.setString(4, "MachineItemTbl");         // item table
                                ppst.setString(5, tmp);                         // corrections
                                ppst.executeQuery();
                                
                            } catch (SQLException e) {
                                System.out.println("[에러]: 쿼리 적용 실패");
                            }
                        } else { // 발주량이 재고량보다 많을 경우
                            System.out.println("[경고]: 주문 대응 불가 - 재고 부족[" + obj1.get("name") + "]");
                        }
                    } else {    // 입고된 제품 ID의 제품명과 요청한 제품 ID의 제품명이 다를 경우
                        System.out.println("[경고: 주문 대응 불가 - 제품 불일치[요청 제품:" + obj2.get("name") + ", 실제 제품: " + obj1.get("name") + "]");
                    }
                }
            }
            obj1 = new JSONObject();
            obj2 = new JSONObject();
        }
        
        // 결과 처리
        obj2.put("status", "success");
        obj2.put("data", new JSONObject());

        classification.setValue(4, obj2.toString());
        receiveMSG = classification.toString();
        System.out.println("[전송]: " + receiveMSG);

        return receiveMSG;
    }
}


//            obj1.put("status", "error");
//            obj1.put("data", new JSONObject());