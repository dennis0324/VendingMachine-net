import * as React from "react";
import { createContext, useState, useMemo, useEffect } from "react";
export const VendingMContext = createContext();

// TODO: 이거 서버로 옮겨야 됨
function CartProvider({ children }) {
  const [displayData, setDisplayData] = useState([]);
  const [cartData, setCartData] = useState([]);

  /////////////////////////////////////////////////////////////////////////////
  ///////// 메소드 선언
  /////////////////////////////////////////////////////////////////////////////

  // compute 총액
  const total = useMemo(() => {
    const total = cartData.reduce((acc, cur) => {
      acc += cur.price * cur.qty;
      return acc;
    }, 0);

    return total;
  }, [cartData]);

  // 카트에 상품 추가
  function addToCart(item, count = 1) {
    const find_item = cartData.findIndex(
      (cartItem) => cartItem.name === item.name,
    );
    if (cartData[find_item]?.qty >= item.qty) return "재고가 부족합니다.";
    if (7000 < item.price * count + total)
      return "7000원 이상 구매 불가능합니다.";

    if (find_item >= 0) {
      cartData[find_item].qty += count;
      setCartData([...cartData]);

      return false;
    }

    const cartProduct = { ...item, qty: count };
    setCartData([...cartData, cartProduct]);
    return false;
  }
  // 카트에서 특정 상품 삭제
  function removeFromCart(itemName) {
    const filteredData = cartData.filter((e) => e.name !== itemName);
    setCartData(filteredData);
  }
  // 카트 비우기
  function clearCart() {
    setCartData([]);
  }

  //purchase
  async function purchaseCart() {
    let a = await window.machine.purchase(cartData);
    if (a.status === undefined) return false;
    if (a.status === "success") return true;
    return false;
  }

  async function getProducts() {
    const { status, data } = await window.machine.getProducts();
    console.log(data);
    data.forEach((item) => {
      Object.keys(item).forEach((key) => {
        if (key !== "id") item[key] = Number(item[key]) || item[key];
      });
    });
    if (status === "success") setDisplayData(data);
  }
  useEffect(() => {
    getProducts();
  }, []);

  const vendingMProvideData = useMemo(() => ({
    displayData,
    cartData,
    total,
    addToCart,
    removeFromCart,
    clearCart,
    purchaseCart,
    getProducts,
  }));

  return (
    <VendingMContext.Provider value={vendingMProvideData}>
      {children}
    </VendingMContext.Provider>
  );
}

export default CartProvider;
