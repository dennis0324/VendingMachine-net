import * as React from "react";
import ButtonCompo from "../ButtonCompo";
function ProductItem({ product, editing, changeProduct = () => {} }) {
  const disabled = "bg-gray-200 cursor-not-allowed";
  const abled = "bg-red-400";

  function checkNumeric(e) {
    console.log(e.key);
    if (isNaN(e.key) || (e.key === "Backspace" && e.key === "Delete"))
      e.preventDefault();
  }

  //TODO: 수금 관련 로직
  return (
    <div className="">
      <div className="bg-gray-300 rounded-lg p-2 m-2 col-span-5">
        <div className="bg-white rounded-lg p-1 flex justify-between my-1">
          <h2>{product.name}</h2>
          {/* <p>{product.price}원</p> */}
          <span>
            <input
              className="h-6 w-10 text-right p-0.5 bg-transparent"
              defaultValue={product.price}
              onKeyPress={(e) => checkNumeric(e)}
              onChange={(e) =>
                changeProduct(product.id, "price", e.target.value)
              }
            />
            원
          </span>
        </div>
        <div className="flex my-1 justify-between">
          <div className="flex p-0.5">
            <span>{"재고 :"}</span>
            <span>{product.qty}개</span>
          </div>
          {editing && (
            <button
              className="bg-white rounded-lg p-0.5"
              onClick={() =>
                changeProduct(product.id, "qty", product.qty_limit)
              }
            >
              채우기
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProductItem;
