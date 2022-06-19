package com.elephantskin.addmenutolayama;

import javax.swing.JFileChooser;

import com.elephantskin.addmenutolayama.MenuBuilder.MenuBuilder;
import com.elephantskin.addmenutolayama.MenuConfig.MenuConfigDTO;
import com.elephantskin.addmenutolayama.MenuConfig.MenuConfigFileManager;
import com.elephantskin.addmenutolayama.Optimizer.OptimizerManager;

public class App {

    private static String projectPath;

    public static void main(String[] args) {
        try {
            System.out.println("==== INICIALIZANDO ====");
            
            askUserForProjectPath();
            MenuConfigDTO configDTO = new MenuConfigFileManager().getMenuConfig(projectPath);
            MenuBuilder.build(projectPath, configDTO);
            OptimizerManager.optimize(projectPath);
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("> Ocorreram erros na execução do programa. Envie um print da tela para o desenvolvedor.");
        } finally {
            System.out.println("==== FINALIZADO ====");
        }
    }
    
    private static void askUserForProjectPath() throws InterruptedException {
        System.out.println("> Selecione a pasta raiz do projeto");
        Thread.sleep(1000);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        Integer opt = fileChooser.showOpenDialog(null);
        if (opt == JFileChooser.APPROVE_OPTION) {
            projectPath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!projectPath.endsWith("/")) projectPath += "/";
            System.out.println("> Caminho selecionado: " + projectPath);
        } else {
            System.out.println("O usuário não selecionou nenhum diretório.");
            System.exit(1);
        }
    }
}
