# Socket

### `handshake`
handshake||""

#### return
handshake|vendingmc-1|""
products|vendingmc-1|""

### `products`
cmd       |                  id| payload
products|vendingmc-1|""
#### return
```json
{
	total:[
	{
		name: string,
		price: number,
		qty: number
	},...
	] 
}
```

### `supply`

#### return
```json
{
	name:string,
	qty: number
}
```

### `Emit`
### `soldout`
``` json
{
	name:string
}
```

### `login`
```
{
	jwt
}
```

#### return
```json
{
	status:string|number
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
