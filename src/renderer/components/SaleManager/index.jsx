import * as React from "react";
import { useContext } from "react";
import ButtonCompo from "../ButtonCompo";

import { MoneyContext } from "../MoneyProvider";

function SaleManager() {
  const { moneyData, getMoney } = useContext(MoneyContext);
  async function collectMoneyAll() {
    const { status } = window.machine.collectMoney(
      Array.from({ length: moneyData.length }, (_, i) => i + 1),
    );
    if (status === "success") getMoney();
  }
  async function collectMoney(priceIndex) {
    const { status } = await window.machine.collectMoney([priceIndex]);
    if (status === "success") {
      getMoney();
    }
  }
  return (
    <section className="col-span-2 h-full flex flex-col justify-around">
      {moneyData.map((item, idx) => {
        return (
          <div
            className="p-0.5 m-2 bg-gray-400 rounded-lg grid grid-cols-3 items-center"
            key={"saleItem-" + idx}
          >
            <span className="text-center">{item.price}원</span>

            <input
              disabled={true}
              value={`${item.qty}개`}
              className="text-right mx-1"
            />
            {/* <span className="text-center">{item.qty}개</span> */}
            <button
              className="bg-red-400 rounded-lg p-0.5"
              onClick={(e) => collectMoney(idx + 1)}
            >
              {" "}
              수금{" "}
            </button>
          </div>
        );
      })}
      <ButtonCompo message="모두 수금" onClick={collectMoneyAll} />
    </section>
  );
}

export default SaleManager;
