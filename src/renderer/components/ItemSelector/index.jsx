import * as React from "react";
import { useContext } from "react";
import { VendingMContext } from "../CartProvider";
import MenuItem from "./MenuItem";

function ItemSelector({ className }) {
  const { displayData } = useContext(VendingMContext);

  const preClassName =
    "grid col-span-2 max-sm:grid-cols-2 grid-cols-3 items-center gap-4 w-4/5";
  const combineClass = [className || "", preClassName].join(" ");

  return (
    <>
      <container className={combineClass}>
        {displayData.length === 0 ? (
          <div>server is not online</div>
        ) : (
          displayData.map((item) => <MenuItem key={item.name} item={item} />)
        )}
      </container>
    </>
  );
}

export default ItemSelector;
