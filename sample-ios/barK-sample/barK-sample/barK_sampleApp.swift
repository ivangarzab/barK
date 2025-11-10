//
//  barK_sampleApp.swift
//  barK-sample
//
//  Created by Ivan Garza on 8/15/25.
//

import SwiftUI
import shared

/**
 * The purpose of this class is to serve as the main App entry point of this
 * sample project, while staying open for extension in different environments.
 */
@main
struct barK_sampleApp: App {
    init() {
        startLogger()
    }

    var body: some Scene {
        WindowGroup {
            SampleView()
        }
    }

    private func startLogger() {
        #if DEBUG
        let volume = Level.verbose
        #else
        let volume = Level.debug
        #endif

        Bark.autoTagDisabled = false
        Bark.train(trainer: NSLogTrainer(volume: volume))
        Bark.v("barK logger has started")
    }
}
