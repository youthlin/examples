package com.youthlin.example.webdav.sardine;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

/**
 * @author youthlin.chen
 * @date 2020-03-16 19:34
 */
public class Main {
    private static String ROOT_URI;

    public static void main(String[] args) throws IOException, URISyntaxException {
        Properties properties = new Properties();
        InputStream in = Main.class.getResourceAsStream("/config.properties");
        properties.load(in);
        String server = properties.getProperty("server");
        URL url = new URL(server);
        if (url.getPort() > 0) {
            ROOT_URI = String.format("%s://%s:%d", url.getProtocol(), url.getHost(), url.getPort());
        } else {
            ROOT_URI = String.format("%s://%s", url.getProtocol(), url.getHost());
        }
        System.out.println("ROOT:" + ROOT_URI);
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        Sardine sardine = SardineFactory.begin(username, password);
        listDir(0, sardine, server);
    }

    private static void listDir(int level, Sardine sardine, String url) throws IOException {
        List<DavResource> list = sardine.list(url);
        list.sort(
                Comparator.comparing(DavResource::isDirectory)
                        .reversed()
                        .thenComparing(DavResource::getPath)
        );
        for (DavResource davResource : list) {
            URI href = davResource.getHref();
            String itemUrl;
            if (href.getHost() != null) {
                // full path
                itemUrl = href.toString();
            } else {
                // without host
                itemUrl = href.toString();
                if (itemUrl.startsWith("/")) {
                    itemUrl = ROOT_URI + itemUrl;
                } else {
                    itemUrl = ROOT_URI + "/" + itemUrl;
                }
            }
            if (davResource.isDirectory() && !itemUrl.equals(url)) {
                listDir(level + 1, sardine, itemUrl);
            } else {
                System.out.println(davResource.getPath() + ": " + davResource.getDisplayName());
            }
        }
    }
}
