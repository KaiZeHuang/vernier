# 更新日志

Vernier 的重要变更，按时间倒序。标签格式为 `v<日期>-<渠道>`，版本号由 git 元数据推导，见
`docs/architecture/README.md`。`nightly` 是滚动构建，不在此逐条列出。

Vernier 处于 alpha 阶段。此处列出的探测器描述的是**设计覆盖**，不是任何具体设备上的实测结论；
可见性边界见每个探测器的 `EVIDENCE.md`。

## [未发布]

### 新增

- PlayIntegrityFix 残留属性检测：补上 `persist.sys.pihooks.first_api_level`。
- 原始启动参数对撞：`ro.boot.warranty_bit`（三星 Knox）与 `ro.boot.realmebootstate`。
  `resetprop` 改的是属性区本身，所有读取路径都一致，来源一致性检查抓不到；而
  `/proc/cmdline` 和 `/proc/bootconfig` 里由内核提供的 `androidboot.*` 值改不了。
- 构建画像一致性检查：`Build.TYPE` / `Build.TAGS` 与 `Build.FINGERPRINT` 尾部编码的类型和标签对撞。
  PlayIntegrityFix 会改前两者，但不改 fingerprint。
- 中英两份 README 加入检测覆盖表：18 个探测器、观测内容，以及直达各自证据记录的链接。
- 新增 `.editorconfig`，统一文本文件换行符与缩进，避免跨平台差异。

### 变更

- 备份与设备迁移规则从「未配置的 Android Studio 模板」改为按域显式全排除。
  `allowBackup="false"` 本来就是主防线，规则文件是第二层。

### 修复

- 文档写的是 17 个探测器，代码里实际是 18 个。已修正 profile README 与两篇社区发帖稿。

## [v2026.10.24-alpha] - 2026-10-24

首个公开 alpha。

- 五个测量组共 18 个探测器，每个都带 `EVIDENCE.md`，记录可观测信号、机制、主源引用、
  适用范围、可见性边界、结果状态与判读方式。
- 同一套探测器同时以不含 UI 的 SDK AAR 发布，其他应用可嵌入，无需引入 Compose 界面。
- Kotlin / Jetpack Compose 界面，配合 C++ 与汇编 JNI 层处理托管代码无法回答的探针。
- 多模块 Gradle 构建；模块边界、原生边界、JNI 契约、反射边界、文本协议、证据记录与
  源码文件长度由 7 个 CI 守卫脚本强制约束。
- 中英双语界面与文档。
- CI 在**没有配置任何密钥**时依然能构建、上传 APK 产物并发布 nightly；签名与 Telegram
  通知仅在对应密钥存在时启用。
- `-alpha` / `-beta` / `-rc` / `-preview` 标签发布为 GitHub 预发布，不抢占 `latest`。
- 发布重建基线从根提交推导，不再钉死一个历史重写后已不存在的 SHA。

[v2026.10.24-alpha]: https://github.com/KaiZeHuang/vernier/releases/tag/v2026.10.24-alpha
