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
    try {
      final args = Map<String, dynamic>.from(call.arguments as Map);
      final userName = args['userName'] as String? ?? '';
      final passWord = args['passWord'] as String? ?? '';

      // 调用 UserRepository 发起登录请求
      final loginModel = await UserRepository.instance.login(
        userName: userName,
        passWord: passWord,
        type: 1,
      );

      // 返回登录结果（原生根据 status、accessToken、refreshToken、userName 判断登录状态）
      return {
        'status': loginModel.isSuccess ? 200 : loginModel.code,
        'msg': loginModel.msg,
        'accessToken': loginModel.data?.accessToken ?? '',
        'refreshToken': loginModel.data?.refreshToken ?? '',
        'userName': userName,
      };
    } catch (e) {
      // 异常时返回错误信息
      return {
        'status': 500,
        'msg': '登录失败: $e',
        'accessToken': '',
        'refreshToken': '',
        'userName': '',
      };
    }

  }

  /// 注册：调用 UserRepository 发起网络请求，返回注册结果
  /// 只返回bool值给原生，true表示注册成功，false表示注册失败
  Future<bool> _handleFlutterRegister(MethodCall call) async {
    print('═══════════════════════════════════════════════════════════');
    print('🔵 注册流程开始');
    print('═══════════════════════════════════════════════════════════');
    try {
      print('📝 步骤1: 解析参数');
      final args = Map<String, dynamic>.from(call.arguments as Map);
      final username = args['userName'] as String? ?? '';
      final password = args['passWord'] as String? ?? '';
      print('   用户名: $username');
      print('   密码: ${password.isNotEmpty ? '***' : '(空)'}');

      print('📝 步骤2: 准备调用 UserRepository.register()');
      // 调用 UserRepository 发起注册请求
      final result = await UserRepository.instance.register(
        username: username,
        password: password,
      );
      print('📝 步骤3: UserRepository.register() 调用完成');

      print('═══════════════════════════════════════════════════════════');
      print('📥 注册请求返回结果:');
      print('═══════════════════════════════════════════════════════════');
      print('   返回数据: $result');

      // 检查注册结果
      final code = result['code'] as int?;
      final msg = result['msg'] as String? ?? '';

      print('   解析结果:');
      print('   code: $code');
      print('   msg: $msg');

      // 判断是否注册成功：code == 0 且 msg == "success"
      final isSuccess = code == 0 && msg == 'success';
      print('   注册${isSuccess ? "成功" : "失败"}');
      print('═══════════════════════════════════════════════════════════');
      
      return isSuccess;
    } catch (e) {
      // 异常时返回false
      print('═══════════════════════════════════════════════════════════');
      print('❌ 注册异常: $e');
      print('═══════════════════════════════════════════════════════════');
      return false;
    }
  }
}

