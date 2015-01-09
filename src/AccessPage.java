import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Calendar;

/*
    AccessPage Class to access given url with given base64-login
 */

public class AccessPage
{
    public static void getPage(String base64login, String urlp1, String urlp2) throws IOException {
        Calendar now = Calendar.getInstance();
        int calendarWeek = now.get(Calendar.WEEK_OF_YEAR);
        if(now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            calendarWeek++;

        String url = urlp1 + (calendarWeek < 10 ? "0" : "") + calendarWeek + urlp2;

        double start = System.nanoTime();
        Document doc = Jsoup.connect(url).header("Authorization", "Basic " + base64login).get();
        System.out.println("Connecting took: " + (System.nanoTime() - start) / 1000000.0 + "ms.");

        start = System.nanoTime();
        ParseCancel.parse(doc);
        System.out.println("Parsing took: " + (System.nanoTime() - start) / 1000000.0 + "ms.");
    }
}