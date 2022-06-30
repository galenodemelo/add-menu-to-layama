package com.elephantskin.addmenutolayama.menubuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.elephantskin.addmenutolayama.App;
import com.elephantskin.addmenutolayama.menuconfig.MenuConfigDTO;

public class MenuBuilder {
    
    private final String menuPathName = "menu";
    private final String menuHtmlIdValue = "sidenav";
    
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
        buildHtml();
        injectMenuHtmlOnIndexFile();
    }

    private void copyMenuModelFilesToProjectPath() throws IOException {
        System.out.println("> Copiando arquivos do modelo para o projeto...");
        
        final File menuModelDir = new File(App.jarLocation + "/sources/menumodel");
        if (!menuModelDir.isDirectory()) throw new IOException("O diretório do modelo de menu não existe: " + menuModelDir.getAbsolutePath());

        File destinationDir = new File(projectPath + this.menuPathName);
        if (destinationDir.exists()) destinationDir.delete();
        
        destinationDir.mkdir();
        FileUtils.copyDirectory(menuModelDir, destinationDir);
    }

    private void buildHtml() {
        System.out.println("> Gerando código HTML do menu...");
        String menuButtonHtml = buildMenuButtonHtml();
        String menuHtml = buildMenuHtml();
        String cssAndJs = buildCssAndJs();

        this.html = "<div id='" + this.menuHtmlIdValue + "'>" 
                        + menuButtonHtml 
                        + menuHtml 
                        + cssAndJs 
                  + "</div>";
    }

    private String buildMenuButtonHtml() {
        StringBuilder html = new StringBuilder()
            .append("<div id='tdbotaomenu' onclick='openNav()'>")
            .append("   <img id='imgButton' src='./menu/img/button_01.gif' />")
            .append("</div>");

        return html.toString();
    }

    private String buildMenuHtml() {
        List<String> menuClassList = new ArrayList<String>();
        menuClassList.add("tdmenu");
        if (this.configDTO.watermark) menuClassList.add("with-watermark");

        StringBuilder html = new StringBuilder()
            .append("<div class='" + String.join(" ", menuClassList) + "'>")
            .append("   <nav class='navbar'>")
            .append("       <ul>");
        
        for (MenuConfigDTO.MenuConfigItem item : this.configDTO.itemList) {
            html.append("       <li class='itemmenu'>")
                .append("           <button class='bto-accordion'>" + item.name + "</button>")
                .append("           <ul id='list'>");

            for (MenuConfigDTO.MenuConfigItem.MenuConfigItemSubItem subItem : item.subItemList) {
                String link = "";
                if (item.url != null) link += item.url;
                if (subItem.cameraName != null) link += "?camera=" + subItem.cameraName;

                html.append("           <li>")
                    .append("               <a href='"+ link + "'>")
                    .append(                    subItem.name)
                    .append("               </a>")
                    .append("           </li>");
            }

            html.append("           </ul>")
                .append("       </li>");

        }
        html.append("       </ul>")
            .append("   </nav>")
            .append("</div>");

        return html.toString();
    }

    private String buildCssAndJs() {
        StringBuilder html = new StringBuilder()
            .append("<link rel='stylesheet' href='./menu/menulateral.css' />")
            .append("<script src='./menu/menulateral.js'></script>");

        return html.toString();
    }

    private void injectMenuHtmlOnIndexFile() throws IOException {
        System.out.println("> Injetando HTML do menu no arquivo index.html...");

        File indexFile = new File(projectPath + "index.html");
        String indexHtmlContent = FileUtils.readFileToString(indexFile, StandardCharsets.UTF_8);
        
        Document indexFileManager = Jsoup.parse(indexHtmlContent);
        Element menuHtmlElement = indexFileManager.getElementById(this.menuHtmlIdValue);
        if (menuHtmlElement != null) menuHtmlElement.remove();

        Element elementToAppendMenu = indexFileManager.getElementById("canvasZone");
        if (elementToAppendMenu == null) throw new IOException("O elemento canvasZone não foi encontrado no arquivo index.html");

        elementToAppendMenu.append(this.html);
        FileUtils.write(indexFile, indexFileManager.html(), StandardCharsets.UTF_8);
    }
}
