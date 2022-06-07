export const randomColorCodeFromString = (string: string) => {
  let hash = 0;
  let i;

  for (i = 0; i < string.length; i += 1) {
    hash = string.charCodeAt(i) + ((hash << 5) - hash);
  }

  let color = '#';

  for (i = 0; i < 3; i += 1) {
    const value = (hash >> (i * 8)) & 0xff;
    color += `00${value.toString(16)}`.slice(-2);
  }
  return color;
};

export const randomHSL = (string: string) => {
  let hash = 0;
  let i;
  for (i = 0; i < string.length; i += 1) {
    hash = string.charCodeAt(i) + ((hash << 5) - hash);
  }
  const valueH = (Math.abs(hash) % 1000) / 1000;
  return (
    'hsl(' +
    360 * valueH +
    ',' +
    (25 + 70 * valueH) +
    '%,' +
    (85 + 10 * valueH) +
    '%)'
  );
};
