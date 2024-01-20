# blogapi

因为一开始的取名失误，其实应该是 `bbsapi`

## 开发

确保配置好 Java 环境和 Gradle

### 开发模式

开发模式使用本地 H2DB 数据库

```bash
./gradlew
```

## 部署

数据库使用 MariaDB

### 环境变量

| 名称           | 示例                                                                                       | 作用             |
| -------------- | ------------------------------------------------------------------------------------------ | ---------------- |
| `DATABASE_URL` | `jdbc:mariadb://localhost:3306/blogapi?useLegacyDatetimeCode=false&user=root&password=xxx` | 数据库连接字符串 |

### 构建

```bash
./gradlew -Pprod clean bootJar
```

### 运行

```bash
# export DATABASE_URL="xxx"
java -jar build/libs/*.jar
```

### Docker

构建 image:

先本地构建出 jar 文件

```bash
docker buildx build -t bbs-server:latest .
```

运行:

```bash
docker run --name bbs-server -d \
    -p 8080:8080 -e DATABASE_URL="xxx" \
    bbs-server:latest
```

docker compose:

```bash
vim docker-compose.yml
docker compose up -d
```
