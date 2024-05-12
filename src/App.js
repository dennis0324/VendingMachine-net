import * as React from "react";
import "./renderer/styles/tailwind.css";

import { createContext, useMemo, useState } from "react";
import { HashRouter as Router, Route, Routes } from "react-router-dom";

import Admin from "./renderer/pages/Admin";
import Home from "./renderer/pages/Home";
import CartProvider from "./renderer/components/CartProvider";
import MoneyProvider from "./renderer/components/MoneyProvider";

// water 450, coffee 500, energydrink 550, premiumcoffee 700, coke 750,
// specialdrink 800
export const VendingMContext = createContext();
function App() {
  return (
    <CartProvider>
      <MoneyProvider>
        <Router basename="/">
          <Routes>
            <Route path="/" element={<Home />}></Route>
            <Route path="/admin" element={<Admin />} />
          </Routes>
        </Router>
      </MoneyProvider>
    </CartProvider>
  );
}

export default App;
