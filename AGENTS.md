# Работа с библиотекой

- Java и Maven запускаются через mise; версии находятся в mise.toml.
- Проверка: `mise exec -- mvn -B clean verify`; CI: `./mvnw -B verify`.
- Библиотека сохраняет Java 11 (`release=11`), сборка по умолчанию на JDK 25.
- Координаты: `by.aurorasoft:conditional-expression-calculator`.
- Версия разрабатываемого цикла — только корневой pom.xml, не Git-тег.
- Первый выпуск после миграции: 7.5 вместо устаревшей 6.1 в POM; смена через
  Maven versions:set входит в legacy-релизный коммит. Следующий цикл: 7.6.
- Применять aurora-release с Maven-вариантом из README: master fast-forward,
  закрытие CHANGELOG, затем bump develop. Вместо Docker публикуется JAR/POM.
- PR и develop только проверяются. Push master выпускает пакет; перед ним
  обязательны проверка origin, разрешений, версии и согласование сводки выпуска.
- Не создавать теги локально: `v<version>` ставит CI после проверки скачанного JAR.
- Не запускать deploy/deploy-file, не публиковать вручную, не перезаписывать пакет.
- `.Codex/settings.json` — шаблон PreToolUse для совместимых клиентов. Текущий
  Codex не гарантирует исполнение Claude-совместимого Bash hook; обязательное
  ограничение задаётся этими инструкциями и правами CI, не наличием JSON-файла.
- Старые теги, ветку mvn-repo и существующий worktree target/thread-safe сохранять.
- Коммиты писать по-английски. Не менять runtime-зависимости в рамках миграции.
