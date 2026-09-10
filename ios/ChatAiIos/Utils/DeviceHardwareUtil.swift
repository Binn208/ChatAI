import Foundation
#if canImport(UIKit)
import UIKit
#endif
import Darwin

public final class DeviceHardwareUtil {

    public static var totalRamBytes: Int64 {
        return Int64(ProcessInfo.processInfo.physicalMemory)
    }

    public static var availableMemoryBytes: Int64 {
        #if os(iOS)
        if #available(iOS 13.0, *) {
            return Int64(os_proc_available_memory())
        }
        #endif

        var vmStats = vm_statistics64()
        var count = mach_msg_type_number_t(MemoryLayout<vm_statistics64>.size / MemoryLayout<integer_t>.size)
        let hostPort = mach_host_self()

        let result = withUnsafeMutablePointer(to: &vmStats) {
            $0.withMemoryRebound(to: integer_t.self, capacity: Int(count)) {
                host_statistics64(hostPort, HOST_VM_INFO64, $0, &count)
            }
        }

        if result == KERN_SUCCESS {
            let pageSize = vm_kernel_page_size
            let freeBytes = Int64(vmStats.free_count) * Int64(pageSize)
            let inactiveBytes = Int64(vmStats.inactive_count) * Int64(pageSize)
            return freeBytes + inactiveBytes
        }

        return totalRamBytes / 3 // Fallback ước lượng an toàn
    }

    public static var formattedTotalRam: String {
        let gb = Double(totalRamBytes) / (1024.0 * 1024.0 * 1024.0)
        return String(format: "%.1f GB", gb)
    }

    public static var formattedAvailableRam: String {
        let mb = Double(availableMemoryBytes) / (1024.0 * 1024.0)
        if mb >= 1024.0 {
            return String(format: "%.2f GB", mb / 1024.0)
        }
        return String(format: "%.0f MB", mb)
    }

    public static func canSafelyRunModel(requiredVramBytes: Int64) -> Bool {
        // iOS Jetsam Threshold: giữ buffer ít nhất 400MB cho OS và UI
        let safeMargin: Int64 = 400 * 1024 * 1024
        return availableMemoryBytes > (requiredVramBytes + safeMargin)
    }

    public static var hardwareSummary: String {
        let total = formattedTotalRam
        let avail = formattedAvailableRam
        return "RAM: \(avail) khả dụng / \(total) tổng"
    }
}
