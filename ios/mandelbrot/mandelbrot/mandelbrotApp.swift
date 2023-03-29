//
//  mandelbrotApp.swift
//  mandelbrot
//
//  Created by Leon von Tippelskirch on 11.01.23.
//

import SwiftUI

@main
struct mandelbrotApp: App {
    var body: some Scene {
        WindowGroup {
            MandelbrotView(image: UIImage(systemName: "link.badge.plus")!)
        }
    }
}

