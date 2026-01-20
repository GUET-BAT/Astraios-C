import '../model/user_info_model.dart';
import '../network/http_unit.dart';

/// 用户相关数据仓库（Repo）
///
/// 负责组织参数、调用底层 `HttpUnit`，并将返回数据转换成领域模型 `UserInfoModel`。
/// MeView 等上层页面只依赖这个仓库，不关心具体网络实现。
class UserRepository {
  UserRepository._internal();

  static final UserRepository instance = UserRepository._internal();

  /// 登录接口，返回后端原样数据
  Future<Map<String, dynamic>> login({
    required String userName,
    required String passWord,
    int type = 1,
  }) async {
    final res = await HttpUnit.shared.post(
      path: '/login',
      body: {
        'userName': userName,
        'passWord': passWord,
        'type': type,
      },
    );
    return res;
  }

  /// 根据用户 id 获取用户主页信息
  ///
  /// 如果 [userId] 为空，表示获取当前登录用户的信息（参考文档中的逻辑）。
  Future<UserInfoModel> fetchUserInfo({String? userId}) async {
    final path = userId == null || userId.isEmpty
        ? '/users/self'
        : '/users/$userId'; // 示例路径，后端确定后可直接替换

    final res = await HttpUnit.shared.get(path: path, parameters: {
      if (userId != null && userId.isNotEmpty) 'user_id': userId,
    });

    // 这里可以根据 status / msg 做统一错误处理，当前先简单解析
    return UserInfoModel.fromApiJson(res);
  }
}


