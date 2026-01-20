import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'home_view.dart';
import 'me_view.dart';
import 'services/native_channel_service.dart';

/// 提前注册 MethodChannel，保证原生调用 flutterLogin 时已就绪
void main() {
  WidgetsFlutterBinding.ensureInitialized();
  _registerMethodChannel();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'GUETAPP',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      // 让原生传入的 initialRoute 生效（/home 或 /me）
      // 不手动写死为 '/'，否则会忽略 Android 侧的路由
      initialRoute: WidgetsBinding.instance.platformDispatcher.defaultRouteName,
      routes: {
        '/home': (context) => const HomeView(),
        '/me': (context) => const MeView(),
        // 兜底：如果原生未传路由，默认跳到主页
        '/': (context) => const HomeView(),
      },
    );
  }

}

void _registerMethodChannel() {
  const channel = MethodChannel('com.example.guetapp/native');
  channel.setMethodCallHandler((call) async {
    print('收到Native调用：method=${call.method}, arguments=${call.arguments}');
    switch (call.method) {
    case 'flutterLogin':
      final args = Map<String, dynamic>.from(call.arguments as Map);
      final userName = args['userName'] as String? ?? '';
      final passWord = args['passWord'] as String? ?? '';
      Map<String, dynamic> res = {
      "username": "ycj",
      "status": 200,
      "accessToken": "",
      "refreshToken": ""
      };
      // await NativeChannelService.callNativeFunction(
      // 'loginStateChanged',
      // params: {
      // 'accessToken': res['accessToken'] ?? '',
      // 'refreshToken': res['refreshToken'] ?? '',
      // 'userName': res['userName'] ?? userName,
      // },);
      return res;
      // try {
      //   final res = await UserRepository.instance.login(
      //     userName: userName,
      //     passWord: passWord,
      //     type: 1,
      //   );
      //   // 通知原生登录态
      //   await NativeChannelService.callNativeFunction(
      //     'loginStateChanged',
      //     params: {
      //       'accessToken': res['accessToken'] ?? '',
      //       'refreshToken': res['refreshToken'] ?? '',
      //       'userName': res['userName'] ?? userName,
      //     },
      //   );
      //   return res;
      // } catch (e) {
      //   return {
      //     'status': 500,
      //     'msg': 'login failed: $e',
      //   };
      // }
      default:
        return null;
    }
  });
}
