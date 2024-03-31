import { useContext, useMemo, useState } from "react";
import ItemSelItem from "./ItemSelItem";
import Popup from "../Popup";
import ConfirmPurchase from "./ConfirmPurchase";
import SelectCompo from "../SelectCompo";
import IconHelper from "../IconHelper";

import { VendingMContext } from "../../App";
import { ReactComponent as Cart } from "../../icons/cart.svg";
import { ReactComponent as Clear } from "../../icons/clear.svg";
import ButtonCompo from "../ButtonCompo";
import MoneySeletor from "./MoneySeletor";

function CartManager(props) {
  const { total, cartData, clearCart } = useContext(VendingMContext);
  const { className } = props;
  const [isOpen, setIsOpen] = useState(false);
  const [isMoneyOpen, setIsMoneyOpen] = useState(false);
  const [cartOpen, setCartOpen] = useState(false);
  const preClassName =
    "max-sm:hidden bg-gray-100 rounded-lg min-w-36 h-full w-full m-3 flex flex-col";
  const combineClass = [className || "", preClassName].join(" ");

  function offPopup() {
    console.log("off");
    setIsOpen(false);
  }
  function onPopup() {
    setIsOpen(true);
  }
  function showCart() {
    setCartOpen(true);
  }
  function hideCart() {
    setCartOpen(false);
  }

  function showMoney() {
    setIsMoneyOpen(true);
  }

  function hideMoney() {
    setIsMoneyOpen(false);
  }
  function List({ className = "", onRight, right }) {
    const predefinedClass = "flex flex-row h-fit m-2";
    const combineclass = [className, predefinedClass].join(" ");

    return (
      <>
        <section className="flex-1">
          {cartData.map((item, i) => (
            <ItemSelItem key={"cart-" + i} item={item} />
          ))}
        </section>
        <section className={combineclass}>
          <span className="flex-1 flex items-center">총 결제 금액</span>
          <span className="flex items-center">{total}원</span>
        </section>
        <SelectCompo onLeft={onPopup} right={right} onRight={onRight} />
        <ButtonCompo message="돈 투입하기" onClick={showMoney} />
      </>
    );
  }

  return (
    <>
      <Popup isOpen={isOpen} setIsOpen={setIsOpen}>
        <ConfirmPurchase onRight={offPopup} />
      </Popup>
      <Popup isOpen={isMoneyOpen} setIsOpen={setIsMoneyOpen}>
        <MoneySeletor onRight={hideMoney} />
      </Popup>
      <container className={combineClass}>
        <List onRight={clearCart} right={"비우기"} />
      </container>
      <container className="max-sm:flex hidden h-12 bg-gray-100 flex-row w-full pl-3">
        <Popup isOpen={cartOpen} setIsOpen={hideCart}>
          <List onRight={hideCart} right={"닫기"} />
        </Popup>
        <div className="flex flex-row flex-1">
          <span className="flex-1 flex items-center">총 결제 금액</span>
          <span className="flex items-center">{total}원</span>
        </div>
        <div className="h-12 w-12 flex justify-center items-center">
          <IconHelper
            className="m-0 flex justify-center items-center"
            onClick={clearCart}
          >
            <Clear />
          </IconHelper>
        </div>
        <div className="h-12 w-12 flex justify-center items-center">
          <IconHelper
            className="m-0 flex justify-center items-center"
            onClick={showCart}
          >
            <Cart />
          </IconHelper>
        </div>
      </container>
    </>
  );
}

export default CartManager;
