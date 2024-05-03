// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts
//
import { contextBridge, ipcRenderer } from "electron";

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
    vendingId:"",
    date: date.toISOString(),
    payload: data,
  };

  return await ipcRenderer.invoke(cmd, payload);
}

contextBridge.exposeInMainWorld("machine", {
  sendCredentials: (id, pass) => {},
  login: async (id, pass) => {
    return await sendToMain("login", { id, password: pass });
  },
  /**
   * @param {ProductDto[]} productDto
   */
  purchase: async (productDto) => {
    return await sendToMain("purchase", productDto);
  },
  vendingId: async () => {
    return await sendToMain("vendingId", "");
  },
  getMoney: async () => {
    return await sendToMain("getMoney", "");
  },
  getConstantProduct: async () => {
    return await sendToMain("getConstantProduct", "");
  },
  getProducts: async () => {
    return await sendToMain("products",{});
  },
  insertMoney: async (moneyDto) => {
    return await sendToMain("insertMoney",moneyDto)
  }
});
