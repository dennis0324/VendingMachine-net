import * as React from "react";
import PopupManager from "../components/PopupManager";

function Logs() {
  function goToAdmin() {
    window.location.hash = "/admin";
  }
  return (
    <div className="App h-screen w-screen flex flex-col">
      <PopupManager />
      <div onClick={goToAdmin}>back</div>
    </div>
  );
}

export default Logs;
