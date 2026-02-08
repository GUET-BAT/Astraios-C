/// 用户信息数据模型
///
/// 对应接口返回结构中的：
/// {
///   "status": 200,
///   "msg": "success",
///   "data": {
///     "user_info": {
///       "userName": "...",
///       "avatar": "..."
///     },
///     "comment": {
///       "template": "",
///       "replys": ""
///     }
///   }
/// }
class UserInfoModel {
  final String userName;
  final String avatar;

  UserInfoModel({
    required this.userName,
    required this.avatar,
  });

  /// 从完整接口 JSON 解析出用户信息
  factory UserInfoModel.fromApiJson(Map<String, dynamic> json) {
    final data = json['data'] as Map<String, dynamic>? ?? {};
    final userInfo = data['user_info'] as Map<String, dynamic>? ?? {};

    return UserInfoModel(
      userName: userInfo['userName'] as String? ?? '',
      avatar: userInfo['avatar'] as String? ?? '',
    );
  }
}


