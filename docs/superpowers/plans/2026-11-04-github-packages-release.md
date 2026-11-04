# GitHub Packages Release Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Подготовить публикацию только выпущенных версий библиотеки в GitHub Packages по циклу aurora-release.

**Architecture:** Корневой POM задаёт координаты, версию и адрес публикации. Один workflow проверяет develop/PR и публикует из master; небольшой Python-скрипт проверяет условия выпуска и скачанный артефакт. Первый выпуск 7.5 выполняется отдельно от реализации.

**Tech Stack:** Maven 3.9.16, Temurin 25, Java release 11, GitHub Actions/Packages, Python 3 standard library для CI-проверок.

**Spec:** `docs/superpowers/specs/2026-11-04-github-packages-release-design.md` (согласован).

## Global Constraints

- Координаты: `by.aurorasoft:conditional-expression-calculator`.
- Repository URL: `https://maven.pkg.github.com/primasoftllc/conditionalexpressioncalculator`.
- Repository/server id: `github-conditional-expression-calculator` во всех примерах и настройках.
- Версия только из корневого POM. Сейчас сохранить 6.1; 7.5 устанавливается через versions:set в отдельном legacy-релизном коммите; следующий цикл 7.6.
- Публикация только при push в master, после verify. Ни develop, ни PR, ни install не публикуют; SNAPSHOT/DEV в Packages отсутствуют.
- mise: `java = "temurin-25.0.4+7.0.LTS"`, `maven = "3.9.16"`; wrapper only-script; `maven.compiler.release=11`.
- Runtime-зависимости и алгоритмы расчёта не менять; Lombok/build-плагины обновлять только для сборки и публикации.
- Сохранять текущие коммиты, старые теги и mvn-repo. Не менять сервисы-потребители и глобальный aurora-release skill.
- Не выполнять deploy, push или создание тегов при реализации. Теги создаёт только CI. Коммиты на английском.

## Review Focus

- Отказ доступа/таймаут/5xx не означает свободную версию; публикация останавливается (Task 2).
- Частичная публикация только POM или JAR блокирует повторный deploy даже без тега (Task 2).
- CRLF, BOM и пустой/дублированный раздел CHANGELOG не должны случайно разрешить выпуск (Task 2).
- JDK 25 не должен повысить минимальную Java или изменить генерируемый Lombok/Jackson API (Task 1).
- Старые JMH-команды не должны молча измерять новую библиотеку вместо baseline (Task 3).

## Файлы и последовательность

| Файлы | Ответственность |
| --- | --- |
| `pom.xml`, `mise.toml`, `.mvn/maven.config`, `.mvn/wrapper/maven-wrapper.properties`, `mvnw`, `mvnw.cmd`, `.gitattributes` | Maven-координаты, сборка и совместимость |
| `src/test/java/by/aurorasoft/expressioncalculator/model/sensor/impl/CalibrationTest.java` | Проверка результата десериализации после обновления Lombok |
| `.github/workflows/ci.yml` | События, права, последовательность проверки/публикации/тегирования |
| `.github/scripts/release_checks.py`, `.github/scripts/tests/test_release_checks.py` | Проверки выпуска и происхождения артефакта; тесты без сети |
| `.Codex/settings.json`, `.Codex/hooks/guard-bash.sh` | Локальные ограничения агентской публикации |
| `benchmarks/pom.xml`, `benchmarks/README.md` | Выбор новых/исторических координат без повторения версии библиотеки |
| `readme.md`, `AGENTS.md`, `CHANGELOG.md`, `.gitignore` | Порядок работы, потребление, ограничения и исключение временных файлов |

Задачи последовательные. Применять using-git-worktrees при начале исполнения:
проверить чистоту дерева, fetch и ancestry, создать develop от текущего master,
затем изолированную рабочую ветку от develop. Не использовать существующий
`target/thread-safe` (ветка fix/thread-safe-sensors) для новой работы. Не помещать
новый worktree в target: clean может удалить его. Выбрать игнорируемый `.worktrees/`
внутри workspace либо уже предоставленную средой изоляцию. При появлении удалённого
develop или расхождении master проверить историю, не перезаписывать ветки.

## Task 1: Maven-сборка по новым координатам с сохранением Java 11

**Files:** modify `pom.xml`, `CalibrationTest.java`; create `mise.toml`, `.gitattributes`, `.mvn/maven.config`, wrapper files из таблицы.

**Interfaces:**
- Consumes: текущие исходники и версия 6.1 из POM.
- Produces: `target/conditional-expression-calculator-<version>.jar`, POM с новыми координатами, manifest `Implementation-Version` и `Build-Revision` (полный Git SHA).
- Commands: `mise exec -- mvn -B clean verify`, `mise exec -- mvn -B install`, `./mvnw -B verify` в CI.

- [ ] **1. Зафиксировать baseline на mise JDK 11.** Выполнить clean verify текущего POM; сохранить число тестов и список публичных/protected сигнатур через javap для всех классов в target вне каталога очищаемой сборки. Если mise отсутствует на PATH, использовать полный путь к mise.exe, не системную Java. Не менять глобальные настройки инструментов.
- [ ] **2. Усилить существующий `CalibrationTest.deserialize`.** Вместо println проверить размер rawDataList = 2, processedDataList = 0 и исходные значения 100/200 и 200/300 в десериализованных элементах. Прогнать тест на исходной сборке: он должен проходить и фиксировать существующее поведение.
- [ ] **3. Настроить Maven.** Новый groupId, прежний artifactId, версия остаётся 6.1. Заменить distributionManagement на согласованный URL/id; удалить site-maven-plugin, altDeploymentRepository и github.global.server. Зафиксировать версии compiler/surefire/deploy/jar и git-commit-id-плагина, совместимые с Maven 3.9.16 и запуском на JDK 11/25. У плагина Git не брать версию, требующую JDK выше 11. Точные совместимые версии выбираются исполнителем по официальным POM/документации.
- [ ] **4. Настроить компиляцию и происхождение JAR.** release=11; Lombok 1.18.42 (как в эталонном gateway) в provided и explicit annotationProcessorPaths с одной property версии. Git revision вычислять в initialize, полный SHA поместить в Build-Revision; Implementation-Version = project.version. Существующие Java-пакеты и runtime-зависимости не менять.
- [ ] **5. Добавить инструменты.** mise.toml с согласованными версиями; wrapper Maven 3.9.16 only-script; LF по умолчанию, cmd/bat CRLF, jar binary; `.mvn/maven.config` содержит `-ntp` и `-Daether.enhancedLocalRepository.trackingFilename=_ignored.repositories`. Для mvnw сохранить executable bit. Не включать JAR wrapper в Git.
- [ ] **6. Проверить новую сборку.** clean verify на JDK 25 и затем JDK 11 через mise; обе успешны, число тестов не уменьшилось. Проверить class-file major=55 для всех классов основного JAR, совпадение baseline публичных/protected сигнатур и заполненные manifest-поля. Объяснить любые различия перед продолжением; аннотации/внутренние детали javap не считать изменением сигнатур.
- [ ] **7. Проверить Maven-контракт.** effective-pom не содержит site-публикации и привязок deploy к ранним фазам; локальный install создаёт новые координаты и зависимости, не обращаясь к endpoint публикации. Корневой POM всё ещё 6.1. Вернуть финальную сборку на JDK 25 только если это требуется следующими проверками.
- [ ] **8. Коммит:** `build: prepare Maven artifacts for GitHub Packages` (точный список файлов Task 1).

## Task 2: Проверяемый релизный workflow

**Files:** create `.github/workflows/ci.yml`, `.github/scripts/release_checks.py`, `.github/scripts/tests/test_release_checks.py`.

**Interfaces:**
- Consumes: manifest/артефакты Task 1, `GITHUB_REPOSITORY`, `GITHUB_SHA`, `GITHUB_ACTOR`, `GITHUB_TOKEN`.
- `validate_release(version: str, changelog: str) -> None`: ошибка через `ReleaseError`; допускается только X.Y или X.Y.Z.
- `check_remote_release(repository: str, group: str, artifact: str, version: str, actor: str, token: str) -> None`: чтение GitHub/Packages с таймаутами; тесты подменяют HTTP transport.
- `verify_download(local_pom: Path, local_jar: Path, fetched_pom: Path, fetched_jar: Path, expected_sha: str) -> None`: координаты, версия, POM/JAR SHA-256 и manifest; несовпадение вызывает ReleaseError.
- CLI `preflight --pom PATH --changelog PATH`: читает env, проверяет локальную версию/CHANGELOG и remote; CLI `verify-download --local-pom PATH --local-jar PATH --fetched-pom PATH --fetched-jar PATH`: проверяет артефакты с GITHUB_SHA. Exit 0 — успех, 1 — отказ с причиной без секретов.

- [ ] **1. Написать unit-тесты guard до реализации.** Использовать unittest и стандартную библиотеку, временные POM/JAR, подменные HTTP-ответы, никаких действующих токенов. Минимальный контракт:

```python
validate_release("7.5", "## 7.5\n\n- Migration\n")  # succeeds
# Each must raise ReleaseError:
# 7.5-SNAPSHOT, 7.5-DEV, empty version, version containing shell metacharacters
# missing/empty/duplicate ## 7.5, any ## Не выпущено
# existing v7.5, historical 7.5, POM-only, JAR-only, version in Maven metadata
# HTTP 401, 403, 429, 500, timeout, malformed successful metadata response
# artifact/POM hash mismatch, wrong coordinates/version/SHA, unresolved manifest property
```

  Дополнить именованными тестами `test_crlf_and_bom_changelog`, `test_missing_registry_version_requires_authenticated_access`, `test_manifest_continuation_lines`, `test_failure_does_not_echo_token`. Успешный сценарий: доступ подтверждён, оба тега и версия отсутствуют, затем идентичный скачанный артефакт принимается. Проверить также CLI nonzero для отказов.
- [ ] **2. Запустить `python -m unittest discover -s .github/scripts/tests -v`.** Ожидание до реализации: ошибки отсутствующего модуля/функций, а не ошибки окружения.
- [ ] **3. Реализовать guard.** Python stdlib XML/ZIP/HTTP/hashlib, без shell-интерполяции версий и без токенов в URL. Отдельно подтвердить аутентификацию/доступ к репозиторию через API; читать точные Git refs для обоих вариантов тега. Проверять Maven metadata и прямые пути POM/JAR, чтобы обнаруживать частично загруженную версию. Только ожидаемый 404 после подтверждения доступа считать отсутствием; неожиданные редиректы, ответы и ошибки — отказ. При первой публикации отсутствие metadata допустимо. Учитывать XML namespace, BOM/CRLF и переносы manifest. Не добавлять удаления или обход проверок.
- [ ] **4. Прогнать unit-тесты до зелёного результата.** Подставные HTTP-вызовы не могут обратиться к настоящей сети. Полный SHA/координаты не брать из скачанного пакета как ожидаемое значение — ожидаемые значения принадлежат проверенному checkout.
- [ ] **5. Создать workflow.** PR в develop/master и push в develop/master; без workflow_dispatch. `permissions: contents: read` по умолчанию; concurrency по ref, cancel-in-progress false для master. Job verify запускает Python unit-тесты и `./mvnw -B verify` на Temurin 25, cache maven. Actions фиксировать на проверенных стабильных версиях. PR не получает write permissions/секреты.
- [ ] **6. Добавить release job.** `needs: verify`, условие строго push + refs/heads/master, checkout точного github.sha; `packages: write`, `contents: write`. setup-java server-id из Global Constraints, credentials через GITHUB_ACTOR/GITHUB_TOKEN. Выполнить preflight, `./mvnw -B clean verify`, затем явный `./mvnw -B deploy -DskipTests` без clean/смены исходников. До deploy сохранить хеш проверенного JAR и POM, после убедиться, что опубликованный кандидат не изменился. При проверке исходного SHA убедиться, что Git HEAD равен GITHUB_SHA.
- [ ] **7. Проверить скачивание до тегирования.** Создать чистый временный Maven local repository, dependency:get фиксированной версии plugin для GAV из POM с transitive=false и согласованным remote id/URL; отдельно получить POM, если он не скачан resolver. Не использовать кеш сборки или install-файл вместо загрузки. Вызвать verify-download с сохранённым кандидатом. Допустим ограниченный повтор чтения при задержке появления пакета; при исчерпании попыток workflow падает без тега.
- [ ] **8. Добавить тегирование и диагностику.** Только после успешного скачивания CI создаёт `v<version>` на точном GITHUB_SHA и пушит только этот тег без force. GitHub Release не создавать. Summary содержит GAV, SHA, URL пакета/workflow и последний завершённый этап; при сбое после deploy явно говорит о возможной частичной публикации и запрете повторного deploy без разбора.
- [ ] **9. Проверить workflow.** actionlint: ноль ошибок. Unit-тесты: PASS. Просмотреть условия и порядок для PR, develop, master; доступ на запись только у release job. На текущем POM 6.1/незакрытом CHANGELOG preflight должен отказать до deploy. Не запускать workflow в GitHub ради проверки.
- [ ] **10. Коммит:** `ci: publish verified releases to GitHub Packages`.

## Task 3: Инструкции, ограничения публикации и совместимые JMH-команды

**Files:** modify `readme.md`, `CHANGELOG.md`, `benchmarks/pom.xml`, `benchmarks/README.md`, `.gitignore`; create `AGENTS.md`, `.Codex/settings.json`, `.Codex/hooks/guard-bash.sh`.

**Interfaces:**
- Consumes: Maven CLI, metadata и CI-контракт Tasks 1–2.
- Produces: инструкции первого релиза 7.5 и следующего цикла 7.6; параметры JMH `calculator.groupId`, `calculator.artifactId`, `calculator.version`.

- [ ] **1. Настроить JMH-координаты.** groupId/artifactId по умолчанию новые; убрать старую hardcoded calculator.version, требовать явное `-Dcalculator.version=<значение из корневого POM>` (проверка обязательного свойства через enforcer с понятной ошибкой). Для baseline все три свойства переопределяются. Версия sensor-benchmarks остаётся своей 1.0-SNAPSHOT и не публикуется. При компиляции JMH на JDK 25 явно подключить существующий JMH annotation processor.
- [ ] **2. Проверить JMH до изменения инструкций.** Из корня install текущей библиотеки, затем `mise exec -- mvn -B -f benchmarks/pom.xml package -Dcalculator.version=6.1`; `mise exec -- java -jar benchmarks/target/benchmarks.jar -l` показывает benchmarks. Без calculator.version сборка должна завершаться понятной ошибкой. Для старого пути задать `-Dcalculator.groupId=com.github.PrimaSoftLLC -Dcalculator.artifactId=ConditionalExpressionCalculator -Dcalculator.version=7.4-thread-safety-baseline-local-SNAPSHOT`, проверить dependency:tree и сборку/список JMH. Если baseline отсутствует локально, воспроизвести описанный в старом README install-file из исходного 7.4 во временном workspace; не подменять baseline новым кодом и не публиковать его.
- [ ] **3. Обновить benchmarks/README.md.** Новые команды сначала читают project.version из корневого POM и используют её в install/package. У исторических команд явно указать старые groupId/artifactId. Сохранить результаты, commit ids и исходные версии измерений; повторный полный JMH-прогон для миграции не нужен.
- [ ] **4. Добавить локальный hook.** Адаптировать `.claude/settings.json` и `.claude/hooks/guard-bash.sh` эталонного gateway в `.Codex`; путь hook должен указывать на новый файл. Allow-list — сборка и чтение, deny — публикация. Проверить подставным stdin JSON: verify/install проходят, `mvn deploy`, `mise exec -- mvn deploy`, `./mvnw deploy`, квалифицированные deploy/deploy-file goals и docker push/docker:push блокируются. Не выполнять команды из примеров. Не заявлять о поддержке PreToolUse текущим Codex, если она не подтверждена; описать ограничение в AGENTS.md.
- [ ] **5. Обновить readme.md.** Разделы: инструменты и сборка; зависимость 7.5 как пример после первого выпуска; repository/settings.xml с env GH_PACKAGES_USER/GH_PACKAGES_TOKEN; read:packages и доступ; цикл aurora-release и legacy-исключение 6.1→7.5→7.6; только CI выполняет deploy; частичная публикация требует разбора без перезаписи/удаления. Отметить, что 7.5 ещё не опубликована и потребители 7.4 не получают исправления автоматически. Старую ссылку о mvn-repo оставить только как историческую справку или удалить из действующих инструкций.
- [ ] **6. Написать AGENTS.md и CHANGELOG.** Зафиксировать Maven-вариант aurora-release без Docker, проверку SHA и branch protections, команды verify, получение версии, английские коммиты. В `## Не выпущено` добавить миграцию Packages, новые координаты и сохранённую Java 11; не закрывать раздел. В .gitignore добавить только реально используемые временные каталоги (например __pycache__), не скрывать исходники и workflow.
- [ ] **7. Проверить примеры и интеграцию.** Команды локальной сборки/получения версии работают; repository/server id совпадают в POM, workflow и README. XML/JSON корректны; `git diff --check` без ошибок. Просмотреть diff на случайный bump версии, изменения runtime-кода/зависимостей и публикацию из develop. Повторять уже прошедшие Maven-тесты только если новые изменения затронули их результат.
- [ ] **8. Коммит:** `docs: document release migration and update benchmark setup`.

## Итоговая проверка и передача

- [ ] Сопоставить итоговый diff с каждой секцией Spec и закрыть замечания ревью всего изменения. Ветка содержит все текущие исправления потокобезопасности, без потери коммитов.
- [ ] Зафиксировать результаты JDK 11/25, guard unit-тестов, actionlint, JMH smoke и локальной установки; указать реальные ограничения, включая непроверенные права организации/живую публикацию.
- [ ] Сообщить текущую ветку и коммиты. Интеграция в develop допустима по выбранному процессу; master не двигать и ничего не пушить в этой реализации.
- [ ] Предложить следующий отдельный шаг `/aurora-release 7.5`; не выполнять его автоматически. Фактический выпуск требует проверки доступа, обновлённого origin, version guards и разрешённого push.
- [ ] В финале реализации предложить `/aurora-impl-report`, как требуют глобальные инструкции.

## Самопроверка плана

Проверены покрытие Spec, единые имена CLI/manifest/properties и порядок зависимостей.
Пять Review Focus распределены между Tasks 1–3. Живая публикация и восстановление
частичного релиза намеренно остаются вне реализации. Тесты добавляются для логики
релизных проверок и совместимости, а не для зеркального повторения конфигурации.
