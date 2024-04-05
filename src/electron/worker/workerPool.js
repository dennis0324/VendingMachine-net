import { ipcMain } from "electron";
import { Worker } from "worker_threads";
import crypto from "crypto";

export function createWorkerPool() {
  const worker = new Worker("./src/electron/worker.mjs");

  ipcMain.on("purchase", (event, data) => {
    worker.postMessage(data);
  });

  ipcMain.on("login", (event, id, pass) => {
    const hashedPassword = crypto
      .createHash("sha256")
      .update(pass)
      .digest("base64");
    worker.postMessage({ id, hashedPassword });
  });
  return worker;
}
