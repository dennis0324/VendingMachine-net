function MenuItem({ item }) {
  function onClickBuy() {
    alert(`${item.name} 해줘 `);
  }
  return (
    <div className="items h-fit flex flex-col justify-center items-center bg-gray-100 rounded-lg p-3">
      <span className="lg:text-2xl mb-3">
        {item.name}({item.qty})
      </span>
      <span className="lg:text-2xl mb-3">{item.price}원</span>
      <p
        className="lg:text-2xl text-nowrap bg-gray-400 rounded-lg p-5 mb-5"
        onClick={(e) => onClickBuy(e)}
      >
        구매하기
      </p>
    </div>
  );
}

export default MenuItem;

// 음료수 6개
// water 450, coffee 500, energydrink 550, premiumcoffee 700, coke 750, specialdrink 800
// default supply 10ea
// 10 won ,50 won ,100 won, 500 won + ( 1000 won <= 5000 won ) <= 7000 won
// show drink that can buy
// keep the money if vending machine don't have enough change
// admin mode
// - money status
// - beverage status
// - add beverage (server)
// - log (server)
