import * as React from "react";
import ProductItem from "./ProductItem";
import { useContext, useState, useEffect } from "react";
import { VendingMContext } from "../CartProvider";
import ButtonCompo from "../ButtonCompo";
import SelectCompo from "../SelectCompo";
import { MoneyContext } from "../MoneyProvider";

function ProductManager({ className }) {
  const { displayData, getProducts } = useContext(VendingMContext);

  const predefinedClass = "md:grid";
  const combineClass = [className, predefinedClass].join(" ");

  const [editing, setEditing] = useState(false);
  const [changedMoney, setChangedMoney] = useState([]);
  const [changedProduct, setChangedProduct] = useState([]);

  async function commitChange() {
    console.debug("commit");
    // console.log({ money: changedMoney, product: changedProduct });
    const { status } = await window.machine.change(changedProduct);
    if (status == "success") {
      getProducts();
    }
  }

  function changeProduct(index, key, value) {
    let findDataIndex = changedProduct.findIndex((e) => e.productId === index);
    console.log(findDataIndex, changedProduct, index, key, value);

    if (findDataIndex !== -1) {
      changedProduct[findDataIndex][key] = Number(value) || value;
      setChangedProduct([...changedProduct]);
    } else {
      const findData = displayData.find((e) => e.productId === index);
      findData[key] = Number(value) || value;
      console.log(findData);
      setChangedProduct([...changedProduct, findData]);
    }
  }

  async function supplyAll() {
    const { status } = window.machine.supply(
      Array.from({ length: displayData.length }, (_, i) => i + 1),
    );

    if (status === "success") getProducts();
  }
  async function supply(priceIndex) {
    const { status } = await window.machine.supply([priceIndex]);

    if (status === "success") getProducts();
  }
  return (
    <section className={combineClass}>
      {displayData.map((item, idx) => (
        <ProductItem
          product={item}
          editing={editing}
          itemId={idx}
          changeProduct={changeProduct}
          supply={supply}
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
            supplyAll();
          }}
          color="bg-red-400"
        />
      </div>
    </section>
  );
}

export default ProductManager;
