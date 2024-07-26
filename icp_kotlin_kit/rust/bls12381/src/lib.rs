#[allow(clippy::all)]
#[allow(dead_code)]
mod bls;

use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::jbyteArray;
use bls::bls12381::bls::{init, core_verify, BLS_OK};

#[no_mangle]
#[allow(non_snake_case)]
pub extern "C" fn Java_com_bity_icp_1kotlin_1kit_RustBindings_blsInstantiate(
    _env: JNIEnv,
    _: JClass
) -> i32 {
    if init() == BLS_OK {
        1
    } else {
        0
    }
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern "C" fn Java_com_bity_icp_1kotlin_1kit_RustBindings_blsVerify(
    env: JNIEnv,
    _: JClass,
    autograph: jbyteArray,
    message: jbyteArray,
    key: jbyteArray
) -> i32 {
    let autograph_array: Vec<u8> = env.convert_byte_array(autograph).unwrap();
    let message_array: Vec<u8> = env.convert_byte_array(message).unwrap();
    let key_array: Vec<u8> = env.convert_byte_array(key).unwrap();
    if core_verify(&autograph_array, &message_array, &key_array) == BLS_OK {
        1
    } else {
        0
    }
}

