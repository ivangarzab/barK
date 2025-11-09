//
//  ContentView.swift
//  barK-sample
//
//  Created by Ivan Garza on 8/15/25.
//

import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")
        }
        .padding()
        .onAppear {
            Bark.train(trainer: NSLogTrainer(volume: Level.verbose))
            Bark.i("barK works on iOS!", throwable: nil)
        }
    }
}

#Preview {
    ContentView()
}
