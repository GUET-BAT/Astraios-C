import 'package:flutter/services.dart';
import '../repositories/user_repository.dart';
import '../services/native_channel_service.dart';

/// 统一管理 MethodChannel 的调用与分发
class SharedChannel {
  SharedChannel._();

  static final SharedChannel instance = SharedChannel._();

  /// 频道名称（原生与 Flutter 保持一致）
  static const String channelName = 'com.example.guetapp/native';

  /// 示例：供 switch 使用的常量
  static const String httpRequest = 'httpRequest';
  static const String flutterLogin = 'flutterLogin';
  static const String flutterRegister = 'flutterRegister';
  static const String editHyperlinkAlert = 'EditHyperlinkAlert';

  final MethodChannel _channel = const MethodChannel(channelName);

  /// 初始化：注册默认回调
  void setup() {
    _channel.setMethodCallHandler(_onMethodCall);
  }

  /// Flutter 主动调原生
  Future<T?> invoke<T>(String method, [dynamic arguments]) {
    return _channel.invokeMethod<T>(method, arguments);
  }

  /// 默认分发逻辑
  Future<dynamic> _onMethodCall(MethodCall call) async {
    switch (call.method) {
      case flutterLogin:
        return _handleFlutterLogin(call);
      case flutterRegister:
        return _handleFlutterRegister(call);
      case httpRequest:
        // 可在此集中处理原生请求网络的透传，这里返回一个占位
        return {'status': 200, 'msg': 'mock httpRequest'};
      case editHyperlinkAlert:
        // 示例占位：返回 true 代表已处理
        return true;
      default:
        return null;
    }
  }

  /// 登录：调用 UserRepository 发起网络请求，返回登录结果
  /// 原生通过返回值判断登录状态并更新 SessionManager
  Future<Map<String, dynamic>> _handleFlutterLogin(MethodCall call) async {
    // try {
    //   final args = Map<String, dynamic>.from(call.arguments as Map);
    //   final userName = args['userName'] as String? ?? '';
    //   final passWord = args['passWord'] as String? ?? '';
    //
    //   // 调用 UserRepository 发起登录请求
    //   final loginModel = await UserRepository.instance.login(
    //     userName: userName,
    //     passWord: passWord,
    //     type: 1,
    //   );
    //
    //   // 返回登录结果（原生根据 status、accessToken、refreshToken、userName 判断登录状态）
    //   return {
    //     'status': loginModel.isSuccess ? 200 : loginModel.code,
    //     'msg': loginModel.msg,
    //     'accessToken': loginModel.data?.accessToken ?? '',
    //     'refreshToken': loginModel.data?.refreshToken ?? '',
    //     'userName': userName,
    //   };
    // } catch (e) {
    //   // 异常时返回错误信息
    //   return {
    //     'status': 500,
    //     'msg': '登录失败: $e',
    //     'accessToken': '',
    //     'refreshToken': '',
    //     'userName': '',
    //   };
    // }

    return {
      'status':  200,
      'msg': "success",
      'accessToken':  '111',
      'refreshToken':  '222',
      'userName': 'ycj',
    };
  }

  /// 注册：直接返回成功（可在此扩展真实逻辑）
  Future<Map<String, dynamic>> _handleFlutterRegister(MethodCall call) async {
    final args = Map<String, dynamic>.from(call.arguments as Map);
    final userName = args['userName'] as String? ?? '';
    final passWord = args['passWord'] as String? ?? '';
    // 简单校验：非空即成功
    if (userName.isEmpty || passWord.isEmpty) {
      return {
        'status': 400,
        'msg': 'userName or passWord empty',
      };
    }
    return {
      'status': 200,
      'msg': 'success',
      'userName': userName,
    };
  }
}

