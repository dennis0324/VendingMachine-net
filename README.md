# 네트워크 프로그래밍 - 텀프로젝트  
 - Update: 14/06/2024   
 - 고동현([@dennisKo](https://github.com/dennis0324))
 - 박민수([@mindsubak](https://github.com/minsubak))
 ## 개요   
 - [설치하는 방법](#설치)   
 - [초기세팅 설정](#설정)
 - [구동하는 방법](#구동)   

 ## 설치  
  
해당 프로그램을 개발/테스트하면서 사용한 프로그램에 상세 스펙입니다.
```
OS: Ubuntu Server 20.04 LTS or Later  
Docker: 20.10.24 or Later (API 1.41)  
docker-compose: 2.27.0 or Later  
```

하단의 명령어를 통해 파일을 받으실 수 있습니다.
```
git clone github.com/dennis0324/VendingMachine-net.git
```

 ## 설정

 프로그램을 구동하기 전 파일의 수정이 필요합니다. (.template 태그는 제거해야 합니다.)
 - DockerfileMySQL.template
```
FROM mysql:8                                    #MySQL 8버전 이미지 호출

#데이터베이스 생성 및 초기화 & MySQL 초기 설정 스크립트를 컨테이너에 복사
COPY MySQL-init.sql /docker-entrypoint-initdb.d/init.sql
COPY MySQL-config.cnf /etc/mysql/my.cnf

                                                #기본 환경 변수 지정
ENV MySQL_DATABASE [DB_NAME]                    #예시) VendingMachine
ENV MYSQL_ROOT_PASSWORD [MYSQL_ROOT_PW]         #예시) root

#set port
EXPOSE 00000                                    #예시) 6124  
```  
   
 - DockerfileOpenJDK.template
```
FROM openjdk:17-jdk                             #OpenJDK 17버전 이미지 호출

COPY OpenJDK-server.jar server.jar              #서버를 구성하는 jar 파일을 복사

                                                # 기본 환경 변수 지정
#예시) jdbc:mysql://mysql:3306/VendingMachine?useUnicode=true&characterEncoding=utf8
ENV MYSQL_HOST_ADDRESS [DB_HOST_ADDRESS]
ENV MYSQL_DATABASE_ID [DB_USER_ID]              #예시) vending
ENV MYSQL_DATABASE_PW [DB_USER_PW]              #예시) vending1234
ENV MYSQL_ADMINISTRATOR [DB_ADMIN_ID]           #예시) admin

EXPOSE 00000                                    #예시) 3306
ENTRYPOINT [ "java", "-jar", "/server.jar" ]    #예시) jar 파일 실행
```

 ## 구동
``` bash
# 해당 폴더 이동
cd VendingMachine-net
# 백엔드 & DB 서버 실행
docker-compose up -d
# 서버 종료
docker-compose down
```
 ## 코드
   [client](https://github.com/dennis0324/VendingMachine-net/tree/client-electron-fix)  
   [server](https://github.com/dennis0324/VendingMachine-net/tree/server)
