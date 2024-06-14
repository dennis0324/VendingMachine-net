import * as React from "react";
import { useState, useContext } from "react";

import ButtonCompo from "./ButtonCompo";
import IconHelper from "./IconHelper";
import Close from "../icons/close.svg";
import ReturnPopup from "./ReturnPopup";

import { addPopup } from "./PopupManager";
import { removePopup,clearPopup } from "./PopupManager";
import { TEXT } from "../utils/constants";

function ChangePasswordForm() {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  async function submit() {
    if (password !== confirmPassword) {
      addPopup(<ReturnPopup msg={TEXT.UNMATCH_PASSWORD} />);
      return;
    }
    const { status } = await window.machine.changePassword(password);
    if (status === "success") {
      addPopup(<ReturnPopup msg={TEXT.SUCCESS_CHANGEPASSWORD} onClick={clearPopup}/>);
    } else {
      addPopup(<ReturnPopup msg={TEXT.FAIL_CHANGEPASSWORD} />);
    }
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
          <input
            className="mb-1 rounded-lg"
            onChange={(e) => setPassword(e.target.value)}
          />
          <span>비밀번호 재입력</span>
          <input
            className="mb-3 rounded-lg"
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
          <ButtonCompo onClick={submit} />
        </div>
      </div>
    </div>
  );
}

export default ChangePasswordForm;
