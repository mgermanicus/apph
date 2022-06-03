export const deepCopy = <T>(list: T[]) => {
  return JSON.parse(JSON.stringify(list));
};
