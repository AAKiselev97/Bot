# Приложение TelegramBot   

1 модуль: сам бот, сканирует сообщения, записывает их в базу, отлавливает мат, указанный в файле BadWords.txt, считывает его, записывает статистику по чату/юзеру в Redis и в файл, может выполнять команды:  
- START("/start - команда для первого входа"),  
- HELLO("/hello - поздороваться с ботом"),  
- HELP("/help - отправляет список команд"),  
- STAT("/stat [@userName] - отправляет статистику пользователя @userName в этом чате"),  
- STATCHAT("/statchat - отправляет статистику по данному чату"),  
- GETCHATID("/getChatId - отправляет id чата, где была вызвана команда"),  
- TOP("/top - отправляет топ 5 пользователей по активности в данном чате"),  
- TOKEN("/token - отправляет токен для получения статистики"),  

Настройки Redis по умолчанию  

Файл telegramBot.properties:  
- userName=[userName бота, полученный от BotFather]
- token=[token бота, полученный от BotFather]  

Файл mysql.properties
- databaseConnection=[url присоединения к базе данных]
- databaseLogin=[логин для базы данных]
- databasePassword=[пароль для базы данных]  

Файл jedis.properties
- jedis.host=[хост для подключения к Redis, по умолчанию 127.0.0.1]
- jedis.port=[порт для подключения к Redis, по умолчанию 6379]

Файл mq.properties
- RabbitMQ.username=[логин для rabbitMq]
- RabbitMQ.password=[пароль для rabbitMQ]
- RabbitMQ.virtualHost=/
- RabbitMQ.host=[хост для подключения к rabbitMq, по умолчанию 127.0.0.1]
- RabbitMQ.port=[порт для подключения к rabbitMq, по умолчанию 5672]


2 Модуль: BotAPI, позволяет брать статистику по юзеру, чату, юзеру в чате в виде JSON, доступ осуществляется по токену, доступному в Redis 30 дней, а так же создавать пдф файл с историей сообщений и скачивать его
