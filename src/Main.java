/**
 * Clase principal que ejecuta el servidor y el cliente en hilos separados.
 * Permite probar la funcionalidad del cliente y servidor simultáneamente.
 */
import java.io.IOException;

public class Main {

    /**
     * Función principal que ejecuta el servidor y el cliente en hilos separados.
     *
     * @param args Argumentos de línea de comandos. No se esperan argumentos.
     */
    public static void main(String[] args) {

        // Crear y ejecutar el hilo del servidor
        new Thread(new Runnable() {
            @Override
            public void run() {
                Servidor.main(new String[]{"6900"});
            }
        }).start();

        // Esperar brevemente para asegurarse de que el servidor esté listo
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Crear y ejecutar el hilo del cliente
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cliente.main(new String[]{"6900"});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
