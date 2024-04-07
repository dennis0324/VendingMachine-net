# Socket

## \<class\> ipcDto 구조

| cmd | hash | vendingId | date | payload |
| --- | ---- | --------- | ---- | ------- |
|     |      |           |      |         |
- cmd: 각 실행 명령어
- hash: date + cmd + vendingId 합친 hash값(소켓 분류 목적)
- vendingId : 서버측에서 지정해준 id
- date: 명령어 전송 시간
- payload: 전송 데이터

## \<class\> productDto 구조
```json
{
	name:string,
	price:number,
	qty:number
}
```
## Socket Cmd 목록
cmd에 기본적으로 아무것도 적혀 있지 않은 내용은 클라이언트 -> 서버 -> 클라이언트로 다시 돌아오는 형태이다.

`x` : 데이터 없음
`-`: 코드에 의해서 알아서 넣어지는 임의 값
`boardcast`: 연결되어 있는 모든 소켓에게 보내는 신호
### 1.`handshake` (서버 구현 완료)

| cmd       | hash | payload | date | vendingId |
| --------- | ---- | ------- | ---- | --------- |
| handshake | -    | x       | -    | x         |

#### return

| cmd       | hash | vendingId | date | payload |
| --------- | ---- | --------- | ---- | ------- |
| handshake | -    | -         | -    | x       |
ex)
`handshake|66590df4e101c556c20608fd20c1ddf80c474324|vendingmc-1|`

### 2.`products`
| cmd      | hash | vendingId | date | payload |
| -------- | ---- | --------- | ---- | ------- |
| products | -    | -         | -    | x       |
#### return
| cmd      | hash | vendingId | date | payload |
| -------- | ---- | --------- | ---- | ------- |
| products | -    | -         | -    | 아래 참조   |
```json
{
	data:Array<proaductDto>
}
```


### 3.`supply`

| cmd    | hash | vendingId | date | payload |
| ------ | ---- | --------- | ---- | ------- |
| supply | -    | -         | -    | x       |

#### return
| cmd    | hash | vendingId | date | payload |
| ------ | ---- | --------- | ---- | ------- |
| supply | -    | -         | -    | 아래 참조   |
```json
{
	status:"success|error|deny",
	data:""
}
```
### 4.`change`(admin)

| cmd    | hash | vendingId | date | payload |
| ------ | ---- | --------- | ---- | ------- |
| supply | -    | -         | -    | 아래참조    |
```json
{
	productDto[]
}
```
#### return
| cmd    | hash | vendingId | date | payload |
| ------ | ---- | --------- | ---- | ------- |
| change | -    | -         | -    | 아래 참조   |
```json
{
	status:"success|error|deny",
	data:[
		{
			before:productDto,
			after:productDto
		}
	]
}
```
### 5.`soldout`
| cmd     | hash | vendingId | date | payload |
| ------- | ---- | --------- | ---- | ------- |
| soldout | -    | -         | -    | 아래 참조   |
``` json
{
	name:string
}
```
#### return
| cmd     | hash | vendingId | date | payload |
| ------- | ---- | --------- | ---- | ------- |
| soldout | -    | -         | -    | 아래 참조   |
```json
{
	status:"success|error|deny",
	data:""
}
```
### 6.`insertMoney`

| cmd         | hash | vendingId | date | payload |
| ----------- | ---- | --------- | ---- | ------- |
| insertMoney | -    | -         | -    | 아래 참조   |
```json
{
	price:number,
	qty:number
}
```
#### return
| cmd         | hash | vendingId | date | payload |
| ----------- | ---- | --------- | ---- | ------- |
| insertMoney | -    | -         | -    | 아래 참조   |

```json
{
	status:"success|error|deny",
	data:""
}
```

### 7.`retrieveMoney`

| cmd           | hash | vendingId | date | payload |
| ------------- | ---- | --------- | ---- | ------- |
| retrieveMoney | -    | -         | -    | x       |

#### return
| cmd           | hash | vendingId | date | payload |
| ------------- | ---- | --------- | ---- | ------- |
| retrieveMoney | -    | -         | -    | 아래 참조   |

```json
{
	status:"success|error|deny",
	data:[
		{
			price:number,
			qty:number
		}
	]
}
```

### 8.`login`

| cmd   | hash | vendingId | date | payload |
| ----- | ---- | --------- | ---- | ------- |
| login | -    | -         | -    | 아래 참조   |
```json
{
	id:string,
	password:string
}
```
note) password는 sha256로 암호화되고 base64로 코딩되어 보내집니다.
#### return
| cmd   | hash | vendingId | date | payload |
| ----- | ---- | --------- | ---- | ------- |
| login | -    | -         | -    | 아래 참조   |
```json
{
	status:"success|error|deny",
	data:""
}
```

### 9..`collectMoney`(admin)

| cmd          | hash | vendingId | date | payload |
| ------------ | ---- | --------- | ---- | ------- |
| collectMoney | -    | -         | -    | x       |


#### return
| cmd          | hash | vendingId | date | payload |
| ------------ | ---- | --------- | ---- | ------- |
| collectMoney | -    | -         | -    | 아래 참조   |

```json
{
	status:"success|error|deny",
	data:""
}
```



# Table
## DB: VendingMachine

### TBL:
- ConstandVarTb
처음 자판기 초기화시에 사용할 데이터이다.

| name  |     |     | 상품 이름    |
| ----- | --- | --- | -------- |
| price |     |     | 상품 가격    |
| qty   |     |     | 상품 재고 개수 |

- CredentialTbl
사용자의 비밀번호와 아이디를 담고 있다.

| idx      | seq,pk |     | 인덱스      |
| -------- | ------ | --- | -------- |
| id       | pk     |     | 사용자 아이디  |
| password |        |     | 사용자 비밀번호 |

- MachineTbl
자판기의 id와 이름을 담고 있다.

| id   | pk  |             | 자판기 ID |
| ---- | --- | ----------- | ------ |
| name |     | default('') | 자판기 이름 |

- MachineItemTbl

| id    | pk,fk(MachineTbl) |     | 자판기 ID   |
| ----- | ----------------- | --- | -------- |
| name  |                   |     | 상품 이름    |
| price |                   |     | 상품 가격    |
| qty   |                   |     | 상품 재고 개수 |

- VendingLogTbl

| id      | pk,fk(MachineTbl) |     | 자판기 ID           |
| ------- | ----------------- | --- | ---------------- |
| code    |                   |     | 판매 / 재고 플래그(0/1) |
| date    |                   |     | 구매 / 재고 기록 날짜    |
| pdtName |                   |     | 상품 이름            |
| qty     |                   |     | 수량               |


# 전달 과정 및 내용

App(renderer)
- machine
	- purchase
	- login


WoorkerPool       worker
ipcMain(data) -> worker.postMessage(data)


worker
send