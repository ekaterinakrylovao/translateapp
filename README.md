# translateapp

## Структура проекта: TranslateApp

### 1. Основная директория проекта

```bash
translateapp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── translateapp/
│   │   │               ├── config/
│   │   │               │   └── AppConfig.java
│   │   │               ├── controller/
│   │   │               │   └── TranslationController.java
│   │   │               ├── entity/
│   │   │               │   └── Translation.java
│   │   │               ├── repository/
│   │   │               │   └── TranslationRepository.java
│   │   │               ├── servise/
│   │   │               │   └── TranslationServise.java
│   │   │               └── TranslationApplication.java
│   │   ├── resources/
│   │   │   ├── static/
│   │   │   ├── templates/
│   │   │   └── application.properties
│   └── test/
├── .gitignore
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```
 
### 2. Описание структуры

#### src/main/java/com/example/translateapp/config/

- **AppConfig.java**: Класс конфигурации Spring Boot, который определяет бины для приложения. В данном случае создается и настраивается бин `RestTemplate`, используемый для выполнения HTTP-запросов.
  
#### src/main/java/com/example/translateapp/controller/

- **TranslationController.java**: Контроллер, который обрабатывает HTTP-запросы на перевод текста.

#### src/main/java/com/example/translateapp/entity/

- **Translation.java**: Класс-сущность, представляющий модель данных для хранения информации о переводе в базе данных.

#### src/main/java/com/example/translateapp/repository/

- **TranslationRepository.java**: Интерфейс репозитория, отвечающий за взаимодействие с базой данных.

#### src/main/java/com/example/translateapp/service/

- **TranslationService.java**: Сервисный класс, содержащий логику для выполнения перевода текста с использованием внешнего API и сохранения данных в базе.

#### src/main/java/com/example/translateapp/

- **TranslationApplication.java**: Главный класс, запускающий Spring Boot приложение.

#### src/main/resources/

- **application.properties**: Файл конфигурации приложения, где хранятся настройки базы данных и API.

#### src/test/

- **TranslateAppApplicationTests.java**: Класс для написания тестов приложения.

### 3. Корневые файлы

- **.gitignore**: Файл, указывающий, какие файлы и папки не должны попадать в репозиторий Git.
- **mvnw** и **mvnw.cmd**: Скрипты для запуска Maven без предварительной установки на машине.
- **pom.xml**: Файл конфигурации Maven, содержащий зависимости и настройки сборки проекта.
- **README.md**: Документация проекта.

## Запуск и тестирование

### 1. Клонируйте репозиторий проекта

### 2. Получите Yandex Translate API

#### Документация доступна по ссылке [Yandex Translate](https://yandex.cloud/ru/docs/translate/).

#### Пошаговое решение для Windows PowerShell:

##### Настройка сервисного аккаунта

- Перейдите в [консоль управления](https://console.yandex.cloud/).
- В фолдере, можно в дефолтном, заведите Платёжный аккаунт.
- Убедитесь, что статус Active.
- Перейдите во вкладку Сервисные аккаунты и Создайте сервисный аккаунт с добавленной ему ролью ai.translate.user

##### Получение IAM-токена

- Установите интерфейс командной строки Yandex Cloud в Windows PowerShell:
```bash
iex (New-Object System.Net.WebClient).DownloadString('https://storage.yandexcloud.net/yandexcloud-yc/install.ps1')
```
- Введите:
```bash
yc init
```
- Перейдите по появившейся ссылке и получите OAuth token (откроется в браузере) и скопируйте его.
- Введите OAuth token. Вы получите информацию о вашем облаке и список фолдеров. Выберите фолдер, в котором у Вас был настроен Сервисный аккаунт.
- Будет предложен выбор Compute zone. Выберите на своё усмотрение.
- Далее, введите:
```bash
yc iam key create --service-account-name translator-java --output key.json
```
где после --service-account-name, вместо предложенного translator-java, введите Имя ранее настроенного Сервисного аккаунта.
- Далее, создайте профиль, например с таким именем:
```bash
yc config profile create my-robot-profile
```
- Введите:
```bash
yc config set service-account-key key.json
```
- И, наконец, введите:
```bash
yc iam create-token
```
И Вам будет выведен Ваш AIM Token, который надо скопировать и подставить в файл `application.properties` после yandex.translate.api.key=

### 3. Запуск и использование приложения

Для запуска проекта используйте команду:
```bash
mvn spring-boot:run
```
из корневой директории.

#### Доступ к базе данных

- Укажите не занятый у вас порт в `application.properties` после server.port= или же оставьте как в коде.
- Перейдите в своём браузере на http://localhost:8081/h2-console
- Введите из `application.properties` url в JDBC URL:, логин и пароль.
- Нажмите Connect.
- Впишите SQL-запрос:
```bash
SELECT * FROM TRANSLATIONS;
```
и нажмите Run.

#### Использование приложения с Postman

- Создайте новую коллекцию Create new collection, например, Translator_java
- Создайте новый реквест Add request. Выберите метод POST. Введите в поле http://localhost:8081/translate. Задайте параметры: text, sourceLang, targetLang. И можете пользоваться.
