import { ipcMain } from "electron";
import Store from "electron-store";
import crypto from "crypto";
import { Worker } from "worker_threads";
import { DataController } from "./DataController.mjs";

/**
 * @typedef IPCDto
 * @property {string} hash
 * @property {string} cmd
 * @property {string} vendingId
 * @property {Date} date
 * @property {any} payload
 */

const store = new Store();
const dataController = new DataController();

/**
 * react <-> electron ipc 통신
 */
export function IpcPool() {
  let worker;
  try {
    worker = new Worker("./src/electron/worker/worker.mjs");
  } catch (e) {
    console.log(e);
  }

  // 전송하면서 hash 설정 및 해당 hash에 promise 걸어두기
  async function postMess(ipcDto) {
    const postData = dataController.append(ipcDto);
    worker.postMessage(postData);
    return await dataController.arrived(postData.hash);
  }

  /**
   *
   */
  worker.on("message", (data) => {
    console.log("parsed", data);
    const [cmd, hash, vendingId, _1, _2] = data.split("|");
    if (cmd === "handshake") {
      store.set("vendingId", vendingId);
      return;
    }
    dataController.remove(hash);
  });

  worker.on("online", () => {
    const ipcDto = {
      cmd: "handshake",
      hash: "",
      vendingId: "",
      date: new Date().toISOString(),
      payload: "",
    };
    worker.postMessage(ipcDto);
  });

  // 구매 요청
  /**
   *
   * @param {IPCDto} ipcDto
   */
  ipcMain.handle("purchase", async (_, ipcDto) => {
    return postMess(ipcDto);
  });

  // 로그인 요청
  ipcMain.handle("login", async (_, ipcDto) => {
    ipcDto.payload.password = crypto
      .createHash("sha256")
      .update(ipcDto.payload.password)
      .digest("base64");
    return postMess(ipcDto);
  });

  // 비밀번호 변경
  ipcMain.handle("changePassword", async (_, ipcDto) => {});

  // 자판기 상품 ID
  ipcMain.handle("vendingId", async (_) => {
    return store.get("vendingId");
  });

  ipcMain.handle("getMoney", async (_, ipcDto) => {
    return postMess(ipcDto);
  });

  ipcMain.handle("getConstantProduct", async (_, ipcDto) => {
    return postMess(ipcDto);
  });

  return worker;
}
