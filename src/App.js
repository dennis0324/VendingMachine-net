import "./App.css";
import "./styles/tailwind.css";
import { useState } from "react";

import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Admin from "./pages/Admin";
// water 450, coffee 500, energydrink 550, premiumcoffee 700, coke 750, specialdrink 800
const testData = [
  {
    name: "물",
    price: 450,
    qty: 10,
  },
  {
    name: "커피",
    price: 500,
    qty: 10,
  },
  {
    name: "이온 음료",
    price: 550,
    qty: 10,
  },
  {
    name: "고급 커피",
    price: 700,
    qty: 10,
  },
  {
    name: "탄산 음료",
    price: 750,
    qty: 10,
  },
  {
    name: "특화 음료",
    price: 800,
    qty: 10,
  },
];

const testCartData = [
  {
    name: "물",
    qty: 1,
  },
  {
    name: "커피",
    qty: 3,
  },
];

function App() {
  const [data, setData] = useState(testData);
  const [cartData, setCartData] = useState(testCartData);
  return (
    <Router>
      <Routes>
        <Route
          path="/"
          element={<Home data={data} cartData={cartData} />}
        ></Route>
        <Route path="/admin" element={<Admin />} />
      </Routes>
    </Router>
  );
}

export default App;
