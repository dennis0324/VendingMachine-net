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

public class Product extends Processing {

    public Product(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - products");

        // 변수
        ResultSet rs;                                   // SQL 데이터 테이블 결과값의 저장을 위한 변수
        PreparedStatement ppst;                         // SQL 처리를 위한 오브젝트
        JSONArray productInfo = new JSONArray();        // JSON 타입 배열

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        try {
            ppst = conn.prepareStatement("CALL MACHINE_PRODUCT(?, ?, ?, ?, ?)");
            ppst.setString(1, "GET");
            ppst.setString(2, classification.getValue(2));
            ppst.setString(3, classification.getValue(3));
            ppst.setString(4, "%");
            ppst.setString(5, "NULL");
            rs = ppst.executeQuery();
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
            productInfo.put(obj); // JSON 배열에 JSON 오브젝트 삽입
        }

        return returnSeq("[알림]: 처리 중", "success", productInfo);
    }

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
