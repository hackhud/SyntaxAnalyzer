# Опис виконаного завдання

Для проєкту `SyntaxAnalyzer` додано автономний набір юніт-тестів на базі `JUnit 5` та налаштовано збірку через `Gradle`.

Фактичний результат перевірки:

- успішно виконано `31` тест;
- кількість помилок: `0`;
- фактичне покриття рядків: `94.20%`.

## Що було зроблено

- додано файл [build.gradle](build.gradle) з підтримкою:
  - `java`;
  - `jacoco`;
  - `JUnit 5`;
  - автоматичної перевірки мінімального покриття `75%`;
- додано файл [settings.gradle](settings.gradle) для конфігурації Gradle-проєкту;
- згенеровано `Gradle Wrapper` для локального запуску через `gradlew.bat`;
- додано файл конфігурації тестового набору [junit-platform.properties](src/test/resources/junit-platform.properties);
- написано тести для таких частин програми:
  - [LexerTest.java](src/test/java/ua/hackhud/simplesyntaxanalyzer/LexerTest.java);
  - [ParserTest.java](src/test/java/ua/hackhud/simplesyntaxanalyzer/ParserTest.java);
  - [InterpreterTest.java](src/test/java/ua/hackhud/simplesyntaxanalyzer/InterpreterTest.java);
  - [MainTest.java](src/test/java/ua/hackhud/simplesyntaxanalyzer/MainTest.java).

## Як виконано вимоги завдання

- покриття коду не менше `75%`:
  - забезпечується через `JaCoCo` та правило `jacocoTestCoverageVerification` у `build.gradle`;
- наявні `setup(fixture)` методи:
  - використано `@BeforeEach` у всіх основних тестових класах;
- використано щонайменше 4 різні `Assert`-вирази:
  - `assertEquals`;
  - `assertTrue`;
  - `assertThrows`;
  - `assertAll`;
  - `assertIterableEquals`;
  - `assertLinesMatch`;
- є щонайменше один тест на виключення:
  - перевірка помилки лексера для символу `!`;
  - перевірка помилок парсера для некоректних програм;
- використано щонайменше 2 складні assert-перевірки:
  - `assertIterableEquals` для колекцій токенів;
  - `assertLinesMatch` для порівняння текстового виводу програми;
- є параметризовані тести:
  - через `@CsvSource`;
  - через `@ValueSource`;
  - через `@MethodSource`;
- наявний файл конфігурації набору тестів:
  - `src/test/resources/junit-platform.properties`.

## Що перевіряють тести

- коректне розпізнавання ключових слів, чисел, ідентифікаторів та коментарів;
- коректний порядок токенів після лексичного аналізу;
- обробку помилок лексера;
- побудову AST для оголошень, присвоєнь, блоків, умовних операторів і циклів;
- пріоритет арифметичних операцій;
- реакцію парсера на синтаксичні помилки;
- виконання арифметичних виразів, логічних умов, циклів і гілок `if/else`;
- обробку помилок інтерпретатора;
- запуск демо-режиму та запуск програми з файлу через `Main`.

## Як запускати

Рекомендований варіант запуску:

```bash
gradlew.bat test
gradlew.bat check
```

Для перевірки покриття:

```bash
gradlew.bat jacocoTestReport
```

Після виконання HTML-звіт `JaCoCo` буде доступний у каталозі:

```text
build/reports/jacoco/test/html/index.html
```
