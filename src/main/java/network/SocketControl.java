package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class SocketControl {

    // 클라이언트와의 연결을 유지하기 위한 PrintWriter 목록
    static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    static int Counter = 0;

    public static void main(String[] args) {

        ServerSocket serverSocket = null;

        try {
            // 서버 소켓 생성 및 포트 6666으로 설정
            serverSocket = new ServerSocket(6124);
            System.out.println("[알림]: 서버 시작");

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
                    System.out.println("[알림]: 소켓 에러");
                }
            }
        }
    }

    // 클라이언트 처리를 담당하는 내부 클래스
    static class ClientHandler implements Runnable {
        private Socket clientSocket;        // 클라이언트 오브젝트
        private PrintWriter clientWriter;   // 소켓 채팅 오브젝트
        boolean debug_mode = false;         // 디버그 모드
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
            String[] sqlData = new String[3];
            String sql = null;

            try {
                InputStream inputStream = clientSocket.getInputStream();
                final InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(streamReader);

                try {
                    // 파일 호출
                    FileReader fileReader = new FileReader("src/main/rsc/mysql.txt");
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    String tmpLine;
                    int i = 0;
                    while((tmpLine = bufferedReader.readLine()) != null) {
                        sqlData[i++] = tmpLine;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Connection connection = null;
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;

                try {
                    // MySQL 연결
                    connection = DriverManager.getConnection(sqlData[0], sqlData[1], sqlData[2]);

                    String line = null;
                    while ((line = br.readLine()) != null) {
                        // 전송받은 데이터 출력
                        System.out.println(line);

                        // 데이터 구분자 분류
                        String[] classification = line.split("\\|");

                        if (debug_mode) { // 디버그 출력
                            for (String s : classification) {
                                System.out.println("[디버그-데이터 구분자 확인]: " + s);
                            }
                        }

                        // 전송받은 메세지 분리
                        String dataSeparation[] = extractStrings(classification[4]);

                        if (debug_mode) { // 디버그 출력
                            for (String s : dataSeparation) {
                                System.out.println("[디버그-페이로드 분리 확인]: " + s);
                            }
                        }

                        // CMD | HASH | ID | DATE | PAYLOAD
                        // 메세지 명령 분류 영역
                        switch (classification[0]) {
                            case "handshake":
                                System.out.println("[알림]: CMD 코드 - handshake");
                                receiveMSG = classification[0] + "|" + "|" + String.format("%04d", Counter++)+ "|" + classification[3] + "|" + "\"\"";
                                sendToClient(receiveMSG);
                                System.out.println("[전송]: " + receiveMSG);

                                break;

                            case "products":
                                System.out.println("[알림]: CMD 코드 - products");
                                break;

                            case "supply":
                                System.out.println("[알림]: CMD 코드 - supply");
                                break;

                            case "change":
                                System.out.println("[알림]: CMD 코드 - change");
                                break;

                            case "soldout":
                                System.out.println("[알림]: CMD 코드 - soldout");
                                break;

                            case "insertMoney":
                                System.out.println("[알림]: CMD 코드 - insertMoney");
                                break;

                            case "retrieveMoney":
                                System.out.println("[알림]: CMD 코드 - retrieveMoney");
                                break;

                            case "login":
                                System.out.println("[알림]: CMD 코드 - login");

                                // 쿼리 준비
                                sql = "CALL GET_LOGIN_INFO('admin')"; // MySQL 저장 프로시저 호출
                                preparedStatement = connection.prepareStatement(sql);

                                // 쿼리 실행 및 결과 가져오기
                                resultSet = preparedStatement.executeQuery();

                                // 결과 처리
                                while (resultSet.next()) {
                                    // 각 행에서 모든 열의 데이터를 가져와서 출력
                                    ResultSetMetaData metaData = resultSet.getMetaData();

                                    if (debug_mode) { // 디버그 출력
                                        int columnCount = metaData.getColumnCount();
                                        for (int i = 1; i <= columnCount; i++) {
                                            Object value = resultSet.getObject(i);
                                            System.out.print(value + "\t");
                                        }
                                    }

                                    String compareTarget = resultSet.getString(3);

                                    if(Objects.equals(dataSeparation[1], compareTarget)) {
                                        sendToClient("[알림]: 로그인 성공");
                                        receiveMSG = classification[0] + "|" + classification[1] + "|" + classification[2] + "|" + classification[3] +
                                                "|{\"status\":\"success\",\"data\":\"\"}";
                                        sendToClient(receiveMSG);
                                        System.out.println("[전송]: " + receiveMSG);
                                    }
                                    else {
                                        sendToClient("[알림]: 로그인 실패");
                                        receiveMSG = classification[0] + "|" + classification[1] + "|" + classification[2] + "|" + classification[3] +
                                                "|{\"status\":\"deny\",\"data\":\"\"}";
                                        sendToClient(receiveMSG);
                                        System.out.println("[전송]: " + receiveMSG);
                                    }

                                }
                                break;

                            case "collectMoney":
                                System.out.println("[알림]: CMD 코드 - collectMoney");
                                break;

                            case "purchase":
                                System.out.println("[알림]: CMD 코드 - purchase");
                                break;

                            case "getMoney":
                                System.out.println("[알림]: CMD 코드 - getMoney");
                                break;

                            default:
                                System.out.println("[에러]: CMD 코드 - Unknown CMD Code");
                                break;
                        }

                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    // 리소스 해제
                    try {
                        if (resultSet != null) resultSet.close();
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

        // 해당 클라이언트에게 메시지 전송하는 메소드
        public void sendToClient(String message) {
            clientWriter.println(message);
        }
    }

    public static String[] extractStrings(String input) {
        ArrayList<String> extractStrings = new ArrayList<>();

        // 정규 표현식을 사용하여 데이터 구조에서 값만 추출
        Pattern pattern = Pattern.compile("\"\\w+\":\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(input);

        // 매칭된 문자열을 추출하여 동적으로 저장
        while (matcher.find()) {
            extractStrings.add(matcher.group(1));
        }

        // ArrayList를 배열로 변환
        String[] dataArray = new String[extractStrings.size()];
        return extractStrings.toArray(dataArray);
    }

//    public static String decodeBase64(String encodedString) {
//        // Base64 디코드하기 전에 패딩 추가
//        int paddingLength = encodedString.length() % 4;
//        if (paddingLength > 0) {
//            StringBuilder paddedString = new StringBuilder(encodedString);
//            for (int i = 0; i < 4 - paddingLength; i++) {
//                paddedString.append("=");
//            }
//            encodedString = paddedString.toString();
//        }
//
//        // Base64 디코드
//        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
//        // 바이트 배열을 문자열로 변환
//        return new String(decodedBytes);
//    }
}
