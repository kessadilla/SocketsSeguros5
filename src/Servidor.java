import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;

/**
 * Clase principal que implementa un servidor SSL seguro.
 * Este servidor solicita una contraseña al cliente y verifica si cumple con requisitos de seguridad.
 */
public class Servidor {

    /**
     * Función principal que configura y arranca el servidor SSL.
     *
     * @param args Argumentos de línea de comandos. Se espera que el primer argumento sea el puerto del servidor.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Por favor, especifica el puerto");
            System.exit(1);
        }

        int puerto = Integer.parseInt(args[0]);

        try {
            // Cargar el almacén de claves del servidor
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream("seguridad/server.keystore"), "alumno".toCharArray());

            // Inicializar KeyManagerFactory con el almacén de claves del servidor
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, "alumno".toCharArray());

            // Configurar el contexto SSL con el almacén de claves
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, new java.security.SecureRandom());

            // Crear un SSLServerSocketFactory con el contexto SSL configurado
            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(puerto);

            System.out.println("Servidor conectado");

            // Esperar una conexión del cliente
            Socket socket = serverSocket.accept();

            // Manejar la conexión en un hilo separado
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try (PrintWriter lapiz = new PrintWriter(socket.getOutputStream(), true);
                         BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        lapiz.println("Introduzca su contraseña");

                        String input = entrada.readLine();
                        lapiz.println(comprobarSeguridad(input) ? "Tu contraseña es segura" : "Tu contraseña no es segura");

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                /**
                 * Comprueba si una contraseña cumple con los requisitos de seguridad.
                 *
                 * @param contrasena La contraseña que se desea verificar.
                 * @return true si la contraseña cumple los requisitos, false en caso contrario.
                 */
                public boolean comprobarSeguridad(String contrasena) {
                    String regexMayuscula = ".*[A-Z].*";
                    String regexMinuscula = ".*[a-z].*";
                    String regexNumero = ".*\\d.*";
                    String regexEspecial = ".*[!@#$%^&*(),.?\":{}|<>].*";

                    if (contrasena.length() < 8) {
                        return false;
                    } else if (!contrasena.matches(regexMayuscula)) {
                        return false;
                    } else if (!contrasena.matches(regexMinuscula)) {
                        return false;
                    } else if (!contrasena.matches(regexNumero)) {
                        return false;
                    } else if (!contrasena.matches(regexEspecial)) {
                        return false;
                    }
                    return true;
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
