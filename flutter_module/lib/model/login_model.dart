/// 登录响应数据模型
///
/// 对应接口返回结构：
/// {
///   "code": 0,
///   "msg": "success",
///   "data": {
///     "accessToken": "xxxxxx",
///     "refreshToken": "xxxxxx"
///   }
/// }
class LoginModel {
  final int code;
  final String msg;
  final LoginData? data;

  LoginModel({
    required this.code,
    required this.msg,
    this.data,
  });

  /// 是否成功（code == 0 表示成功）
  bool get isSuccess => code == 0;

  /// 从接口 JSON 解析登录响应
  factory LoginModel.fromJson(Map<String, dynamic> json) {
    final dataJson = json['data'] as Map<String, dynamic>?;
    return LoginModel(
      code: json['code'] as int? ?? -1,
      msg: json['msg'] as String? ?? '',
      data: dataJson != null ? LoginData.fromJson(dataJson) : null,
    );
  }

  /// 转换为 Map（用于传递给原生）
  Map<String, dynamic> toMap() {
    return {
      'code': code,
      'msg': msg,
      'data': data?.toMap(),
    };
  }
}

/// 登录数据（包含 token）
class LoginData {
  final String accessToken;
  final String refreshToken;

  LoginData({
    required this.accessToken,
    required this.refreshToken,
  });

  factory LoginData.fromJson(Map<String, dynamic> json) {
    return LoginData(
      accessToken: json['accessToken'] as String? ?? '',
      refreshToken: json['refreshToken'] as String? ?? '',
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'accessToken': accessToken,
      'refreshToken': refreshToken,
    };
  }
}

