import Foundation

@objc public class BluetoothLePeripheral: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
