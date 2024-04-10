import * as React from "react";
import ButtonCompo from "../ButtonCompo";
function ProductItem({ product, editing }) {
  const disabled = "bg-gray-200 cursor-not-allowed";
  const abled = "bg-red-400";

  //TODO: 수금 관련 로직
  return (
    <div className="">
      <div className="bg-gray-300 rounded-lg p-2 m-2 col-span-5">
        <div className="bg-white rounded-lg p-1 flex justify-between my-1">
          <h2>{product.name}</h2>
          <p>{product.price}원</p>
        </div>
        <div className="flex my-1 justify-between">
          <div className="flex p-0.5">
            <span>{"재고 : "}</span>
            <input
              className="h-6 w-6 text-right p-0.5 bg-transparent"
              defaultValue={product.qty}
            />

            <span>개</span>
          </div>
          {editing && (
            <button className="bg-white rounded-lg p-0.5">채우기</button>
          )}
        </div>
      </div>
      {/* <button */}
      {/*   disabled={editing} */}
      {/*   className={"col-span-2 m-2 rounded-lg " + (editing ? disabled : abled)} */}
      {/*   onCLick={() => {}} */}
      {/* > */}
      {/*   수금 */}
      {/* </button> */}
    </div>
  );
}

export default ProductItem;
