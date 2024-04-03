import ProductItem from "./ProductItem";
import { useContext } from "react";
import { VendingMContext } from "../../App.js";

function ProductManager({ className }) {
  const predefinedClass = "";
  const combineClass = [className, predefinedClass].join(" ");
  const { cartData } = useContext(VendingMContext);

  return (
    <conatiner className={combineClass}>
      {cartData.map((item) => (
        <ProductItem product={item} />
      ))}
    </conatiner>
  );
}

export default ProductManager;
