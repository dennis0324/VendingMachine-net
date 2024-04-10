import * as React from "react";
import { removePopup } from "./PopupManager";
function Popup({ children, pivot, style, blur = true }) {
  let translate = "-translate-x-1/2 -translate-y-1/2";

  if (pivot == "right-corner") {
    translate = "-translate-x-1 -translate-y-1";
  }
  return (
    <div className="fixed">
      <div
        className={
          "fixed top-0 left-0 h-screen w-screen " +
          (blur ? "backdrop-blur-sm" : null)
        }
        onClick={() => removePopup()}
      />
      <div className={["fixed", translate].join(" ")} style={style}>
        {children}
      </div>
    </div>
  );
}

export default Popup;
