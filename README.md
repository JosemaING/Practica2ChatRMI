--------------------------------------------------------------------------------
                                   README.TXT
--------------------------------------------------------------------------------

                                CHAT RMI EN JAVA

--------------------------------------------------------------------------------
AUTOR:
--------------------------------------------------------------------------------

Nombre del Autor: José María Santos
Email: jsr1002@alu.ubu.es
Fecha: 07/04/2024

--------------------------------------------------------------------------------
DESCRIPCIÓN:
--------------------------------------------------------------------------------

Este proyecto incluye un servidor y cliente de chat RMI implementado en Java.
Se proporcionan los scripts y comandos para facilitar la puesta a punto
y la ejecución tanto del servidor como del cliente en modo consola.

Es importante no modificar el fichero pom.xml para
asegurar el correcto funcionamiento del proyecto.

Para iniciar el servidor y conectar clientes, se deben seguir las instrucciones
detalladas en la sección de ejecución. Cualquier usuario puede apagar el servidor
o banear a cualquier otro cliente.

Un usuario baneado puede leer los mensajes de otros clientes
pero no podrá enviar mensajes hasta que este sea desbaneado,
un cliente no puede banearse ni desbanearse a si mismo.

--------------------------------------------------------------------------------
REQUISITOS:
--------------------------------------------------------------------------------

1. Maven instalado y configurado correctamente.
2. Servidor con Glassfish 5 configurado correctamente.
2. Java JDK versión 8 o superior.
3. No modificar el fichero pom.xml para evitar problemas en la ejecución.

--------------------------------------------------------------------------------
EJECUCIÓN:
--------------------------------------------------------------------------------
Para la puesta a punto del servidor:

1. Ejecutar el comando 'mvn clean package' en Practica2ChatRMI.
2. Ejecutar el comando 'mvn clean package' en Practica2ChatRMI-Web.
3. Desde Eclipse, antes de iniciar el servidor realizar 'Alt + F5' de ambos proyectos.
4. Asegurar que 'Practica2ChatRMI-0.0.1-SNAPSHOT.jar' está añadido como resource al servidor Glassfish.
5. Iniciar el servidor desde Eclipse.

Utilice los siguientes comandos en la terminal dentro del directorio del proyecto:

- REALIZAR REGISTRO
registro.bat

- INICIAR SERVIDOR
servidor.bat

- CONECTAR CLIENTE
cliente.bat Usuario

--------------------------------------------------------------------------------
COMANDOS:
--------------------------------------------------------------------------------

- `shutdown`: Apaga el servidor (cualquier usuario).
- `logout`: Cierra la sesión del cliente.
- `ban <username>`: Banea a un usuario especifico "username".
- `unban <username>`: Desbanea a un usuario especifico "username".

--------------------------------------------------------------------------------
SUGERENCIAS:
--------------------------------------------------------------------------------

- Se ha realizado una limpieza del proyecto con Maven `mvn clean`.
Se sugiere no realizar modificaciones en el archivo `pom.xml` para mantener la estabilidad del proyecto.

- Se ha realizado la ejecución con ANT `ant`, que compila, empaqueta y documenta.
Se sugiere no realizar modificaciones en el archivo `build.xml` para mantener la estabilidad del proyecto.

--------------------------------------------------------------------------------
