//
//  MandelbrotView.swift
//  mandelbrot
//
//  Created by Leon von Tippelskirch on 11.01.23.
//

import SwiftUI

struct MandelbrotView: View {
    @State public var image:UIImage = UIImage(systemName: "multiply.circle.fill")!

    
    var body: some View {
        VStack {
            Text("Hello Rust!")
            Button("Echo") {
                print(Mandelbrot.echo(str:" Echo"))
            }
            
            Button(action: {
                if let newImage = Mandelbrot.render() {
                    self.image = newImage
                } else {
                    self.image = UIImage(systemName: "multiply.circle.fill")!
                }
             }, label: {
                 Text("Change the image")
             })
            Image(uiImage: image).frame(width: 256.0, height: 256.0)


        }
        .padding()
    }
}

struct MandelbrotView_Previews: PreviewProvider {
    static var previews: some View {
        MandelbrotView(image: UIImage(systemName: "link.badge.plus")!)
    }
}
