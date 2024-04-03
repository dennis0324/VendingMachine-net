import * as React from "react";
import { createElement, useState } from "react";
import Popup from "./Popup";

let popupItem = {};
let setPopupItems = () => {};
export function addPopup(component) {
  console.log("addPopup", popupItem);
  const compo = {
    component: () => <Popup>{component}</Popup>,
    name: `popup-${popupItem.length + 1}`,
  };
  const arr = [...popupItem, compo];
  console.log(arr);
  setPopupItems(arr);
  console.log("addPopup2", popupItem);
}

export function removePopup() {
  console.log("removePopup", popupItem);
  const removedData = [...popupItem];
  removedData.pop();
  setPopupItems(removedData);
}
function PopupManager() {
  const [popupData, setPopupData] = useState([]);
  popupItem = popupData;
  setPopupItems = setPopupData;

  return (
    <div className="popup-manager">
      {popupData.map((item, index) => {
        return createElement(item.component, { key: index, isOpen: true });
      })}
    </div>
  );
}

export default PopupManager;
