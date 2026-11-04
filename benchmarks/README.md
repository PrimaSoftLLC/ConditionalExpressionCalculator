# Измерения калькулятора

## Текущая сборка

Из корня репозитория, после mise trust/install:

```powershell
mise exec -- mvn -B install
$calculatorVersion = mise exec -- mvn -q help:evaluate '-Dexpression=project.version' -DforceStdout
mise exec -- mvn -B -f benchmarks/pom.xml package "-Dcalculator.version=$calculatorVersion"
mise exec -- java -jar benchmarks/target/benchmarks.jar -l
```

По умолчанию используются by.aurorasoft:conditional-expression-calculator.
Версию необходимо передать явно из установленного корневого POM.
Для исторических измерений ниже к каждой Maven-команде добавить
`'-Dcalculator.groupId=com.github.PrimaSoftLLC' '-Dcalculator.artifactId=ConditionalExpressionCalculator'`.
Это исключает случайную замену baseline текущей библиотекой. Локальные SNAPSHOT
для измерений не публикуются в GitHub Packages.

## Исторические измерения

JMH 1.37 находится в отдельном Maven-проекте и не входит в runtime-зависимости
библиотеки. Сравниваются исходный commit 7b3b0ba (тег 7.4) и исправленный код.
Координаты локальной установки: com.github.PrimaSoftLLC:ConditionalExpressionCalculator,
версии 7.4-thread-safety-baseline-local-SNAPSHOT и 7.4-thread-safety-local-SNAPSHOT.
Для установки используется install:install-file с копией исходного POM, где заменены
только координаты; это сохраняет зависимости и не перезаписывает опубликованную 7.4.

Из каталога benchmarks:

```powershell
mise exec java@temurin-11.0.32+9 -- mvn -B package '-Dmaven.jar.forceCreation=true' '-Dcalculator.version=7.4-thread-safety-local-SNAPSHOT'
mise exec java@temurin-11.0.32+9 -- java -jar target/benchmarks.jar '.*calculate' -f 3 -wi 5 -i 5 -w 1s -r 1s -t 1 -prof gc -rf json -rff ../target/measurements/fixed-t1.json
```

Повторить с -t 5 и с baseline-версией, пересобрав JAR перед сменой версии.
Для стоимости подготовки: заменить фильтр на `.*prepareSnapshot`, добавить
`-p scenario=formula`, использовать один поток. Подготовка включает создание свежих
датчиков, карты и компиляцию. Все выражения перед calculate подготовлены через
совместимые с обеими версиями buildFormula/buildCondition.

Каждое измерение: три отдельных JVM, пять секунд прогрева и пять секунд измерения
в каждой JVM; результат потребляет JMH. У каждого потока свой курсор по 256 сообщениям,
а карта и токены общие. Параметры читаются через обычный MessageImpl/ParametersUtil,
поэтому B/op включает разбор параметров, числовые стеки и остальные затраты вызова.

Первый прогон fixed пересекался с компиляцией Locator и исключён из итогового
сравнения. Итоговые прогоны выполняются последовательно после завершения тестов.
Данные baseline при пяти потоках показывают только затраты: прежняя версия может
возвращать неверные результаты. Корректность исправления подтверждается отдельными
детерминированными тестами и пяти-поточным тестом Locator, не timed assertions JMH.

Интеграционные проверки Locator:

```powershell
mise exec -- mvn -B -pl aurora-app -am test '-Dconditional-expression-calculator.version=7.4-thread-safety-local-SNAPSHOT' '-Dsurefire.failIfNoSpecifiedTests=false' '-Dtest=com.locator.server.domain.dto.sensor.SensorBunchCalculatorConcurrencyTest,com.locator.server.domain.dto.sensor.calc.*Test,com.locator.server.domain.dto.messagedata.ModeSensorDataTest'
```

Обычная зависимость Locator пока остаётся 7.4. После публикации исправленной
библиотеки необходимо изменить её версию: одна предварительная подготовка не
устраняет запись токенов внутри старой библиотеки. Публикация не выполнялась.

## Результаты

Измерено 2026-11-04, Windows, Temurin 11.0.32+9; fixed — commit 7b972c6,
baseline — 7b3b0ba. Среднее ± ошибка JMH (99,9%), млн операций/с:

| Сценарий | Потоки | Baseline | Fixed | Байт/вызов, обе версии |
|---|---:|---:|---:|---:|
| Формула | 1 | 7,966 ± 0,223 | 8,693 ± 0,666 | 408 |
| Условие | 1 | 6,229 ± 0,712 | 7,375 ± 0,314 | 448 |
| Зависимость | 1 | 2,931 ± 0,273 | 3,650 ± 0,091 | 856 |
| Формула | 5 | 7,612 ± 0,205 | 39,651 ± 2,559 | 408 |
| Условие | 5 | 6,869 ± 0,641 | 33,687 ± 1,871 | 448 |
| Зависимость | 5 | 3,353 ± 0,310 | 15,759 ± 1,264 | 856 |

Средние однопоточного пути выше baseline на 9–25%; устойчивой регрессии в этих
сценариях не видно. При пяти потоках средние выше в 4,7–5,2 раза; это сравнение затрат
со старым некорректным параллельным алгоритмом, а не рекомендация использовать его.
Числа зависят от формул, сообщений, JIT и компьютера и не задают универсального SLA.

Создание и подготовка снимка двух датчиков: baseline 1,778 ± 0,106 млн снимков/с,
2752 B/op; fixed 1,475 ± 0,205 млн снимков/с, 2853 ± 33 B/op. Цена неизменяемых
списков оплачивается при подготовке, не на каждом вычислении. Приблизительная стоимость
одного снимка по обратному throughput: 0,56 против 0,68 мкс.

Профилирование первой реализации выявило захватывающую лямбду в пути SensorCalculator;
финальная реализация передаёт контекст напрямую и не выделяет объект разрешителя.
Публичные перегрузки с ToDoubleFunction сохранены. Числовые стеки остаются локальными,
без pooling или ThreadLocal.

Сырые JSON и логи финальных прогонов находятся в `../target/measurements/`:
`baseline-t1`, `baseline-t5`, `baseline-prepare`, `fixed-t1`, `fixed-t5`,
`fixed-prepare`. Предварительные результаты вне этого каталога не использованы.
