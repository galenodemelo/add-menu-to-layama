package com.elephantskin.addmenutolayama.menubuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.elephantskin.addmenutolayama.menuconfig.MenuConfigDTO;
import com.elephantskin.addmenutolayama.menuconfig.MenuConfigDTO.MenuConfigItem;
import com.elephantskin.addmenutolayama.menuconfig.MenuConfigDTO.MenuConfigItem.MenuConfigItemSubItem;

public class MenuHtmlBuilder {
    
    private StringBuilder html = new StringBuilder();

    private MenuConfigDTO configDTO;

    private String sidenavId;

    private String menuStateId;

    private String menuStateTriggerId;

    private MenuHtmlBuilder(MenuConfigDTO configDTO, String sidenavId, String menuStateId, String menuStateTriggerId) {
        this.configDTO = configDTO;
        this.sidenavId = sidenavId;
        this.menuStateId = menuStateId;
        this.menuStateTriggerId = menuStateTriggerId;
    }

    public static MenuHtmlBuilder build(MenuConfigDTO configDTO, String sidenavId, String menuStateId, String menuStateTriggerId) {
        System.out.println("> Gerando código HTML do menu...");
        return new MenuHtmlBuilder(configDTO, sidenavId, menuStateId, menuStateTriggerId).build();
    }

    public MenuHtmlBuilder build() {
        this.addCss();
        this.addMenuButton();
        this.html.append("<nav class='menu js-menu-wrapper' id='" + this.sidenavId + "'>");
        this.addMenuLinks();
        this.addJs();
        this.html.append("</nav>");

        return this;
    }

    public String getHtml() {
        return this.html.toString();
    }

    private void addCss() {
        this.html.append("<link rel='stylesheet' href='./assets/style.css' type='text/css' />");
    }

    private void addMenuButton() {
        this.html.append("<input type='checkbox' value='1' id='" + this.menuStateId + "' class='menu__toggle__state hide js-menu-state' />");
        this.html.append("<label for='" + this.menuStateId + "' id='" + this.menuStateTriggerId + "' class='menu__toggle__trigger' title='Open menu'>");
        this.html.append("    <hr />");
        this.html.append("    <hr />");
        this.html.append("    <hr />");
        this.html.append("</label>");
    }

    private void addMenuLinks() {
        this.html.append("<ul class='menu__list slide-left-on-toggle'>");

        for (MenuConfigItem menuConfigItem : this.configDTO.itemList) {
            this.addMenuTitle(menuConfigItem);
        }

        this.html.append("</ul>");
    }

    private void addMenuTitle(MenuConfigItem menuConfigItem) {
        String uuid = UUID.randomUUID().toString();

        List<String> stateAttributeList = new ArrayList<String>();
        stateAttributeList.add("id='" + uuid + "'");
        if (this.configDTO.opened) stateAttributeList.add("checked");

        this.html.append("<li class='menu__list__item'>");
        this.html.append("    <input type='checkbox' class='menu__list__item__state hide js-menu-item-state-first-level' " + String.join(" ", stateAttributeList) + " />");
        this.html.append("    <label for='" + uuid + "' class='menu__list__item__trigger'>");
        this.html.append(menuConfigItem.name);
        this.html.append("    </label>");
        
        this.addMenuItem(menuConfigItem.subItemList, menuConfigItem.url);
        this.html.append("</li>");
    }

    private void addMenuItem(List<MenuConfigItemSubItem> subItemList, String url) {
        this.html.append("<ul class='menu__list__item__list slide-down-on-toggle'>");
        for (MenuConfigItemSubItem subItem : subItemList) {
            this.addMenuSubItem(subItem, url);
        }
        this.html.append("</ul>");
    }

    private void addMenuSubItem(MenuConfigItemSubItem subItem, String baseUrl) {
        if (baseUrl == null) baseUrl = "";
        Boolean hasSubItems = subItem.subItemList != null;

        this.html.append("<li class='menu__list__item__list__item'>");

        if (!hasSubItems) {
            String url = baseUrl + "?camera=" + subItem.cameraName;
            this.html.append("    <a href='" + url + "' class='menu__list__item__list__item__label'>");
            this.html.append(subItem.name);
            this.html.append("    </a>");
        } else {
            String uuid = UUID.randomUUID().toString();
            this.html.append("<input type='checkbox' class='menu__list__item__list__item__state hide js-menu-item-state-second-level' id='" + uuid + "' />");
            this.html.append("<label for='" + uuid + "' class='menu__list__item__list__item__label menu__list__item__list__item__label--trigger'>");
            this.html.append(subItem.name);
            this.html.append("</label>");

            this.addMenuItem(subItem.subItemList, baseUrl);
        }

        this.html.append("</li>");
    }

    private void addJs() {
        this.html.append("<script src='./assets/scripts.js'></script>");
    }
}
