import 'dart:async';
import 'package:dio/dio.dart';
import 'log_interceptor.dart';

/// 网络请求工具类，使用 Dio 实现
///
/// 支持拦截器、请求取消、自动重试等高级功能
class HttpUnit {
  HttpUnit._internal() {
    _dio = Dio(BaseOptions(
      baseUrl: 'https://astraios.g-oss.top/api',
    ));

    // 添加日志拦截器
    _dio.interceptors.add(AppLogInterceptor());

    // 可以在这里添加其他拦截器，例如 token 拦截器
    // _dio.interceptors.add(TokenInterceptor());
  }

  static final HttpUnit shared = HttpUnit._internal();

  late Dio _dio;

  /// 获取 Dio 实例（用于高级配置）
  Dio get dio => _dio;

  /// GET 请求
  ///
  /// [path] 对应具体接口路径，例如：`/users/{user_id}`
  /// [parameters] 对应 query 参数
  /// [options] 可选的请求配置（用于覆盖默认配置）
  Future<Map<String, dynamic>> get({
    required String path,
    Map<String, dynamic>? parameters,

  }) async {
    try {
      final response = await _dio.get(
        path,
        queryParameters: parameters,
      );
      return response.data as Map<String, dynamic>;
    } on DioException catch (e) {
      // 处理 Dio 错误
      return _handleDioError(e);
    } catch (e) {
      // 处理其他错误
      return {
        'code': -1,
        'msg': '网络请求失败: $e',
        'data': null,
      };
    }
  }

  /// POST 请求
  ///
  /// [path] 对应具体接口路径，例如：`/login/`
  /// [body] 请求体参数
  /// [options] 可选的请求配置（用于覆盖默认配置）
  /// [cancelToken] 可选的取消令牌（用于取消请求）
  Future<Map<String, dynamic>> post({
    required String path,
    Map<String, dynamic>? body,

  }) async {
    try {
      final response = await _dio.post(
        path,
        data: body,
      );
      return response.data as Map<String, dynamic>;
    } on DioException catch (e) {
      // 处理 Dio 错误
      return _handleDioError(e);
    } catch (e) {
      // 处理其他错误
      return {
        'code': -1,
        'msg': '网络请求失败: $e',
        'data': null,
      };
    }
  }

  /// 处理 Dio 错误，转换为统一的错误格式
  Map<String, dynamic> _handleDioError(DioException error) {
    String errorMsg = '网络请求失败';
    
    switch (error.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        errorMsg = '请求超时，请检查网络连接';
        break;
      case DioExceptionType.badResponse:
        // 服务器返回了错误状态码
        errorMsg = '服务器错误: ${error.response?.statusCode}';
        break;
      case DioExceptionType.cancel:
        errorMsg = '请求已取消';
        break;
      case DioExceptionType.connectionError:
        errorMsg = '网络连接失败，请检查网络';
        break;
      default:
        errorMsg = error.message ?? '未知错误';
    }

    return {
      'code': error.response?.statusCode ?? -1,
      'msg': errorMsg,
      'data': null,
    };
  }
}


