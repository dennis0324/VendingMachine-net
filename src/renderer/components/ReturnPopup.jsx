import * as React from "react";
import ButtonCompo from "./ButtonCompo";

function ReturnPopup({ msg, onClick }) {
  return (
    <div className="bg-gray-300 p-10 rounded-lg">
      <h2>{msg}</h2>
      <ButtonCompo message="확인" onClick={onClick} />
    </div>
  );
}

export default ReturnPopup;
