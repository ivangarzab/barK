//
//  User.swift
//  barK-sample
//

import Foundation

struct User: Identifiable, Equatable {
    let id: String
    let name: String
    var isActive: Bool

    init(id: String, name: String, isActive: Bool = true) {
        self.id = id
        self.name = name
        self.isActive = isActive
    }
}