import * as React from "react";
import { useContext, useEffect } from "react";
import { VendingMContext } from "../CartProvider";
import MenuItem from "./MenuItem";

function ItemSelector({ className }) {
  const { displayData, addToCart,setDisplayData } = useContext(VendingMContext);
  
  const preClassName =
    "grid col-span-2 max-sm:grid-cols-2 grid-cols-3 items-center gap-4 w-4/5";
  const combineClass = [className || "", preClassName].join(" ");

  useEffect(() => {
    async function gettingProducts(){
      const products = await window.machine.getProducts();
      setDisplayData(products.data);
    }
    gettingProducts();
  },[])

  return (
    <>
      <container className={combineClass}>
        {displayData.map((item) => (
          <MenuItem key={item.name} item={item} />
        ))}
      </container>
    </>
  );
}

export default ItemSelector;
