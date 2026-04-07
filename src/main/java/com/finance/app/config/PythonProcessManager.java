package com.finance.app.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.awt.Desktop;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PythonProcessManager {
    
    private Process pythonProcess;
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("========================================");
        System.out.println("🚀 FinanceApp - Inicializando...");
        System.out.println("========================================");
        
        // Iniciar Python Service
        startPythonService();
        
        // Aguardar Python iniciar
        waitForPythonService();
        
        // Abrir navegador
        openBrowser();
    }
    
    private void startPythonService() {
        try {
            // Caminho para a pasta python-service
            Path projectPath = Paths.get(System.getProperty("user.dir"));
            Path pythonServicePath = projectPath.resolve("python-service");
            
            System.out.println("📁 Projeto: " + projectPath);
            System.out.println("🐍 Python Service: " + pythonServicePath);
            
            // Lista de comandos possíveis para o Python
            String[][] commands = {
                {"python", "app.py"},
                {"python3", "app.py"},
                {"py", "app.py"},
                {"C:\\Python311\\python.exe", "app.py"},
                {"C:\\Python312\\python.exe", "app.py"},
                {"C:\\Python313\\python.exe", "app.py"},
                {"C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Local\\Programs\\Python\\Python311\\python.exe", "app.py"},
                {"C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Local\\Programs\\Python\\Python312\\python.exe", "app.py"}
            };
            
            ProcessBuilder processBuilder = null;
            String usedCommand = "";
            
            // Tentar cada comando
            for (String[] cmd : commands) {
                try {
                    System.out.print("🔍 Tentando: " + cmd[0] + " ... ");
                    ProcessBuilder testPb = new ProcessBuilder(cmd[0], "--version");
                    Process testProcess = testPb.start();
                    int exitCode = testProcess.waitFor();
                    
                    if (exitCode == 0) {
                        System.out.println(" OK!");
                        processBuilder = new ProcessBuilder(cmd[0], cmd[1]);
                        usedCommand = cmd[0];
                        break;
                    } else {
                        System.out.println(" Falhou");
                    }
                } catch (Exception e) {
                    System.out.println(" Erro");
                }
            }
            
            if (processBuilder == null) {
                System.err.println("❌ Python não encontrado!");
                System.err.println("Por favor, instale o Python e tente novamente");
                return;
            }
            
            // Configurar o processo
            processBuilder.directory(pythonServicePath.toFile());
            processBuilder.redirectErrorStream(true);
            
            System.out.println("✅ Usando Python: " + usedCommand);
            System.out.println("🚀 Iniciando Python Service...");
            
            // Iniciar o processo
            pythonProcess = processBuilder.start();
            
            // Ler a saída em uma thread separada
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("Running on") || line.contains("servidor")) {
                            System.out.println("🐍 " + line);
                        }
                    }
                } catch (IOException e) {
                    // Thread morreu
                }
            }).start();
            
            System.out.println("✅ Python Service iniciado!");
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao iniciar Python: " + e.getMessage());
        }
    }
    
    private void waitForPythonService() {
        System.out.print("⏳ Aguardando Python Service (http://localhost:5000)");
        
        int maxAttempts = 30;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                URL url = new URL("http://localhost:5000/health");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                connection.connect();
                
                if (connection.getResponseCode() == 200) {
                    System.out.println("\n✅ Python Service está pronto!");
                    return;
                }
            } catch (Exception e) {
                // Ainda não está pronto
            }
            
            System.out.print(".");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\n⚠️ Python Service pode não ter iniciado completamente");
    }
    
    private void openBrowser() {
        String url = "http://localhost:8082/pages/register.html";
        System.out.println("🌐 Abrindo navegador: " + url);
        
        try {
            // Tenta abrir o navegador
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("✅ Navegador aberto!");
                return;
            }
            
            // Fallback por SO
            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();
            
            if (os.contains("win")) {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                rt.exec("open " + url);
            } else {
                rt.exec("xdg-open " + url);
            }
            System.out.println("✅ Navegador aberto!");
            
        } catch (Exception e) {
            System.out.println("❌ Não foi possível abrir o navegador");
            System.out.println("📱 Acesse manualmente: " + url);
        }
    }
}