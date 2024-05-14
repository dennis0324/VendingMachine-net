import crypto from "crypto";
import * as types from "../../utils/typedefs.js";
import { v4 as uuidv4 } from "uuid";

export class DataController {
  ipcHash = new Map();

  /**
   * create hash value with cmd, vendingId, date for preventing duplicate request
   *
   * @param {types.ipcDto} ipcDto
   * @return {string} hashValue
   */
  createHash(ipcDto) {
    return uuidv4();
  }

  /**
   * append ipcDto to ipchash for waiting response
   *
   * @param {types.ipcDto} ipcDto
   * @return {types.ipcDto}
   */
  append(ipcDto) {
    if (ipcDto.hash === "") ipcDto.hash = this.createHash(ipcDto);

    // make promise for waiting response
    // 해당 요청을 보내고 응답을 기다리게 하기 위해 만든 프로미스이다.
    // 메인 스레드와 자식 스레드의 통신은 이벤트 핸들러로 관리하기 때문에 프로미스를 사용하여도 무방하다.
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

  /**
   * wait hash until response arrived to worker
   *
   * @param {string} hash
   * @return {Promise<string>} payload that not parsed
   */
  async arrived(hash) {
    return await this.ipcHash.get(hash).arrived;
  }

  /**
   * set payload for update when message arrived
   *
   * @param {string} hash
   * @param {Object} data
   */
  setData(hash, data) {
    const ipcDto = this.ipcHash.get(hash);
    ipcDto.payload = data;
  }

  /**
   * remove hahs from ipchash when message arrived
   *
   * @param {string} hash
   */
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
