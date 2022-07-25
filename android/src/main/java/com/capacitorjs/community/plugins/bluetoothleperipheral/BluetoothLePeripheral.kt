package com.capacitorjs.community.plugins.bluetoothleperipheral

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import java.util.*
import kotlin.collections.HashMap


private const val TAG = "BluetoothLePeripheral"

public class BluetoothLePeripheral(ctx: Context, btm: BluetoothManager, bta: BluetoothAdapter) {
  private var context: Context = ctx
  private var bluetoothManager: BluetoothManager = btm
  private var bluetoothAdapter: BluetoothAdapter = bta
  private var gattServer: BluetoothGattServer? = null
  private val registeredDevices = mutableSetOf<BluetoothDevice>()
  private val gattServices = HashMap<String, BluetoothGattService>()
  private var callbacksMap = HashMap<String, (() -> Unit)>()
  private var subscriptions = HashMap<String, MutableList<BluetoothDevice>>()
  private val CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

  private val gattServerCallback = object : BluetoothGattServerCallback() {
    override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
      Log.d("GattServer", "Our gatt server connection state changed, new state ")
      Log.d("GattServer", newState.toString())
      super.onConnectionStateChange(device, status, newState)
    }

    override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
      Log.d("GattServer", "Our gatt server service was added.")
      super.onServiceAdded(status, service)
    }

    override fun onCharacteristicReadRequest(device: BluetoothDevice?, requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic) {
      Log.d("GattServer", "Our gatt characteristic was read.")
      super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
      gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
        characteristic.value)
    }

    override fun onCharacteristicWriteRequest(device: BluetoothDevice?, requestId: Int, characteristic: BluetoothGattCharacteristic?, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray) {
      Log.d("GattServer", "We have received a write request for one of our hosted characteristics")
      Log.d("GattServer", "data = $value")
      super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
      if (responseNeeded) {
        gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
      }
    }

    override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
      Log.d("GattServer", "onNotificationSent")
      super.onNotificationSent(device, status)
    }

    override fun onDescriptorReadRequest(device: BluetoothDevice?, requestId: Int, offset: Int, descriptor: BluetoothGattDescriptor?) {
      Log.d("GattServer", "Our gatt server descriptor was read.")
      super.onDescriptorReadRequest(device, requestId, offset, descriptor)
      gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, descriptor?.value)
    }

    override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
      Log.d("GattServer", "Our gatt server on execute write.")
      super.onExecuteWrite(device, requestId, execute)
    }

    override fun onDescriptorWriteRequest(device: BluetoothDevice, requestId: Int,
                                          descriptor: BluetoothGattDescriptor,
                                          preparedWrite: Boolean, responseNeeded: Boolean,
                                          offset: Int, value: ByteArray) {
      val characteristic = descriptor.characteristic
      val service = characteristic.service
      if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
        Log.d(TAG, "Subscribe device to notifications: $device")
        val key = subscribedCharacteristicKey(service.uuid, characteristic.uuid)
        if (callbacksMap.containsKey(key)) {
          callbacksMap[key]!!.invoke()
        }
        val subKey = "${service.uuid}${characteristic.uuid}"
        if (!subscriptions.containsKey(subKey)) {
          subscriptions[subKey] = mutableListOf()
        }
        if (!subscriptions[subKey]!!.contains(device)) {
          subscriptions[subKey]!!.add(device)
        }
      } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
        Log.d(TAG, "Unsubscribe device from notifications: $device")
        val key = unsubscribedCharacteristicKey(service.uuid, characteristic.uuid)
        if (callbacksMap.containsKey(key)) {
          callbacksMap[key]!!.invoke()
        }
        val subKey = "${service.uuid}${characteristic.uuid}"
        if (subscriptions.containsKey(subKey) && subscriptions[subKey]!!.contains(device)) {
          subscriptions[subKey]!!.remove(device)
          if (subscriptions[subKey]!!.size == 0) {
            subscriptions.remove(subKey)
          }
        }
      }
      super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value)
      if (responseNeeded) {
        gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
      }
    }
  }

  private val advertiseCallback = object : AdvertiseCallback() {
    override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
      Log.i(TAG, "LE Advertise Started.")
    }

    override fun onStartFailure(errorCode: Int) {
      Log.w(TAG, "LE Advertise Failed: $errorCode")
    }
  }

  fun isEnabled(): Boolean {
    return bluetoothAdapter.isEnabled
  }

  fun enable(): Boolean {
    return bluetoothAdapter.enable()
  }

  fun disable(): Boolean {
    return bluetoothAdapter.disable()
  }

  fun startServer() {
    gattServer = bluetoothManager.openGattServer(context, gattServerCallback)
  }

  fun addService(uuid: UUID, serviceType: Int): Boolean {
    if (gattServer == null) {
      return false
    }
    if (gattServices.containsKey(uuid.toString())) {
      return true
    }
    val service = BluetoothGattService(uuid, serviceType)
    gattServices[uuid.toString()] = service
    return true
  }

  fun advertise(serviceUuids: List<UUID>, mode: Int?, connectable: Boolean?, txPower: Int?): Boolean {
    if (gattServer == null || serviceUuids.isEmpty()) {
      return false
    }
    val bluetoothLeAdvertiser: BluetoothLeAdvertiser? =
      bluetoothManager.adapter.bluetoothLeAdvertiser
    gattServer!!.clearServices()
    bluetoothLeAdvertiser?.let {
      adv ->
      val settings = AdvertiseSettings.Builder()
        .setAdvertiseMode(mode ?: AdvertiseSettings.ADVERTISE_MODE_BALANCED)
        .setConnectable(connectable ?: true)
        .setTxPowerLevel(txPower ?: AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
        .build()
      var dataBuilder = AdvertiseData.Builder()
        .setIncludeDeviceName(true)
        .setIncludeTxPowerLevel(true)
      serviceUuids.forEach {
        dataBuilder = dataBuilder.addServiceUuid(ParcelUuid(it))
        val service = gattServices[it.toString()]
        if (service != null) {
          gattServer!!.addService(service)
        }
      }
      val data = dataBuilder.build()
      adv.startAdvertising(settings, data, advertiseCallback)
    }
    return true
  }

  fun addCharacteristic(serviceUuid: UUID, uuid: UUID, properties: Int, permissions: Int): Boolean {
    val service = gattServices[serviceUuid.toString()]
    if (gattServer == null || service == null) {
      return false
    }
    try {
      val characteristic = BluetoothGattCharacteristic(uuid, properties, permissions)
      val descriptor = BluetoothGattDescriptor(CLIENT_CONFIG, properties)
      characteristic.addDescriptor(descriptor)
      return service.addCharacteristic(characteristic)
    } catch (_: RuntimeException) {
    }
    return false
  }

  fun setCharacteristicValue(serviceUuid: UUID, uuid: UUID, value: String): Boolean {
    val service = gattServices[serviceUuid.toString()]
    if (gattServer == null || service == null) {
      return false
    }
    try {
      val characteristic = service.getCharacteristic(uuid)
      val bytes = stringToBytes(value)
      if (characteristic.setValue(bytes)) {
        val subKey = "${service.uuid}${characteristic.uuid}"
        subscriptions[subKey]?.forEach { device -> gattServer!!.notifyCharacteristicChanged(device, characteristic, false) }
      }
    } catch (_: RuntimeException) {
    }
    return false
  }

  fun onCharacteristicSubscribed(serviceUuid: UUID, uuid: UUID, callback: () -> Unit): Boolean {
    val service = gattServices[serviceUuid.toString()]
    if (gattServer == null || service == null) {
      return false
    }
    try {
      val characteristic = service.getCharacteristic(uuid)
      val result = characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY == BluetoothGattCharacteristic.PROPERTY_NOTIFY
      if (result) {
        val key = subscribedCharacteristicKey(serviceUuid, uuid)
        callbacksMap[key] = callback
      }
      return result
    } catch (_: RuntimeException) {
    }
    return false
  }

  fun onCharacteristicUnsubscribed(serviceUuid: UUID, uuid: UUID, callback: () -> Unit): Boolean {
    val service = gattServices[serviceUuid.toString()]
    if (gattServer == null || service == null) {
      return false
    }
    try {
      val characteristic = service.getCharacteristic(uuid)
      val result = characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY == BluetoothGattCharacteristic.PROPERTY_NOTIFY
      if (result) {
        val key = unsubscribedCharacteristicKey(serviceUuid, uuid)
        callbacksMap[key] = callback
      }
      return result
    } catch (_: RuntimeException) {
    }
    return false
  }

  fun unCharacteristicSubscribed(serviceUuid: UUID, uuid: UUID): Boolean {
    val service = gattServices[serviceUuid.toString()]
    if (gattServer == null || service == null) {
      return false
    }
    val key = subscribedCharacteristicKey(serviceUuid, uuid)
    if (callbacksMap.containsKey(key)) {
      callbacksMap.remove(key)
      return true
    }
    return false
  }

  fun unCharacteristicUnsubscribed(serviceUuid: UUID, uuid: UUID): Boolean {
    if (gattServer == null) {
      return false
    }
    val key = unsubscribedCharacteristicKey(serviceUuid, uuid)
    if (callbacksMap.containsKey(key)) {
      callbacksMap.remove(key)
      return true
    }
    return false
  }

  fun subscribedCharacteristicKey(serviceUuid: UUID, uuid: UUID): String {
    return "subscribedCharacteristic|${serviceUuid}|${uuid}"
  }

  fun unsubscribedCharacteristicKey(serviceUuid: UUID, uuid: UUID): String {
    return "subscribedCharacteristic|${serviceUuid}|${uuid}"
  }

}
