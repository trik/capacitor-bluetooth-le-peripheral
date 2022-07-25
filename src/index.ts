import { registerPlugin } from '@capacitor/core';

import type { BluetoothLePeripheralPlugin } from './definitions';

const BluetoothLePeripheral = registerPlugin<BluetoothLePeripheralPlugin>(
  'BluetoothLePeripheral',
  {
    web: () => import('./web').then(m => new m.BluetoothLePeripheralWeb()),
  },
);

export * from './conversion';
export * from './definitions';
export { BluetoothLePeripheral };
