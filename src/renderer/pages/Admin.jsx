import * as React from "react";
import { useState } from "react";
import ProductManager from "../components/ProductManager";
import SaleManager from "../components/SaleManager";
import PopupManager from "../components/PopupManager";
import LoginForm from "../components/LoginForm";

import ArrowBack from "../icons/arrowBack.svg";
import IconHelper from "../components/IconHelper";
import Terminal from "../icons/terminal.svg";
import Key from "../icons/key.svg";

import { addPopup } from "../components/PopupManager";
import TerminalLog from "../components/Terminal";
import ChangePasswordForm from "../components/ChangePasswordForm";

function Admin() {
  const [id, setId] = useState("");
  const [password, setPassword] = useState("");
  const [login, setLogin] = useState(false);
  function goToHome() {
    window.location.hash = "/";
  }

  async function submit() {
    const a = await window.machine.login(id, password);
    console.log(a);
    if (a.status === "success") {
      setLogin(true);
    }
  }

  function terminal() {
    addPopup(<TerminalLog />, {
      direction: "right-top",
      pos: { x: "20", y: "20" },
      pivot: "right-corner",
      blur: false,
    });
  }

  function showChangePasswordPop() {
    addPopup(<ChangePasswordForm />, { blur: false });
  }

  return (
    <div className="App h-screen w-screen flex flex-col">
      <PopupManager />
      <div className="flex items-center justify-between">
        <IconHelper className="flex cursor-pointer" onClick={goToHome}>
          <ArrowBack />
          <span>돌아가기</span>
        </IconHelper>
        <div className="flex">
          <div className={"flex " + (login ? "" : "hidden")}>
            <IconHelper onClick={showChangePasswordPop}>
              <Key className="cursor-pointer" />
            </IconHelper>
            <IconHelper onClick={terminal}>
              <Terminal className="cursor-pointer" />
            </IconHelper>
          </div>
        </div>
      </div>
      {login ? (
        <div className="h-full flex justify-center md:items-center overflow-y-auto">
          <section className="lg:grid lg:grid-cols-6  items-center h-fit">
            <ProductManager className={"col-span-4"} />
            <SaleManager />
          </section>
        </div>
      ) : (
        <LoginForm setId={setId} setPassword={setPassword} submit={submit} />
      )}
    </div>
  );
}

export default Admin;
