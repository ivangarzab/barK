//
//  NotificationService.swift
//  barK-sample
//

import Foundation
import shared

class NotificationService {
    func showSuccess(_ message: String) {
        Bark.i("Success notification: \(message)")
        // In real app, would show toast/banner
    }

    func showError(_ message: String) {
        Bark.e("Error notification: \(message)")
        // In real app, would show error dialog
    }

    func showWarning(_ message: String) {
        Bark.w("Warning notification: \(message)")
        // In real app, would show warning banner
    }

    func showInfo(_ message: String) {
        Bark.d("Info notification: \(message)")
        // In real app, would show info banner
    }
}
