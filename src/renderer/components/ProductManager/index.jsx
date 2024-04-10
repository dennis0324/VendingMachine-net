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

  return (
    <section className={combineClass}>
      {displayData.map((item, idx) => (
        <ProductItem
          product={item}
          editing={editing}
          key={"ProcutItem-" + idx}
        />
      ))}
      <div className="col-span-3">
        {editing ? (
          <SelectCompo
            onRight={() => {
              setEditing(false);
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
            setEditing(true);
          }}
          color="bg-red-400"
        />
      </div>
    </section>
  );
}

export default ProductManager;
