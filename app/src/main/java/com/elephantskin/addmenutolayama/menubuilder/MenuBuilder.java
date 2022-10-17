package com.elephantskin.addmenutolayama.menubuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.elephantskin.addmenutolayama.App;
import com.elephantskin.addmenutolayama.menuconfig.MenuConfigDTO;

public class MenuBuilder {
    
    private final String menuPathName = "assets";
    private final String sidenavId = "sidenav";
    private final String menuStateId = "menu-state";
    private final String menuStateTriggerId = "menu-state-trigger";
    
    private MenuConfigDTO configDTO;
    private String projectPath;
    private String html;

    private MenuBuilder(String projectPath, MenuConfigDTO configDTO) {
        this.projectPath = projectPath;
        this.configDTO = configDTO;
    }

    public static void build(String projectPath, MenuConfigDTO configDTO) throws IOException {
        new MenuBuilder(projectPath, configDTO).build();
    }

    public void build() throws IOException {
        copyMenuModelFilesToProjectPath();
        this.html = MenuHtmlBuilder.build(this.configDTO, sidenavId, menuStateId, menuStateTriggerId).getHtml();
        injectMenuHtmlOnIndexFile();
    }

    private void copyMenuModelFilesToProjectPath() throws IOException {
        System.out.println("> Copiando arquivos do modelo para o projeto...");
        
        final File menuModelDir = new File(App.jarLocation + "/sources/assets");
        if (!menuModelDir.isDirectory()) throw new IOException("O diretório do modelo de menu não existe: " + menuModelDir.getAbsolutePath());

        File destinationDir = new File(projectPath + this.menuPathName);
        if (destinationDir.exists()) destinationDir.delete();
        
        destinationDir.mkdir();
        FileUtils.copyDirectory(menuModelDir, destinationDir);
    }

    private void injectMenuHtmlOnIndexFile() throws IOException {
        System.out.println("> Injetando HTML do menu no arquivo index.html...");

        File indexFile = new File(projectPath + "index.html");
        String indexHtmlContent = FileUtils.readFileToString(indexFile, StandardCharsets.UTF_8);
        
        Document indexFileManager = Jsoup.parse(indexHtmlContent);
        removeItemsFromPreviousBuild(indexFileManager);

        Element elementToAppendMenu = indexFileManager.getElementById("canvasZone");
        if (elementToAppendMenu == null) throw new IOException("O elemento canvasZone não foi encontrado no arquivo index.html");

        elementToAppendMenu.append(this.html);
        FileUtils.write(indexFile, indexFileManager.html(), StandardCharsets.UTF_8);
    }

    private void removeItemsFromPreviousBuild(Document indexFileManager) {
        Element sidenavElement = indexFileManager.getElementById(this.sidenavId);
        if (sidenavElement != null) sidenavElement.remove();

        Element menuStateElement = indexFileManager.getElementById(this.menuStateId);
        if (menuStateElement != null) menuStateElement.remove();

        Element menuStateTriggerElement = indexFileManager.getElementById(this.menuStateTriggerId);
        if (menuStateTriggerElement != null) menuStateTriggerElement.remove();
    }
}
