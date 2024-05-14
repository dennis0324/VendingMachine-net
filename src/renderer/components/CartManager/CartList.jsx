import * as React from "react";
import { useContext, useState } from "react";

import { VendingMContext } from "../CartProvider";
import { addPopup, removePopup, clearPopup } from "../PopupManager";

import ItemSelItem from "./ItemSelItem";
import SelectCompo from "../SelectCompo";
import ButtonCompo from "../ButtonCompo";
import ConfirmPurchase from "./ConfirmPurchase";
import MoneySelector from "./MoneySelector";
import ReturnPopup from "../ReturnPopup";

import { TEXT } from "../../utils/constants";

function List({ className = "", onRight, right }) {
  const {
    total,
    cartData,
    purchaseCart,
    clearCart,
    getProducts,
    payTotal,
    getRemain,
  } = useContext(VendingMContext);
  const predefinedClass = "flex flex-row h-fit mx-2 my-1";
  const combineclass = [className, predefinedClass].join(" ");

  // 구매 버튼
  // 해당 구매가 성공시에 카트의 모든 아이템을 비우고
  // 팝업창을 모두 닫는다.
  async function purchase() {
    if (cartData.length == 0) {
      addPopup(<ReturnPopup msg={TEXT.NOSELECT} onClick={removePopup} />);
      return;
    }
    const t = await purchaseCart();
    if (t) {
      removePopup();
      clearCart();
      clearPopup();
      getProducts();
      getRemain();
    }
  }

  function onConfirmPurchase() {
    if (total <= 0) {
      addPopup(<ReturnPopup msg={TEXT.NOSELECT} onClick={removePopup} />);
      return;
    }
    if (payTotal <= total)
      addPopup(<ConfirmPurchase onRight={removePopup} onLeft={purchase} />);
  }

  return (
    <>
      <section className="flex-1">
        {cartData.map((item, i) => (
          <ItemSelItem key={"cart-" + i} item={item} />
        ))}
      </section>
      <section className={combineclass}>
        <span className="flex-1 flex items-center">잔액</span>
        <span className="flex items-center">{payTotal}원</span>
      </section>
      <section className={combineclass}>
        <span className="flex-1 flex items-center">총 결제 금액</span>
        <span className="flex items-center">{total}원</span>
      </section>
      <SelectCompo onLeft={onConfirmPurchase} right={right} onRight={onRight} />
      <ButtonCompo
        message="돈 투입하기"
        onClick={() => addPopup(<MoneySelector />)}
      />
    </>
  );
}

export default List;
