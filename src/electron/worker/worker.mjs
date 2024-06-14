import { parentPort } from "worker_threads";
import { createTcpDTO } from "../util/tcpSocketDto.mjs";
import net from "net";

let HOST = process.env.HOST;
let PORT = process.env.PORT;
if (process.env.MODE === "development") {
  HOST = process.env.DEV_HOST;
  PORT = process.env.DEV_PORT;
}
// create tcp client
let client = new net.Socket();
let arrived;

// flag for unconnected state
var intervalId = false;
var longData = 0;
console.log(">>> worker start Host: ", HOST);

const sendQueue = [];
let isConnected = false;
const IDLE = 0;
const SEND_INIT = 1;
const RECV_INIT = 2;
const COMP_INIT = 3;
let handShakeStatus = IDLE;

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
  console.log("rcv data:", data.toString().split("|")[0]);
  const cmd = data.toString().split("|")[0];
  if(arrived) arrived();
  if (cmd === "length") {
    // console.log(data.toString().split("|")[1]);
    longData = Number(data.toString().split("|")[1]);
    return;
  }
  if (cmd === "handshake"){
    handShakeStatus = RECV_INIT;
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
  isConnected = true;
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

  if(!isConnected || handShakeStatus !== COMP_INIT){
    console.log("pushed");
    sendQueue.push(data)
    return
  }

  const payload = createTcpDTO(data);
  console.log(payload);
  client.write(payload);
});
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}
let running = false;
const connectIntveral = setInterval(async () => {
  if(running) return;
  if(isConnected && handShakeStatus === COMP_INIT) clearInterval(connectIntveral);
  if(handShakeStatus === RECV_INIT){
    running = true;
    for await (const e of sendQueue) {
      
      const waitArrived = new Promise(resolve => {arrived = resolve})
      const payload = createTcpDTO(e);
      client.write(payload);
      await waitArrived;
      arrived = null;
    }
    
    handShakeStatus = COMP_INIT;
  }
  if(handShakeStatus === IDLE){
    const indexHandshake = sendQueue.findIndex((e) => e.cmd === 'handshake');
    const sendData = sendQueue.splice(indexHandshake,1)[0]
    const payload = createTcpDTO(sendData);
    client.write(payload);
    handShakeStatus = SEND_INIT;
  }

},500);

tryConnect();
