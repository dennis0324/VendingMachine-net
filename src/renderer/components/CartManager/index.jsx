import * as React from "react";
import { useContext } from "react";
import IconHelper from "../IconHelper";

import { VendingMContext } from "../CartProvider";
import Cart from "../../icons/cart.svg";
import Clear from "../../icons/clear.svg";
import { addPopup, removePopup } from "../PopupManager";
import CartList from "./CartList";

function CartManager(props) {
  const { total, clearCart } = useContext(VendingMContext);
  const { className } = props;
  const preClassName =
    "max-sm:hidden bg-gray-100 rounded-lg min-w-36 h-full w-full m-3 flex flex-col";
  const combineClass = [className || "", preClassName].join(" ");

  function showCart() {
    addPopup(<CartList right={"닫기"} onRight={removePopup} />);
  }

  return (
    <>
      <container className={combineClass}>
        <CartList right={"비우기"} onRight={removePopup} />
      </container>
      <container className="max-sm:flex hidden h-12 bg-gray-100 flex-row w-full">
        <div className="flex flex-row flex-1 pl-3">
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
