export function createTcpDTO(ipcDto) {
  const arr = Object.entries(ipcDto).map(([k, v]) => {
    return k === "payload" ? JSON.stringify(v) : v;
  });
  return arr.join("|") + "\n";
}
