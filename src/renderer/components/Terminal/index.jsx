import * as React from "react";

const dummyData = [
  {
    date: new Date().toLocaleTimeString(),
    cmd: "purchase",
    data: [
      {
        name: "물",
        qty: 3,
      },
      {
        name: "커피",
        qty: 3,
      },
    ],
  },
  {
    date: new Date().toLocaleTimeString(),
    cmd: "restock",
    data: [
      {
        name: "물",
        qty: 2,
      },
      {
        name: "커피",
        qty: 4,
      },
    ],
  },
];

function TerminalLog() {
  const [log, setLog] = React.useState(dummyData);
  return (
    <div className="w-[50vw] h-[70vh] bg-white shadow-lg rounded-lg">
      {log.map((item) => (
        <div>
          <span>{item.date}</span>
          <span>{item.cmd}</span>
          {item.data.map((item) => {
            return (
              <div>
                <span>{item.name}</span>
                <span>{item.qty}개</span>
              </div>
            );
          })}
        </div>
      ))}
    </div>
  );
}

export default TerminalLog;
