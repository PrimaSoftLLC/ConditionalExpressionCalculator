# ConditionalExpressionCalculator

## Сборка

Инструменты фиксируются в mise.toml: Temurin 25 и Maven 3.9.16.
После `mise trust` и `mise install`:

```powershell
mise exec -- mvn -B clean verify
mise exec -- mvn -B install
```

CI использует `./mvnw -B verify`. Wrapper only-script, без JAR в репозитории.
Артефакт сохраняет совместимость с Java 11. `package`, `verify`, `install`
не публикуют пакет. Manifest содержит Implementation-Version и Build-Revision.

## Подключение из GitHub Packages

Первый планируемый выпуск — 7.5; до его выполнения эта версия недоступна.
После выпуска заменить JitPack-координаты и добавить repository:

```xml
<repositories>
  <repository>
    <id>github-conditional-expression-calculator</id>
    <url>https://maven.pkg.github.com/primasoftllc/conditionalexpressioncalculator</url>
    <releases><enabled>true</enabled></releases>
    <snapshots><enabled>false</enabled></snapshots>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>by.aurorasoft</groupId>
    <artifactId>conditional-expression-calculator</artifactId>
    <version>7.5</version>
  </dependency>
</dependencies>
```

Для скачивания нужен PAT classic с read:packages и доступом к репозиторию
(при необходимости авторизованный для SSO организации). Пример server для
локального ~/.m2/settings.xml и CI потребителя:

```xml
<settings><servers><server>
  <id>github-conditional-expression-calculator</id>
  <username>${env.GH_PACKAGES_USER}</username>
  <password>${env.GH_PACKAGES_TOKEN}</password>
</server></servers></settings>
```

В CI потребителя GH_PACKAGES_USER задаётся именем пользователя GitHub,
GH_PACKAGES_TOKEN — секретом с read:packages. Токен не сохраняется в Git.
Прежние потребители `com.github.PrimaSoftLLC:ConditionalExpressionCalculator:7.4`
остаются на старой версии, включая старое поведение параллельных расчётов,
пока их зависимость и настройки repository не будут обновлены отдельно.

## Релиз и версии

Разработка ведётся в develop. Версия в корневом POM — разрабатываемая;
теги не определяют версию. PR в develop/master и push develop запускают
тесты. Только push master публикует Maven JAR/POM в GitHub Packages.
SNAPSHOT и DEV-сборки не публикуются.

Используется `/aurora-release 7.5` с адаптацией для библиотеки: вместо Docker
CI выполняет Maven deploy. Перед первым выпуском проверить разрешения Actions
на packages:write, contents:write и создание тегов по правилам организации.
Для публикации своего пакета CI использует встроенный GITHUB_TOKEN, не PAT.

Первый переход является legacy-исключением: POM ещё содержит 6.1, хотя последний
исторический тег — 7.4. В `chore: release 7.5` команда Maven versions:set
устанавливает 7.5 и раздел `Не выпущено` закрывается как `7.5`. После проверки
`mise exec -- mvn -B verify` и согласования релизной сводки master fast-forward
переводится на этот коммит. Develop получает `chore: start 7.6` с новым разделом
`Не выпущено` и версией 7.6. В дальнейшем bump происходит только после выпуска.
Уважать branch protection; не использовать force push и не обходить проверки.

CI блокирует уже существующие теги `vX.Y` и `X.Y`, опубликованную или частично
опубликованную Maven-версию, ошибки доступа и незакрытый CHANGELOG. После verify
он публикует пакет, скачивает JAR/POM в чистый Maven-кеш, сверяет хеши и Git SHA,
затем создаёт тег `vX.Y` на проверенном коммите. Люди теги вручную не создают.
Глобальный aurora-release skill не изменяется; эта инструкция задаёт вариант
для библиотеки. Локальная публикация агентом запрещена.

Если пакет уже появился, а скачивание или тегирование завершилось ошибкой,
не повторять deploy вслепую, не удалять пакет и не перезаписывать версию.
Проверить последний успешный шаг CI, POM/JAR в registry и SHA. Завершение
тегирования через проверенный CI либо выпуск следующего номера — отдельное
действие после разбора. Автоматического recovery workflow нет.

Проверка CI-логики без публикации:

```powershell
python -m unittest discover -s .github/scripts/tests -v
```

## Параллельные вычисления

Создайте полную карту датчиков и подготовьте её до передачи рабочим потокам:

```java
SensorCalculator.prepare(sensorMap);
// Далее передавайте снимок задачам через ExecutorService или concurrent cache.
double value = SensorCalculator.calculateValue(sensor, message, sensorMap);
```

Вычисление читает токены и использует локальный числовой стек. Значения параметров,
свойств, зависимостей и `{x}` не записываются в общие токены. Переопределения
`SensorI.calculateValue` сохраняются для зависимых датчиков.

Без подготовки выражение компилируется локально при каждом вызове; прежнего
автоматического кеширования во время расчёта больше нет. Ошибочное выражение без
подготовки также будет повторно логироваться. `buildFormula` и `buildCondition`
остаются явными операциями подготовки, вновь созданные списки неизменяемы.

Не изменяйте sensorMap, конфигурацию, калибровки, options, сообщения или токены во
время расчётов; подготовка не должна пересекаться с вычислением. Безопасную передачу
снимка потокам обеспечивает вызывающий код. Собственные реализации SensorI/MessageI
также должны поддерживать параллельное чтение. Старый API ExpressionCalculator
продолжает читать значения, заданные через EToken.setValue.

`{x}` в формуле равен исходному 0; в собственном условии — откалиброванному значению,
во внешнем условии — значению целевого датчика. Логические операнды вычисляются без
короткого замыкания; результаты зависимостей не кешируются.

## Историческая публикация

Как сделать свой Maven Repository на Github?
https://devcolibri.com/%D0%BA%D0%B0%D0%BA-%D1%81%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C-%D1%81%D0%B2%D0%BE%D0%B9-maven-repository-%D0%BD%D0%B0-github/
