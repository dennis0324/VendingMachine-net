import { ipcMain } from "electron";
import { Worker } from "worker_threads";

export function createWorkerPool() {
  const worker = new Worker("./src/electron/worker.mjs");

  ipcMain.on("purchase", (event, data) => {
    worker.postMessage(data);
  });
  return worker;
}
