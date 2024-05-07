// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts
//
import { contextBridge, ipcRenderer } from "electron";
import { timeStamp } from "./utils/date";
import * as types from "./utils/typedefs.js";

/**
 *  create ipcDto and send to child process
 */
async function sendToMain(cmd, data) {
  const payload = {
    hash: "",
    cmd,
    vendingId: "",
    date: timeStamp(),
    payload: data,
  };

  return await ipcRenderer.invoke(cmd, payload);
}

/**
 * Expose protected methods that allow the renderer process to use
 */
contextBridge.exposeInMainWorld("machine", {
  /**
   * send login request to server
   *
   * @return {Promise<types.ReturnPayload>}
   */
  login: async (id, pass) => {
    return await sendToMain("login", { id, password: pass });
  },

  /**
   * send changePassword request to server
   *
   * @return {Promise<types.ReturnPayload>}
   */
  changePassword: async (id, pass) => {
    return await sendToMain("login", { id, password: pass });
  },
  /**
   * send purchase request to server
   *
   * @param {types.ipcDto[]} productDto
   * @return {Promise<types.ReturnPayload>}
   */
  purchase: async (productDto) => {
    return await sendToMain("purchase", productDto);
  },
  /**
   * send getVendingMachine ID requeste to server
   *
   * @return {Promise<types.ReturnPayload>}
   */
  vendingId: async () => {
    return await sendToMain("vendingId", "");
  },
  /**
   * send get Constant Product request to server
   *
   * @return {Promise<types.ReturnPayload>}
   */
  getConstantProduct: async () => {
    return await sendToMain("getConstantProduct", "");
  },
  /**
   * send get Products request to server
   *
   * @return {Promise<types.ReturnPayload>}
   */
  getProducts: async () => {
    return await sendToMain("products", {});
  },
  /**
   * send getVendingMachine ID requeste to server
   *
   * @param {types.MoneyDto[]}
   * @return {Promise<types.ReturnPayload>}
   */
  insertMoney: async (moneyDto) => {
    return await sendToMain("insertMoney", moneyDto);
  },
  /**
   * send getVendingMachine ID requeste to server
   *
   * @return {Promise<types.ReturnPayload>}
   */
  getMoney: async () => {
    return await sendToMain("getMoney", {});
  },
});
