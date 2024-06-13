package network;

import network.processes.*;

import com.sun.tools.javac.Main;

import java.io.*;
import java.sql.*;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;

import static network.SocketControl.clientHandlers;

public class ThreadSocketControl {

    static boolean debugFlag = true;
    /* 디버그 플래그 */

    public static void main(String[] args) {

        System.out.println("[알림]: 디버그 켜짐");

        // 두 개의 서버를 각각 다른 포트에서 실행
        Thread server1Thread = new Thread(new ServerThread(6125));
        Thread server2Thread = new Thread(new ServerThread(6126));

        // 서버 스레드 시작
        server1Thread.start();
        server2Thread.start();

        try { // 클라이언트 연결을 분산하는 로드 밸런서 역할
            ServerSocket loadBalancerSocket = new ServerSocket(6124);
            System.out.println("[알림]: 로드 밸런서 서버 시작 중 - 포트: 6124");

            while (true) {
                Socket clientSocket = loadBalancerSocket.accept();

                // 로드 밸런서가 짝수 포트로 접속한 클라이언트는 첫 번째 서버로,
                // 홀수 포트로 접속한 클라이언트는 두 번째 서버로 보냄
                int serverPort = clientSocket.getPort() % 2 == 0 ? 6125 : 6126;

                // 해당 포트로 클라이언트 소켓 전달
                Socket serverSocket = new Socket("localhost", serverPort);
                ClientForwarder forwarder = new ClientForwarder(clientSocket, serverSocket);
                forwarder.start();
            }
        } catch (IOException e) {
            exceptionMSG(e, "[에러]: 로드 밸런서 동작 중 에러 발생");
        }
    }

    static class ServerThread implements Runnable {
        int port;
        int num;
        public ServerThread(int port) { this.port = port; }

        @Override
        public void run() {
            try {
                if (port == 6125) num = 1;
                else num = 2;
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("[알림]: " + num + "번 서버 시작 중 - 포트: " + port);

                while (true) { // 클라이언트 수신 대기 및 접속 처리
                    Socket clientSocket         = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    Thread clientThread         = new Thread(clientHandler);

                    System.out.println("[알림|" + num + "번 서버]: 클라이언트 접속 - " + clientSocket);
                    clientThread.start();
                }
            } catch (IOException e) {
                exceptionMSG(e, "[에러|" + num + "번 서버]: 진행 중 에러 발생");
            }
        }
    }

    static class ClientForwarder extends Thread {
        private final Socket clientSocket;
        private final Socket serverSocket;

        public ClientForwarder(Socket clientSocket, Socket serverSocket) {
            this.clientSocket = clientSocket;
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try { // 클라이언트로부터 받은 메시지를 서버로 전달
                new Thread(new Forwarder(clientSocket.getInputStream(), serverSocket.getOutputStream())).start();
                // 서버로부터 받은 응답을 클라이언트로 전달
                new Thread(new Forwarder(serverSocket.getInputStream(), clientSocket.getOutputStream())).start();
            } catch (IOException e) {
                exceptionMSG(e, "[에러|" + serverSocket.getPort() + "]: 클라이언트-서버 간 통신 중 에러 발생");
            }
        }
    }

    static class Forwarder implements Runnable {
        private final InputStream input;
        private final OutputStream output;

        public Forwarder(InputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    output.flush();
                }
            } catch (IOException e) {
                System.out.println("[알림]: 클라이언트 연결 끊김");
            }
        }
    }

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
            FileReader fileReader = new FileReader("src/main/resources/mysql.txt");
            BufferedReader br = new BufferedReader(fileReader);

            /* sql 로그인 데이터 처리 */
            while((s = br.readLine()) != null) { sqlData[i++] = s; }

        } catch (IOException e) { // 예외 처리
            exceptionMSG(e, "[경고]: 파일 탐색 실패");

            try { // Jar 내에서 파일 읽어오기
                InputStream inputStream = Main.class.getResourceAsStream("/data/mysql.txt");
                if(inputStream == null) {
                    System.out.println("[경고]: Jar 내 파일 탐색 실패");
                    return null;
                }

                /* sql 로그인 데이터 처리 */
                BufferedReader br2 = new BufferedReader(new InputStreamReader(inputStream));
                while((s = br2.readLine()) != null) { sqlData[i++] = s; }
                
            } catch (IOException ex) { // 예외 처리
                exceptionMSG(ex, "[에러]: Jar 내부 파일 탐색 실패");
            }

        } return sqlData;
    }

    static void exceptionMSG(IOException e, String msg) {
        if(debugFlag) e.printStackTrace();
        System.out.println(msg);
    }
}
