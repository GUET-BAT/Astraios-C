import 'package:flutter/services.dart';

class NativeChannelService {
  static const String _channelName = 'com.example.guetapp/native';
  static const MethodChannel _channel = MethodChannel(_channelName);

  static Future<bool> showToast(String message) async {
    try {
      final result = await _channel.invokeMethod<bool>(
        'showToast',
        {'message': message},
      );
      return result ?? false;
    } on PlatformException catch (e) {
      print('Error showing toast: ${e.message}');
      return false;
    }
  }

  static Future<Map<String, dynamic>> getDeviceInfo() async {
    try {
      final result = await _channel.invokeMethod<Map<Object?, Object?>>(
        'getDeviceInfo',
      );
      return Map<String, dynamic>.from(result ?? {});
    } on PlatformException catch (e) {
      print('Error getting device info: ${e.message}');
      return {};
    }
  }

  static Future<bool> navigateToPage(int pageIndex) async {
    try {
      final result = await _channel.invokeMethod<bool>(
        'navigateToPage',
        {'pageIndex': pageIndex},
      );
      return result ?? false;
    } on PlatformException catch (e) {
      print('Error navigating to page: ${e.message}');
      return false;
    }
  }

  static Future<Map<String, dynamic>> getUserData() async {
    try {
      final result = await _channel.invokeMethod<Map<Object?, Object?>>(
        'getUserData',
      );
      return Map<String, dynamic>.from(result ?? {});
    } on PlatformException catch (e) {
      print('Error getting user data: ${e.message}');
      return {};
    }
  }

  static Future<dynamic> callNativeFunction(
      String functionName, {
        Map<String, dynamic>? params,
      }) async {
    try {
      final result = await _channel.invokeMethod(
        'callNativeFunction',
        {
          'functionName': functionName,
          'params': params ?? {},
        },
      );
      return result;
    } on PlatformException catch (e) {
      print('Error calling native function: ${e.message}');
      return null;
    }
  }
}