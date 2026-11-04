# Thread-safe Sensor Calculation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Безопасно вычислять датчики параллельно по одному подготовленному снимку, сохранив семантику LocatorServer и небольшой расход памяти.

**Architecture:** Подготовка сохраняет выражения до передачи снимка потокам. Вычисление читает динамические операнды прямо в локальный double[] и не изменяет токены или SensorI. Неподготовленные выражения компилируются локально без автоматического кеширования.

**Tech Stack:** Java 11, Maven 3.9.16, JUnit 4.13.2 в библиотеке; Java 25, JUnit Jupiter и существующие builders в LocatorServer; изолированный JMH benchmark.

**Spec:** [Согласованный дизайн](../specs/2026-11-04-thread-safe-sensor-calculation-design.md).

## Global Constraints

- Сохранить публичные сигнатуры, арифметику, NaN, калибровку, порядок разрешения динамических операндов и поведение собственных/внешних условий.
- Не вводить короткое замыкание логических выражений, мемоизацию зависимостей, изменение обработки циклических ссылок, новую грамматику или новые ошибки.
- В подготовленном пути нет блокировок, ThreadLocal, глобального кеша, клонирования датчиков и коллекций значений.
- EToken.setValue сохраняется для совместимости старого API, но SensorCalculator его больше не вызывает.
- Одновременная подготовка и вычисление одного снимка не поддерживается и прямо документируется.
- Публикация, создание релизных тегов и деплой не входят в это согласование.
- Все Java/Maven команды запускать через mise; commit messages — English.
- Не менять пользовательские изменения; перед исполнением прочитать AGENTS.md обоих репозиториев и создать изоляцию согласно using-git-worktrees. Дизайн и план должны быть доступны из исполнительных checkout.

## Review Focus

1. Неподготовленный сторонний SensorI с обычными полями: параллельное вычисление не вызывает setters — задача 1.
2. Повторные ссылки на один датчик и преобразования в calculateValue: порядок и количество вызовов сохраняются — задача 1.
3. {x} после калибровки и во внешнем условии другого датчика: значение привязано к текущему вычислению — задачи 1 и 3.
4. Ошибочное или пустое выражение: подготовка не ломает создание снимка, повторный расчёт не компилирует пустой результат заново — задача 2.
5. Уже подготовленные списки и смешанный снимок: сохраняются переданные ссылки, повторная подготовка не заменяет их — задача 2.

## Files and responsibilities

Все пути относительны корню библиотеки, кроме явно обозначенных LocatorServer.

- Modify `src/main/java/by/aurorasoft/expressioncalculator/ExpressionCalculator.java`: вычисление с источником динамических значений.
- Modify `src/main/java/by/aurorasoft/expressioncalculator/SensorCalculator.java`: локальное разрешение операндов, подготовка, безопасный fallback.
- Modify `src/main/java/by/aurorasoft/expressioncalculator/model/EToken.java` и `model/sensor/SensorI.java`: только уточнение Javadoc контракта, без изменения интерфейса.
- Create `src/test/java/by/aurorasoft/expressioncalculator/SensorCalculatorConcurrencyTest.java`: управляемые пересечения и неизменность состояния.
- Create `src/test/java/by/aurorasoft/expressioncalculator/SensorPreparationTest.java`: подготовка и совместимость fallback.
- Modify `src/test/java/ExpressionCalculatorTest.java`: сохранение старого API.
- Modify `src/test/java/by/aurorasoft/expressioncalculator/SensorCalculatorTest.java`: подготовить датчик перед существующим миллионным циклом.
- Modify `readme.md`; create `CHANGELOG.md`: контракт, миграция вызовов, проверки.
- LocatorServer modify `aurora-app/src/main/java/com/locator/server/domain/dto/sensor/SensorBunchCalculator.java`: подготовка снимка.
- LocatorServer create `aurora-app/src/test/java/com/locator/server/domain/dto/sensor/SensorBunchCalculatorConcurrencyTest.java`: реальные SensorCalc и внешние условия.
- LocatorServer modify `aurora-app/pom.xml`: overridable property версии зависимости с исходным default 7.4.
- LocatorServer modify `changelog/README.md`: запись в существующий раздел невыпущенных изменений, следуя его формату.
- Create `benchmarks/pom.xml`, `benchmarks/src/main/java/by/aurorasoft/expressioncalculator/benchmark/SensorCalculationBenchmark.java`, `benchmarks/README.md`: отдельный JMH проект, команды и результаты.
- Modify `.gitignore`: игнорировать `/benchmarks/target/`, где лежат сырые измерения и benchmark JAR.

## Execution preparation

- [ ] Проверить статус обоих репозиториев и существующие локальные инструкции; создать изолированные ветки/checkout, не смешивая задачи с работой пользователя. Изменения LocatorServer вне writable roots требуют штатного разрешения sandbox.
- [ ] Зафиксировать исходный результат `mise exec java@temurin-11.0.32+9 -- mvn -B test` в библиотеке. Сохранить исходный JAR и POM от commit `7b3b0ba` для сравнения скорости; выполнять сборку baseline в отдельном checkout. Не применять исправления к baseline.

## Task 1: Stateless expression and sensor evaluation

**Files:** ExpressionCalculator.java, SensorCalculator.java, EToken.java, SensorI.java, ExpressionCalculatorTest.java, SensorCalculatorConcurrencyTest.java.

**Interfaces:**
- Preserve public `double calculateMath(List<EToken> tokens)` and `boolean calculateConditional(List<EToken> tokens)`.
- Add public overloads with second argument `ToDoubleFunction<EToken> valueResolver`; resolver is used for numeric operands and returns primitive double.
- Preserve all existing SensorCalculator and SensorI signatures.
- Add private `double resolveValue(EToken token, MessageI message, double currentValue, Map<Long, ? extends SensorI> sensorMap)` in SensorCalculator.
- Resolver handles NUMB via getValue, PROPERTY/PARAMETER via message, SENSOR_ID 0 via currentValue, otherwise via `sensorMap.get(id).calculateValue(message, sensorMap)`.
- A captured resolver per expression is permitted initially; no object per token. Measure allocation and optimize this mechanism in task 4 if needed.

- [ ] **Write failing deterministic regressions.** Prepare expressions using the existing build methods, so original failure is token interference rather than compilation. Use MessageImpl subclass overrides for controlled getters, CountDownLatch and Future timeouts; always release gates and shut down executors in finally. Define one reusable overlap fixture that pauses A while reading its second operand, executes all of B, then resumes A. Do not synchronize in EToken.setValue: the new implementation never calls it.

  Tests and exact observations:
  ```java
  // shared formula "{@a}+{@b}"; A: a=1,b=2, B: a=100,b=200
  assertEquals(3d, resultA, 0d);
  assertEquals(300d, resultB, 0d);
  // A pauses at getParameterValue("b"); legacy produces 102 for A.
  // own condition "{@a}<{@b}"; A:1,2 B:100,200; formula is constant 7
  assertEquals(7d, resultA, 0d);
  // external condition "{@a}<{@b}" under same overlap
  assertTrue(conditionA);
  // two parent sensors referencing shared dependency "{@a}+{@b}"
  assertEquals(4d, parentAResult, 0d); // parent formula "{shared}+1"
  assertEquals(301d, parentBResult, 0d);
  ```
  Names: `sharedFormulaKeepsMessageValues`, `ownConditionKeepsMessageValues`, `externalConditionKeepsMessageValues`, `sharedDependencyKeepsMessageValues`.
  Добавить `propertyValuesStayLocal` с формулой `{#speed}+{@b}` (speed=1/100, b=2/200, результаты 3/300); `currentValueStaysLocalInConditions` для собственного и внешнего `{x}<{@limit}` (x=20/200, limit=30/300, оба true), задерживая A на чтении limit.
  Snapshot token values as primitive values, not shallow copies of mutable tokens; assert all values and list identities remain unchanged.

- [ ] **Run RED.** `mise exec java@temurin-11.0.32+9 -- mvn -B test '-Dtest=SensorCalculatorConcurrencyTest'`. Expect wrong numeric/boolean results on original implementation, not latch timeout.
- [ ] **Implement evaluation.** Delegate legacy expression entry points using `EToken::getValue`. New overloads retain existing stack and operations. SensorCalculator uses resolving overloads; remove replaceTokens. Read each expression-list reference once. If raw is non-null and list null, compile locally with the old error-to-empty behavior; never call setFormula/setCondition from calculation. Share a private compiler with explicit build methods rather than duplicating exception handling.
- [ ] **Add compatibility assertions.** Names: `unpreparedCallsDoNotStoreTokens` (throwing setters, calculate twice and concurrently, fields remain null); `legacyEvaluatorReadsAssignedValues` (EToken.parameter with setValue(9), old API returns 9); `dependencyOverridesAreCalledEveryTime` (same dependency twice, overridden value 42, result 84, call count 2); `nestedDependencyUsesSeparateStack` (leaf 3, middle leaf+2, parent middle*4 =>20); `currentValueRetainsCalibrationSemantics` (calibration raw 10=>20, own condition x=20 =>20; formula x+1 =>1 with identity calibration); `externalConditionUsesTargetValue` (target result 20, external x=20 true); `missingParameterRemainsNaN`; `logicalOperandsAreStillResolvedInOrder` (instrument getters, no short circuit). Assert prepared token values unchanged for properties, parameters and sensor references.
- [ ] **Run GREEN and existing suite.** `mise exec java@temurin-11.0.32+9 -- mvn -B test`. Expect no new failures; distinguish any independently recorded baseline failures. Check `rg -n 'setValue|setFormula|setCondition' src/main/java/by/aurorasoft/expressioncalculator/SensorCalculator.java` manually: setters only in explicit preparation, never execution.
- [ ] **Commit:** `fix: isolate sensor expression values per calculation`.

## Task 2: Explicit snapshot preparation and migration contract

**Files:** SensorCalculator.java, SensorPreparationTest.java, SensorCalculatorTest.java, readme.md, CHANGELOG.md.

**Interfaces:** Add `public static void prepare(Map<Long, ? extends SensorI> sensorMap)`; calls existing public buildFormula/buildCondition for every sensor. Existing non-null lists are preserved. Newly compiled lists are structurally immutable (`List.copyOf`); compile errors produce `List.of()`. Null raw retains old no-expression behavior.

- [ ] **Write tests.** `prepareCompilesAllSensorsBeforeUse`: dependency named shared, parent formula `{shared}+1`, own condition `{x}>0`; after prepare all non-null raw expressions have a non-null list; result with shared=3 is 4. `prepareIsIdempotent`: second call preserves list identity. `preparedCalculationDoesNotRecompile`: instrument getName/setters after preparation, repeated calculation performs neither name resolution nor setters. `mixedSnapshotPreservesExistingLists`: preset one formula and retain reference while filling missing conditions. `emptyAndInvalidExpressionsKeepFallback`: null/empty/unknown-name formula with identity calibration yields 0, invalid/empty own condition yields true; prepared invalid expression stays the same empty list. Assert mutation of newly compiled list throws UnsupportedOperationException and prepare(emptyMap) succeeds.
- [ ] **Run RED.** `mise exec java@temurin-11.0.32+9 -- mvn -B test '-Dtest=SensorPreparationTest'`; initially missing prepare method or failing assertions.
- [ ] **Implement prepare and immutable output.** No recursive dependency evaluation and no locks. Keep evaluation fallback local; keep explicit build methods as the only writers. Prepare before the existing million-iteration test loop so it continues measuring reuse rather than repeated parsing.
- [ ] **Run GREEN.** `mise exec java@temurin-11.0.32+9 -- mvn -B test`; no new failures. Confirm all calculation APIs are tested with both prepared and unprepared inputs.
- [ ] **Document migration.** README example: construct full map → prepare(map) → submit jobs. Explicitly state no concurrent preparation/configuration/token mutation; caller safely publishes messages and snapshot. Describe loss of implicit caching, repeated error logging without preparation, preserved custom calculateValue, and unchanged {x}. Add CHANGELOG entry; do not invent a release version or change pom 6.1 merely to match tag 7.4.
- [ ] **Commit:** `feat: add explicit sensor snapshot preparation`.

## Task 3: LocatorServer snapshot integration

**Files:** LocatorServer SensorBunchCalculator.java, SensorBunchCalculatorConcurrencyTest.java, aurora-app/pom.xml, changelog/README.md.

**Interfaces:** Constructor `SensorBunchCalculator(List<SensorCalc> list)` prepares all final sensors after super(list). For source compatibility with dependency 7.4, invoke the already available `SensorCalculator.buildFormula(sensor, sensorsMap)` and `buildCondition(sensor, sensorsMap)` in a single loop. This is equivalent to new prepare(map); do not call the new method before Locator can depend on its published version. Existing calculateValue dispatch and FuelSensorDataUtils calls remain unchanged.

- [ ] **Create local validation artifact.** Build library with JDK 11. Under target create a copy of its POM whose GAV is `com.github.PrimaSoftLLC:ConditionalExpressionCalculator:7.4-thread-safety-local-SNAPSHOT`, preserving dependencies. Install JAR plus this POM using explicit `mvn install:install-file`, never deploy. Do not overwrite any 7.4 artifact. Add property `conditional-expression-calculator.version` default `7.4` to aurora-app/pom.xml and reference it from the existing dependency; tests below override the property.
- [ ] **Write constructor and concurrency tests with existing builders.** `constructorPreparesFullSnapshot`: `BuilderSensorCalc.custom` parent `{shared}+1` before shared in list, formula and condition present immediately. `snapshotPreservesCustomDependencyResult`: `BuilderSensorCalc.keyValue` maps 200→2, parent `{shared}+1`, result=3. `sharedSnapshotSupportsFiveWorkers`: five parent sensors sharing one dependency, immutable messages with distinct values, each task checks every result through Future.get; assert token snapshots unchanged. `externalConditionRemainsReusable`: build another sensor's x-based condition, apply through sb.applyCondition to target values 20 and 30, expected true and false for x=20; assert same token values. `emptySnapshotIsPrepared` and `invalidExpressionDoesNotBreakConstruction`.
- [ ] **Run RED constructor test** from Locator root:
  `mise exec -- mvn -B -pl aurora-app -am test '-Dconditional-expression-calculator.version=7.4-thread-safety-local-SNAPSHOT' '-Dsurefire.failIfNoSpecifiedTests=false' '-Dtest=SensorBunchCalculatorConcurrencyTest'`.
  Expect initially null prepared lists, not a dependency resolution failure.
- [ ] **Implement constructor preparation** after the entire map exists. Add a comment explaining preparation-before-publication; no alteration of service caching or threading. Add unreleased changelog note that the race fix requires the new library artifact, while constructor preparation alone does not fix 7.4.
- [ ] **Run GREEN and relevant regressions** with the same command, replacing test selector with `SensorBunchCalculatorConcurrencyTest,SensorCalc*Test,ModeSensorDataTest`. These are focused tests; if a selected existing class requires Spring/Docker, inspect its setup and run required infrastructure rather than silently skip it.
- [ ] **Check source compatibility with default 7.4:** root `mise exec -- mvn -B -pl aurora-app -am compile`. This validates only compilation; do not claim thread safety on 7.4. Commit property/default and source changes with `fix: prepare sensor snapshots before concurrent use`.
- [ ] **Record delivery boundary.** Keep default version at published 7.4; document exact override used for validation and that race fix becomes effective only after the updated library is published and dependency version changed. No release/tag/push/deploy in this task.

## Task 4: Performance evidence and final review

**Files:** benchmarks/pom.xml, benchmarks/src/main/java/by/aurorasoft/expressioncalculator/benchmark/SensorCalculationBenchmark.java, benchmarks/README.md, .gitignore; evaluation internals only if measurements identify overhead.

**Interfaces:** Standalone Maven benchmark project, not a root module or runtime dependency. Java release 11; JMH core + annotation processor 1.37, shade executable JAR. Property `calculator.version` selects locally installed comparison artifact. Preparation uses old public build methods, so identical benchmark source works against baseline and fixed JAR.

- [ ] **Create fixtures and correctness checks.** Shared benchmark state contains prepared sensors and at least 256 immutable messages; per-thread cursor varies input. Benchmarks: `formula` (`{@a}+{@b}*2`), `condition` (same formula plus `{x}>0`), `dependency` (parent uses shared twice); return numeric result to JMH. `prepareSnapshot` creates fresh sensors and explicitly prepares them; report its costs separately. Check expected values in fixture setup; run a separate five-worker correctness pass on fixed JAR, not assertions inside timed methods. Use ordinary message parameter/property access, matching library usage. Add `/benchmarks/target/` to .gitignore.
- [ ] **Install baseline and fixed artifacts** with preserved dependency POMs and distinct versions `7.4-thread-safety-baseline-local-SNAPSHOT` and `7.4-thread-safety-local-SNAPSHOT`. Never overwrite published coordinates. Package the same benchmark project separately for each version and label output with SHA/JDK.
- [ ] **Measure both** with same JDK 11/JVM options and machine, 3 forks, 5 one-second warmup iterations, 5 one-second measurement iterations, throughput and `-prof gc`; run threads 1 then 5. For example from benchmarks: `mise exec java@temurin-11.0.32+9 -- mvn -B package '-Dcalculator.version=7.4-thread-safety-local-SNAPSHOT'`, then `mise exec java@temurin-11.0.32+9 -- java -jar target/benchmarks.jar -f 3 -wi 5 -i 5 -w 1s -r 1s -t 1 -prof gc -rf json -rff target/fixed-t1.json`. Run long commands asynchronously and provide progress. Preserve raw outputs under ignored targets; summarize throughput, gc.alloc.rate.norm, uncertainty and preparation separately in README.
- [ ] **Resolve regressions if present.** If per-expression captured resolver survives escape analysis and increases bytes/op or throughput regresses consistently, replace the internal mechanism with direct context overloads while preserving task 1's public APIs. No ThreadLocal/pooling/global cache. Re-run affected correctness tests and only affected benchmarks after changes. Do not conclude speedup from a single run; old parallel output may be wrong and must be labelled accordingly.
- [ ] **Final verification:** library JDK 11 `mvn -B verify`; focused Locator tests with the fixed-version override; diff checks for both repositories; confirm no calculation path writes shared state and dependency dispatch preserved. Report actual tests/counts, performance results, any baseline failures and unresolved publication step.
- [ ] **Commit:** `perf: benchmark concurrent sensor calculation` (and separate focused fix commit if measurement required one). Execute appropriate independent code review per chosen execution workflow, then offer integration choices through finishing-a-development-branch. Do not merge or publish automatically.
- [ ] **Final report:** explain changed behavior, measured throughput/allocation, test results and dependency publication boundary; end with `Отчёт для ревью: /aurora-impl-report`.

## Self-review

- Coverage: token isolation and compatibility in task 1; preparation/fallback contract in task 2; real consumers and publication in task 3; performance evidence in task 4.
- Type consistency: only new public API requirements are two primitive-resolver overloads and void prepare(Map<Long, ? extends SensorI>); Locator uses existing build methods until release.
- Review Focus: each of five conditions has named assertions in its owning task.
- Scope: no implementation yet; design approval is recorded. Execution method awaits user selection as required by writing-plans.
