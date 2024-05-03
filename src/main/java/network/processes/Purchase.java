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

        JSONObject jo = new JSONObject();
        JSONArray tmp = (JSONArray)payload.get();

        int productID = 1;

        System.out.println(tmp);

        String corrections = "null";
        //(String)payload.get("data");     // purchase 코드에 맞는 payload 데이터 변환 필요

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        try {
            PreparedStatement ppst = conn.prepareStatement("CALL EXE_PRODUCT(?, ?, ?, ?, ?)");
            // cmd, vending id, product id, tbl name, tbl data
            ppst.setString(1, "SET"); // SET | GET
            ppst.setString(2, (String) classification.getValue(2)); // 0000 ~
            ppst.setInt(3, productID);    // 1 ~ 6
            ppst.setString(4, "MachineItemTbl");    // NULL 값 사용 가능
            ppst.setString(5, corrections);    // NULL 값 사용 가능

            // 쿼리 실행
            ResultSet rs = ppst.executeQuery();

            // 결과 처리
            jo.put("status", "success");
            jo.put("data", "");
        } catch (SQLException e) {
            jo.put("status", "error");
            jo.put("data", "");
        }
        classification.setValue(4, jo.toString());
        String receiveMSG = classification.toString();
        System.out.println("[전송]: " + receiveMSG);

        return receiveMSG;
    }
}
