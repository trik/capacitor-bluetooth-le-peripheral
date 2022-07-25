export function dataViewToNumbers(value: DataView): number[] {
  return Array.from(new Uint8Array(value.buffer));
}

export function uint8ArrayToNumbers(value: Uint8Array): number[] {
  return Array.from(value);
}

export function dataViewToHexString(value: DataView): string {
  return dataViewToNumbers(value)
    .map(n => {
      let s = n.toString(16);
      if (s.length == 1) {
        s = '0' + s;
      }
      return s;
    })
    .join(' ');
}

export function uint8ArrayToHexString(value: Uint8Array): string {
  return uint8ArrayToNumbers(value)
    .map(n => {
      let s = n.toString(16);
      if (s.length == 1) {
        s = '0' + s;
      }
      return s;
    })
    .join(' ');
}
