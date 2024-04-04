import * as React from "react";
import { createContext, useState, useMemo } from "react";
export const VendingMContext = createContext();

// TODO: 이거 서버로 옮겨야 됨
const testData = [
  {
    name: "물",
    price: 450,
    qty: 6,
  },
  {
    name: "커피",
    price: 500,
    qty: 6,
  },
  {
    name: "이온 음료",
    price: 550,
    qty: 6,
  },
  {
    name: "고급 커피",
    price: 700,
    qty: 6,
  },
  {
    name: "탄산 음료",
    price: 750,
    qty: 6,
  },
  {
    name: "특화 음료",
    price: 800,
    qty: 6,
  },
];
function CartProvider({ children }) {
  const [displayData, setDisplayData] = useState(testData);
  const [cartData, setCartData] = useState([]);
  const [sellData, setSellData] = useState([]);

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
      const data = {
        cmd: "purchase",
        payload: cartData[find_item],
      };
      window.machine.setCount(data);

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

  const vendingMProvideData = useMemo(() => ({
    displayData,
    cartData,
    sellData,
    total,
    addToCart,
    removeFromCart,
    clearCart,
  }));

  return (
    <VendingMContext.Provider value={vendingMProvideData}>
      {children}
    </VendingMContext.Provider>
  );
}

export default CartProvider;
