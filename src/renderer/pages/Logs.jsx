import * as React from "react";
import { useState } from "react";
import PopupManager from "../components/PopupManager";
import IconHelper from "../components/IconHelper";
import ArrowBack from "../icons/arrowBackIos.svg";

function Logs() {
  function goToAdmin() {
    window.location.hash = "/admin";
  }
  const [year, setYear] = useState("%");
  const [month, setMonth] = useState("%");
  const [date, setDate] = useState("%");
  const [limit, setLimit] = useState(10);
  const [logs, setLogs] = useState([]);

  async function getLogs() {
    const { status, data } = await window.machine.getLogs(
      year,
      month,
      date,
      limit,
    );
    if (status === "success") setLogs(data);
  }

  function opType(optype) {
    switch (Number(optype)) {
      case 1:
        return "보충";
      case 2:
        return "판매";
    }
  }
  return (
    <div className="App h-screen w-screen flex flex-col">
      <PopupManager />
      <div className="flex items-center justify-between">
        <IconHelper className="flex cursor-pointer m-2" onClick={goToAdmin}>
          <ArrowBack />
          {/* <span>돌아가기</span> */}
        </IconHelper>
        <div className="flex">
          <div className={"flex"}></div>
        </div>
      </div>
      <div className="flex flex-1 flex-row max-sm:flex-col max-sm:py-0 justify-center items-center">
        <section className="max-sm:h-full max-sm:flex max-sm:flex-col max-sm:justify-between max-sm:mb-0 m-6 w-full grid grid-cols-8 justify-items-center items-center h-4/5">
          <div className="col-span-6 col-start-2 h-full w-full">
            <div className="flex justify-between">
              <div>
                <input
                  placeholder="Year"
                  className="w-12 mx-2"
                  onChange={(e) =>
                    setYear(e.target.value === "" ? "%" : e.target.value)
                  }
                />
                <input
                  placeholder="Month"
                  className="w-12 mx-2"
                  onChange={(e) =>
                    setMonth(e.target.value === "" ? "%" : e.target.value)
                  }
                />
                <input
                  placeholder="Date"
                  className="w-12 mx-2"
                  onChange={(e) =>
                    setDate(e.target.value === "" ? "%" : e.target.value)
                  }
                />
                <input
                  placeholder="Limit"
                  className="w-12 mx-2"
                  onChange={(e) => setLimit(e.target.value)}
                />
              </div>
              <button onClick={getLogs}>Search</button>
            </div>
            <div className="bg-gray-100 rounded-lg w-full h-full scroll-auto p-3">
              {logs.map((item, index) => (
                <div
                  className="grid grid-cols-6 p-1 my-3 rounded-lg bg-gray-300"
                  key={"Log-" + index}
                >
                  <span className="col-span-2">{item.time}</span>
                  <span className="col-span-2">{item.name}</span>
                  <span className="col-span-1">{item.qty}개</span>
                  <span className="col-span-1">{opType(item.opType)}</span>
                </div>
              ))}
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}

export default Logs;
