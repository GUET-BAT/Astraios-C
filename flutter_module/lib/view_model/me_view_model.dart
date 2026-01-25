import 'package:flutter/foundation.dart';
import '../model/user_info_model.dart';
import '../repositories/user_repository.dart';

/// Me 页面的 ViewModel，负责持有 UI 状态并调度数据请求
class MeViewModel extends ChangeNotifier {
  final UserRepository _repository;

  UserInfoModel? userInfo;
  bool isLoading = false;
  String? error;

  MeViewModel({UserRepository? repository})
      : _repository = repository ?? UserRepository.instance;

  Future<void> loadUserInfo({String? userId}) async {
    isLoading = true;
    error = null;
    notifyListeners();

    try {
      final info = await _repository.fetchUserInfo(userId: userId);
      userInfo = info;
    } catch (_) {
      error = '加载用户信息失败';
    } finally {
      isLoading = false;
      notifyListeners();
    }
  }
}


