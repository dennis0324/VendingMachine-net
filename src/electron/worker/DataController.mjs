import crypto from "crypto";
export class DataController {
  ipcHash = new Map();

  createHash(ipcDto) {
    const joined = [ipcDto.cmd, ipcDto.vendingID, ipcDto.date].join("");
    return crypto.createHash("sha1").update(joined).digest("hex");
  }

  append(ipcDto) {
    if (ipcDto.hash === "") ipcDto.hash = this.createHash(ipcDto);

    ipcDto.arrived = new Promise((res, rej) => {
      ipcDto.resolve = res;
      ipcDto.reject = rej;
    });

    this.ipcHash.set(ipcDto.hash, ipcDto);

    const postData = {
      cmd: ipcDto.cmd,
      hash: ipcDto.hash,
      vendingId: ipcDto.vendingId,
      date: ipcDto.date,
      payload: ipcDto.payload,
    };
    return postData;
  }

  async arrived(hash) {
    return await this.ipcHash.get(hash).arrived;
  }

  setData(hash,data){
    const ipcDto = this.ipcHash.get(hash);
    ipcDto.payload = data;
  }

  remove(hash) {
    const ipcDto = this.ipcHash.get(hash);

    if (ipcDto === undefined) return;
    let jsonPayload = "";
    try {
      jsonPayload = JSON.parse(ipcDto.payload);
    } catch (e) {
      jsonPayload = ipcDto.payload;
    }
    this.ipcHash.delete(hash);
    ipcDto.resolve(jsonPayload);
  }
}
