package com.elephantskin.addmenutolayama.optimizer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OptimizerManager {

    private final String compressedJsFilename = "compressed.js";
    private final String layamaCustomJsFilename = "layama.custom.js";
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
        removeJsFromHead();
        injectNecessaryJs();
        injectPreloaders();
        ImageOptimizerManager.optimize(this.projectPath);
        addCachePolicy();
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

    private void removeJsFromHead() throws IOException {
        System.out.println("> Removendo injeção do JS do heading...");

        File indexFile = new File(projectPath + "index.html");
        String indexHtmlContent = FileUtils.readFileToString(indexFile, StandardCharsets.UTF_8);

        Document indexFileManager = Jsoup.parse(indexHtmlContent);
        Elements jsScriptInjectionList = indexFileManager.getElementsByTag("head").first()
            .getElementsByAttributeValueStarting("src", "./");
        if (!jsScriptInjectionList.isEmpty())
            jsScriptInjectionList.remove();

        FileUtils.write(indexFile, indexFileManager.html(), StandardCharsets.UTF_8);
    }

    private void injectNecessaryJs() throws IOException {
        File indexFile = new File(projectPath + "index.html");
        String indexHtmlContent = FileUtils.readFileToString(indexFile, StandardCharsets.UTF_8);

        Document indexFileManager = Jsoup.parse(indexHtmlContent);
        Element canvasElement = indexFileManager.getElementById("canvasZone");
        if (canvasElement == null)
            throw new IOException("O elemento canvasZone não existe no arquivo index.html");

        injectCompressedJsFile(canvasElement);
        reinjectLayamaSceneJsFile(canvasElement);
        injectLayamaCustomJsFile(canvasElement);

        FileUtils.write(indexFile, indexFileManager.html(), StandardCharsets.UTF_8);
    }

    private void injectCompressedJsFile(Element element) throws IOException {
        System.out.println("> Injetando JS comprimido...");

        final File compressedJsFile = new File("../sources/" + this.compressedJsFilename);
        if (!compressedJsFile.exists())
            throw new IOException("O arquivo JS comprimido não existe: " + compressedJsFile.getAbsolutePath());

        File destination = new File(projectPath + "bjs/" + this.compressedJsFilename);
        FileUtils.copyFile(compressedJsFile, destination);

        String compressedJsFilepath = "./bjs/" + this.compressedJsFilename;
        element.getElementsByAttributeValue("src", compressedJsFilepath).first().remove();
        element.append("<script src=\"" + compressedJsFilepath + "\"></script>");
    }

    private void reinjectLayamaSceneJsFile(Element element) {
        System.out.println("> Reinjetando layama.scene.js...");

        String layamaSceneFilepath = "./layama.scene.js";
        Elements layamaInjectionElement = element.getElementsByAttributeValue("src", layamaSceneFilepath);
        if (!layamaInjectionElement.isEmpty()) layamaInjectionElement.first().remove();

        element.append("<script src=\"" + layamaSceneFilepath + "\"></script>");
    }

    private void injectLayamaCustomJsFile(Element element) throws IOException {
        System.out.println("> Injetando funções que sobrepõe o Layama...");

        final File layamaCustomJsFile = new File("../sources/" + this.layamaCustomJsFilename);
        if (!layamaCustomJsFile.exists())
            throw new IOException("O arquivo customizado do Layama não existe: " + layamaCustomJsFile.getAbsolutePath());

        File destination = new File(projectPath + "bjs/" + this.layamaCustomJsFilename);
        FileUtils.copyFile(layamaCustomJsFile, destination);

        String layamaCustomJsFilepath = "./bjs/" + this.layamaCustomJsFilename;
        element.getElementsByAttributeValue("src", layamaCustomJsFilepath).first().remove();
        element.append("<script src=\"" + layamaCustomJsFilepath + "\"></script>");
    }

    private void injectPreloaders() throws IOException {
        System.out.println("> Injetando preloaders dos recursos...");

        File indexFile = new File(projectPath + "index.html");
        String indexHtmlContent = FileUtils.readFileToString(indexFile, StandardCharsets.UTF_8);

        Document indexFileManager = Jsoup.parse(indexHtmlContent);
        Elements preloadLinkList = indexFileManager.getElementsByAttributeValue("rel", "preload");
        if (!preloadLinkList.isEmpty())
            preloadLinkList.remove();

        Element headElement = indexFileManager.getElementsByTag("head").first();
        headElement.append("<link rel=\"preload\" href=\"./bjs/" + this.compressedJsFilename + "\" as=\"script\" />");

        List<String> fontNameList = Arrays.asList("subset-Etna.woff2", "subset-Etna.woff",
                "subset-AauxProMedium.woff2", "subset-AauxProMedium.woff");
        for (String fontName : fontNameList) {
            headElement.append("<link rel=\"preload\" href=\"./menu/font/" + fontName + "\" as=\"font\" />");
        }

        FileUtils.write(indexFile, indexFileManager.html(), StandardCharsets.UTF_8);
    }

    private void addCachePolicy() throws IOException {
        System.out.println("> Adicionando cache policy...");

        final File htAccessSource = new File("../sources/.htaccess");
        FileUtils.copyFile(htAccessSource, new File(projectPath + ".htaccess"));
    }
}
