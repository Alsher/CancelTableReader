import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Phil on 09.06.14.
 */
public class AccessPage
{
    public static void getPage(String base64login, String urlp1, String urlp2) throws IOException {
        Calendar now = Calendar.getInstance();
        int calendarWeek = now.get(Calendar.WEEK_OF_YEAR);
        if(now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            calendarWeek++;

        String url = urlp1 + calendarWeek + urlp2;

        Document doc = Jsoup
                .connect(url)
                .header("Authorization", "Basic " + base64login)
                .get();

        double start = System.nanoTime();
        ParseCancel.parse(doc);
        System.out.println("Connecting and Parsing took: " + (System.nanoTime() - start) / 1000000.0 + "ms.");
    }
}