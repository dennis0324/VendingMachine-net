package network;

import network.processes.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;

public class SocketControl {

    static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    /* 클라이언트와의 연결을 유지하기 위한 PrintWriter 목록 */
    static boolean debugFlag = false;
    /* 디버그 플래그 */
    // static HashMap<String, Socket> clientData = new HashMap<>();
    /* 클라이언트 접근 이력 버퍼 */

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);           // input stream

        System.out.println("[알림]: 디버그 여부 (y/N)");
        if(Objects.equals(scanner.nextLine(), "y")) {    // 디버그 여부 결정
            debugFlag = true;
            System.out.println("[알림]: 디버그 켜짐");
        }

        System.out.println("[알림]: 서버 시작 중");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(6124); // 서버 소켓 생성 및 포트 6124으로 설정

            while (true) { // 클라이언트 수신 대기 및 접속 처리
                Socket clientSocket         = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread         = new Thread(clientHandler);
                clientHandlers.add(clientHandler);

                System.out.println("[알림]: 클라이언트 접속 - " + clientSocket);
                clientThread.start();
            }
        } catch (IOException e) { // 예외 처리
            if(debugFlag) exceptionMSG(e, "[경고]: 진행 중 에러 발생");
        } finally { // 서버 소켓 종료
            if (serverSocket != null) {
                System.out.println("[알림]: 서버 종료");
                serverSocket.close();
            }
        }
    }

    // 클라이언트 처리를 담당하는 내부 클래스
    static class ClientHandler implements Runnable {

        private final Socket clientSocket;  // 클라이언트 오브젝트
        private PrintWriter clientWriter;   // 소켓 채팅 오브젝트
        static int counter = 0;             // 클라이언트 카운터

        public ClientHandler(Socket clientSocket) { // 클라이언트 핸들러

            this.clientSocket = clientSocket;
            try { // 소켓 채팅 오브젝트 초기화
                this.clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) { // 예외 처리
                if(debugFlag) exceptionMSG(e, "[경고]: 클라이언트 핸들러 에러 발생");
            }
        }

        @Override
        public void run() {

            try { // 클라이언트 입력 수신 준비 과정
                final InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader bufferedReader        = new BufferedReader(streamReader);
                Connection connection                = null;            // SQL Connection 데이터
                String[] sqlData                     = getConfig();     // MySQL 접속 정보
                String clientInput;                                     // 클라이언트로부터의 입력

                try { // MySQL 연결
                    connection = DriverManager.getConnection(sqlData[0], sqlData[1], sqlData[2]);
                    while ((clientInput = bufferedReader.readLine()) != null) {
                        // 데이터 구분자 분류
                        String[] classification = clientInput.split("\\|");
                        Classification classData = new Classification(clientInput);

                        try { // CMD 에 따른 작업 처리
                            switch (classification[0]) {
                                case "handshake": // 연결된 클라이언트에게 고유 번호 부여
                                    classData.setValue(4, String.format("%04d", counter++));
                                    Handshake handshake = new Handshake(connection, sqlData, classData);
                                    sendToClient(handshake.run(new Payload(classData.getValue(4))));
                                    break;

                                case "products": // 자판기 제품 리스트와 재고 현황 불러오기
                                    Product product = new Product(connection, sqlData, classData);
                                    sendToClient(product.run(new Payload(classData.getValue(4))));
                                    break;

                                case "change": // 자판기 제품 목록 및 재고 변경(운영자 전용)
                                    Change change = new Change(connection, sqlData, classData);
                                    sendToClient(change.run(new Payload(classData.getValue(4))));
                                    break;

                                case "changePassword": // 비밀번호 변경
                                    ChangePassword changePassword = new ChangePassword(connection, sqlData, classData);
                                    sendToClient(changePassword.run(new Payload(classData.getValue(4))));
                                    break;

                                case "insertMoney": // 화페 삽입
                                    InsertMoney insertMoney= new InsertMoney(connection, sqlData, classData);
                                    sendToClient(insertMoney.run(new Payload(classData.getValue(4))));
                                    break;

                                case "retrieveMoney": // 화페 반환
                                    RetrieveMoney retrieveMoney = new RetrieveMoney(connection, sqlData, classData);
                                    sendToClient(retrieveMoney.run(new Payload(classData.getValue(4))));
                                    break;

                                case "login": // 로그인
                                    Login login = new Login(connection, sqlData, classData);
                                    sendToClient(login.run(new Payload(classData.getValue(4))));
                                    break;

                                case "collectMoney": // 화폐 취합
                                    CollectMoney collectMoney = new CollectMoney(connection, sqlData, classData);
                                    sendToClient(collectMoney.run(new Payload(classData.getValue(4))));
                                    break;

                                case "supply": // 제품 재고 보급
                                    Supply supply = new Supply(connection, sqlData, classData);
                                    sendToClient(supply.run(new Payload(classData.getValue(4))));
                                    break;

                                case "purchase": // 구매
                                    Purchase purchase = new Purchase(connection, sqlData, classData);
                                    sendToClient(purchase.run(new Payload(classData.getValue(4))));
                                    break;

                                case "getMoney": // MACHINE_MONEY(GET) 반환
                                    GetMoney getMoney = new GetMoney(connection, sqlData, classData);
                                    sendToClient(getMoney.run(new Payload(classData.getValue(4))));
                                    break;

                                case "getLogs": // 자판기 로그 반환
                                    GetLogs getLogs = new GetLogs(connection, sqlData, classData);
                                    String s = getLogs.run(new Payload(classData.getValue(4)));
                                    sendToClient("length|" + s.length());
                                    sendToClient(s);
                                    break;

                                default: // 등록되지 않은 CMD 코드 예외 처리
                                    System.out.println("[에러]: CMD 코드 - Unknown CMD Code");
                                    break;
                            }

                        } catch (JSONException e) {
                            if(debugFlag) e.printStackTrace();
                            System.out.println("[경고]: 올바르지 않은 형태/타입의 CMD 코드");
                            System.out.print("CMD: ");
                            for(int i = 0; i < 5; i++)
                                System.out.print(classification[i] + "|");
                            System.out.println();

                        }
                    }
                } catch (SQLException e) {
                    if(debugFlag) e.printStackTrace();
                    System.out.println("[경고]: CMD 코드 처리 과정 중 예외 발생");

                } finally {
                    try { // 리소스 해제
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        if(debugFlag) e.printStackTrace();
                        System.out.println("[경고]: 리소스 해제 과정 중 예외 발생");
                    }
                }
                System.out.println("[알림]: 클라이언트 연결 끊김 - " + clientSocket);
                clientSocket.close();
                clientHandlers.remove(this);

            } catch (IOException e) { // 클라이언트와의 연결이 끊긴 경우
                System.out.println("[알림]: 클라이언트 연결 끊김 - " + clientSocket);
                try {
                    clientSocket.close();
                    clientHandlers.remove(this);
                } catch (IOException e2) {
                    exceptionMSG(e2, "[경고]: 소켓 종료 과정 중 예외 발생");
                }
            }
        }

        public void sendToClient(String message) {
            // 해당 클라이언트에게 메시지 전송하는 메소드
            clientWriter.println(message);
        }
    }

    public static String[] getConfig() {

        // 변수
        String[] sqlData = new String[4];
        String s;
        int i = 0;

        try { // DB 서버 연결 정보를 가진 파일 호출
            FileReader fileReader = new FileReader("src/main/rsc/mysql.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            /* sql 로그인 데이터 처리 */
            while((s = bufferedReader.readLine()) != null) { sqlData[i++] = s; }

        } catch (IOException e) { // 예외 처리
            exceptionMSG(e, "[경고]: 파일 호출 및 처리 과정 중 예외 발생");

        } return sqlData;
    }

    static void exceptionMSG(IOException e, String msg) {
        if(debugFlag) e.printStackTrace();
        System.out.println(msg);
    }
}