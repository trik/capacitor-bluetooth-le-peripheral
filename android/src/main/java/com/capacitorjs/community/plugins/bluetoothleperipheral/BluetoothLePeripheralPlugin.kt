package com.capacitorjs.community.plugins.bluetoothleperipheral;

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.Permission
import com.getcapacitor.annotation.PermissionCallback
import java.util.UUID

@CapacitorPlugin(
  name = "BluetoothLePeripheral",
  permissions = [
    Permission(
      strings = [
        Manifest.permission.ACCESS_COARSE_LOCATION,
      ], alias = "ACCESS_COARSE_LOCATION"
    ),
    Permission(
      strings = [
        Manifest.permission.ACCESS_FINE_LOCATION,
      ], alias = "ACCESS_FINE_LOCATION"
    ),
    Permission(
      strings = [
        Manifest.permission.BLUETOOTH,
      ], alias = "BLUETOOTH"
    ),
    Permission(
      strings = [
        Manifest.permission.BLUETOOTH_ADMIN,
      ], alias = "BLUETOOTH_ADMIN"
    ),
  ]
)
public class BluetoothLePeripheralPlugin : Plugin() {

  private var implementation: BluetoothLePeripheral? = null;
  private var aliases: Array<String> = arrayOf<String>()

  @PluginMethod
  fun initialize(call: PluginCall) {
    if (Build.VERSION.SDK_INT >= 31) {
      val neverForLocation = call.getBoolean("androidNeverForLocation", false) as Boolean
      if (neverForLocation) {
        call.resolve()
        return
      }
      aliases = arrayOf<String>(
        "ACCESS_FINE_LOCATION",
      )
    } else {
      aliases = arrayOf<String>(
        "ACCESS_COARSE_LOCATION",
        "ACCESS_FINE_LOCATION",
        "BLUETOOTH",
        "BLUETOOTH_ADMIN",
      )
    }
    requestPermissionForAliases(aliases, call, "checkPermission");
  }

  @PluginMethod
  fun isEnabled(call: PluginCall) {
    assertBluetoothAdapter(call) ?: return
    val enabled = implementation?.isEnabled() == true
    val result = JSObject()
    result.put("value", enabled)
    call.resolve(result)
  }

  @PluginMethod
  fun enable(call: PluginCall) {
    assertBluetoothAdapter(call) ?: return
    implementation?.enable()
    call.resolve()
  }

  @PluginMethod
  fun disable(call: PluginCall) {
    assertBluetoothAdapter(call) ?: return
    implementation?.disable()
    call.resolve()
  }

  @PluginMethod
  fun startServer(call: PluginCall) {
    assertBluetoothAdapter(call) ?: return
    implementation?.startServer()
    call.resolve()
  }

  @PluginMethod
  fun advertise(call: PluginCall) {
    assertBluetoothAdapter(call) ?: return
    val serviceUuids = call.getArray("uuids").toList<String>().mapNotNull { uuid -> parseUuidString(uuid) }
    val mode = call.getInt("mode")
    val connectable = call.getBoolean("connectable")
    val txPower = call.getInt("txPower")
    if (implementation?.advertise(serviceUuids, mode, connectable, txPower) == true) {
      call.resolve()
    } else {
      call.reject("Unable to start advertising")
    }
  }

  @PluginMethod
  fun addService(call: PluginCall) {
    assertBluetoothAdapter(call) ?: return
    val uuid: UUID? = parseUuidString(call.getString("uuid"))
    val serviceType: Int = call.getInt("serviceType", -1) as Int
    if (implementation == null || uuid == null || serviceType < 0) {
      call.reject("Invalid parameters")
    } else {
      val created = implementation!!.addService(uuid, serviceType)
      val result = JSObject()
      result.put("value", created)
      call.resolve(result)
    }
  }

  @PluginMethod
  fun addCharacteristic(call: PluginCall) {
    assertBluetoothAdapter(call) ?: return
    val serviceUuid: UUID? = parseUuidString(call.getString("service"))
    val uuid: UUID? = parseUuidString(call.getString("uuid"))
    val properties: Int = call.getInt("properties", -1) as Int
    val permissions: Int = call.getInt("permissions", -1) as Int
    if (implementation == null || serviceUuid == null || uuid == null || properties < 0 || permissions < 0) {
      call.reject("Invalid parameters")
    } else {
      val created = implementation!!.addCharacteristic(serviceUuid, uuid, properties, permissions)
      val result = JSObject()
      result.put("value", created)
      call.resolve(result)
    }
  }

  @PluginMethod
  fun setCharacteristicValue(call: PluginCall) {
    assertBluetoothAdapter(call) ?: return
    val serviceUuid: UUID? = parseUuidString(call.getString("service"))
    val uuid: UUID? = parseUuidString(call.getString("uuid"))
    val value = call.getString("value")
    if (implementation == null || serviceUuid == null || uuid == null || value == null) {
      call.reject("Invalid parameters")
    } else {
      val written = implementation!!.setCharacteristicValue(serviceUuid, uuid, value)
      val result = JSObject()
      result.put("value", written)
      call.resolve(result)
    }
  }

  @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
  fun onCharacteristicSubscribed(call: PluginCall) {
    val serviceUuid: UUID? = parseUuidString(call.getString("service"))
    val uuid: UUID? = parseUuidString(call.getString("uuid"))
    if (implementation == null || serviceUuid == null || uuid == null) {
      call.reject("Invalid parameters")
    } else {
      val result = implementation!!.onCharacteristicSubscribed(serviceUuid, uuid) {
        run {
          call.resolve()
        }
      }
      if (result) {
        call.setKeepAlive(true)
      } else {
        call.reject("Unable to set listener")
      }
    }
  }

  @PluginMethod
  fun onCharacteristicUnsubscribed(call: PluginCall) {
    val serviceUuid: UUID? = parseUuidString(call.getString("service"))
    val uuid: UUID? = parseUuidString(call.getString("uuid"))
    if (implementation == null || serviceUuid == null || uuid == null) {
      call.reject("Invalid parameters")
    } else {
      val result = implementation!!.onCharacteristicUnsubscribed(serviceUuid, uuid) {
        run {
          call.resolve()
        }
      }
      if (result) {
        call.setKeepAlive(true)
      } else {
        call.reject("Unable to set listener")
      }
    }
  }

  @PluginMethod
  fun unCharacteristicSubscribed(call: PluginCall) {
    val callbackId: String? = call.getString("callbackId")
    val serviceUuid: UUID? = parseUuidString(call.getString("service"))
    val uuid: UUID? = parseUuidString(call.getString("uuid"))
    if (implementation == null || callbackId == null || serviceUuid == null || uuid == null) {
      call.reject("Invalid parameters")
    } else {
      val result = implementation!!.unCharacteristicSubscribed(serviceUuid, uuid)
      if (result) {
        val savedCall = bridge.getSavedCall(callbackId)
        savedCall?.release(bridge)
        call.resolve()
      } else {
        call.reject("Unable to remove listener")
      }
    }
  }

  @PluginMethod
  fun unCharacteristicUnsubscribed(call: PluginCall) {
    val serviceUuid: UUID? = parseUuidString(call.getString("service"))
    val uuid: UUID? = parseUuidString(call.getString("uuid"))
    if (implementation == null || serviceUuid == null || uuid == null) {
      call.reject("Invalid parameters")
    } else {
      val result = implementation!!.unCharacteristicUnsubscribed(serviceUuid, uuid)
      if (result) {
        call.resolve()
      } else {
        call.reject("Unable to remove listener")
      }
    }
  }

  @PermissionCallback()
  private fun checkPermission(call: PluginCall) {
    val granted: List<Boolean> = aliases.map { alias ->
      getPermissionState(alias) == PermissionState.GRANTED
    }
    // all have to be true
    if (granted.all { it }) {
      runInitialization(call);
    } else {
      call.reject("Permission denied.")
    }
  }

  private fun runInitialization(call: PluginCall) {
    if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
      call.reject("BLE is not supported.")
      return
    }

    var bluetoothManager = activity.getSystemService(Context.BLUETOOTH_SERVICE)
      as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter

    if (bluetoothAdapter == null) {
      call.reject("BLE is not available.")
      return
    }
    implementation = BluetoothLePeripheral(context, bluetoothManager, bluetoothAdapter)
    call.resolve()
  }

  private fun assertBluetoothAdapter(call: PluginCall): Boolean? {
    if (implementation == null) {
      call.reject("Bluetooth LE not initialized.")
      return null
    }
    return true
  }

  private fun parseUuidString(uuid: String?): UUID? {
    if (uuid == null) {
      return null
    }
    try {
      return UUID.fromString(uuid)
    } catch (e: java.lang.RuntimeException) {}
    try {
      return UUID.fromString("0000$uuid-0000-1000-8000-00805F9B34FB")
    } catch (e: java.lang.RuntimeException) {}
    return null
  }
}
