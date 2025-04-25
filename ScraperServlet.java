import java.io.*;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet("/scrape")
public class ScrapeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) visitCount = 0;
        visitCount++;
        session.setAttribute("visitCount", visitCount);

        String url = request.getParameter("url");
        String[] options = request.getParameterValues("options");

        Document doc = Jsoup.connect(url).get();

        ScrapedData scrapedData = new ScrapedData();
        scrapedData.title = doc.title();

        if (options != null) {
            for (String option : options) {
                switch (option) {
                    case "title":
                        scrapedData.showTitle = true;
                        break;
                    case "links":
                        Elements links = doc.select("a[href]");
                        for (Element link : links) {
                            scrapedData.links.add(new Item(link.text(), link.absUrl("href")));
                        }
                        break;
                    case "images":
                        Elements images = doc.select("img[src]");
                        for (Element img : images) {
                            scrapedData.images.add(new Item(img.attr("alt"), img.absUrl("src")));
                        }
                        break;
                }
            }
        }

        Gson gson = new Gson();
        String json = gson.toJson(scrapedData);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Scrape Results</title></head><body>");
        out.println("<h2>Scraped Results</h2>");
        out.println("<h3>You have visited this page " + visitCount + " times.</h3>");
        out.println("<table border='1'><tr><th>Type</th><th>Content</th></tr>");

        if (scrapedData.showTitle) {
            out.println("<tr><td>Title</td><td>" + scrapedData.title + "</td></tr>");
        }

        for (Item link : scrapedData.links) {
            out.println("<tr><td>Link</td><td><a href='" + link.url + "'>" + link.text + "</a></td></tr>");
        }

        for (Item img : scrapedData.images) {
            out.println("<tr><td>Image</td><td><img src='" + img.url + "' width='100'></td></tr>");
        }

        out.println("</table>");
        out.println("<pre id='jsonData' style='display:none'>" + json + "</pre>");
        out.println("</body></html>");
    }
    
    class ScrapedData {
        String title;
        boolean showTitle = false;
        List<Item> links = new ArrayList<>();
        List<Item> images = new ArrayList<>();
    }

    class Item {
        String text;
        String url;

        Item(String text, String url) {
            this.text = text;
            this.url = url;
        }
    }
}
