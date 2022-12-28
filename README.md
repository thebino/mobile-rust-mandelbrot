# Android Rust Mandelbrot

This project is a simplified showcase to generate [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set) with the [Rust Programming Language](https://www.rust-lang.org/) and use it as a native library within a mobile app (Android / iOS).

Inspired by Google's [Comprehensive Rust Course](https://google.github.io/comprehensive-rust/) and [Programming Rust: ](https://www.amazon.de/Programming-Rust-Systems-Development-English-ebook/dp/B0979PWD4Z/)


![Android Mandelbrot](./demo_android.png?raw=true)

## Rust

### Requirements

 * [cargo-ndk](https://github.com/bbqsrc/cargo-ndk) Cargo extension to compile against Android NDK
 * [cargo-cocoapods](https://github.com/bbqsrc/cargo-cocoapods) Cargo extension to compile for Xcode integration


## Android 

To build the rust code to a native library for android, compile with the **ndk extension**

```shell
$ cd rust
$ cargo ndk -t armeabi-v7a -t arm64-v8a -t x86 -t x86_64 -o ../android/app/src/main/jniLibs build --release
```

Build the android project (including the native library) with gradle
```shell
$ ./gradlew :app:installDebug
```

## iOS


## Documentation
```shell
$ cargo doc --open
```
