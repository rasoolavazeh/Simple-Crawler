import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Crawler {

    private final int MAX_PAGES = 300;
    private String host;
    private List<String> pages;
    private Set<String> visited;
    private int discoveredList;

    public Crawler(String host) {
        this.host = host;
        pages = new LinkedList<>();
        visited = new HashSet<>();
        discoveredList = 0;
    }

    public void crawl() throws IOException {
        Connection connection;
        Document doc;
        String url;
        pages.add(host);
        pages.add(host + "robots.txt");


        while (visited.size() < MAX_PAGES) {
            url = pages.remove(0);
            if (!visited.contains(url)) {
                connection = Jsoup.connect(url);
                doc = connection.get();
                visited.add(url);
                System.out.println(pages.size() + " " + visited.size() + " " + url);

                Elements elements = doc.select("a[href]");

                for (Element link : elements) {
                    if (link.absUrl("href").startsWith(url))
                        pages.add(link.absUrl("href"));
                }

                discoveredList += elements.size();
            }
        }
        System.out.println(discoveredList);
    }

}
