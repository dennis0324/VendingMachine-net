import * as React from 'react';
import SelectCompo from "../SelectCompo";
import ButtonCompo from "../ButtonCompo";
import { useMemo, useState } from "react";

import { getCookie, setCookie } from "../../utils/cookies";
import { removePopup } from "../PopupManager";

import { TEXT } from '../../utils/constants';

const moneyOptions = [[10], [50], [100], [500], [1000, 5]];
const LIMIT = 7000;
function MoneySelector() {
  let cookie = getCookie("money");

  if (!cookie) {
    cookie = Array.from({ length: 5 }, (_, i) => ({
      use: 0,
      realLimit: moneyOptions[i][1] || 0, //실제 제한 개수
      price: moneyOptions[i][0],
    }));
    setCookie("money", cookie);
  }

  const [moneyCount, setMoneyCount] = useState(cookie);

  // 투입한 동전의 개수를 증가시킨다.
  function increase(index) {
    const money = moneyCount.at(index);
    if (money.realLimit != 0 && money.use >= money.realLimit) return;
    if (total + money.price > LIMIT) return;
    money.use += 1;
    setMoneyCount([...moneyCount]);
  }

  // 거스름 반환
  function clear() {
    moneyCount.forEach((e) => (e.use = 0));
    setMoneyCount([...moneyCount]);
    setCookie("money", moneyCount);
  }

  // 저장시 사용되는 함수
  function ReturnWithPaper() {
    //TODO: 서버에서 잔돈 개수 구하고 받아오기
    setCookie("money", moneyCount);
    removePopup();
  }
  function ReturnWithoutPaper() {
    setCookie("money", moneyCount);
    removePopup();
  }
  function confirm() {
    const finded = moneyCount.filter(e => e.use > 0);
    if(finded.length == 0) {
      alert(TEXT.NOSELECT);
      removePopup();
      return;
    }
    window.machine.insertMoney(moneyCount.map(e => ({use:e.use,price:e.price})))
    removePopup();
  }

  // total computed
  const total = useMemo(() => {
    const total = moneyCount.reduce((acc, cur, index) => {
      acc += cur.price * cur.use;
      return acc;
    }, 0);

    return total;
  }, [moneyCount]);

  return (
    <div>
      <section className="flex bg-gray-300 rounded-lg p-4">
        <span className="flex-1 flex items-center">총 투입 금액</span>
        <span className="flex items-center">{total}원</span>
      </section>
      <section>
        <div className="grid grid-cols-3 items-center py-2">
          {moneyOptions.map((option, index) => (
            <div className="items-center py-2 px-2" key={'moneyOptions-'+index}>
              <button
                disabled={false}
                className="bg-gray-300 p-2 rounded-lg w-full"
                onClick={() => increase(index)}
              >
                {option[0]}원
              </button>
            </div>
          ))}
          <ButtonCompo message='반환하기' onClick={clear} color='bg-red-400'/>
        </div>
      </section>
      <section className="grid">
        <ButtonCompo message="투입하기" onClick={confirm} />
      </section>
    </div>
  );
}

export default MoneySelector;
