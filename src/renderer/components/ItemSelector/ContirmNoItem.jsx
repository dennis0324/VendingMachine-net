import * as React from "react";
import ButtonCompo from "../ButtonCompo";

function ConfirmNoItem({ onPopupCancel = () => {} }) {
  return (
    <div className="bg-gray-300 p-10 rounded-lg">
      <h2>선택 하신 상품은 재고가 존재하지 않습니다.</h2>
      <ButtonCompo message="확인" onClick={onPopupCancel} />
    </div>
  );
}

export default ConfirmNoItem;
