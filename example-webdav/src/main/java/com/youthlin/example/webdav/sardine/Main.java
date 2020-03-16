package com.youthlin.example.webdav.sardine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author youthlin.chen
 * @date 2020-03-16 19:34
 */
public class Main {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static String SERVER;

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        InputStream in = Main.class.getResourceAsStream("/config.properties");
        properties.load(in);
        SERVER = properties.getProperty("server");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        Sardine sardine = SardineFactory.begin(username, password);
        listDir(0, sardine, SERVER + "/");
    }

    private static void listDir(int level, Sardine sardine, String href) throws IOException {
        for (DavResource davResource : sardine.list(href)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= level; i++) {
                sb.append("--");
            }
            String prefix = sb.toString();
            System.out.println(prefix + level + "---");
            String itemUri = SERVER + davResource.getHref().toString();
            System.out.println(prefix + href);
            System.out.println(prefix + itemUri);
            if (davResource.isDirectory() && !itemUri.equals(href)) {
                listDir(level + 1, sardine, itemUri);
            } else {
                System.out.println(prefix + MAPPER.writeValueAsString(davResource));
            }
        }
    }
}
