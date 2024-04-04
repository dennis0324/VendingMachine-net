import * as React from "react";
import IconHelper from "../IconHelper";
import Delete from "../../icons/delete.svg";
import { useContext } from "react";
import { VendingMContext } from "../CartProvider";
function ItemSelItem({ item, qty, onDelete }) {
  const { removeFromCart } = useContext(VendingMContext);

  return (
    <div className="grid grid-cols-5 mx-2 my-3 items-center">
      <div className="col-span-3">{item.name}</div>
      <div className="col-span-1">{item.qty}</div>
      <div className="col-span-1">
        <IconHelper onClick={() => removeFromCart(item.name)}>
          <Delete className="fill-red-500" />{" "}
        </IconHelper>
      </div>
    </div>
  );
}

export default ItemSelItem;
