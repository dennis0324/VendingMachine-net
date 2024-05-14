import * as React from "react";
import { useRef } from "react";
function ProductItem({
  product,
  editing,
  changeProduct = () => {},
  supply = () => {},
}) {
  const buttonRef = useRef(null);

  function checkNumeric(e) {
    console.log(e.key);
    if (isNaN(e.key) || (e.key === "Backspace" && e.key === "Delete"))
      e.preventDefault();
  }

  return (
    <div className="">
      <div className="bg-gray-300 rounded-lg p-2 m-2 col-span-5">
        <div className="bg-white rounded-lg p-1 flex justify-between my-1">
          <input
            className="h-6 w-20 text-left p-0.5 bg-transparent"
            defaultValue={product.name}
            disabled={!editing}
            onChange={(e) =>
              changeProduct(product.productId, "name", e.target.value)
            }
          />
          <span>
            <input
              className="h-6 w-10 text-right p-0.5 bg-transparent"
              defaultValue={product.price}
              disabled={!editing}
              onKeyPress={(e) => checkNumeric(e)}
              onChange={(e) => {
                changeProduct(product.productId, "price", e.target.value);
              }}
              ref={buttonRef}
            />
            원
          </span>
        </div>
        <div className="flex my-1 justify-between">
          <div className="flex p-0.5">
            <span>{"재고 :"}</span>
            <span>{product.qty}개</span>
          </div>
          <button
            className="bg-white rounded-lg py-0.5 px-2"
            onClick={() => supply(product.productId)}
          >
            채우기
          </button>
        </div>
      </div>
    </div>
  );
}

export default ProductItem;
