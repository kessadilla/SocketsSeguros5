# SocketsSeguros

## Objetivo

El objetivo del programa es el siguiente: tenemos un servidor que permite conexiones mediante sockets. 
El objetivo del servidor es analizar las contraseñas que le envíen y dar información al cliente sobre si son seguras o no.\
Los clientes se conectan mediante un puerto, en este caso el 6900, y envían un mensaje al servidor con la contraseña a analizar. \
Para que no se puedan interceptar las contraseñas se usarán sockets seguros, que usan SSL y TLS.

## Desarrollo

### Servidor
Primero creamos la clase servidor, que requiere que se le indique como parámetro el puerto que va a escuchar. \
Para la seguridad, primero creamos una contraseña autofirmada, generamos el contexto SSL con dicha clave, y por último usamos ese contexto para generar sockets SSL, cuya conexión y envío de datos es cifrado y totalmente seguro.\
Una vez hecho esto, esperamos a que se conecte algún cliente, y una vez eso ocurre, lanzamos un nuevo hilo donde manejamos los flujos de entrada y salida de datos para poder recibir y enviar información. Pedimos la contraseña al cliente y esperamos su respuesta. Cuando el cliente responde analizamos la contraseña y devolvemos una respuesta u otra. 

Para analizar si es segura o no la contraseña, la comparamos con algunas expresiones regulares simples, pero en caso de querer un análisis real, deberíamos ser mucho más estrictos e incluso buscar coincidencias en bases de datos de contraseñas. 

### Cliente
Para iniciar los sockets del cliente el proceso es similar, solo que en vez de crear una clave y usarla, debemos exportar la clave, indicar que confiamos en ella, y usar ese archivo truststore para el contexto SSL de los sockets. \
Con esto hecho, conectamos el socket mediante ip y puerto, y leemos los datos de entrada, que nos solicitan la contraseña. Enviamos con la clase PrintWriter el String a analizar, y esperamos la respuesta. 

### Principal
Esta es la clase ejecutable. Simplemente, inicia el servidor, espera medio segundo, e inicia el cliente.

## Comandos necesarios
Para generar la clave, exportar el certificado, y generar el truststore se necesitan 3 comandos.

### KeyStore
```bash
keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -storetype JKS -keystore <ruta/a/clave.keystore> -validity 3650
```

### Exportar certificado
```bash
keytool -export -alias server -keystore <ruta/a/clave.keystore> -file <ruta/al/certificado.cer>
```

### Truststore
```bash
keytool -import -alias server -file <ruta/al/certificado.cer> -keystore <ruta/al/cliente.truststore>
```

## Bibliotecas
Las bibliotecas usadas en el proyecto son las siguientes:

### Servidor
```
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
```

### Cliente
```
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
```

### Principal 
```
import java.io.IOException;
```