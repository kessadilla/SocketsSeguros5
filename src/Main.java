import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Servidor.main(new String[]{"6900"});
            }
        }).start();

        try {
            Thread.sleep(500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

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