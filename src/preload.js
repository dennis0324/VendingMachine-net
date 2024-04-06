// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts
//
import { contextBridge, ipcRenderer } from "electron";

// import dayjs from "dayjs";
// import timezone from "dayjs/plugin/timezone";
// import utc from "dayjs/plugin/utc";
// dayjs.extend(timezone);
// dayjs.extend(utc);

/**
 * @typedef ProductDto
 * @property {string} name
 * @property {number} price
 * @property {number} qty
 */

// function createHash(cmd, date, vendingID) {
//   const joined = [cmd, vendingID, date].join("");
//   return crypto.createHash("sha-1").update(joined);
// }
async function sendToMain(cmd, data) {
  const vendingId = "testing";
  const date = new Date();
  // const hash = createHash(cmd, date, vendingId);

  const payload = {
    hash: "",
    cmd,
    vendingId,
    date,
    payload: data,
  };

  return await ipcRenderer.invoke(cmd, payload);
}

contextBridge.exposeInMainWorld("machine", {
  sendCredentials: (id, pass) => {
    // ipcRenderer.send("login", id, pass);
    // ipcRenderer.invoke();
  },
  login: async (id, pass) => {
    return;
  },
  /**
   * @param {ProductDto[]} productDto
   */
  purchase: async (productDto) => {
    return await sendToMain("purchase", productDto);
  },
  products: () => ipcRenderer.invoke("products").then((result) => result),
});
