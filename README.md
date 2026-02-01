# 自律日 (ZiLvRi)

一款帮助你管理日常待办事项、培养自律习惯的 Android 应用。

## 📱 功能特点

### 首页
- **日历视图**: 支持年/月/周/日四种视图切换
- **待办事项清单**: 分组管理待办事项
- **完成状态指示**: 
  - 进行中任务显示浅色横杠+绿点
  - 全部完成显示绿色勾
- **完成动画**: 全部完成后显示立体"赞"图标+经验粒子特效

### 备忘录
- 创建和编辑备忘录
- 支持置顶功能
- 搜索功能
- 多彩卡片样式

### 个人中心
- 账号系统（支持QQ/微信登录）
- 等级成长系统
- 主题切换（浅色/深色/跟随系统）
- 音效和震动设置

## 🛠 技术栈

- **语言**: Kotlin
- **架构**: MVVM
- **数据库**: Room
- **异步处理**: Kotlin Coroutines + Flow
- **UI组件**: Material Design 3
- **动画**: Lottie

## 📦 项目结构

```
app/src/main/java/com/ziluri/app/
├── data/
│   ├── model/          # 数据模型
│   ├── database/       # Room数据库
│   └── repository/     # 数据仓库
├── ui/
│   ├── home/          # 首页相关
│   ├── memo/          # 备忘录相关
│   ├── profile/       # 个人中心相关
│   └── common/        # 公共组件
├── util/              # 工具类
└── service/           # 后台服务
```

## 🚀 编译运行

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17
- Android SDK 34

### 编译步骤
1. 克隆项目
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步完成
4. 点击 Run 按钮运行

### 生成 APK
```bash
./gradlew assembleRelease
```
APK 文件位于: `app/build/outputs/apk/release/`

## 📝 待完成功能

- [ ] 接入 QQ SDK 实现真实登录
- [ ] 接入微信 SDK 实现真实登录
- [ ] 添加音效资源文件
- [ ] 实现通知提醒功能
- [ ] 添加数据云同步
- [ ] 实现成就系统
- [ ] 添加 Widget 小组件

## 🎨 设计规范

### 颜色
- 主色: #10B981 (绿色)
- 强调色: #6366F1 (蓝紫色)
- 背景: #F9FAFB (浅灰)

### 字体
- 使用系统默认字体
- 可替换为 SF Pro 或其他字体

## 📄 许可证

MIT License

## 🙏 致谢

- Material Design 3 组件库
- Lottie 动画库
- Room 数据库
