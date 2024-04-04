const { ipcRenderer } = window.require("electron");

export const sendData = (data) => {
  ipcRenderer.send("send-data-event-name", JSON.stringify(data));
};

ipcRenderer.on("send-data-event-name-reply", (event, arg) => {
  console.log(arg);
});
