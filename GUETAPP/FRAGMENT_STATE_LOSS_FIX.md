# Fragment State Loss 错误修复

## 问题

运行时错误：
```
java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
at com.example.guetapp.fragment.FlutterFragmentWrapper.onDestroyView(FlutterFragmentWrapper.java:195)
```

## 原因

在 `FlutterFragmentWrapper.onDestroyView()` 方法中，尝试移除 `FlutterFragment` 时，Activity 已经保存了状态（`onSaveInstanceState`），导致无法执行 Fragment 事务。

**触发场景**：
- ViewPager2 在页面切换时会回收 Fragment
- 当切换到其他页面时，之前的 Fragment 会调用 `onDestroyView()`
- 如果此时 Activity 正在保存状态，就会抛出异常

## 解决方案

### 修复前（有问题的代码）

```java
@Override
public void onDestroyView() {
    super.onDestroyView();
    
    // 移除FlutterFragment
    if (flutterFragment != null && getChildFragmentManager() != null) {
        getChildFragmentManager()
                .beginTransaction()
                .remove(flutterFragment)
                .commit(); // ❌ 可能在onSaveInstanceState后执行，导致崩溃
        flutterFragment = null;
    }
}
```

### 修复后（安全的代码）

```java
@Override
public void onDestroyView() {
    super.onDestroyView();
    
    // 移除FlutterFragment（需要检查状态，避免在onSaveInstanceState后执行）
    if (flutterFragment != null && getChildFragmentManager() != null) {
        try {
            // 检查FragmentManager状态，只有在安全的情况下才执行事务
            if (isAdded() && !getChildFragmentManager().isStateSaved()) {
                getChildFragmentManager()
                        .beginTransaction()
                        .remove(flutterFragment)
                        .commit(); // ✅ 安全执行
            } else {
                // 如果状态已保存，使用commitAllowingStateLoss（不推荐但安全）
                getChildFragmentManager()
                        .beginTransaction()
                        .remove(flutterFragment)
                        .commitAllowingStateLoss(); // ✅ 允许状态丢失，避免崩溃
            }
        } catch (IllegalStateException e) {
            // 如果仍然失败，记录错误但不崩溃
            Log.w(TAG, "Failed to remove FlutterFragment: " + e.getMessage());
        }
        flutterFragment = null;
    }
}
```

## 关键点

1. **检查状态**：使用 `isAdded()` 和 `isStateSaved()` 检查是否可以安全执行事务
2. **使用 commitAllowingStateLoss()**：如果状态已保存，使用 `commitAllowingStateLoss()` 避免崩溃
3. **异常处理**：添加 try-catch 确保即使出错也不会崩溃

## 最佳实践

### 1. 检查 FragmentManager 状态

```java
if (isAdded() && !getChildFragmentManager().isStateSaved()) {
    // 安全执行事务
    getChildFragmentManager()
            .beginTransaction()
            .remove(fragment)
            .commit();
}
```

### 2. 使用 commitAllowingStateLoss()（谨慎使用）

```java
// 只在必要时使用，因为可能导致状态不一致
getChildFragmentManager()
        .beginTransaction()
        .remove(fragment)
        .commitAllowingStateLoss();
```

### 3. 避免在生命周期方法中执行事务

- ✅ 推荐：在 `onViewCreated()` 或用户操作时执行
- ❌ 避免：在 `onDestroyView()`、`onSaveInstanceState()` 等生命周期方法中执行

## 验证

修复后，应该：
1. ✅ 不再出现 `IllegalStateException`
2. ✅ 页面切换正常
3. ✅ Fragment 正确清理

## 相关资源

- [Android Fragment 生命周期](https://developer.android.com/guide/fragments/lifecycle)
- [Fragment Transactions](https://developer.android.com/guide/fragments/transactions)
- [Fragment State Loss](https://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html)

