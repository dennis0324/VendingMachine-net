import * as React from "react";
import ButtonCompo from "../ButtonCompo";
import { useContext } from "react";

import { removePopup, addPopup } from "../PopupManager";
import { MoneyContext } from "../MoneyProvider";

import { TEXT } from "../../utils/constants";
import ReturnPopup from "../ReturnPopup";

const moneyOptions = [[10], [50], [100], [500], [1000, 5]];
function MoneySelector() {
  const { moneyData, total, increaseMoney, clearMoney, getMoney } =
    useContext(MoneyContext);

  // 저장시 사용되는 함수
  async function ReturnWithPaper() {
    removePopup();
    const { status } = await window.machine.retrieveMoney();
    if (status === "success")
      addPopup(<ReturnPopup msg="잔돈을 반환합니다." onClick={removePopup} />);
    else
      addPopup(
        <ReturnPopup msg="잔돈을 반환할 수 없습니다." onClick={removePopup} />,
      );
    getMoney();
  }

  function confirm() {
    const finded = moneyData.filter((e) => e.use > 0);
    removePopup();
    if (finded.length == 0) {
      // alert(TEXT.NOSELECT);
      addPopup(<ReturnPopup msg={TEXT.NOSELECT} onClick={removePopup} />);
      return;
    }
    window.machine.insertMoney(
      moneyData.map((e) => ({ use: e.use, price: e.price })),
    );
    clearMoney();
    getMoney();
  }

  // total computed

  return (
    <div>
      <section className="flex bg-gray-300 rounded-lg p-4">
        <span className="flex-1 flex items-center">총 투입 금액</span>
        <span className="flex items-center">{total}원</span>
      </section>
      <section>
        <div className="grid grid-cols-3 items-center py-2">
          {moneyOptions.map((option, index) => (
            <div
              className="items-center py-2 px-2"
              key={"moneyOptions-" + index}
            >
              <button
                disabled={false}
                className="bg-gray-300 p-2 rounded-lg w-full"
                onClick={() => increaseMoney(index)}
              >
                {option[0]}원
              </button>
            </div>
          ))}
          <ButtonCompo
            message="반환하기"
            onClick={ReturnWithPaper}
            color="bg-red-400"
          />
        </div>
      </section>
      <section className="grid">
        <ButtonCompo message="투입하기" onClick={confirm} />
      </section>
    </div>
  );
}

export default MoneySelector;
