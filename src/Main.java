import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Crawler crawler = new Crawler("https://www.aparat.com/");
        crawler.crawl();
    }
}
