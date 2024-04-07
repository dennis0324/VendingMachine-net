import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerControl {

    // 클라이언트와의 연결을 유지하기 위한 PrintWriter 목록
    static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {

        ServerSocket serverSocket = null;

        try {
            // 서버 소켓 생성 및 포트 6666으로 설정
            serverSocket = new ServerSocket(6666);
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
        private Socket clientSocket;
        private PrintWriter clientWriter;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                final InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(streamReader);

                // readLine blocks until line arrives or socket closes, upon which it returns null
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
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
}
