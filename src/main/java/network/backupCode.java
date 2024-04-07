////import java.io.*;
////import java.net.ServerSocket;
////import java.net.Socket;
////import java.util.ArrayList;
////import java.util.Collections;
////import java.util.List;
////
////public class ServerControl {
////
////    // 클라이언트와의 연결을 유지하기 위한 PrintWriter 목록
////    static List<PrintWriter> list =
////            Collections.synchronizedList(new ArrayList<PrintWriter>());
////
////    public static void main(String[] args) {
////        ServerSocket serverSocket = null;
////        try {
////            // 서버 소켓 생성 및 포트 6666으로 설정
////            serverSocket = new ServerSocket(6666);
////            System.out.println("[알림]: 서버 시작");
////
////            while (true) {
////                // 클라이언트의 연결 대기
////                System.out.println("[알림]: 새 클라이언트 접근 대기");
////                Socket clientSocket = serverSocket.accept();
////
////                // 클라이언트와 입출력을 위한 BufferedReader 및 PrintWriter 생성
////                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
////                PrintWriter clientInput = new PrintWriter(clientSocket.getOutputStream(), true);
////
////                // 클라이언트로부터 받은 데이터 출력
////                String data = input.readLine();
////                System.out.println("[알림]: 새 연결 생성 - 클라이언트 코드(" + data + ")");
////
////                // 현재 재고 데이터 전송
////                sendAll("현재 재고 데이터 전송 파트");
////
////                // 연결된 클라이언트의 PrintWriter를 목록에 추가
////                list.add(clientInput);
////
////                // 클라이언트와의 통신을 위한 새로운 스레드 생성
////                new Thread(() -> {
////                    try {
////                        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
////                        String inputMsg;
////                        while ((inputMsg = clientReader.readLine()) != null) {
////                            // 클라이언트로부터 메시지 수신
////                            if ("quit".equals(inputMsg)) {
////                                // 클라이언트가 종료 메시지를 보낸 경우
////                                break;
////                            }
////                            // 모든 클라이언트에게 메시지 전송
////                            sendAll(data + " >> " + inputMsg);
////                        }
////                    } catch (IOException e) {
////                        // 클라이언트와의 연결이 끊긴 경우
////                        System.out.println("[알림]: 연결 끊김 - 클라이언트 코드(" + data + ")");
////                    } finally {
////                        // 클라이언트와의 연결 해제 알림 전송 및 PrintWriter 목록에서 제거
////                        sendAll("[알림]: 연결 해제 - 클라이언트 코드(" + data + ")");
////                        list.remove(clientInput);
////                        try {
////                            // 클라이언트 소켓 닫기
////                            clientSocket.close();
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                }).start();
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        } finally {
////            // 서버 소켓 닫기
////            if (serverSocket != null) {
////                try {
////                    serverSocket.close();
////                    System.out.println("[알림]: 서버 종료");
////                } catch (IOException e) {
////                    e.printStackTrace();
////                    System.out.println("[알림]: 소켓 에러");
////                }
////            }
////        }
////    }
////
////    // 모든 클라이언트에게 메시지를 전송하는 메서드
////    private static void sendAll(String s) {
////        for (PrintWriter out : list) {
////            out.println(s);
////            out.flush();
////        }
////    }
////}
//
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class ServerControl {
//
//    // 클라이언트와의 연결을 유지하기 위한 PrintWriter 목록
//    static List<PrintWriter> list = Collections.synchronizedList(new ArrayList<PrintWriter>());
//
//    static int clientStackCounter = 0;
//
//    public static void main(String[] args) {
//
//        ServerSocket serverSocket = null;
//
//        try {
//            // 서버 소켓 생성 및 포트 6666으로 설정
//            serverSocket = new ServerSocket(6666);
//            System.out.println("[알림]: 서버 시작");
//
//            while (true) {
//                // 클라이언트의 연결 대기
//                System.out.println("[알림]: 새 클라이언트 접근 대기");
//                Socket clientSocket = serverSocket.accept();
//                System.out.println("[알림]: 새 연결 생성 - 클라이언트 코드(" + clientStackCounter++ + ")");
//
//                // 클라이언트와 입출력을 위한 BufferedReader 및 PrintWriter 생성
//                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                PrintWriter clientInput = new PrintWriter(clientSocket.getOutputStream(), true);
//
//                // 클라이언트로부터 받은 데이터 출력
//                String data = input.readLine();
//                System.out.println(data);
//
//                // 현재 재고 데이터 전송
//                sendAll("현재 재고 데이터 전송 파트");
//
//                // 연결된 클라이언트의 PrintWriter를 목록에 추가
//                list.add(clientInput);
//
//                // 클라이언트와의 통신을 위한 새로운 스레드 생성
//                new Thread(() -> {
//                    try {
//                        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                        String inputMsg;
//                        while ((inputMsg = clientReader.readLine()) != null) {
//                            // 클라이언트로부터 메시지 수신
//                            if ("quit".equals(inputMsg)) {
//                                // 클라이언트가 종료 메시지를 보낸 경우
//                                break;
//                            }
//                            // 모든 클라이언트에게 메시지 전송
//                            sendAll(data + " >> " + inputMsg);
//                        }
//                    } catch (IOException e) {
//                        // 클라이언트와의 연결이 끊긴 경우
//                        System.out.println("[알림]: 연결 끊김 - 클라이언트 코드(" + data + ")");
//                    } finally {
//                        // 클라이언트와의 연결 해제 알림 전송 및 PrintWriter 목록에서 제거
//                        sendAll("[알림]: 연결 해제 - 클라이언트 코드(" + data + ")");
//                        list.remove(clientInput);
//                        try {
//                            // 클라이언트 소켓 닫기
//                            clientSocket.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            // 서버 소켓 닫기
//            if (serverSocket != null) {
//                try {
//                    serverSocket.close();
//                    System.out.println("[알림]: 서버 종료");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("[알림]: 소켓 에러");
//                }
//            }
//        }
//    }
//
//    // 모든 클라이언트에게 메시지를 전송하는 메서드
//    private static void sendAll(String s) {
//        for (PrintWriter out : list) {
//            out.println(s);
//            out.flush();
//        }
//    }
//}
