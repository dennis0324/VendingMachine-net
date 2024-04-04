const money = [10, 50, 100, 500, 1000];
function CashManger() {
  return (
    <div className="">
      <section className="flex flex-row flex-wrap justify-center items-center">
        <container>
          <span className="text-4xl">현재 투입 금액</span>
          <div className="text-4xl w-80">
            <span className="w-full">0</span>
            <span>원</span>
          </div>
        </container>
      </section>
      <section className="flex flex-row flex-wrap justify-center items-center">
        <container className="grid sm: lg:grid-cols-3 gap-3">
          {money.map((m, i) => (
            <div
              key={`money-${i}`}
              className={
                "text-2xl my-4 border-solid border-2 rounded-lg p-3 " +
                (m === 1000 ? "col-span-2" : "")
              }
            >
              {m}원
            </div>
          ))}
        </container>
      </section>
    </div>
  );
}

export default CashManger;
