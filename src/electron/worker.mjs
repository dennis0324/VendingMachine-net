// const net = require("net");
import { parentPort } from "worker_threads";
import { createTcpDTO } from "./util/tcpSocketDto.mjs";
import net from "net";

let client;
let host = "";

const workerCode = () => {
  async function run() {
    try {
      // client = net.connect(6666, "121.127.186.183");
      client = net.connect(6124);
      console.log(host);

      client.on("data", (data) => {
        console.log(data);
      });
    } catch (e) {
      console.log("can't connect to server");
      return;
    }
  }

  run();
};

parentPort.on("message", (data) => {
  console.log("parentPort", data);
  if (data?.cmd === undefined) {
    return;
  }

  let payload;
  switch (data.cmd) {
    case "handshake":
    case "products":
      payload = createTcpDTO(data.cmd, "");
      client.write(payload);
      break;
    case "purchase":
      payload = createTcpDTO(data.cmd, data.id, data.payload);
      client.write(payload);
      break;
  }

  if (data.cmd === "purchase") {
    client.write(JSON.stringify(data.payload));
  }
});
workerCode();
