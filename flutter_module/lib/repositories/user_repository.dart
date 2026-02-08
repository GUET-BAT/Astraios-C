import '../model/user_info_model.dart';
import '../model/login_model.dart';
import '../network/http_unit.dart';

/// 用户相关数据仓库（Repo）
///
/// 负责组织参数、调用底层 `HttpUnit`，并将返回数据转换成领域模型。
/// MeView 等上层页面只依赖这个仓库，不关心具体网络实现。
class UserRepository {
  UserRepository._internal();

  static final UserRepository instance = UserRepository._internal();

  /// 登录接口，返回 LoginModel
  Future<LoginModel> login({
    required String userName,
    required String passWord,
    int type = 1,
  }) async {
    final res = await HttpUnit.shared.post(
      path: '/login/',
      body: {
        'userName': userName,
        'passWord': passWord,
        'type': type,
      },
    );
    return LoginModel.fromJson(res);
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

  /// 注册接口
  ///
  /// [username] 用户名
  /// [password] 密码
  /// 返回注册结果，包含 code 和 msg
  Future<Map<String, dynamic>> register({
    required String username,
    required String password,
  }) async {
    print('═══════════════════════════════════════════════════════════');
    print('📦 UserRepository.register() 方法开始执行');
    print('═══════════════════════════════════════════════════════════');
    
    try {
      // 构建完整URL
      print('📝 步骤1: 获取baseUrl');
      final baseUrl = "astraios.g-oss.top/api";
      print('   baseUrl: $baseUrl');
      
      final path = '/v1/register';
      // 如果baseUrl没有协议，添加https://
      final fullUrl = baseUrl.startsWith('http://') || baseUrl.startsWith('https://')
          ? '$baseUrl$path'
          : 'https://$baseUrl$path';
      
      print('═══════════════════════════════════════════════════════════');
      print('🌐 注册请求完整URL: $fullUrl');
      print('   请求方法: POST');
      print('   请求路径: $path');
      print('═══════════════════════════════════════════════════════════');
      
      print('📝 步骤2: 准备调用 HttpUnit.shared.post()');
      final res = await HttpUnit.shared.post(
        path: path,
        body: {
          'usernaame': username,  // 按照接口要求使用 usernaame
          'password': password,
        },
      );
      print('📝 步骤3: HttpUnit.shared.post() 调用完成');
    
      print('═══════════════════════════════════════════════════════════');
      print('📥 注册请求返回结果:');
      print('═══════════════════════════════════════════════════════════');
      print('   完整URL: $fullUrl');
      print('   返回数据: $res');
      print('═══════════════════════════════════════════════════════════');
      
      return res;
    } catch (e, stackTrace) {
      print('═══════════════════════════════════════════════════════════');
      print('❌ UserRepository.register() 发生异常:');
      print('═══════════════════════════════════════════════════════════');
      print('   异常信息: $e');
      print('   堆栈信息: $stackTrace');
      print('═══════════════════════════════════════════════════════════');
      rethrow;
    }
  }
}


