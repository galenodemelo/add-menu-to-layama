package com.elephantskin.addmenutolayama.MenuConfig;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class MenuConfigFileManager {

    private File menuConfigFile;
    private final String menuConfigFileName = "menu-config.yaml";

    public MenuConfigDTO getMenuConfig(String projectPath) {
        File menuConfigFile = this.getMenuConfigFile(projectPath);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(menuConfigFile, MenuConfigDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File getMenuConfigFile(String projectPath) {
        if (this.menuConfigFile != null) return this.menuConfigFile;

        this.menuConfigFile = new File(projectPath + this.menuConfigFileName);
        if (!this.menuConfigFile.exists()) {
            System.out.println("O arquivo " + this.menuConfigFileName + " não existe na pasta " + this.menuConfigFile.getAbsolutePath() + ".");
            System.exit(1);
        }

        return this.menuConfigFile;
    }
}
