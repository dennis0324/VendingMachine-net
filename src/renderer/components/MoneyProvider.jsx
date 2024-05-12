import * as React from "react";
import { createContext, useState, useEffect, useMemo } from "react";

export const MoneyContext = createContext();

export const MONEY_LIMIT = 7000;
function MoneyProvider({ children }) {
  const [moneyData, setMoneyData] = useState([]);

  async function getMoney() {
    const { status, data } = await window.machine.getMoney();
    if (status === "success") {
      data.forEach((e) => {
        e.price = Number(e.price) || 0;
        e.qty = Number(e.qty) || 0;
        e["realLimit"] = Number(e.qty) || e.qty;
        e["use"] = 0;
      });
      console.log(data);
      setMoneyData(data);
    }
  }
  useEffect(() => {
    getMoney();
  }, []);

  const total = useMemo(() => {
    const total = moneyData.reduce((acc, cur, index) => {
      acc += cur.price * cur.use;
      return acc;
    }, 0);

    return total;
  }, [moneyData]);

  function increaseMoney(index) {
    const money = moneyData.at(index);
    if (money.realLimit != 0 && money.use >= money.realLimit) return;
    if (total + money.price > MONEY_LIMIT) return;
    money.use += 1;
    setMoneyData([...moneyData]);
  }

  function clearMoney() {
    moneyData.forEach((e) => (e.use = 0));
    setMoneyData([...moneyData]);
  }

  const provideValue = {
    moneyData,
    increaseMoney,
    total,
    clearMoney,
    getMoney,
  };
  return (
    <MoneyContext.Provider value={provideValue}>
      {children}
    </MoneyContext.Provider>
  );
}

export default MoneyProvider;
