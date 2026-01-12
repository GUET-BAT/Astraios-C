# Gradle Repositories 配置修复说明

## 问题

错误信息：
```
Build was configured to prefer settings repositories over project repositories 
but repository 'maven' was added by plugin class 'FlutterPlugin'
```

## 原因

`settings.gradle` 中配置了：
```gradle
repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
```

这个设置**禁止**在项目级别的 `build.gradle` 中添加仓库，但 Flutter 插件需要在项目级别添加 maven 仓库来下载依赖，导致冲突。

## 解决方案

将 `FAIL_ON_PROJECT_REPOS` 改为 `PREFER_SETTINGS`：

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}
```

## RepositoriesMode 说明

### FAIL_ON_PROJECT_REPOS
- **严格模式**：完全禁止在项目级别添加仓库
- **问题**：Flutter 插件无法添加所需的 maven 仓库
- **适用场景**：纯原生 Android 项目，不需要第三方插件添加仓库

### PREFER_SETTINGS（推荐）
- **优先使用 settings 中的仓库**，但允许项目级别添加
- **优点**：兼容 Flutter 等需要添加仓库的插件
- **适用场景**：混合开发项目（Android + Flutter）

### PREFER_PROJECT
- **优先使用项目级别的仓库**
- **适用场景**：多模块项目，各模块需要不同的仓库配置

## 验证

修复后，重新同步 Gradle：
1. File → Sync Project with Gradle Files
2. 应该不再出现仓库相关的错误

## 注意事项

1. `PREFER_SETTINGS` 是混合开发项目的最佳选择
2. 如果仍有问题，可以在 `dependencyResolutionManagement.repositories` 中添加 Flutter 需要的仓库
3. 某些旧版本 Flutter 可能需要 jcenter（已废弃），如果遇到相关错误可以临时添加


