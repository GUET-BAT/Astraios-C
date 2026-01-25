import 'package:flutter/material.dart';
import 'services/native_channel_service.dart';
import 'model/user_info_model.dart';
import 'view_model/me_view_model.dart';

class MeView extends StatefulWidget {
  const MeView({Key? key}) : super(key: key);

  @override
  State<MeView> createState() => _MeViewState();
}

class _MeViewState extends State<MeView> with WidgetsBindingObserver {
  late final MeViewModel _viewModel = MeViewModel();
  bool _isGuest = false;
  String? _sessionUserName;
  DateTime? _lastRefreshTime;
  static const Duration _refreshCooldown = Duration(milliseconds: 500);

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _loadSession();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _viewModel.dispose();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    // 当应用从后台恢复时，刷新用户信息
    if (state == AppLifecycleState.resumed) {
      _loadSession();
    }
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // 页面依赖变化时（如从登录页返回），刷新用户信息
    _loadSession();
  }

  Future<void> _loadSession() async {
    if (!mounted) return;
    
    // 防抖：避免在短时间内重复刷新
    final now = DateTime.now();
    if (_lastRefreshTime != null && 
        now.difference(_lastRefreshTime!) < _refreshCooldown) {
      return;
    }
    _lastRefreshTime = now;
    
    final data = await NativeChannelService.getUserData();
    final bool isGuest = data['isGuest'] == true;
    final String name = (data['userName'] as String?) ?? '';
    if (mounted) {
      setState(() {
        _isGuest = isGuest;
        _sessionUserName = name.isNotEmpty ? name : '游客';
      });
      if (!isGuest) {
        await _viewModel.loadUserInfo();
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    // 在 build 时确保状态是最新的（页面可见时自动刷新）
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadSession();
    });
    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false, // 禁用返回按钮，避免返回后复用错误路由
        title: const Text('我的'),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              if (_isGuest) ...[
                const Icon(Icons.person, size: 80, color: Colors.blue),
                const SizedBox(height: 16),
                Text(
                  _sessionUserName ?? '游客',
                  style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w600),
                ),
                const SizedBox(height: 8),
                const Text('当前为游客登录', style: TextStyle(fontSize: 16, color: Colors.grey)),
                const SizedBox(height: 24),
                ElevatedButton(
                  onPressed: () async {
                    await NativeChannelService.callNativeFunction('navigateToLogin');
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blue,
                    padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 12),
                  ),
                  child: const Text('去登录', style: TextStyle(fontSize: 16)),
                ),
              ] else ...[
              AnimatedBuilder(
                animation: _viewModel,
                builder: (context, _) {
                  final bool isLoading = _viewModel.isLoading;
                  final String? error = _viewModel.error;
                  final UserInfoModel? info = _viewModel.userInfo;

                  if (isLoading) {
                    return const CircularProgressIndicator();
                  }

                  if (error != null) {
                    return Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text(
                          error,
                          style: const TextStyle(color: Colors.red),
                        ),
                        const SizedBox(height: 12),
                        ElevatedButton(
                          onPressed: _viewModel.loadUserInfo,
                          child: const Text('重试'),
                        ),
                      ],
                    );
                  }

                  if (info != null) {
                    return Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        info.avatar.isNotEmpty
                            ? CircleAvatar(
                                radius: 40,
                                backgroundImage: NetworkImage(info.avatar),
                              )
                            : const Icon(
                                Icons.person,
                                size: 80,
                                color: Colors.blue,
                              ),
                        const SizedBox(height: 16),
                        Text(
                          info.userName.isNotEmpty
                              ? info.userName
                              : '未登录用户',
                          style: const TextStyle(
                            fontSize: 22,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(height: 8),
                        const Text(
                          '我的页面（Flutter）',
                          style: TextStyle(fontSize: 16, color: Colors.grey),
                        ),
                        const SizedBox(height: 24),
                      ],
                    );
                  }

                  return const Text('暂无用户信息');
                },
              ),
              ],
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: () async {
                  await NativeChannelService.showToast('来自我的页面的Toast');
                },
                child: const Text('测试Toast'),
              ),
              if (!_isGuest) ...[
                const SizedBox(height: 12),
                ElevatedButton(
                  onPressed: () async {
                    await NativeChannelService.callNativeFunction('logout');
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.redAccent,
                  ),
                  child: const Text('退出登录'),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}


