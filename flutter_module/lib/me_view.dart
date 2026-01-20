import 'package:flutter/material.dart';
import 'services/native_channel_service.dart';
import 'model/user_info_model.dart';
import 'view_model/me_view_model.dart';

class MeView extends StatefulWidget {
  const MeView({Key? key}) : super(key: key);

  @override
  State<MeView> createState() => _MeViewState();
}

class _MeViewState extends State<MeView> {
  late final MeViewModel _viewModel = MeViewModel();
  bool _isGuest = false;
  String? _sessionUserName;

  @override
  void initState() {
    super.initState();
    _loadSession();
  }

  @override
  void dispose() {
    _viewModel.dispose();
    super.dispose();
  }

  Future<void> _loadSession() async {
    final data = await NativeChannelService.getUserData();
    final bool isGuest = data['isGuest'] == true;
    final String name = (data['userName'] as String?) ?? '';
    setState(() {
      _isGuest = isGuest;
      _sessionUserName = name.isNotEmpty ? name : '游客';
    });
    if (!isGuest) {
      await _viewModel.loadUserInfo();
    }
  }

  @override
  Widget build(BuildContext context) {
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


