import * as React from "react";
import ItemSelector from "../components/ItemSelector";
import CartManager from "../components/CartManager";
import PopupManager from "../components/PopupManager";
import MoneyIndicator from "../components/CartManager/MoneyIndicator";

function Home() {
  function goToAdmin() {
    window.location.hash = "/admin";
  }
  return (
    <div className="App h-screen w-screen flex flex-col">
      <PopupManager />
      <nav className="h-fit">
        <button onClick={goToAdmin}>관리자 로그인</button>
      </nav>
      <div className="flex flex-1 flex-row max-sm:flex-col max-sm:py-0 justify-center items-center">
        <section className="max-sm:h-full max-sm:flex max-sm:flex-col max-sm:justify-between max-sm:mb-0 m-6 w-full grid grid-cols-6 justify-items-center items-center">
          <MoneyIndicator className={"col-span-6 grid justify-center"}/>
          <ItemSelector className={"col-span-4 mx-5"} />
          <CartManager className={"col-span-2"} />
        </section>
      </div>
    </div>
  );
}

export default Home;
