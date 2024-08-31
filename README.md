# [DiscordMusicBot](https://github.com/Marc0Franc0/DiscordMusicBot#discrodmusicbot)

Bot para Discord para gestionar comandos y ofrecer la funcionalidad de reproducir canciones dentro de servidores de Discord

## Características
- Integración con JDA:
  Utilización de la biblioteca Java Discord API (JDA) para la gestión de interacciones y eventos en Discord. 
- Despliegue en Servidor Discord:
Preparación del bot para ser desplegado en servidores de Discord y responder a interacciones de usuarios.

## Tecnologías
- Spring Boot 3.3.1
- Java 21
- JDA
- Lavaplayer
- Maven
## Comandos Disponibles
Utilice estos comandos en un canal de texto de Discord
### `/help`

Proporciona ayuda sobre los comandos disponibles.
### `/play`

Reproduce una canción en un canal de voz.
### `/queue`

Muestra la lista de canciones que están en la cola de reproducción.
### `/pause`

Pausa la reproducción de lo que se este escuchando.
### `/resume`

Reanuda la reproducción y sigue desde donde fue pausado.
### `/clear`

Elimina todas las canciones que estén presentes en la cola de reproducción.
### `/track-info`

Muestra información sobre lo que se este reproduciendo.
### `/stop`

Salta la reproducción de lo que se este escuchando para escuchar lo siguiente y en el caso de no haber una cola de reproducción se detiene.
## Instalación y configuración
1. Clonar repositorio
```shell
git clone https://github.com/Marc0Franc0/DiscordMusicBot
```
2. Configura el token del bot en el [archivo de configuración](https://github.com/Marc0Franc0/DiscordMusicBot/blob/main/src/main/resources/application.properties):
- Modifica el valor de ${BOT_TOKEN}
3. Seguir pasos para ejecución con Maven

## Ejecución con Maven

Para construir y ejecutar la aplicación necesita:

- [JDK 21+](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3+](https://maven.apache.org)

Ejecutar localmente
1. Instala las dependencias
```shell
cd DiscordMusicBot
mvn clean install
```
2. Inicia la app
```shell
mvn spring-boot:run
```