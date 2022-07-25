import { WebPlugin } from '@capacitor/core';

import type {
  AddCharacteristicOptions,
  AddServiceOptions,
  AdvertiseOptions,
  BluetoothLePeripheralPlugin,
  CallbackID,
  CharacteristicOptions,
  RemoveCharacteristicWatcherOptions,
  SetCharacteristicValueOptions,
} from './definitions';

export class BluetoothLePeripheralWeb
  extends WebPlugin
  implements BluetoothLePeripheralPlugin {
  private notImplemented = 'Not implemented on web.';
  async initialize(): Promise<void> {
    throw this.unimplemented(this.notImplemented);
  }

  async isEnabled(): Promise<boolean> {
    throw this.unimplemented(this.notImplemented);
  }

  async enable(): Promise<boolean> {
    throw this.unimplemented(this.notImplemented);
  }

  async disable(): Promise<boolean> {
    throw this.unimplemented(this.notImplemented);
  }

  async startServer(): Promise<void> {
    throw this.unimplemented(this.notImplemented);
  }

  async advertise(_: AdvertiseOptions): Promise<void> {
    throw this.unimplemented(this.notImplemented);
  }

  async addService(_: AddServiceOptions): Promise<boolean> {
    throw this.unimplemented(this.notImplemented);
  }

  async addCharacteristic(_: AddCharacteristicOptions): Promise<boolean> {
    throw this.unimplemented(this.notImplemented);
  }

  async setCharacteristicValue(
    _: SetCharacteristicValueOptions,
  ): Promise<boolean> {
    throw this.unimplemented(this.notImplemented);
  }

  async onCharacteristicSubscribed(
    _: CharacteristicOptions,
    __: () => void,
  ): Promise<CallbackID> {
    throw this.unimplemented(this.notImplemented);
  }

  async onCharacteristicUnsubscribed(
    _: CharacteristicOptions,
    __: () => void,
  ): Promise<CallbackID> {
    throw this.unimplemented(this.notImplemented);
  }

  async unCharacteristicSubscribed(
    _: RemoveCharacteristicWatcherOptions,
  ): Promise<void> {
    throw this.unimplemented(this.notImplemented);
  }

  async unCharacteristicUnsubscribed(
    _: RemoveCharacteristicWatcherOptions,
  ): Promise<void> {
    throw this.unimplemented(this.notImplemented);
  }
}
