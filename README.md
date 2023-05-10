## Trivia
Приложение-игра, в котором есть два режима: "Кто хочет стать миллионером?"(с выбором варианта ответа) и "Викторина"(ответ необходимо вводить вручную).
Проект был написан с использованием инструментов Jetpack Compose.
В проекте осуществлена работа с компонентами Firebase, а именно - Authentication и Realtime Database. В рамках работы с первым реализована регистрация, подтверждение регистрации по почте, и последующая авторизация
, в планах добавить взаимодествие с протоколом авторизации OAuth 2.0. Realtime Database использовалась для хранения базы вопросов к викторине. Вопросы и ответы для режима "Кто хочет стать миллионером?" берутся с API OpenTriviaDatabase
посредством REST-клиента Retrofit и JSON-сериализатора Moshi. Для оптимальной работы с получением данных используются корутины.
