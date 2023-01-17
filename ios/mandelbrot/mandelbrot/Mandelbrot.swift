//
//  Mandelbrot.swift
//  mandelbrot
//
//  Created by Leon von Tippelskirch on 11.01.23.
//

import Foundation
import UIKit

struct Mandelbrot {
    static func echo(str :String) -> String {
        
        let result = rust_echo(str)
        
        let swift_result = String(cString: result!)
        
        rust_echo_free(UnsafeMutablePointer<Int8>(mutating: result))
        
        return swift_result
    }
    
    static func render() -> UIImage? {
            
        let width = 384
        let height = 384
        
        let buffer = rust_render(Int32(width), Int32(height))
    
        let renderer = UIGraphicsImageRenderer(size: CGSize(width: width, height: height))

        let img = renderer.image { ctx in
            ctx.cgContext.setStrokeColor(UIColor.black.cgColor)
            
            for x in 0...width-1 {
                for y in 0...height-1 {
                    let currentBuffer = (buffer?[x*width+y] ?? 255)
                    
                    ctx.cgContext.setStrokeColor(CGColor(gray: CGFloat(currentBuffer)/255.0, alpha: 1.0))

                    if currentBuffer == 0 {
                        ctx.fill(CGRect(x: y, y: x, width: 2, height: 1))
                    }
                    
                }
            }
        }        
        
        return img
    }
    
    
    static func imageFromPixelValues(pixelValues: [UInt8]?, width: Int, height: Int) ->  CGImage?
    {
        var imageRef: CGImage?
        if let pixelValues = pixelValues {
            let bitsPerComponent = 8
            let bytesPerPixel = 1
            let bitsPerPixel = bytesPerPixel * bitsPerComponent
            let bytesPerRow = bytesPerPixel * width
            let totalBytes = width * height
            let unusedCallback: CGDataProviderReleaseDataCallback = { optionalPointer, pointer, valueInt in }
            let providerRef = CGDataProvider(dataInfo: nil, data: pixelValues, size: totalBytes, releaseData: unusedCallback)

            let bitmapInfo: CGBitmapInfo = [CGBitmapInfo(rawValue: CGImageAlphaInfo.none.rawValue), CGBitmapInfo(rawValue: CGImageByteOrderInfo.orderDefault.rawValue)]
            imageRef = CGImage(width: width,
                               height: height,
                               bitsPerComponent: bitsPerComponent,
                               bitsPerPixel: bitsPerPixel,
                               bytesPerRow: bytesPerRow,
                               space: CGColorSpaceCreateDeviceGray(),
                               bitmapInfo: bitmapInfo,
                               provider: providerRef!,
                               decode: nil,
                               shouldInterpolate: false,
                               intent: .defaultIntent)
        }

        return imageRef
    }
}
