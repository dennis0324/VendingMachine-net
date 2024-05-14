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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Supply extends Processing {

    public Supply(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws SQLException, JSONException {
        System.out.println("[알림]: CMD 코드 - supply (admin only)");

        // 변수
        ResultSet rs;
        JSONArray productInfo = new JSONArray();
        String currVendingID = classification.getValue(2);            // 현재 작업 중인 자판기 ID
        String currTimeStamp = classification.getValue(3);            // 현재 작업 중인 자판기 동작 시간
        String currPayload   = classification.getValue(4);            // 현재 작업 중인 자판기에서 보낸 payload

        int[] result = extractNumbers(currPayload); // string to int array

        try { // DB 에서 Product TBL 정보 불러오기
            rs = exeQuery(conn, "CALL MACHINE_PRODUCT(?, ?, ?, ?, ?)", "GET", currVendingID, currTimeStamp, "%", "NULL");
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return returnSeq("[에러]: 쿼리 실행 실패", "error");
        }

        while(rs.next()) { // JSON 배열에 불러온 데이터 저장
            JSONObject obj = new JSONObject();
            // 각 행에서 모든 열의 데이터를 가져와서 출력
            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                obj.put(rs.getMetaData().getColumnLabel(i), rs.getString(i));
            }
            productInfo.put(obj); // JSON 배열에 JSON 오브젝트 삽입
        }

        try { // 제품 초기화
            for (int i = 0; i < result.length; i++) {
                JSONObject tmpJson = new JSONObject();
                for (int j = 0; j < productInfo.length(); j++) {
                    if(productInfo.getJSONObject(j).getInt("productId") == result[i]) tmpJson = productInfo.getJSONObject(j);
                }
                StringBuilder valueData = new StringBuilder();

                valueData.append(currVendingID).append("|");
                valueData.append(result[i]).append("|");
                valueData.append(tmpJson.getString("name")).append("|");
                valueData.append(tmpJson.getString("price"));
                valueData.append("|6|6");

                exeQuery(conn, "CALL MACHINE_PRODUCT(?, ?, ?, ?, ?)", "ADD", currVendingID, currTimeStamp, "NULL", String.valueOf(valueData));
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
            ps.setString(i + 1, str[i]);
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

    public static int[] extractNumbers(String s) {
        // 변수
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(s);
        int count = 0, index = 0;

        while (matcher.find()) { count++; } // 숫자 개수 세기

        int[] numbers = new int[count];     // 결과를 담을 배열 생성
        matcher.reset();                    // Matcher 초기화
        while (matcher.find()) { numbers[index++] = Integer.parseInt(matcher.group()); } // 숫자 추출하여 배열에 저장

        return numbers;
    } // 정규표현식을 사용하여 문자열에서 숫자 추출
}
