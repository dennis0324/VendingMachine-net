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
import java.util.Objects;

public class Handshake extends Processing {

    public Handshake(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws JSONException {
        System.out.println("[알림]: CMD 코드 - handshake");

        // 변수
        String newVendingID = String.valueOf(classification.getValue(4)); // 현재 작업 중인 자판기 ID
        String oldVendingID = String.valueOf(classification.getValue(2)); // 현재 작업 중인 자판기가 보유 중인 ID

        if (!Objects.equals(oldVendingID, "")) return returnSeq("[알림]: 클라이언트 재연결 감지", "deny", "");

        try { // 쿼리 실행 - 기존의 번호를 사용하던 기기의 DB 내역 삭제
            exeQuery(conn, "CALL MACHINE_REMOVE(?)", newVendingID);
        } catch (SQLException e) { // 예외 처리
            System.out.println("[경고]: 더미 데이터 삭제 실패");
        }

        try { // 쿼리 실행 - 접근한 자판기 번호의 DB 내역 초기화
            exeQuery(conn, "CALL MACHINE_INIT(?, ?)", newVendingID, "null");
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error", "");
        }

        // 결과 처리
        return returnSeq("[알림]: 처리 중", "success", newVendingID);
    }

    static ResultSet exeQuery(Connection conn, String query, String... str) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        for(int i=0; i<str.length; i++){
            ps.setString(i+1, str[i]);
        }
        return ps.executeQuery();
    } // 쿼리 실행 및 결과 반환 함수

    String returnSeq(String MSG, String type, String number) throws JSONException {
        System.out.println(MSG);

        JSONObject obj = new JSONObject();
        obj.put("status", type);
        obj.put("data", new JSONObject());

        classification.setValue(2, number);
        classification.setValue(4, obj.toString());
        String msg = classification.toString();
        System.out.println("[전송]: " + msg);

        return msg;
    } // 리턴 메세지 출력 및 반환
}
