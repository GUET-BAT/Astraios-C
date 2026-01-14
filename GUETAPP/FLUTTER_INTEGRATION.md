# Flutter集成说明

本项目使用混合开发架构，主页和我的页面使用Flutter实现，视频和聊天页面使用Android原生实现。

## Flutter集成步骤

> **📖 详细步骤说明**：本文档提供快速参考，详细的集成步骤、两种方法的对比说明、以及使用Android Studio创建Flutter项目的完整指南，请参考 [FLUTTER_INTEGRATION_DETAILED.md](FLUTTER_INTEGRATION_DETAILED.md)

### 方法一：使用Flutter Module（推荐）

**作用说明**：Flutter Module提供完整的Flutter开发环境，支持热重载、调试工具，适合开发阶段使用。详见详细指南。

1. **创建Flutter Module**
   ```bash
   cd ..
   flutter create -t module flutter_module
   ```
   或使用Android Studio：File → New → New Module → Flutter Module

2. **在settings.gradle中添加Flutter模块**
   ```gradle
   // 添加Flutter配置
   def localPropertiesFile = new File(rootProject.projectDir, "local.properties")
   def properties = new Properties()
   assert localPropertiesFile.exists()
   localPropertiesFile.withReader("UTF-8") { reader -> properties.load(reader) }
   def flutterSdkPath = properties.getProperty("flutter.sdk")
   assert flutterSdkPath != null, "flutter.sdk not set in local.properties"
   apply from: "$flutterSdkPath/packages/flutter_tools/gradle/app_plugin_loader.gradle"
   
   // 包含Flutter模块
   include ':flutter'
   project(':flutter').projectDir = new File('../flutter_module')
   ```

3. **在local.properties中添加Flutter SDK路径**
   ```properties
   flutter.sdk=C\:\\flutter
   ```
   （将路径替换为你的实际Flutter SDK路径）

4. **在app/build.gradle中添加依赖**
   ```gradle
   dependencies {
       implementation project(':flutter')
   }
   ```

5. **更新FlutterFragmentWrapper.java**
   - FlutterFragmentWrapper已自动支持MethodChannel
   - 确保Flutter模块已正确集成

### 方法二：使用Flutter Engine AAR

**作用说明**：Flutter Engine AAR是预编译的二进制文件，适合生产环境，但不支持热重载和开发调试。详见详细指南。

1. **下载Flutter Engine AAR包**
   - 从Flutter官方仓库获取对应版本的AAR包

2. **在app/build.gradle中添加依赖**
   ```gradle
   repositories {
       flatDir {
           dirs 'libs'
       }
   }
   
   dependencies {
       implementation(name: 'flutter_embedding_debug', ext: 'aar')
   }
   ```

3. **更新FlutterFragmentWrapper.java**
   - 取消注释Flutter相关代码
   - 添加import语句

### Flutter页面路由配置

在Flutter项目中，需要配置以下路由：
- `/home` - 主页
- `/me` - 我的页面

示例（Flutter项目的main.dart）：
```dart
import 'package:flutter/material.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      initialRoute: '/',
      routes: {
        '/home': (context) => HomePage(),
        '/me': (context) => MePage(),
      },
    );
  }
}
```

## MethodChannel通信

本项目已实现Flutter与原生Android之间的双向通信，通过MethodChannel实现。

### 原生端实现

- **MethodChannelHandler**: 处理Flutter通过MethodChannel调用的原生功能
- **FlutterFragmentWrapper**: 自动初始化MethodChannel Handler
- **MainActivity**: 提供页面导航等原生功能

### 支持的原生功能

1. **showToast**: 显示Toast消息
2. **getDeviceInfo**: 获取设备信息
3. **navigateToPage**: 导航到指定页面（0-主页, 1-视频, 2-聊天, 3-我的）
4. **getUserData**: 获取用户数据
5. **callNativeFunction**: 调用通用原生功能（如打开设置、获取电池电量等）

### Flutter端使用

详细的使用说明和示例代码请参考 [FLUTTER_METHOD_CHANNEL_GUIDE.md](FLUTTER_METHOD_CHANNEL_GUIDE.md)

### Channel名称

MethodChannel名称：`com.example.guetapp/native`

**注意**: Flutter端和原生端的Channel名称必须完全一致。

## 当前状态

目前FlutterFragmentWrapper已支持MethodChannel通信。需要按照上述步骤集成Flutter后才能显示实际的Flutter页面并测试通信功能。

## 注意事项

1. 确保Flutter SDK已正确安装
2. 确保Flutter和Android项目的版本兼容
3. 集成后需要重新编译项目
4. 在 `local.properties` 中添加 `flutter.sdk` 路径（参考详细指南）

## 快速参考

- **详细集成指南**：[FLUTTER_INTEGRATION_DETAILED.md](FLUTTER_INTEGRATION_DETAILED.md)
  - Flutter Module vs Flutter Engine AAR 详细对比
  - Android Studio创建Flutter Module的完整步骤
  - 常见问题解决方案
  - 开发工作流说明

- **MethodChannel使用指南**：[FLUTTER_METHOD_CHANNEL_GUIDE.md](FLUTTER_METHOD_CHANNEL_GUIDE.md)

- **MethodChannel实现总结**：[METHOD_CHANNEL_SUMMARY.md](METHOD_CHANNEL_SUMMARY.md)

