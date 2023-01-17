use jni::{
    // interface to the JVM
    JNIEnv,

    // JVM objects
    objects::{JClass, JString},

    // JVM return types
    sys::{jstring, jbyteArray},
    // sys::{jbyteArray, jint, jstring},
};

// android logging
#[cfg(target_os="android")]
use android_log_sys::{
    LogPriority::{INFO},
    __android_log_write,
};


use std::os::raw::{c_char};
use std::ffi::{*};

// Macro for logging
#[macro_export]
#[cfg(target_os="android")]
macro_rules! log_i {
    ($msg:expr) => {
        unsafe {
            let tag = CString::new("Mandelbrot").unwrap();
            let msg = CString::new($msg).unwrap();
            #[cfg(target_os="android")]
            __android_log_write(INFO as i32, tag.as_ptr(), msg.as_ptr());
        }
    };
}
#[cfg(not (target_os="android"))]
macro_rules! log_i {
    ($msg:expr) => {

    };
}

/// Reads an string input from JNI and returns the same string with a "Echo: " prefix.
///
/// # Examples
///
/// ```kotlin
/// val echo: String = echo("This is a test.")
///
/// assertEquals(expected = "Echo: This is a test", actual = echo)
/// ```
#[no_mangle]
pub extern "system" fn Java_pro_stuermer_mandelbrot_MandelbrotJNI_echo(
    env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    // Read the input string from JNI
    let input: String = env.get_string(input).expect("Could not read input!").into();

    // write to android log
    log_i!(format!("Read input: '{}'", input));

    let format = format!("Echo: {}", input);

    return env
        .new_string(format)
        .expect("Couldn't create jni string!")
        .into_raw();
}


#[no_mangle]
pub extern fn rust_echo(to: *const c_char) -> *mut c_char {
    let c_str = unsafe { CStr::from_ptr(to) };
    let recipient = match c_str.to_str() {
        Err(_) => "<empty>",
        Ok(string) => string,
    };

    CString::new("Echo: ".to_owned() + recipient).unwrap().into_raw()
}

#[no_mangle]
pub extern fn rust_echo_free(s: *mut c_char) {
    unsafe {
        if s.is_null() { return }
        CString::from_raw(s)
    };
}

#[no_mangle]
unsafe extern fn rust_render(length: c_int, width: c_int) -> *mut c_uchar {
    let upper_left = Complex { re: 0.35, im: -1.20 };
    let lower_right = Complex { re: -1.0, im: 0.20 };

    let b1 = length as usize;
    let b2 = width as usize;
    let bounds = (b1, b2);
    let mut pixels = vec![0; bounds.0 * bounds.1];
    render(&mut pixels, bounds, upper_left, lower_right);
    
    pixels.leak().as_mut_ptr()
 }


/// Plot/Render the Mandelbrot set and returns as byte array to JVM.
///
/// # Examples
///
/// ```kotlin
/// val bytes: ByteArray = render("4000x3000")
/// ```
#[no_mangle]
pub extern "system" fn Java_pro_stuermer_mandelbrot_MandelbrotJNI_render(
    env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jbyteArray {
    // Read the resolution from the JVM input.
    let _input: String = env.get_string(input).expect("Could not read resolution!").into();

    log_i!(format!("Input resolution: {}!", _input));

    let bounds = parse_pair::<usize>(&_input, 'x')
        .expect("error parsing image dimensions");

    log_i!(format!("Bounds: {} x {}", bounds.0, bounds.1));
    
    let upper_left = Complex { re: 0.35, im: -1.20 };
    let lower_right = Complex { re: -1.0, im: 0.20 };


    let mut pixels = vec![0; bounds.0 * bounds.1];

    render(&mut pixels, bounds, upper_left, lower_right);

    log_i!(format!("Pixels: '{:?}'", pixels));
    println!("{:?}", pixels);

    // Return the pixels as byte array.
    env.byte_array_from_slice(&pixels).unwrap()
}





use std::str::FromStr;

/// Parse the string `s` as a coordinate pair, like `"400x600"` or `"1.0,0.5"`.
///
/// Specifically, `s` should have the form <left><sep><right>, where <sep> is
/// the character given by the `separator` argument, and <left> and <right> are both
/// strings that can be parsed by `T::from_str`.
///
/// If `s` has the proper form, return `Some<(x, y)>`. If it doesn't parse
/// correctly, return `None`.
fn parse_pair<T: FromStr>(s: &str, separator: char) -> Option<(T, T)> {
    match s.find(separator) {
        None => None,
        Some(index) => {
            match (T::from_str(&s[..index]), T::from_str(&s[index + 1..])) {
                (Ok(l), Ok(r)) => Some((l, r)),
                _ => None
            }
        }
    }
}

#[test]
fn test_parse_pair() {
    assert_eq!(parse_pair::<i32>("",        ','), None);
    assert_eq!(parse_pair::<i32>("10,",     ','), None);
    assert_eq!(parse_pair::<i32>(",10",     ','), None);
    assert_eq!(parse_pair::<i32>("10,20",   ','), Some((10, 20)));
    assert_eq!(parse_pair::<i32>("10,20xy", ','), None);
    assert_eq!(parse_pair::<f64>("0.5x",    'x'), None);
    assert_eq!(parse_pair::<f64>("0.5x1.5", 'x'), Some((0.5, 1.5)));
}




use num::Complex;

/// Try to determine if `c` is in the Mandelbrot set, using at most `limit`
/// iterations to decide.
///
/// If `c` is not a member, return `Some(i)`, where `i` is the number of
/// iterations it took for `c` to leave the circle of radius two centered on the
/// origin. If `c` seems to be a member (more precisely, if we reached the
/// iteration limit without being able to prove that `c` is not a member),
/// return `None`.
fn escape_time(c: Complex<f64>, limit: usize) -> Option<usize> {
    let mut z = Complex { re: 0.0, im: 0.0 };
    for i in 0..limit {
        if z.norm_sqr() > 4.0 {
            return Some(i);
        }
        z = z * z + c;
    }

    None
}




/// Given the row and column of a pixel in the output image, return the
/// corresponding point on the complex plane.
///
/// `bounds` is a pair giving the width and height of the image in pixels.
/// `pixel` is a (column, row) pair indicating a particular pixel in that image.
/// The `upper_left` and `lower_right` parameters are points on the complex
/// plane designating the area our image covers.
fn pixel_to_point(bounds: (usize, usize),
                  pixel: (usize, usize),
                  upper_left: Complex<f64>,
                  lower_right: Complex<f64>)
    -> Complex<f64>
{
    let (width, height) = (lower_right.re - upper_left.re,
                           upper_left.im - lower_right.im);
    Complex {
        re: upper_left.re + pixel.0 as f64 * width  / bounds.0 as f64,
        im: upper_left.im - pixel.1 as f64 * height / bounds.1 as f64
        // Why subtraction here? pixel.1 increases as we go down,
        // but the imaginary component increases as we go up.
    }
}

#[test]
fn test_pixel_to_point() {
    assert_eq!(pixel_to_point((100, 200), (25, 175),
                              Complex { re: -1.0, im:  1.0 },
                              Complex { re:  1.0, im: -1.0 }),
               Complex { re: -0.5, im: -0.75 });
}





/// Render a rectangle of the Mandelbrot set into a buffer of pixels.
///
/// The `bounds` argument gives the width and height of the buffer `pixels`,
/// which holds one grayscale pixel per byte. The `upper_left` and `lower_right`
/// arguments specify points on the complex plane corresponding to the upper-
/// left and lower-right corners of the pixel buffer.
fn render(pixels: &mut [u8],
          bounds: (usize, usize),
          upper_left: Complex<f64>,
          lower_right: Complex<f64>)
{
    assert!(pixels.len() == bounds.0 * bounds.1);

    for row in 0..bounds.1 {
        for column in 0..bounds.0 {
            let point = pixel_to_point(bounds, (column, row),
                                       upper_left, lower_right);
            pixels[row * bounds.0 + column] =
                match escape_time(point, 255) {
                    None => 0,
                    Some(count) => 255 - count as u8
                };
        }
    }
}
