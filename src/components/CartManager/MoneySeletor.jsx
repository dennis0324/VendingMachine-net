function MoneySeletor({ money, setMoney }) {
  const moneyOptions = [10, 20, 50, 100, 200, 500];
  return (
    <div>
      {moneyOptions.map((option) => (
        <div>{option}</div>
      ))}
    </div>
  );
}

export default MoneySeletor;
