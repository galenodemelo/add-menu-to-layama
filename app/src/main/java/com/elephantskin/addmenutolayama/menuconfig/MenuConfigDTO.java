package com.elephantskin.addmenutolayama.menuconfig;

import java.util.List;

import javax.annotation.Nullable;

public class MenuConfigDTO {
    
    public List<MenuConfigItem> itemList;

    public static class MenuConfigItem {
        
        public String name;

        public List<MenuConfigItemSubItem> subItemList;

        public static class MenuConfigItemSubItem {
            
            public String name;

            @Nullable
            public String cameraName;

            @Nullable
            public String url;
        }
    }
}
