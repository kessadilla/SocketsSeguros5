import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;

public class Cliente {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Por favor, especifica el puerto");
            System.exit(1);
        }

        int puerto = Integer.parseInt(args[0]);
        InetAddress ip = InetAddress.getLocalHost();

        try {
            // Cargar truststore del cliente
            KeyStore truststore = KeyStore.getInstance("JKS");
            truststore.load(new FileInputStream("seguridad/client.truststore"), "alumno".toCharArray());

            // Inicializar TrustManagerFactory con el truststore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(truststore);

            // Configurar SSLContext con el TrustManagerFactory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());

            // Crear un SSLSocketFactory con el SSLContext configurado
            SSLSocketFactory factory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket(ip, puerto);

            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {

                System.out.println(in.readLine());  // Solicita la contraseña
                String pass = stdin.readLine();     // Lee la contraseña del usuario
                out.println(pass);                  // Envía la contraseña al servidor

                System.out.println(in.readLine());  // Respuesta del servidor

                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
