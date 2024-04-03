import { removePopup } from "./PopupManager";
function Popup({ children }) {
  return (
    <div className="fixed">
      <div
        className="fixed top-0 left-0 h-screen w-screen backdrop-blur-sm"
        onClick={() => removePopup()}
      />
      <div className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2">
        {children}
      </div>
    </div>
  );
}

export default Popup;
