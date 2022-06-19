package com.elephantskin.addmenutolayama.Optimizer;

import java.io.IOException;

public class OptimizerManager {

    private String projectPath;

    private OptimizerManager(String projectPath) {
        this.projectPath = projectPath;
    }

    public static void optimize(String projectPath) throws IOException, InterruptedException {
        new OptimizerManager(projectPath).optimize();
    }

    private void optimize() throws IOException, InterruptedException {
        System.out.println("> Iniciando otimização...");
        removeBootstrapCss();
        System.out.println("> Otimizações finalizadas...");
    }

    private void removeBootstrapCss() throws IOException {
        System.out.println("> Removendo injeção do bootstrap.css...");

        File indexFile = new File(projectPath + "index.html");
        String indexHtmlContent = FileUtils.readFileToString(indexFile, StandardCharsets.UTF_8);

        Document indexFileManager = Jsoup.parse(indexHtmlContent);
        Elements boostrapLinkList = indexFileManager.getElementsByAttributeValueEnding("href", "bootstrap.min.css");
        if (!boostrapLinkList.isEmpty())
            boostrapLinkList.remove();

        FileUtils.write(indexFile, indexFileManager.html(), StandardCharsets.UTF_8);
    }
}
