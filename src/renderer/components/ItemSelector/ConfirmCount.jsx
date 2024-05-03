import * as React from "react";
import SelectCompo from "../SelectCompo";
import IconHelper from "../IconHelper";
import Add from "../../icons/add.svg";
import Remove from "../../icons/remove.svg";

import { useState, useContext } from "react";
import { VendingMContext } from "../CartProvider";

function ConfirmCount({ qty = 10, item, onPopupCancel }) {
  const [count, setCount] = useState(1);
  const { addToCart } = useContext(VendingMContext);

  function increase() {
    if (count < qty) {
      setCount(count + 1);
    }
  }
  function decrease() {
    if (count > 1) {
      setCount(count - 1);
    }
  }

  function confirm() {
    let temp;
    if ((temp = addToCart(item, count))) {
      console.log(temp);
      return;
    }
    onPopupCancel();
  }

  return (
    <div className="bg-gray-300 p-10 rounded-lg">
      <p>{item.name} 구매 개수</p>
      <section className="grid grid-cols-3 justify-center items-center">
        <div className="mx-3">
          <IconHelper onClick={decrease} className="cursor-pointer">
            <Remove />
          </IconHelper>
        </div>
        <span className="text-lg text-center">{count}</span>
        <div className="mx-3">
          <IconHelper onClick={increase} className="cursor-pointer">
            <Add />
          </IconHelper>
        </div>
      </section>
      <SelectCompo
        left={"담기"}
        onLeft={confirm}
        right={"취소"}
        onRight={onPopupCancel}
      />
    </div>
  );
}

export default ConfirmCount;
