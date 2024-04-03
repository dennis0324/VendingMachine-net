import SelectCompo from "../SelectCompo";

function ConfirmPurchase(props) {
  const { onRight } = props;

  const onLeft = () => {};

  return (
    <div className="bg-gray-300 p-10 rounded-lg">
      <h2>정말로 구매하시겠습니까?</h2>
      <SelectCompo onLeft={onLeft} onRight={onRight} />
    </div>
  );
}

export default ConfirmPurchase;
