// const net = require("net");
import { parentPort } from "worker_threads";
import { createTcpDTO } from "./util/tcpSocketDto.mjs";
import net from "net";
import dotenv from "dotenv";

dotenv.config();

let client = new net.Socket();
let host = "";
var intervalId = false;
console.log(">>> worker start Host: ", process.env.HOST);
function tryConnect() {
  console.log(">>> trying to connect...");
  try {
    client.connect(process.env.PORT, process.env.HOST);
  } catch (e) {
    tryConnectInterval();
  }
  // client.connect(6124);
}

function tryConnectInterval() {
  if (intervalId !== false) return;
  intervalId = setInterval(tryConnect, 5000);
}

function clearConnectInterval() {
  if (intervalId === false) return;
  clearInterval(intervalId);
  intervalId = false;
}

client.on("data", (data) => {
  console.log("rcv data:", data.toString());

  parentPort.postMessage(data.toString());
});

client.on("connect", () => {
  clearConnectInterval();
  console.log("tcp connected");
});

client.on("error", (err) => {
  console.log("tcp error:", err.code);
  tryConnectInterval();
});
client.on("end", () => {
  console.log("tcp closed try to reconnect");
  tryConnectInterval();
});
client.on("close", () => {
  console.log("tcp closed try to reconnect");
  tryConnectInterval();
});

parentPort.on("message", (data) => {
  console.log("send data:", data);
  if (data?.cmd === undefined) {
    return;
  }

  let payload;
  switch (data.cmd) {
    case "handshake":
    case "products":
    case "getMoney":
    case "getConstantProduct":
    case "quit":
      payload = createTcpDTO(data);
      client.write(payload);
      break;
    case "purchase":
      payload = createTcpDTO(data);
      client.write(payload);
      break;
    case "login":
      console.log("worker:", data);
      payload = createTcpDTO(data);
      client.write(payload);
      break;
  }
});

tryConnect();
