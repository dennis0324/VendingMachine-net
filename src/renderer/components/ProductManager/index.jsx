import * as React from "react";
import ProductItem from "./ProductItem";
import { useContext } from "react";
import { VendingMContext } from "../CartProvider";

function ProductManager({ className }) {
  const predefinedClass = "";
  const combineClass = [className, predefinedClass].join(" ");
  const { cartData } = useContext(VendingMContext);

  return (
    <section className={combineClass}>
      {cartData.map((item) => (
        <ProductItem product={item} />
      ))}
    </section>
  );
}

export default ProductManager;
