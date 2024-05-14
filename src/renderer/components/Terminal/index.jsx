import * as React from "react";
import { useEffect, useState } from "react";
import IconHelper from "../IconHelper";
import More from "../../icons/more.svg";

function TerminalLog() {
  const [log, setLog] = useState([]);

  function goToLogs() {
    window.location.hash = "/logs";
  }

  async function getLogs() {
    const date = new Date();
    console.log(date.getFullYear(), date.getMonth(), date.getDate());
    const { status, data } = await window.machine.getLogs("%", "%", "%", 20);
    console.log(data);
    if (status === "success") setLog(data);
  }
  useEffect(() => {
    getLogs();
  }, []);

  function opType(optype) {
    switch (Number(optype)) {
      case 1:
        return "판매";
      case 2:
        return "보충";
    }
  }

  return (
    <div className="w-[50vw] h-[70vh] bg-white shadow-lg rounded-lg overflow-auto p-3">
      <section className="flex justify-between">
        <div className=""></div>
        <div className="cursor-pointer m-0.5">
          <IconHelper onClick={goToLogs}>
            <More className="cursor-pointer" />
          </IconHelper>
        </div>
      </section>
      {log.map((item) => (
        <div className="grid grid-cols-6 p-1 my-3 rounded-lg bg-gray-100">
          <span className="col-span-2">{item.time.split(" ")[0]}</span>
          <span className="col-span-2">{item.name}</span>
          <span className="col-span-1">{item.qty}개</span>
          <span className="col-span-1">{opType(item.opType)}</span>
        </div>
      ))}
    </div>
  );
}

export default TerminalLog;
