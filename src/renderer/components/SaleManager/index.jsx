import * as React from "react";
import { useState } from "react";
import ButtonCompo from "../ButtonCompo";

const money = [10, 50, 100, 500, 1000];
const testData = [
  {
    price: 10,
    qty: 10,
  },
  {
    price: 50,
    qty: 10,
  },
  {
    price: 100,
    qty: 10,
  },
  {
    price: 500,
    qty: 10,
  },
  {
    price: 1000,
    qty: 10,
  },
];
function SaleManager() {
  const [moneyData, setMoneyData] = useState(testData);
  function sendGetMoney() {}
  function collectMoneyAll() {
    window.machine.collectMoney(
      Array.from({ length: moneyData.length }, (_, i) => i + 1),
    );
  }
  function collectMoney(priceIndex) {
    window.machine.collectMoney([priceIndex]);
  }
  return (
    <section className="col-span-2 h-full flex flex-col justify-around">
      {/* <button onClick={sendGetMoney}>test</button> */}
      {moneyData.map((item, idx) => {
        return (
          <div
            className="p-0.5 m-2 bg-gray-400 rounded-lg grid grid-cols-3 items-center"
            key={"saleItem-" + idx}
          >
            <span className="text-center">{item.price}원</span>

            <input
              disabled={true}
              defaultValue={`${item.qty}개`}
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
