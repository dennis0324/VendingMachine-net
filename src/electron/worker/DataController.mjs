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

  arrived(hash) {
    return this.ipcHash.get(hash);
  }

  remove(hash) {
    const ipcDto = this.ipcHash.get(hash);

    if (ipcDto === undefined) return;

    let jsonPayload = "";
    try {
      jsonPayload = JSON.parse(payload);
    } catch (e) {
      jsonPayload = payload;
    }
    this.ipcHash.delete(hash);
    ipcDto.resolve(payload);
  }
}
