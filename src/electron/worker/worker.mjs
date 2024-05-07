// const net = require("net");
import { parentPort } from "worker_threads";
import { createTcpDTO } from "../util/tcpSocketDto.mjs";
import net from "net";
import dotenv from "dotenv";

// load env
dotenv.config();

// create tcp client
let client = new net.Socket();

// flag for unconnected state
var intervalId = false;
console.log(">>> worker start Host: ", process.env.HOST);
function tryConnect() {
  console.log(">>> trying to connect...");
  try {
    client.connect(process.env.PORT, process.env.HOST);
  } catch (e) {
    tryConnectInterval();
  }
}

/**
 * execute interval for reconnect
 */
function tryConnectInterval() {
  if (intervalId !== false) return;
  intervalId = setInterval(tryConnect, 5000);
}

/**
 * delete interval
 */
function clearConnectInterval() {
  if (intervalId === false) return;
  clearInterval(intervalId);
  intervalId = false;
}

/**
 * TCP event handler for receive data
 */
client.on("data", (data) => {
  console.log("rcv data:", data.toString());
  parentPort.postMessage(data.toString());
});

/**
 * TCP event handler when tcp connected
 */
client.on("connect", () => {
  clearConnectInterval();
  console.log(">> tcp connected");
});

/**
 * TCP event handler when tcp got error
 */
client.on("error", (err) => {
  console.log(">>> tcp error:", err.code);
  tryConnectInterval();
});

/**
 * TCP event handler when tcp end
 */
client.on("end", () => {
  console.log(">>> tcp closed try to reconnect");
  tryConnectInterval();
});

/**
 * TCP event handler when tcp close
 */
client.on("close", () => {
  console.log(">>> tcp closed try to reconnect");
  tryConnectInterval();
});

/**
 * TCP event handler when server send message
 */
parentPort.on("message", (data) => {
  console.log("send data:", data);
  if (data?.cmd === undefined) {
    return;
  }

  const payload = createTcpDTO(data);
  client.write(payload);
});

tryConnect();
