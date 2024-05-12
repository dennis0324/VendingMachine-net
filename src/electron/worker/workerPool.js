import { ipcMain } from "electron";
import Store from "electron-store";
import crypto from "crypto";
import { Worker } from "worker_threads";
import { DataController } from "./DataController.mjs";
import { timeStamp } from "../util/date.mjs";

const store = new Store();
const dataController = new DataController();

/**
 * react <-> electron ipc 통신
 */
export function IpcPool() {
  let worker;
  try {
    worker = new Worker("./src/electron/worker/worker.mjs", {
      env: { MODE: process.env.MODE },
    });
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
   * handle message from worker when message arrived
   */
  worker.on("message", (data) => {
    const [cmd, hash, vendingId, _1, payload] = data.split("|");
    // need to store vendingId for use universal
    if (cmd === "handshake") {
      store.set("vendingId", vendingId);
      return;
    }
    let jsonPayload = "";
    try {
      jsonPayload = JSON.parse(payload);
    } catch (e) {
      console.log("[warn]: payload json parse failed");
      jsonPayload = payload;
    }
    dataController.setData(hash, jsonPayload);
    dataController.remove(hash);
  });

  worker.on("online", () => {
    const ipcDto = {
      cmd: "handshake",
      hash: "",
      vendingId: "",
      date: timeStamp(),
      payload: {},
    };
    worker.postMessage(ipcDto);
  });

  /**
   * commands collections
   */
  const cmdCollections = [
    "purchase",
    "getMoney",
    "getConstantProduct",
    "products",
    "insertMoney",
    "login",
    "changePassword",
    "collectMoney",
    "retrieveMoney",
  ];

  /**
   * commands that need crypto
   */
  const needCrypto = ["login", "changePassword"];

  /**
   * register handler for worker
   */
  cmdCollections.forEach((cmd) => {
    ipcMain.handle(cmd, async (_, ipcDto) => {
      ipcDto.vendingId = store.get("vendingId");
      if (needCrypto.includes(cmd)) {
        ipcDto.payload.password = crypto
          .createHash("sha256")
          .update(ipcDto.payload.password)
          .digest("base64");
      }
      return postMess(ipcDto);
    });
  });

  /**
   * return vendingId
   */
  ipcMain.handle("vendingId", async (_) => {
    return store.get("vendingId");
  });

  return worker;
}