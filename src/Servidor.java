import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

public class Servidor {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Por favor, especifica el puerto");
            System.exit(1);
        }

        int puerto = Integer.parseInt(args[0]);
        int contador = 0;
        final int MAX_CONEXIONES = 5;

        try {
            // Cargar keystore del servidor
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream("seguridad/server.keystore"), "alumno".toCharArray());

            // Inicializar KeyManagerFactory con el keystore del servidor
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, "alumno".toCharArray());

            // Configurar SSLContext con el keystore
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, new java.security.SecureRandom());

            // Crear un SSLServerSocketFactory con el SSLContext configurado
            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(puerto);

            System.out.println("Servidor conectado");


            Socket socket = serverSocket.accept();

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

                public boolean comprobarSeguridad(String a) {
                    String regexMayuscula = ".*[A-Z].*";
                    String regexMinuscula = ".*[a-z].*";
                    String regexNumero = ".*\\d.*";
                    String regexEspecial = ".*[!@#$%^&*(),.?\":{}|<>].*";

                    if (a.length() < 8) {
                        return false;
                    } else if (!a.matches(regexMayuscula)) {
                        return false;
                    } else if (!a.matches(regexMinuscula)) {
                        return false;
                    } else if (!a.matches(regexNumero)) {
                        return false;
                    } else if (!a.matches(regexEspecial)) {
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
