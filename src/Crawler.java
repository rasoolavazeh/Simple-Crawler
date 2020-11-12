import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.zip.DeflaterOutputStream;

public class Crawler {

    private final int MAX_PAGES = 300;
    private Connection connection;
    private String baseUrl;
    private List<String> urls;
    private List<String> visited;
    private int allDiscoveredLink;
    private int distinctDiscoveredLink;
    private int linksSize;
    private int pagesSizeWithoutCompression;
    private int pagesSizeWithCompression;
    private long minPageSize;
    private long maxPageSize;
    private int minUrlSize;
    private int maxUrlSize;
    private int index;

    public Crawler(String baseUrl) {
        this.baseUrl = baseUrl;
        urls = new ArrayList<>();
        visited = new ArrayList<>();
        distinctDiscoveredLink = 0;
        allDiscoveredLink = 0;
        linksSize = 0;
        pagesSizeWithoutCompression = 0;
        pagesSizeWithCompression = 0;
        maxPageSize = 0;
        minPageSize = Long.MAX_VALUE;
        maxUrlSize = 0;
        minUrlSize = Integer.MAX_VALUE;
    }

    public void start() throws IOException {

        urls.add(baseUrl + "robots.txt");
        urls.add(baseUrl + "comment");
        urls.add(baseUrl);

        for (int i = 0; i < MAX_PAGES; i++) {
            System.out.println(i + " " + urls.get(i));
            Document doc = fetch(urls.get(i));
            parse(doc);
        }

//        Connection connection;
//        Document doc;
//        String url;
//        pages.add(host + "robots.txt");
//        pages.add(host);
//
//
//        int i = 0;
//        while (visited.size() < MAX_PAGES && pages.size() > 0) {
//            url = pages.remove(0);
//            if (!visited.contains(url)) {
//                try {
//                    connection = Jsoup.connect(url).timeout(10000);
//                    doc = connection.get();
//                    visited.add(url);
//                    linksSize += url.length();
//                    pageSize += doc.html().length();
//                    try (FileWriter writer = new FileWriter(i + ".html")) {
//                        writer.write(doc.html());
//                    }
//                    System.out.println(pages.size() + " " + visited.size() + " " + url);
//                    compressedSize += compress(String.valueOf(i));
//
//                    Elements elements = doc.select("a[href]");
//
//                    String tmp;
//                    for (Element link : elements) {
//                        tmp = link.absUrl("href");
//                        if (tmp.startsWith(host) && !pages.contains(tmp) && !visited.contains(tmp)) {
//                            pages.add(tmp);
//                            distinctDiscoveredLink++;
//                        }
////                    System.out.println(link.absUrl("href"));
//                    }
//
//
//                    allDiscoveredLink += elements.size();
//                    i++;
//                } catch (HttpStatusException e) {
//                    System.out.println(e.getStatusCode());
//                }
//            }
//        }
        System.out.println("Number of discovered links: " + distinctDiscoveredLink);
        System.out.println("Mean out degree: " + allDiscoveredLink / MAX_PAGES);
        System.out.println("Mean size of each page: " + pagesSizeWithoutCompression / MAX_PAGES);
        System.out.println("Mean size of each page with compression: " + pagesSizeWithCompression / MAX_PAGES);
        System.out.println("Mean size of each link: " + linksSize / MAX_PAGES);
        System.out.println("Max page size: " + maxPageSize);
        System.out.println("Min page size: " + minPageSize);
        System.out.println("Max url size: " + maxUrlSize);
        System.out.println("Min url size: " + minUrlSize);
    }

    private Document fetch(String url) {
        try (FileWriter writer = new FileWriter(index + ".html")) {
            connection = Jsoup.connect(url);
            Document doc = connection.get();
//            System.out.println(doc.documentType());
            visited.add(url);
            writer.write(doc.html());
            linksSize += url.length();

            if (url.length() < minUrlSize) {
                minUrlSize = url.length();
            }
            if (url.length() > maxUrlSize) {
                maxUrlSize = url.length();
            }
            return doc;
        } catch (HttpStatusException e) {
            System.out.println(e.getStatusCode() + " " + e.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parse(Document doc) {
        if (doc == null)
            return;
        Elements elements = doc.select("a[href]");
        pagesSizeWithoutCompression += doc.html().length();
        pagesSizeWithCompression += compress(String.valueOf(index));

        if (doc.html().length() < minPageSize) {
            minPageSize = doc.html().length();
        }

        if (doc.html().length() > maxPageSize) {
            maxPageSize = doc.html().length();
        }
        String url;
        for (Element link : elements) {
            url = link.absUrl("href");
            if (!urls.contains(url) && (url.startsWith(baseUrl) || url.contains("blogfa.com"))) {
                urls.add(url);
                distinctDiscoveredLink++;
            }
        }
        allDiscoveredLink += elements.size();
        index++;
    }

    private long compress(String fileName) {

        try (FileInputStream fis = new FileInputStream(fileName + ".html");
                FileOutputStream fos = new FileOutputStream(fileName);
             DeflaterOutputStream dos = new DeflaterOutputStream(fos)) {

            int data;
            while ((data = fis.read()) != -1) {
                dos.write(data);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(fileName);

        return file.length();
    }

}
