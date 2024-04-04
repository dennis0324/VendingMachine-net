export function createTcpDTO(cmd, id, payload) {
  if (payload === undefined) payload = "";
  const payloadStringify = JSON.stringify(payload);
  return [cmd, id, payloadStringify].join("|");
}
