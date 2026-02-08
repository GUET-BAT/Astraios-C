import 'package:flutter/material.dart';
import 'services/native_channel_service.dart';

class HomeView extends StatefulWidget {
  const HomeView({Key? key}) : super(key: key);

  @override
  State<HomeView> createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> with WidgetsBindingObserver {
  String _userDisplayName = '游客';
  bool _isLoading = true;
  DateTime? _lastRefreshTime;
  static const Duration _refreshCooldown = Duration(milliseconds: 500);

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _loadUserInfo();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    // 当应用从后台恢复或页面重新可见时，刷新用户信息
    if (state == AppLifecycleState.resumed) {
      _loadUserInfo();
    }
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // 页面依赖变化时（如从登录页返回），刷新用户信息
    _loadUserInfo();
  }

  Future<void> _loadUserInfo() async {
    final data = await NativeChannelService.getUserData();
    final bool isGuest = data['isGuest'] == true;
    final String userName = (data['userName'] as String?) ?? '';
    
    if (mounted) {
      setState(() {
        if (isGuest || userName.isEmpty) {
          _userDisplayName = '游客';
        } else {
          _userDisplayName = userName;
        }
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    // 在 build 时确保状态是最新的（页面可见时自动刷新）
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadUserInfo();
    });
    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false, // 禁用返回按钮，避免返回到原生后再复用错误路由
        title: const Text('主页'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // 显示用户名/游客状态
            if (_isLoading)
              const CircularProgressIndicator()
            else
              Text(
                _userDisplayName,
                style: const TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: Colors.blue,
                ),
              ),
            const SizedBox(height: 20),
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


