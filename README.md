# Rebloom

> Мобильное приложение для ухода за комнатными растениями - расписание полива, напоминания и отслеживание прогресса.
>
> Разработчик: команда **AndrTurtleS**

---

## О проекте

**Rebloom** - Android-приложение для владельцев комнатных растений. Приложение позволяет добавлять свои растения, задавать расписание полива и получать напоминания. На главном экране отображается мини-календарь задач на неделю и прогресс за сегодня, а виджет на рабочем столе показывает состояние выполнения в одном взгляде.

### Ключевые возможности

- **Каталог растений** - добавляйте растения с фото, описанием и датой посадки; фотографии кэшируются локально для работы офлайн
- **Расписание полива** - настраиваемый интервал полива (уровень 1–5) для каждого растения
- **Задачи по дням** - выбор даты в календаре на главном экране, отметка выполнения одним касанием
- **Уведомления** - ежечасные напоминания о невыполненных задачах по поливу
- **Home-screen виджет** - показывает количество выполненных/всего задач на сегодня
- **Облачная синхронизация** - данные хранятся в Supabase; pull-to-refresh синхронизирует со всех устройств
- **Аккаунт** - регистрация и вход по email/паролю; восстановление пароля через письмо

---

## Требования к устройству

| Параметр | Значение |
|---|---|
| Минимальная версия Android | **8.0 Oreo (API 26)** |
| Целевая версия Android | Android 16 (API 36) |
| Архитектура | arm64-v8a, x86_64 |
| Разрешения | INTERNET, POST_NOTIFICATIONS, ACCESS_NETWORK_STATE |
| Интернет | Требуется для первого входа и синхронизации; базовый функционал доступен офлайн |

---

## Требования для сборки в Android Studio

- **Android Studio** Meerkat 2024.3.1 или новее (поддержка AGP 9.x)
- **JDK** 11 (bundled с Android Studio)
- **Kotlin** 2.3.20 (настроен через `libs.versions.toml`)
- **Android SDK** с установленным API 36 (compileSdk = 36)

### Шаг 1 - Клонировать репозиторий

```bash
git clone <repo-url>
cd AndrTurtleS
```

### Шаг 2 - Настроить Supabase-ключи

Создайте файл `local.properties` в корне проекта (если его нет) и добавьте:

```properties
sdk.dir=/path/to/android/sdk
supabase.url=https://<your-project>.supabase.co
supabase.anonKey=<your-anon-key>
```

> **Важно:** Без этих переменных сборка завершится ошибкой - они подставляются в `BuildConfig` через `build.gradle.kts`.

### Шаг 3 - Открыть в Android Studio

File → Open → выбрать папку проекта → дождаться Gradle sync.

### Шаг 4 - Запустить

Run → Run 'app' (или `Shift+F10`) на реальном устройстве или эмуляторе с API ≥ 26.

---

## Стек технологий

| Область | Технология |
|---|---|
| UI | Jetpack Compose + Material 3 (BOM 2024.09.00) |
| Язык | Kotlin 2.3.20 |
| Навигация | Navigation Compose 2.8.3 |
| Архитектура | Clean Architecture + MVVM |
| Локальная БД | Room 2.7.1 |
| Бэкенд | Supabase 3.5.0 (Auth, Postgrest, Storage) |
| Загрузка изображений | Coil 2.7.0 |
| Фоновые задачи | WorkManager 2.9.0 |
| Асинхронность | Kotlin Coroutines 1.8.1 + Flow |
| Сериализация | Kotlinx Serialization 1.7.3 |
| DI | Нет (ручное создание через конструктор) |
| Build System | Gradle KTS + Version Catalog (libs.versions.toml) |
| AGP | 9.2.1 |

---

## Архитектура

Проект следует **Clean Architecture** с разделением на три слоя:

```
ui/          ← Jetpack Compose screens + ViewModels (MVVM)
domain/      ← Domain models + Use Cases (TaskScheduler, WateringScheduleConverter)
data/        ← Repositories, Room DAO, Supabase Remote Sources, DTOs, Mappers
```

**Поток данных:** Supabase / Room → Repository → ViewModel (StateFlow) → Composable

**Офлайн-стратегия:** Room - источник правды для UI; синхронизация с Supabase происходит в фоне (WorkManager + pull-to-refresh); фотографии кэшируются в `filesDir/plant_photos/`.

---

## Структура проекта

```
app/src/main/java/com/rebloom/app/
├── data/
│   ├── dto/                  # DTO для Supabase API
│   ├── local/                # Room (AppDatabase, UserPlantDao, UserPlantEntity, TaskCompletionStore)
│   ├── mapper/               # Маппинг DTO ↔ Entity ↔ Domain
│   ├── notification/         # WateringCheck (WorkManager), Notification
│   ├── remote/               # Supabase data sources, SyncPlantsWorker
│   └── repository/           # UserPlantRepository, AuthRepository, ProfileRepository, …
├── domain/
│   ├── model/                # Plant, User, TaskType, TaskOccurrence, …
│   └── usecase/              # TaskScheduler, WateringScheduleConverter
├── ui/
│   ├── navigation/           # AppRoot, AppDestinations, BottomItems
│   ├── screens/
│   │   ├── auth/             # Login, Register, ForgotPassword
│   │   ├── home/             # HomeScreen, HomeViewModel, HomeComponents
│   │   ├── plants/           # PlantsScreen, PlantDetails, AddEditPlant
│   │   ├── tasks/            # TasksScreen, TaskViewModel
│   │   ├── profile/          # ProfileScreen, EditProfile
│   │   └── widget/           # MascotWidgetReceiver (AppWidgetProvider)
│   ├── common/               # UiState
│   └── theme/                # Color, Typography
├── RebloomApp.kt             # Application: инициализация Supabase, WorkManager, Notification channel
└── MainActivity.kt           # Точка входа, обработка deep links
```

---

## Бэкенд (Supabase)

| Таблица | Назначение |
|---|---|
| `user_plants` | Растения пользователя (id, nickname, watering_level, custom_image_url, …) |
| `plants_cache` | Справочник видов растений (common_name, scientific_name, image_url) |
| `users_profile` | Профиль (name, email, avatar_image_name) |

**Storage bucket:** `plant-images` (публичный) - хранит фото растений по пути `user_plants/{plantId}.jpg`.

**Auth:** Email/Password + deep links для подтверждения email и смены пароля (`rebloom://auth-callback/`).

---

## Команда

**AndrTurtleS** - команда разработчиков мобильных приложений.
