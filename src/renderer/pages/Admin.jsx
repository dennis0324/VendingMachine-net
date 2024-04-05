import * as React from "react";
import { useState } from "react";
import ProductManager from "../components/ProductManager";
import LoginForm from "../components/LoginForm";

function Admin() {
  const [id, setId] = useState("");
  const [password, setPassword] = useState("");
  function goToHome() {
    window.location.hash = "/";
  }

  function submit() {
    window.machine.sendCredentials(id, password);
  }
  return (
    <div className="App h-screen w-screen flex flex-col">
      <div>
        <button onClick={goToHome}>돌아가기</button>
      </div>
      <LoginForm setId={setId} setPassword={setPassword} submit={submit} />
      {/* <section className="grid grid-cols-6 h-full"> */}
      {/*   <ProductManager className={"col-span-2"} /> */}
      {/*   <LogManager className={"col-span-2"} /> */}
      {/*   <ProductManager className={"col-span-2"} /> */}
      {/* </section> */}
    </div>
  );
}

export default Admin;
