import { ipcMain } from "electron";
import Store from "electron-store";
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
const store = new Store();

function createHash(ipcDto) {
  const joined = [ipcDto.cmd, ipcDto.vendingID, ipcDto.date].join("");
  return crypto.createHash("sha1").update(joined).digest("hex");
}

/**
 * react <-> electron ipc 통신
 */
export function IpcPool() {
  let worker;
  try {
    worker = new Worker("./src/electron/worker.mjs");
  } catch (e) {
    console.log(e);
  }

  // 전송하면서 hash 설정 및 해당 hash에 promise 걸어두기
  async function postMess(ipcDto) {
    if (ipcDto.hash === "") ipcDto.hash = createHash(ipcDto);

    ipcDto.arrived = new Promise((res, rej) => {
      ipcDto.resolve = res;
      ipcDto.reject = rej;
    });
    IpcHash.set(ipcDto.hash, ipcDto);
    const postData = {
      cmd: ipcDto.cmd,
      hash: ipcDto.hash,
      vendingId: ipcDto.vendingId,
      date: ipcDto.date,
      payload: ipcDto.payload,
    };
    worker.postMessage(postData);
    await ipcDto.arrived;

    let payload;
    try {
      payload = JSON.parse(ipcDto.payload);
    } catch (e) {
      payload = ipcDto.payload;
    }
    return payload;
  }

  /**
   *
   */
  worker.on("message", (data) => {
    console.log("parsed", data);
    const [cmd, hash, vendingId, date, payload] = data.split("|");
    if (cmd === "handshake") {
      store.set("vendingId", vendingId);
      return;
    }

    const ipcDto = IpcHash.get(hash);

    if (ipcDto === undefined) return;

    ipcDto.payload = payload || "";

    ipcDto.resolve();
    IpcHash.delete(hash);
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
