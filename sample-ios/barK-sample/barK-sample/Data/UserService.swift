//
//  UserService.swift
//  barK-sample
//

import Foundation
import shared

/**
 * Sample service class to demonstrate auto-tag detection
 */
class UserService {
    func performAction() {
        // Should show tag "UserService"
        Bark.i("UserService performing action")
        Bark.d("Action completed successfully")
    }
}
