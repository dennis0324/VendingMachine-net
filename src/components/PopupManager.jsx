import { useContext } from "react";
import { PopupContext } from "../App";
function PopupManager() {
  const { popupData } = useContext(PopupContext);
  return <div>{}</div>;
}

export default PopupManager;
