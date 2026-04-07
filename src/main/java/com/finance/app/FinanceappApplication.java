package com.finance.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import java.awt.Desktop;
import java.io.*;
import java.net.URI;

@SpringBootApplication
public class FinanceappApplication {

    private static Process pythonProcess;

    public static void main(String[] args) {
        // Iniciar Python Service ANTES do Spring
        startPythonService();

        // Iniciar Spring Boot
        SpringApplication.run(FinanceappApplication.class, args);
    }

    private static void startPythonService() {
        try {
            System.out.println("========================================");
            System.out.println("🐍 Iniciando Python Service...");
            System.out.println("========================================");

            // Comando para rodar o Python (sem abrir janela)
            ProcessBuilder processBuilder = new ProcessBuilder("python", "app.py");

            // Define a pasta onde está o app.py
            String pythonServicePath = System.getProperty("user.dir") + File.separator + "python-service";
            processBuilder.directory(new File(pythonServicePath));

            // Esconder a janela do Python (Windows)
            processBuilder.redirectErrorStream(true);

            // Iniciar o processo
            pythonProcess = processBuilder.start();

            // Ler a saída do Python e mostrar no console do Spring
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[PYTHON] " + line);
                    }
                } catch (IOException e) {
                    System.out.println("[PYTHON] Processo encerrado");
                }
            }).start();

            // Aguardar o Python iniciar completamente
            Thread.sleep(5000);
            System.out.println("✅ Python Service iniciado em: http://localhost:5000");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar Python: " + e.getMessage());
            System.err.println("Verifique se o Python está instalado e no PATH");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            // Abrir navegador automaticamente
            String url = "http://localhost:8082/pages/register.html";
            System.out.println("🌐 Abrindo navegador: " + url);
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            System.out.println("⚠️ Não foi possível abrir o navegador automaticamente");
            System.out.println("📱 Acesse: http://localhost:8082/pages/register.html");
        }
    }
}