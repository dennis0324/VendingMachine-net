// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts
//
import { contextBridge, ipcRenderer } from "electron";

contextBridge.exposeInMainWorld("machine", {
  setCount: (data) => {
    ipcRenderer.send("purchase", data);
  },
  sendCredentials: (id, pass) => {
    ipcRenderer.send("login", id, pass);
  },
  products: () => ipcRenderer.invoke("products").then((result) => result),
});
