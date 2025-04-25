import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class WebScraper {
    public static void main(String[] args) {
        String url = "https://www.bbc.com";
        Document doc = Jsoup.connect(url).get();
        List<NewsArticle> articles = new ArrayList<>();

        Elements headlines = doc.select("h3");

        for (Element headline : headlines) {
            String title = headline.text();
            String date = "N/A";
            String author = "N/A";

            NewsArticle article = new NewsArticle(title, date, author);
            articles.add(article);
        }

        for (NewsArticle article : articles) {
            System.out.println(article);
        }
        // TODO: Scrape a URL and print its title
    }
}

public class News{
    String headline;
    String publicationDate;
    String author;

    public News(String headline, String publicationDate, String author) {
        this.headline = headline;
        this.publicationDate = publicationDate;
        this.author = author;
    }

    public String toString() {
        return "Headline: " + headline + "\nDate: " + publicationDate + "\nAuthor: " + author + "\n";
    }
    
}
