import 'package:flutter/material.dart';

import 'home_page.dart';
import 'me_page.dart';

void main() => runApp(const MyApp());

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
        '/home': (context) => const HomePage(),
        '/me': (context) => const MePage(),
        // 兜底：如果原生未传路由，默认跳到主页
        '/': (context) => const HomePage(),
      },
    );
  }
}
