import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Crawler crawler;
        crawler = new Crawler("http://blogfa.com/");
        crawler.start();
    }
}
