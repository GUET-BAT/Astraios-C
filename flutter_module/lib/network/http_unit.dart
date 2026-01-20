import 'dart:async';

/// 简化版 Http 工具类，参考文档中的 `HttpUnit.shared.get()`
///
/// 目前还没有真实后端，这里通过路径判断返回模拟数据，后续接入真实接口时，
/// 只需要在这里统一改成使用 `http` / `dio` 等库发起请求即可。
class HttpUnit {
  HttpUnit._internal();

  static final HttpUnit shared = HttpUnit._internal();

  /// 基础域名，占位用
  final String baseUrl = 'https://api.example.com';

  /// GET 请求
  ///
  /// [path] 对应具体接口路径，例如：`/users/{user_id}`
  /// [parameters] 对应 query 参数
  Future<Map<String, dynamic>> get({
    required String path,
    Map<String, dynamic>? parameters,
  }) async {
    // TODO: 接入真实网络库
    // 这里根据 path 返回不同的 mock 数据，模拟网络请求耗时
    await Future.delayed(const Duration(milliseconds: 600));

    if (path.startsWith('/users/')) {
      // 模拟用户信息接口返回
      return <String, dynamic>{
        'status': 200,
        'msg': 'success',
        'data': {
          'user_info': {
            'userName': 'Flutter 用户',
            'avatar':
                'https://avatars.githubusercontent.com/u/14101776?s=200&v=4',
          },
          'comment': {
            'template': '',
            'replys': '',
          },
        },
      };
    }

    // 默认返回一个通用结构，方便后续扩展
    return <String, dynamic>{
      'status': 500,
      'msg': 'unknown path',
      'data': {},
    };
  }

  /// POST 请求（模拟登录/注册）
  Future<Map<String, dynamic>> post({
    required String path,
    Map<String, dynamic>? body,
  }) async {
    //await Future.delayed(const Duration(milliseconds: 500));

    if (path == '/login') {
      final user = body?['userName'] as String? ?? '';
      final pwd = body?['passWord'] as String? ?? '';
      // 固定账号/密码 123456 作为通过条件
      if (user == '123456' && pwd == '123456') {
        return <String, dynamic>{
          'status': 200,
          'msg': 'success',
          'accessToken': 'access_mock_token_123456',
          'refreshToken': 'refresh_mock_token_123456',
          'userName': user,
        };
      }
      // 失败返回
      return <String, dynamic>{
        'status': 401,
        'msg': 'invalid credentials',
      };
    }

    if (path == '/register') {
      return <String, dynamic>{
        'status': 200,
        'msg': 'success',
      };
    }

    return <String, dynamic>{
      'status': 500,
      'msg': 'unknown path',
      'data': {},
    };
  }
}


