import * as React from "react";
import ButtonCompo from "./ButtonCompo";
import IconHelper from "./IconHelper";
import Close from "../icons/close.svg";
import { removePopup } from "./PopupManager";
function ChangePasswordForm() {
  async function submit() {
    window.machine.changePassword();
  }
  return (
    <div className="md:w-[50vw] md:h-[70vh] h-screen w-screen bg-white shadow-lg rounded-lg flex flex-col">
      <div className="flex flex-row-reverse">
        <IconHelper onClick={removePopup} className="cursor-pointer">
          <Close />
        </IconHelper>
      </div>
      <div className="flex w-full h-full justify-center items-center">
        <div className="bg-gray-300 h-fit flex flex-col p-4 rounded-lg">
          <span>비밀번호</span>
          <input className="mb-1 rounded-lg" />
          <span>비밀번호 재입력</span>
          <input className="mb-3 rounded-lg" />
          <ButtonCompo onClick={submit} />
        </div>
      </div>
    </div>
  );
}

export default ChangePasswordForm;
