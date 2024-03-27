function SelectCompo({
  onLeft = () => {},
  onRight = () => {},
  left = "확인",
  right = "취소",
}) {
  return (
    <section className="grid grid-cols-2 gap-2 text-nowrap m-2">
      <button className="bg-red-400 p-3 rounded-lg" onClick={() => onLeft()}>
        {left}
      </button>
      <button className="bg-gray-500 p-3 rounded-lg" onClick={onRight}>
        {right}
      </button>
    </section>
  );
}

export default SelectCompo;
