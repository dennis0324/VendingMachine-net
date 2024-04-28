package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.*;

import network.processes.*;
import org.json.JSONException;
import org.json.JSONObject;

public class SocketControl {

    static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    // 클라이언트와의 연결을 유지하기 위한 PrintWriter 목록
    static int counter = 0;
    // 클라이언트 카운터

    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        try {
            // 서버 소켓 생성 및 포트 6124으로 설정
            serverSocket = new ServerSocket(6124);
            System.out.println("[알림]: 서버 시작");

            String[] sqlData = getConfig();
            try {
                Connection firstConn = DriverManager.getConnection(sqlData[0], sqlData[1], sqlData[2]);
                PreparedStatement ps = firstConn.prepareStatement("CALL REMOVE_ALL_MACHINE()");
                ps.executeQuery();
                System.out.println("[알림]: 서버 초기화");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("[경고]: 종료 중 에러 발생");
            }
            while (true) {
                // 클라이언트의 연결 대기
                Socket clientSocket = serverSocket.accept();
                System.out.println("[알림]: 클라이언트 접속 - " + clientSocket);

                // 클라이언트와의 통신을 담당하는 새로운 클라이언트 핸들러 생성
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 서버 소켓 닫기
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    System.out.println("[알림]: 서버 종료");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("[경고]: 소켓 에러 발생");
                }
            }
        }
    }

    // 클라이언트 처리를 담당하는 내부 클래스
    static class ClientHandler implements Runnable {
        private Socket clientSocket;        // 클라이언트 오브젝트
        private PrintWriter clientWriter;   // 소켓 채팅 오브젝트
        public String receiveMSG = null;    // 회신 메세지 변수

        public ClientHandler(Socket clientSocket) { // 클라이언트 핸들러
            this.clientSocket = clientSocket;
            try {
                this.clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            // MySQL 접속 정보
            String[] sqlData = getConfig();
            String sql = null;
            PreparedStatement preparedStatement = null;
            Connection connection = null;

            try {
                // 클라이언트 메세지 변수
                InputStream inputStream = clientSocket.getInputStream();
                final InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(streamReader);

                try {
                    // MySQL 연결
                    connection = DriverManager.getConnection(sqlData[0], sqlData[1], sqlData[2]);

                    String line = null;
                    boolean addExec = false;
                    while ((line = br.readLine()) != null) {
                        // 데이터 구분자 분류
                        String[] classification = line.split("\\|");
                        Classification classData = new Classification(line);
                        try {
                            if (!addExec) { // 쿼리 추가 준비/실행
                                preparedStatement = connection.prepareStatement("CALL ADD_MACHINE(?, ?)");
                                preparedStatement.setString(1, classification[2]);
                                preparedStatement.setString(2, "null");
                                preparedStatement.executeQuery();
                                addExec = true;
                            }
                        } catch (SQLIntegrityConstraintViolationException e) {
                            System.out.println("[알림]: " + classification[2] + "번 머신 건너뜀");
                        }


                        // CMD | HASH | ID | DATE | PAYLOAD
                        // 메세지 명령 분류 영역
                        try {
                            switch (classification[0]) {
                                case "handshake":
                                    String tmp = String.format("%04d", counter++); // vending ID 부여
                                    Handshake handshake = new Handshake(classData, tmp);
                                    sendToClient(handshake.run(new Payload(classData.getValue(4))));
                                    break;

                                case "products":
                                    Product product = new Product(connection, sqlData, classData);
                                    sendToClient(product.run(new Payload(classData.getValue(4))));
                                    break;

                                case "supply":
                                    Supply supply = new Supply(connection, sqlData, classData);
                                    sendToClient(supply.run(new Payload(classData.getValue(4))));
                                    break;

                                case "change":
                                    Change change = new Change(connection, sqlData, classData);
                                    sendToClient(change.run(new Payload(classData.getValue(4))));
                                    break;

                                case "soldout":
                                    Soldout soldout = new Soldout(connection, sqlData, classData);
                                    sendToClient(soldout.run(new Payload(classData.getValue(4))));
                                    break;

                                case "insertMoney":
                                    InsertMoney insertMoney= new InsertMoney(connection, sqlData, classData);
                                    sendToClient(insertMoney.run(new Payload(classData.getValue(4))));
                                    break;

                                case "retrieveMoney":
                                    RetrieveMoney retrieveMoney = new RetrieveMoney(connection, sqlData, classData);
                                    sendToClient(retrieveMoney.run(new Payload(classData.getValue(4))));
                                    break;

                                case "login":
                                    Login login = new Login(connection, sqlData, classData);
                                    sendToClient(login.run(new Payload(classData.getValue(4))));
                                    break;

                                case "collectMoney":
                                    CollectMoney collectMoney = new CollectMoney(connection, sqlData, classData);
                                    sendToClient(collectMoney.run(new Payload(classData.getValue(4))));
                                    break;

                                case "purchase":
                                    Purchase purchase = new Purchase(connection, sqlData, classData);
                                    sendToClient(purchase.run(new Payload(classData.getValue(4))));
                                    break;

                                default:
                                    System.out.println("[에러]: CMD 코드 - Unknown CMD Code");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try { // 리소스 해제
                        if (preparedStatement != null) preparedStatement.close();
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("[알림]: 클라이언트 연결 끊김 - " + clientSocket);
                clientSocket.close();
                clientHandlers.remove(this);

            } catch (IOException e) {
                // 클라이언트와의 연결이 끊긴 경우
                System.out.println("[알림]: 클라이언트 연결 끊김 - " + clientSocket);
            } finally {
                // 클라이언트와의 연결 해제
                clientHandlers.remove(this);
                try {
                    // 클라이언트 소켓 닫기
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendToClient(String message) {
            // 해당 클라이언트에게 메시지 전송하는 메소드
            clientWriter.println(message);
        }
    }

    public static JSONObject extractStrings(String input) {

        JSONObject jo = null;
        try { jo = new JSONObject(input);
        } catch (JSONException e) {
            e.printStackTrace();
        } return jo;
    }

    public static String[] getConfig() {

        String[] sqlData = new String[4];
        try {
            // 파일 호출
            FileReader fileReader = new FileReader("src/main/rsc/mysql.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // sql 접근 데이터 처리
            String tmpLine;
            int i = 0;
            while((tmpLine = bufferedReader.readLine()) != null) {
                sqlData[i++] = tmpLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sqlData;
    }
}