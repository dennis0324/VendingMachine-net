## Prerequisites:
```
npm > 21
```
### 환경 변수
```
HOST=[IP ADDRESS] # xxx.xxx.xxx.xxx
PORT=[PORT] # 6124
```

## 빌드 방법
### 필요 모듈 다운
```bash
npm i
```

### 빌드 없이 실행
```bash
npm run start
```

### 빌드
```bash
npm run make 
npm run make --template win64
```


### 설치

`./out/make/x64/client-electron-1.0.0 Setup.exe` 실행시
`C:\Users\sindo\AppData\Local\client_electron`에 설치됨

### 사용가능 명령어

#### -n

```bash
npm run make -- -- -n # 빌드 하지 않고 실행할 경우
./app-1.0.0/client-electron -- -n # 빌드후 실행할 경우
```

