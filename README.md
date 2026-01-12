# Astraios-C
卢布海港,旅行者们于此扬起航帆

## 项目架构

本项目是一个Android混合开发应用，使用ViewPager2和底部导航栏实现4个主要页面：

### 页面结构

1. **主页（HomeFragment）** - 使用Flutter实现
2. **视频页（VideoFragment）** - 使用Android原生实现
3. **聊天页（ChatFragment）** - 使用Android原生实现
4. **我的页（MeFragment）** - 使用Flutter实现

### 技术栈

- **Android原生**: ViewPager2, BottomNavigationView, Fragment
- **Flutter**: FlutterFragment（需要集成Flutter模块）

### 主要组件

- `MainActivity`: 主Activity，管理ViewPager和底部导航栏
- `MainViewPagerAdapter`: ViewPager适配器，管理4个Fragment
- `FlutterFragmentWrapper`: Flutter Fragment包装类，用于嵌入Flutter页面
- `HomeFragment`, `VideoFragment`, `ChatFragment`, `MeFragment`: 4个页面Fragment

### Flutter集成

详细集成说明请参考 [FLUTTER_INTEGRATION.md](GUETAPP/FLUTTER_INTEGRATION.md)

### MethodChannel通信

本项目已实现Flutter与Android原生之间的双向通信，通过MethodChannel实现。

- **原生端**: `MethodChannelHandler` 处理Flutter调用
- **Flutter端**: `NativeChannelService` 调用原生功能
- **支持功能**: Toast显示、设备信息获取、页面导航、用户数据获取等

详细使用说明请参考：
- [MethodChannel使用指南](GUETAPP/FLUTTER_METHOD_CHANNEL_GUIDE.md)
- [MethodChannel实现总结](GUETAPP/METHOD_CHANNEL_SUMMARY.md)

### 运行说明

1. 确保Android Studio已正确配置
2. 如需使用Flutter页面，请先按照FLUTTER_INTEGRATION.md集成Flutter
3. 直接运行即可查看原生页面，Flutter页面会显示占位视图（未集成时）