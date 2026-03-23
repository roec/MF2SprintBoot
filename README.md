# Semantic Migration Studio (React + TypeScript + DeepSeek)

这是一个可落地的 **ReactJS + TypeScript** 工程，前端用于录入 RPG/PF/LF 等主机程序资产，后端（Node + TypeScript）调用 **DeepSeek** 并基于语义 IR 生成 Spring Boot 迁移代码骨架。

## Architecture

- `src/`: React + TypeScript 前端（Migration Studio UI）
- `server/`: TypeScript API（语义 IR 构建 + DeepSeek 调用 + 代码生成）

## Key Flow

1. 前端提交 `programName/programType/artifacts` 到 `/api/migrate`
2. 后端 `buildSemanticIr` 完成语义 IR 结构化
3. 后端调用 DeepSeek (`/chat/completions`) 生成迁移建议
4. 后端输出 Spring Boot 代码片段（Entity/Repository/Service/Controller/OpenAPI）

## Run

```bash
npm install
npm run dev
```

- 前端: `http://localhost:5173`
- API: `http://localhost:8787`

## DeepSeek Config

创建 `.env`：

```bash
DEEPSEEK_API_KEY=your_key
DEEPSEEK_MODEL=deepseek-chat
DEEPSEEK_BASE_URL=https://api.deepseek.com/v1
PORT=8787
```

未配置 `DEEPSEEK_API_KEY` 时，系统自动进入 dry-run 模式（可本地离线验证）。
