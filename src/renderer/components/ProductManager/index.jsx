import * as React from "react";
const { useState } = React;
import ProductItem from "./ProductItem";
import { useContext } from "react";
import { VendingMContext } from "../CartProvider";
import ButtonCompo from "../ButtonCompo";
import SelectCompo from "../SelectCompo";

function ProductManager({ className }) {
  const predefinedClass = "md:grid";
  const combineClass = [className, predefinedClass].join(" ");
  const [editing, setEditing] = useState(false);
  const { displayData } = useContext(VendingMContext);
  const [changedProduct, setChangedProduct] = useState(
    Array.from({ length: displayData.length }, (_) => ({})),
  );

  function commitChange() {
    console.debug("commit");
  }

  function fillProductAll() {
    console.debug("fill all product");
  }

  function changeProduct(index, key, value) {
    changedProduct[index][key] = value;
    setChangedProduct([...changedProduct]);
  }

  return (
    <section className={combineClass}>
      {displayData.map((item, idx) => (
        <ProductItem
          product={item}
          editing={editing}
          // fillProduct={fillProduct}
          changeProduct={changeProduct}
          key={"ProcutItem-" + idx}
        />
      ))}
      <div className="col-span-3">
        {editing ? (
          <SelectCompo
            onRight={() => {
              setEditing(false);
            }}
            onLeft={() => {
              setEditing(false);
              commitChange();
            }}
          />
        ) : (
          <ButtonCompo
            message="수정하기"
            onClick={() => {
              setEditing(true);
            }}
          />
        )}
        <ButtonCompo
          message="모두 채우기"
          onClick={() => {
            fillProductAll();
          }}
          color="bg-red-400"
        />
      </div>
    </section>
  );
}

export default ProductManager;
