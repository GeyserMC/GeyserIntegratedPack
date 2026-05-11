package org.geysermc.integratedpack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
    public static final String JAVA_TARGET_VERSION = "26.1.2";
    public static final String BEDROCK_TARGET_VERSION = "1.26.20.26";

    public static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();
}
