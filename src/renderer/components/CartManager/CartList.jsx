import * as React from "react";
import { useContext } from "react";

import { VendingMContext } from "../CartProvider";
import { addPopup, removePopup, clearPopup } from "../PopupManager";

import ItemSelItem from "./ItemSelItem";
import SelectCompo from "../SelectCompo";
import ButtonCompo from "../ButtonCompo";

import ConfirmPurchase from "./ConfirmPurchase";
import MoneySelector from "./MoneySelector";
function List({ className = "", onRight, right }) {
  const { total, cartData, purchaseCart, clearCart } =
    useContext(VendingMContext);
  const predefinedClass = "flex flex-row h-fit m-2";
  const combineclass = [className, predefinedClass].join(" ");

  // 구매 버튼
  // 해당 구매가 성공시에 카트의 모든 아이템을 비우고
  // 팝업창을 모두 닫는다.
  async function purchase() {
    const t = await purchaseCart();
    if (t) {
      removePopup();
      clearCart();
      clearPopup();
    }
  }

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
      <SelectCompo
        onLeft={() =>
          addPopup(<ConfirmPurchase onRight={removePopup} onLeft={purchase} />)
        }
        right={right}
        onRight={onRight}
      />
      <ButtonCompo
        message="돈 투입하기"
        onClick={() => addPopup(<MoneySelector />)}
      />
    </>
  );
}

export default List;
