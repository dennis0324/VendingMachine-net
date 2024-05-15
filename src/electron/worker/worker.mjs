// const net = require("net");
import { parentPort } from "worker_threads";
import { createTcpDTO } from "../util/tcpSocketDto.mjs";
import net from "net";
import dotenv from "dotenv";

// load env
dotenv.config();

let HOST = process.env.HOST;
let PORT = process.env.PORT;
if (process.env.MODE === "development") {
  HOST = process.env.DEV_HOST;
  PORT = process.env.DEV_PORT;
}
// create tcp client
let client = new net.Socket();

// flag for unconnected state
var intervalId = false;
var longData = 0;
console.log(">>> worker start Host: ", HOST);
function tryConnect() {
  console.log(">>> trying to connect...");
  try {
    client.connect(PORT, HOST);
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
var sendBuffer = [];
var sendLength = 0;
// var sendBuffer = new Uint8Array();
function getLongdata(data) {
  if (longData > sendBuffer.length) {
    sendBuffer.push(data);
    sendLength += data.toString().length;
    console.log(sendLength, longData);
  }

  // last '\0' was not included
  if (longData + 1 === sendLength) {
    const concatBuffer = Buffer.from(...sendBuffer);
    sendBuffer = [];
    sendLength = 0;
    longData = 0;
    console.log("sendBuffer", concatBuffer.toString());
    parentPort.postMessage(concatBuffer.toString());
  }
}
/**
 * TCP event handler for receive data
 */
client.on("data", (data) => {
  // console.log("rcv data:", data.toString());
  const checkLength = data.toString().split("|")[0];
  if (checkLength === "length") {
    console.log(data.toString().split("|")[1]);
    longData = Number(data.toString().split("|")[1]);
    return;
  }

  if (longData > 0) getLongdata(data);
  else parentPort.postMessage(data.toString());
  // parentPort.postMessage(data.toString());
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
  // console.log("send data:", data);
  if (data?.cmd === undefined) {
    return;
  }

  const payload = createTcpDTO(data);
  client.write(payload);
});

tryConnect();
