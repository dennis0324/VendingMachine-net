import { ipcMain } from "electron";
import crypto from "crypto";
import { Worker } from "worker_threads";

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

/**
 * @typedef IPCDto
 * @property {string} hash
 * @property {string} cmd
 * @property {string} vendingId
 * @property {Date} date
 * @property {any} payload
 */

class IpcMap extends Map {
  add(cmd, vendingId, value) {
    let resolve, reject;
    const hash = createHash(cmd, vendingId);
    const promise = new Promise((res, rej) => {
      resolve = res;
      reject = rej;
    });
    this.set(hash, resolve);

    return [
      promise,
      {
        cmd,
        vendingId,
        hash,
        payload: JSON.stringify(value),
      },
    ];
  }
}
const IpcHash = new IpcMap();
function createHash(ipcDto) {
  const joined = [ipcDto.cmd, ipcDto.vendingID, ipcDto.date].join("");
  return crypto.createHash("sha1").update(joined).digest("hex");
}
export function IpcPool() {
  const worker = new Worker("./src/electron/worker.mjs");

  async function postMess(ipcDto) {
    if (ipcDto.hash === "") ipcDto.hash = createHash(ipcDto);

    ipcDto.arrived = new Promise((res, rej) => {
      ipcDto.resolve = res;
      ipcDto.reject = rej;
    });
    IpcHash.set(ipcDto.hash, ipcDto);
    console.log("IpcHash", IpcHash);
    const postData = {
      cmd: ipcDto.cmd,
      hash: ipcDto.hash,
      vendingId: ipcDto.vendingId,
      payload: ipcDto.payload,
    };
    worker.postMessage(postData);
    await ipcDto.arrived;

    return ipcDto.payload;
  }

  /**
   *
   */
  worker.on("message", (data) => {
    const parsed = JSON.parse(data);
    const ipcDto = IpcHash.get(parsed.hash);
    if (ipcDto === undefined) return;
    ipcDto.payload = parsed?.payload || "";
    ipcDto.resolve();
    IpcHash.delete(parsed.hash);

    // console.log("workerPool", parsed);
  });

  //////////////////////////
  // 구매 요청
  //////////////////////////
  /**
   *
   * @param {IPCDto} ipcDto
   */
  ipcMain.handle("purchase", async (event, ipcDto) => {
    return postMess(ipcDto);
  });
  //////////////////////////

  // ipcMain.on("login", (event, id, pass) => {
  //   const hashedPassword = crypto
  //     .createHash("sha256")
  //     .update(pass)
  //     .digest("base64");
  //   worker.postMessage({
  //     cmd: "login",
  //     id: "",
  //     payload: { id, hashedPassword },
  //   });
  //
  // });
}
