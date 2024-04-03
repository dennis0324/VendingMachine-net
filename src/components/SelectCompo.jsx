function SelectCompo({
  onLeft = () => {},
  onRight = () => {},
  left = "확인",
  right = "취소",
  leftColor = "bg-red-400",
  rightColor = "bg-gray-500",
}) {
  return (
    <section className="grid grid-cols-2 gap-2 text-nowrap m-2">
      <button
        className={leftColor + " p-3 rounded-lg"}
        onClick={() => onLeft()}
      >
        {left}
      </button>
      <button className={rightColor + " p-3 rounded-lg"} onClick={onRight}>
        {right}
      </button>
    </section>
  );
}

export default SelectCompo;
