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

public class Supply extends Processing {

    public Supply(Connection conn, String[] sqlData, Classification classification) {
        super(conn, sqlData, classification);
    }

    @Override
    public String run(Payload payload) throws JSONException {
        System.out.println("[알림]: CMD 코드 - supply");

        JSONObject jo = new JSONObject();
        int productID = (int)payload.get("productID");

        // 쿼리 준비 및 실행 그리고 결과 가져오기
        try {
            PreparedStatement ppst = conn.prepareStatement("CALL EXE_PRODUCT(?, ?, ?, ?, ?)");
            // cmd, vending id, product id, tbl name, tbl data
            ppst.setString(1, "SET"); // SET | GET
            ppst.setString(2, (String) classification.getValue(2)); // 0000 ~
            ppst.setInt(3, productID);    // 1 ~ 6
            ppst.setString(4, "MachineItemTbl");    // NULL 값 사용 가능
            ppst.setString(5, null);    // NULL 값 사용 가능
            
            // 쿼리 실행
            ResultSet rs = ppst.executeQuery();

            // 결과 처리
            jo.put("status", "success");
            jo.put("data", new JSONObject());
        } catch (SQLException e) {
            jo.put("status", "error");
            jo.put("data", new JSONObject());
        }
        classification.setValue(4, jo.toString());
        String receiveMSG = classification.toString();
        System.out.println("[전송]: " + receiveMSG);

        return receiveMSG;
    }
}
