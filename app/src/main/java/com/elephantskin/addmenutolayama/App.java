package com.elephantskin.addmenutolayama;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import com.elephantskin.addmenutolayama.menubuilder.MenuBuilder;
import com.elephantskin.addmenutolayama.menuconfig.MenuConfigDTO;
import com.elephantskin.addmenutolayama.menuconfig.MenuConfigFileManager;
import com.elephantskin.addmenutolayama.optimizer.OptimizerManager;

public class App {

    private static List<String> projectPathList = new ArrayList<String>();

    public static void main(String[] args) {
        try {
            System.out.println("==== INICIALIZANDO ====");
            
            askUserForProjectPath();
            for (String projectPath : App.projectPathList) {
                System.out.println("||| Processando projeto " + projectPath + " |||");
                MenuConfigDTO configDTO = new MenuConfigFileManager().getMenuConfig(projectPath);
                MenuBuilder.build(projectPath, configDTO);
                OptimizerManager.optimize(projectPath);
                System.out.println("||| Projeto " + projectPath + " finalizado |||");
            }
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
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        Integer opt = fileChooser.showOpenDialog(null);
        if (opt == JFileChooser.APPROVE_OPTION) {
            for (File file : fileChooser.getSelectedFiles()) {
                String absolutePath = file.getAbsolutePath();
                if (!absolutePath.endsWith("/")) absolutePath += "/";
                
                App.projectPathList.add(absolutePath);
                System.out.println("> Caminho selecionado: " + absolutePath);
            }
        } else {
            System.out.println("O usuário não selecionou nenhum diretório.");
            System.exit(1);
        }
    }
}
