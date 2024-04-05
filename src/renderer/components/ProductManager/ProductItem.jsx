import * as React from "react";
function ProductItem({ product }) {
  return (
    <div>
      <h2>{product.name}</h2>
      <p>{product.price}</p>
      <p>{product.qty}</p>
    </div>
  );
}

export default ProductItem;
