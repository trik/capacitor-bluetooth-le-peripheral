export type CallbackID = string;

export interface AddServiceOptions {
  uuid: string;
  serviceType: number;
}

export interface CharacteristicOptions {
  service: string;
  uuid: string;
}

export interface AddCharacteristicOptions extends CharacteristicOptions {
  properties: number;
  permissions: number;
}

export interface SetCharacteristicValueOptions extends CharacteristicOptions {
  value: string;
  indicate?: boolean;
}

export interface RemoveWatcherOptions {
  callbackId: CallbackID;
}

export interface AdvertiseOptions {
  uuids: string[];
  mode?: number;
  connectable?: boolean;
  txPower?: number;
}

export type RemoveCharacteristicWatcherOptions = CharacteristicOptions &
  RemoveWatcherOptions;

export const enum ServiceType {
  Primary = 0,
  Secondary = 1,
}

export const enum CharacteristicProperty {
  Broadcast = 1,
  ExtendedProps = 128,
  Indicate = 32,
  Notify = 16,
  Read = 2,
  SignedWrite = 64,
  Write = 8,
  WriteNoResponse = 4,
}

export const enum CharacteristicPermission {
  Read = 1,
  ReadEncrypted = 2,
  ReadEncryptedMITM = 4,
  Write = 16,
  WriteEncrypted = 32,
  WriteEncryptedMITM = 64,
  WriteSigned = 128,
  WriteSignedMITM = 256,
}

export interface BluetoothLePeripheralPlugin {
  initialize(): Promise<void>;
  isEnabled(): Promise<boolean>;
  enable(): Promise<boolean>;
  disable(): Promise<boolean>;
  startServer(): Promise<void>;
  advertise(options: AdvertiseOptions): Promise<void>;
  addService(options: AddServiceOptions): Promise<boolean>;
  addCharacteristic(options: AddCharacteristicOptions): Promise<boolean>;
  setCharacteristicValue(
    options: SetCharacteristicValueOptions,
  ): Promise<boolean>;
  onCharacteristicSubscribed(
    options: CharacteristicOptions,
    callback: () => void,
  ): Promise<CallbackID>;
  onCharacteristicUnsubscribed(
    options: CharacteristicOptions,
    callback: () => void,
  ): Promise<CallbackID>;
  unCharacteristicSubscribed(
    options: RemoveCharacteristicWatcherOptions,
  ): Promise<void>;
  unCharacteristicUnsubscribed(
    options: RemoveCharacteristicWatcherOptions,
  ): Promise<void>;
}
