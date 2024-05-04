import * as React from "react";
import { createElement, useState } from "react";
import Popup from "./Popup";

let popupItem = {};
let setPopupItems = () => {};

// popup을 스택에 추가한다.
export function addPopup(component, opt) {
  const style = {};
  if (opt?.direction !== undefined) {
    let [opt1, opt2] = opt.direction.split("-");
    if (!opt1) opt1 = "left";
    if (!opt2) opt2 = "top";

    let posX = opt.pos.x;
    let posY = opt.pos.y;
    if (opt.pos.x.indexOf("%") < 0) posX += "px";
    if (opt.pos.y.indexOf("%") < 0) posY += "px";
    style[opt1] = posX || "50%";
    style[opt2] = posY || "50%";
  } else {
    style["left"] = "50%";
    style["top"] = "50%";
  }

  const compo = {
    component: () => (
      <Popup style={style} pivot={opt?.pivot} blur={opt?.blur}>
        {component}
      </Popup>
    ),
    name: `popup-${popupItem.length + 1}`,
  };
  const arr = [...popupItem, compo];
  setPopupItems(arr);
}

// popup창에 있는 창중 가장 최근에 띄워졌던 팝업을 삭제한다.
export function removePopup() {
  const removedData = [...popupItem];
  removedData.pop();
  setPopupItems(removedData);
}

// stack에 올라가 있는 모든 팝업창을 삭제한다.
export function clearPopup() {
  setPopupItems([]);
}

// stack에 올라가있는 팝업을 랜더링 해준다.
function PopupManager() {
  const [popupData, setPopupData] = useState([]);
  popupItem = popupData;
  setPopupItems = setPopupData;

  return (
    <div className="popup-manager">
      {popupData.map((item, index) => {
        return createElement(item.component, {
          key: index,
          isOpen: true,
        });
      })}
    </div>
  );
}

export default PopupManager;
