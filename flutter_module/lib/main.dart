import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'home_view.dart';
import 'me_view.dart';
import 'services/shared_channel.dart';

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

/// 注册MethodChannel，处理原生调用Flutter登录/注册/HTTP等
void _registerMethodChannel() {
  SharedChannel.instance.setup();
}
