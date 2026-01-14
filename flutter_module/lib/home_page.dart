import 'package:flutter/material.dart';
import '../services/native_channel_service.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false, // 禁用返回按钮，避免返回到原生后再复用错误路由
        title: const Text('主页'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              '主页（Flutter）',
              style: TextStyle(fontSize: 24),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async {
                await NativeChannelService.showToast('来自主页的Toast');
              },
              child: const Text('显示Toast'),
            ),
            const SizedBox(height: 10),
            ElevatedButton(
              onPressed: () async {
                await NativeChannelService.navigateToPage(1);
              },
              child: const Text('跳转到视频页'),
            ),
          ],
        ),
      ),
    );
  }
}