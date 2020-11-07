import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;

public class Crawler {

    private final int MAX_PAGES = 300;
    private String host;
    private List<String> pages;
    private Set<String> visited;
    private int allDiscoveredLink;
    private int distinctDiscoveredLink;
    private int linksSize;
    private int pageSize;
    private int compressedSize;

    public Crawler(String host) {
        this.host = host;
        pages = new LinkedList<>();
        visited = new HashSet<>();
        distinctDiscoveredLink = 0;
        allDiscoveredLink = 0;
        linksSize = 0;
        pageSize = 0;
        compressedSize = 0;
    }

    public void crawl() throws IOException {

        Connection connection;
        Document doc;
        String url;
        pages.add(host);
        pages.add(host + "robots.txt");


        int i = 0;
        while (visited.size() < MAX_PAGES && pages.size() > 0) {
            url = pages.remove(0);
            if (!visited.contains(url)) {
                connection = Jsoup.connect(url).timeout(100000);
                doc = connection.get();
                System.out.println(connection.response().statusCode());
                visited.add(url);
                linksSize += url.length();
                pageSize += doc.html().length();
                try (FileWriter writer = new FileWriter(i + ".html")) {
                    writer.write(doc.html());
                }
                System.out.println(pages.size() + " " + visited.size() + " " + url);
                compressedSize += compress(String.valueOf(i));

                Elements elements = doc.select("a[href]");

                String tmp;
                for (Element link : elements) {
                    tmp = link.absUrl("href");
                    if (tmp.startsWith(host) && !pages.contains(tmp) && !visited.contains(tmp)) {
                        pages.add(tmp);
                        distinctDiscoveredLink++;
                    }
//                    System.out.println(link.absUrl("href"));
                }


                allDiscoveredLink += elements.size();
                i++;
            }
        }
        System.out.println("Number of discovered links: " + distinctDiscoveredLink);
        System.out.println("Mean out degree: " + allDiscoveredLink / MAX_PAGES);
        System.out.println("Mean size of each page: " + pageSize / MAX_PAGES);
        System.out.println("Mean size of each page with compression: " + compressedSize / MAX_PAGES);
        System.out.println("Mean size of each link: " + linksSize / MAX_PAGES);
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
