package com.finance.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import java.awt.Desktop;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class FinanceappApplication {

    private static Process pythonProcess;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("🚀 Iniciando FinanceApp...");
        System.out.println("========================================");

        // Iniciar Python Service ANTES do Spring
        startPythonService();

        // Iniciar Spring Boot
        SpringApplication.run(FinanceappApplication.class, args);
    }

    private static void startPythonService() {
        try {
            System.out.println("Python Service...");
            System.out.println("----------------------------------------");

            // Verificar se o Python está instalado
            if (!isPythonInstalled()) {
                System.err.println("Python nao encontrado! Instale o Python e adicione ao PATH.");
                System.err.println("   Download: https://www.python.org/downloads/");
                return;
            }

            // Caminho para o diretório python-service
            String pythonServicePath = System.getProperty("user.dir") + File.separator + "python-service";
            File pythonDir = new File(pythonServicePath);

            System.out.println("Diretorio Python: " + pythonDir.getAbsolutePath());

            if (!pythonDir.exists()) {
                System.err.println("Diretorio python-service nao encontrado: " + pythonServicePath);
                return;
            }

            // Verificar se o app.py existe
            File appFile = new File(pythonDir, "app.py");
            if (!appFile.exists()) {
                System.err.println("app.py nao encontrado em: " + appFile.getAbsolutePath());
                return;
            }

            System.out.println("app.py encontrado em: " + appFile.getAbsolutePath());

            // Configurar o encoding para UTF-8
            ProcessBuilder processBuilder = new ProcessBuilder("python", "app.py");
            processBuilder.directory(pythonDir);
            processBuilder.redirectErrorStream(true);

            // Configurar variáveis de ambiente para encoding
            processBuilder.environment().put("PYTHONUNBUFFERED", "1");
            processBuilder.environment().put("PYTHONIOENCODING", "utf-8");
            processBuilder.environment().put("PYTHONUTF8", "1");

            // Iniciar o processo
            pythonProcess = processBuilder.start();
            System.out.println("Processo Python iniciado (PID: " + pythonProcess.pid() + ")");

            // Thread para ler a saída do Python com encoding UTF-8
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(pythonProcess.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[PYTHON] " + line);
                    }
                } catch (IOException e) {
                    // Processo encerrou normalmente
                }
                System.out.println("[PYTHON] Processo encerrado");
            }).start();

            // Aguardar o Python iniciar completamente
            System.out.println("Aguardando Python Service iniciar...");
            boolean pythonStarted = waitForPythonService(30);

            if (pythonStarted) {
                System.out.println("Python Service iniciado com sucesso!");
                System.out.println("URL: http://localhost:5000");
                System.out.println("Health: http://localhost:5000/health");
                System.out.println("Test: http://localhost:5000/test");
            } else {
                System.err.println("Python Service pode nao ter iniciado corretamente");
                System.err.println("Verifique o console para mensagens de erro");
            }

            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("Erro ao iniciar Python: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isPythonInstalled() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"python", "--version"});
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean waitForPythonService(int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.print(".");

                if (pythonProcess == null || !pythonProcess.isAlive()) {
                    System.out.println();
                    System.err.println("Processo Python morreu!");
                    return false;
                }

                HttpURLConnection connection = (HttpURLConnection)
                        new URL("http://localhost:5000/health").openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    System.out.println();
                    return true;
                }
            } catch (Exception e) {
                // Python ainda não está pronto
            }
        }
        System.out.println();
        return false;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            String url = "http://localhost:8082/pages/register.html";
            System.out.println("========================================");
            System.out.println("Abrindo navegador: " + url);
            System.out.println("========================================");
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            System.out.println("Nao foi possivel abrir o navegador automaticamente");
            System.out.println("Acesse manualmente: http://localhost:8082/pages/register.html");
        }
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void onApplicationShutdown() {
        if (pythonProcess != null && pythonProcess.isAlive()) {
            System.out.println("Encerrando Python Service...");
            pythonProcess.destroy();
            try {
                boolean terminated = pythonProcess.waitFor(5, TimeUnit.SECONDS);
                if (!terminated) {
                    pythonProcess.destroyForcibly();
                }
                System.out.println("Python Service encerrado");
            } catch (InterruptedException e) {
                pythonProcess.destroyForcibly();
            }
        }
    }
}