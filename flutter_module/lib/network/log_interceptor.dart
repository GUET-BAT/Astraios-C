import 'dart:convert';
import 'package:dio/dio.dart';

/// 应用自定义日志拦截器
///
/// 用于打印详细的请求和响应信息，便于调试和排查问题
/// 注意：为避免与 Dio 内置的 LogInterceptor 冲突，命名为 AppLogInterceptor
class AppLogInterceptor extends InterceptorsWrapper {
  AppLogInterceptor();

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    _printRequest(options);
    super.onRequest(options, handler);
  }

  @override
  void onResponse(Response response, ResponseInterceptorHandler handler) {
    _printResponse(response);
    super.onResponse(response, handler);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    _printError(err);
    super.onError(err, handler);
  }

  /// 打印请求信息
  void _printRequest(RequestOptions options) {
    print('═══════════════════════════════════════════════════════════');
    print('📤 请求信息');
    print('═══════════════════════════════════════════════════════════');
    print('🌐 URL: ${options.method} ${options.uri}');
    print('⏰ 时间: ${DateTime.now().toString().substring(0, 19)}');
    
    if (options.headers.isNotEmpty) {
      print('📋 请求头:');
      options.headers.forEach((key, value) {
        print('   $key: $value');
      });
    }
    
    if (options.queryParameters.isNotEmpty) {
      print('🔍 Query参数:');
      options.queryParameters.forEach((key, value) {
        print('   $key: $value');
      });
    }
    
    if (options.data != null) {
      print('📦 请求体:');
      try {
        if (options.data is Map || options.data is List) {
          print('   ${const JsonEncoder.withIndent('   ').convert(options.data)}');
        } else {
          print('   ${options.data}');
        }
      } catch (e) {
        print('   ${options.data}');
      }
    }
    print('═══════════════════════════════════════════════════════════');
  }

  /// 打印响应信息
  void _printResponse(Response response) {
    print('═══════════════════════════════════════════════════════════');
    print('📥 响应信息');
    print('═══════════════════════════════════════════════════════════');
    print('🌐 URL: ${response.requestOptions.method} ${response.requestOptions.uri}');
    print('✅ 状态码: ${response.statusCode}');
    print('⏰ 时间: ${DateTime.now().toString().substring(0, 19)}');
    
    if (response.headers.map.isNotEmpty) {
      print('📋 响应头:');
      response.headers.map.forEach((key, value) {
        print('   $key: ${value.join(', ')}');
      });
    }
    
    if (response.data != null) {
      print('📦 响应体:');
      try {
        if (response.data is Map || response.data is List) {
          print('   ${const JsonEncoder.withIndent('   ').convert(response.data)}');
        } else {
          print('   ${response.data}');
        }
      } catch (e) {
        print('   ${response.data}');
      }
    }
    print('═══════════════════════════════════════════════════════════');
  }

  /// 打印错误信息
  void _printError(DioException error) {
    print('═══════════════════════════════════════════════════════════');
    print('❌ 错误信息');
    print('═══════════════════════════════════════════════════════════');
    print('🌐 URL: ${error.requestOptions.method} ${error.requestOptions.uri}');
    print('⏰ 时间: ${DateTime.now().toString().substring(0, 19)}');
    print('🔴 错误类型: ${error.type}');
    print('📝 错误消息: ${error.message}');
    
    if (error.response != null) {
      print('📊 响应状态码: ${error.response?.statusCode}');
      print('📋 响应头:');
      error.response?.headers.map.forEach((key, value) {
        print('   $key: ${value.join(', ')}');
      });
      if (error.response?.data != null) {
        print('📦 错误响应体:');
        try {
          if (error.response!.data is Map || error.response!.data is List) {
            print('   ${const JsonEncoder.withIndent('   ').convert(error.response!.data)}');
          } else {
            print('   ${error.response!.data}');
          }
        } catch (e) {
          print('   ${error.response!.data}');
        }
      }
    } else {
      print('⚠️  无响应数据（可能是网络错误或超时）');
    }
    print('═══════════════════════════════════════════════════════════');
  }
}

