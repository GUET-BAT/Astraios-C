import 'package:flutter/material.dart';
import '../services/native_channel_service.dart';

class MePage extends StatelessWidget {
  const MePage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('我的'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.person, size: 80, color: Colors.blue),
            const SizedBox(height: 16),
            const Text(
              '我的页面（Flutter）',
              style: TextStyle(fontSize: 24),
            ),
            const SizedBox(height: 32),
            ElevatedButton(
              onPressed: () async {
                await NativeChannelService.showToast('来自我的页面的Toast');
              },
              child: const Text('测试Toast'),
            ),
          ],
        ),
      ),
    );
  }
}