import * as React from "react";
import { useContext } from "react";
import { MoneyContext } from "../MoneyProvider";

function MoneyIndicator({ className }) {
  const { moneyData } = useContext(MoneyContext);
  return (
    <div className={"" + className}>
      <div className="grid max-sm:grid-cols-3 grid-cols-5 ">
        {moneyData?.map((e, i) => (
          <div
            className="p-2 bg-gray-400 m-2 rounded-lg grid grid-cols-2 items-center justify-items-center"
            key={"moneyIndicate-" + i}
          >
            <span className="text-nowrap">{e.price}원</span>
            <div
              className={
                "rounded-full w-3 h-3 grid " +
                ((Number(e?.qty) ?? 0) > 0 ? "bg-cyan-500" : "bg-red-400")
              }
            ></div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default MoneyIndicator;
