function ButtonCompo({ onClick = () => {}, message = "확인" }) {
  return (
    <section className="grid grid-cols-1 gap-2 text-nowrap m-2">
      <button className="bg-gray-400 p-3 rounded-lg" onClick={() => onClick()}>
        {message}
      </button>
    </section>
  );
}

export default ButtonCompo;
