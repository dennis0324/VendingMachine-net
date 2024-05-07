import * as React from "react";
import { useEffect, useState } from "react";
import { Money } from "../../utils/constants";

function MoneyIndicator({ className, active = true }) {
  const [moneyList, setMoneyList] = useState([]);
  useEffect(() => {
    //TODO: 여기서 돈 재고 확인
    async function getMoney() {
      const result = await window.machine.getMoney();
      if (result.status === "success") {
        setMoneyList(result.data);
      }
    }
    getMoney();
  }, []);
  return (
    <div className={"" + className}>
      <div className="grid max-sm:grid-cols-3 grid-cols-5 ">
        {Money.map((e, i) => (
          <div className="p-2 bg-gray-400 m-2 rounded-lg grid grid-cols-2 items-center justify-items-center">
            <span className="text-nowrap">{e[0]}원</span>
            <div
              className={
                "rounded-full w-3 h-3 grid " +
                ((moneyList[i]?.qty ?? 0) > 0 ? "bg-cyan-500" : "bg-red-400")
              }
            ></div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default MoneyIndicator;

