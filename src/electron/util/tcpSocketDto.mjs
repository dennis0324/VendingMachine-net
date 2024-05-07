import * as types from "../../utils/typedefs.js";

/**
 * create TCP Data Transfer Object
 *
 * @param {types.ipcDto} ipcDto
 */
export function createTcpDTO(ipcDto) {
  const arr = Object.entries(ipcDto).map(([k, v]) => {
    return k === "payload" ? JSON.stringify(v) : v;
  });
  return arr.join("|") + "\n";
}
