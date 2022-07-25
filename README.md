# capacitor-bluetooth-le-peripheral

Capacitor plugin for Bluetooth Low Energy Peripheral

## Install

```bash
npm install capacitor-bluetooth-le-peripheral
npx cap sync
```

## API

<docgen-index>

* [`initialize()`](#initialize)
* [`isEnabled()`](#isenabled)
* [`enable()`](#enable)
* [`disable()`](#disable)
* [`startServer()`](#startserver)
* [`advertise(...)`](#advertise)
* [`addService(...)`](#addservice)
* [`addCharacteristic(...)`](#addcharacteristic)
* [`setCharacteristicValue(...)`](#setcharacteristicvalue)
* [`onCharacteristicSubscribed(...)`](#oncharacteristicsubscribed)
* [`onCharacteristicUnsubscribed(...)`](#oncharacteristicunsubscribed)
* [`unCharacteristicSubscribed(...)`](#uncharacteristicsubscribed)
* [`unCharacteristicUnsubscribed(...)`](#uncharacteristicunsubscribed)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize()

```typescript
initialize() => Promise<void>
```

--------------------


### isEnabled()

```typescript
isEnabled() => Promise<boolean>
```

**Returns:** <code>Promise&lt;boolean&gt;</code>

--------------------


### enable()

```typescript
enable() => Promise<boolean>
```

**Returns:** <code>Promise&lt;boolean&gt;</code>

--------------------


### disable()

```typescript
disable() => Promise<boolean>
```

**Returns:** <code>Promise&lt;boolean&gt;</code>

--------------------


### startServer()

```typescript
startServer() => Promise<void>
```

--------------------


### advertise(...)

```typescript
advertise(options: AdvertiseOptions) => Promise<void>
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#advertiseoptions">AdvertiseOptions</a></code> |

--------------------


### addService(...)

```typescript
addService(options: AddServiceOptions) => Promise<boolean>
```

| Param         | Type                                                            |
| ------------- | --------------------------------------------------------------- |
| **`options`** | <code><a href="#addserviceoptions">AddServiceOptions</a></code> |

**Returns:** <code>Promise&lt;boolean&gt;</code>

--------------------


### addCharacteristic(...)

```typescript
addCharacteristic(options: AddCharacteristicOptions) => Promise<boolean>
```

| Param         | Type                                                                          |
| ------------- | ----------------------------------------------------------------------------- |
| **`options`** | <code><a href="#addcharacteristicoptions">AddCharacteristicOptions</a></code> |

**Returns:** <code>Promise&lt;boolean&gt;</code>

--------------------


### setCharacteristicValue(...)

```typescript
setCharacteristicValue(options: SetCharacteristicValueOptions) => Promise<boolean>
```

| Param         | Type                                                                                    |
| ------------- | --------------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#setcharacteristicvalueoptions">SetCharacteristicValueOptions</a></code> |

**Returns:** <code>Promise&lt;boolean&gt;</code>

--------------------


### onCharacteristicSubscribed(...)

```typescript
onCharacteristicSubscribed(options: CharacteristicOptions, callback: () => void) => Promise<CallbackID>
```

| Param          | Type                                                                    |
| -------------- | ----------------------------------------------------------------------- |
| **`options`**  | <code><a href="#characteristicoptions">CharacteristicOptions</a></code> |
| **`callback`** | <code>() =&gt; void</code>                                              |

**Returns:** <code>Promise&lt;string&gt;</code>

--------------------


### onCharacteristicUnsubscribed(...)

```typescript
onCharacteristicUnsubscribed(options: CharacteristicOptions, callback: () => void) => Promise<CallbackID>
```

| Param          | Type                                                                    |
| -------------- | ----------------------------------------------------------------------- |
| **`options`**  | <code><a href="#characteristicoptions">CharacteristicOptions</a></code> |
| **`callback`** | <code>() =&gt; void</code>                                              |

**Returns:** <code>Promise&lt;string&gt;</code>

--------------------


### unCharacteristicSubscribed(...)

```typescript
unCharacteristicSubscribed(options: RemoveCharacteristicWatcherOptions) => Promise<void>
```

| Param         | Type                                                                                              |
| ------------- | ------------------------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#removecharacteristicwatcheroptions">RemoveCharacteristicWatcherOptions</a></code> |

--------------------


### unCharacteristicUnsubscribed(...)

```typescript
unCharacteristicUnsubscribed(options: RemoveCharacteristicWatcherOptions) => Promise<void>
```

| Param         | Type                                                                                              |
| ------------- | ------------------------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#removecharacteristicwatcheroptions">RemoveCharacteristicWatcherOptions</a></code> |

--------------------


### Interfaces


#### AdvertiseOptions

| Prop              | Type                  |
| ----------------- | --------------------- |
| **`uuids`**       | <code>string[]</code> |
| **`mode`**        | <code>number</code>   |
| **`connectable`** | <code>boolean</code>  |
| **`txPower`**     | <code>number</code>   |


#### AddServiceOptions

| Prop              | Type                |
| ----------------- | ------------------- |
| **`uuid`**        | <code>string</code> |
| **`serviceType`** | <code>number</code> |


#### AddCharacteristicOptions

| Prop              | Type                |
| ----------------- | ------------------- |
| **`properties`**  | <code>number</code> |
| **`permissions`** | <code>number</code> |


#### SetCharacteristicValueOptions

| Prop           | Type                 |
| -------------- | -------------------- |
| **`value`**    | <code>string</code>  |
| **`indicate`** | <code>boolean</code> |


#### CharacteristicOptions

| Prop          | Type                |
| ------------- | ------------------- |
| **`service`** | <code>string</code> |
| **`uuid`**    | <code>string</code> |


#### RemoveWatcherOptions

| Prop             | Type                                              |
| ---------------- | ------------------------------------------------- |
| **`callbackId`** | <code><a href="#callbackid">CallbackID</a></code> |


### Type Aliases


#### CallbackID

<code>string</code>


#### RemoveCharacteristicWatcherOptions

<code><a href="#characteristicoptions">CharacteristicOptions</a> & <a href="#removewatcheroptions">RemoveWatcherOptions</a></code>

</docgen-api>
