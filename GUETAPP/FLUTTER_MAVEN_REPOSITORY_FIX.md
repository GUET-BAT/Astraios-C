# Flutter Maven仓库配置修复

## 问题

错误信息：
```
Could not find io.flutter:flutter_embedding_debug:1.0.0-9064459a8b0dcd32877107f6002cc429a71659d1
```

## 原因

Flutter的依赖不在远程Maven仓库（Maven Central、Google Maven）中，而是在Flutter SDK的本地目录中。当使用`dependencyResolutionManagement`时，需要在`settings.gradle`中手动添加Flutter的本地Maven仓库。

## 解决方案

### 方案一：在settings.gradle中添加Flutter本地Maven仓库（已实现）

已在`settings.gradle`的`dependencyResolutionManagement.repositories`中添加了Flutter本地Maven仓库的配置。

### 方案二：如果方案一不工作，使用PREFER_PROJECT模式

如果Flutter的gradle脚本无法正确添加仓库，可以改为`PREFER_PROJECT`模式：

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}
```

这样Flutter插件可以在项目级别添加仓库。

### 方案三：确保Flutter Module正确初始化

1. **运行flutter pub get**
   ```bash
   cd flutter_module
   flutter pub get
   ```

2. **检查Flutter Module的local.properties**
   确保`flutter_module/.android/local.properties`存在并包含正确的Flutter SDK路径。

3. **清理并重新构建**
   ```bash
   cd GUETAPP
   ./gradlew clean
   ./gradlew build
   ```

## 验证

1. 同步Gradle：File → Sync Project with Gradle Files
2. 检查是否还有依赖解析错误
3. 如果仍有问题，查看详细的错误信息

## 注意事项

1. Flutter的依赖版本号格式为：`1.0.0-<git-hash>`
2. Flutter依赖存储在Flutter SDK的`bin/cache/artifacts/engine`目录中
3. 不同架构的Flutter Engine存储在不同的子目录中


