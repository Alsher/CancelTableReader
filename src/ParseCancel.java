import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Phil on 30.10.14.
 */
public class ParseCancel
{
    private static final String dateIdentifier = "<b>";
    private static final int cancelLength = 12;

    private static ArrayList<IndexedDay> dayList;

    public static void parse(Document doc) throws IOException
    {
        if(dayList == null)
            dayList = new ArrayList<>();
        ArrayList<String> contentList = new ArrayList<>();

        /** convert from elem.toString() to a newLine-separated ArrayList **/
        for(Element elem : doc.getAllElements()) {
            String[] sArray = elem.toString().split("\n");
            for(String s : sArray)
                contentList.add(s.trim());
        }

        /** fix some weird bugs **/
        for(int i = 0; i < contentList.size(); i++)
        {
            if(contentList.get(i).equals("</html>")) {
                contentList = new ArrayList<>(contentList.subList(0, i));
                break;
            }
        }

        /** get and parse days **/
        int startDay = 0;
        int endDay = 0;

        for(int i = 0; i < contentList.size(); i++)
        {
            String currentLine = contentList.get(i);
            /** check for day identifier **/
            if(currentLine.startsWith("<p>") && currentLine.contains(dateIdentifier))
            {
                if (endDay != 0)
                {
                    endDay = i;
                    dayList.add(parseDay(new ArrayList<>(contentList.subList(startDay, endDay))));
                    startDay = i;
                }
                else
                {
                    endDay = i;
                    startDay = i;
                }
            }
            /** check for special case with first day **/
            else if(currentLine.startsWith("<b>") &&
                    contentList.get(i + 1).startsWith("<a href=") &&
                    contentList.get(i + 2).startsWith("<a href="))
            {
                if(endDay != 0)
                {
                    endDay = i;
                    dayList.add(parseDay(new ArrayList<>(contentList.subList(startDay, endDay))));
                    startDay = i;
                }
                else {
                    endDay = i;
                    startDay = i;
                }
            }
            /** check for special case with the last day **/
            else if(currentLine.startsWith("</tr>") &&
                    contentList.get(i + 1).startsWith("</tbody>") &&
                    contentList.get(i + 2).startsWith("</table>") &&
                    contentList.get(i + 3).startsWith("<p>") &&
                    contentList.get(i + 4).startsWith("</div></font>") &&
                    contentList.get(i + 5).startsWith("<font "))
            {
                endDay = i;
                dayList.add(parseDay(new ArrayList<>(contentList.subList(startDay, endDay))));
            }
        }
    }

    private static IndexedDay parseDay(ArrayList<String> contentList)
    {
        IndexedDay day = new IndexedDay();

        /** get current Day Namy**/
        String line = contentList.get(0);
        int start = 0, end = 0;
        for(int i = 0; i < line.length(); i++)
        {
            char[] lineCharArray = line.toCharArray();
            if(lineCharArray[i] == '<' && lineCharArray[i + 1] == 'b')
                start = i + 3;

            if(lineCharArray[i] == '<' && lineCharArray[i + 1] == '/' && lineCharArray[i + 2] == 'b')
                end = i;

        }
        day.setDayName(line.substring(start, end));

        /** check for valid cancel data **/
        boolean hasCancelContent = true;
        for(String s : contentList)
        {
            if(s.contains("Vertretungen sind nicht freigegeben")) {
                hasCancelContent = false;
                break;
            }
        }
        /** add data **/
        ArrayList<ArrayList<String>> inceptionList = new ArrayList<>();
        if(hasCancelContent)
        {
            for(int i = 0; i < contentList.size(); i++)
                if(contentList.get(i).startsWith("<tr class=\"list "))
                    inceptionList.add(new ArrayList<>(contentList.subList(i, i + cancelLength)));

            for(ArrayList<String> list : inceptionList) {
                IndexedCancel cancel = parseCancel(list);
                day.addCancel(cancel);
            }
        }

        /** attempt to auto-fix some known table issues **/
        for(int i = 0; i < day.getCancelList().size(); i++)
        {
            IndexedCancel currentCancel = day.getCancelList().get(i);
            if(currentCancel.getLessonNumber().equals(IndexedCancel.EMPTY_MESSAGE))
                if(!currentCancel.getComment().equals(IndexedCancel.EMPTY_MESSAGE))
                {
                    IndexedCancel fixedCancel = day.getCancelList().get(i - 1);
                    fixedCancel.setComment(day.getCancelList().get(i - 1).getComment() + currentCancel.getComment());
                    day.setCancel(fixedCancel, i - 1);
                    day.removeCancel(i);
                    i--;
                }
                else if(!currentCancel.getSubject().equals(IndexedCancel.EMPTY_MESSAGE))
                {
                    IndexedCancel fixedCancel = day.getCancelList().get(i - 1);
                    fixedCancel.setSubject(day.getCancelList().get(i - 1).getSubject() + currentCancel.getSubject());
                    day.setCancel(fixedCancel, i - 1);
                    day.removeCancel(i);
                    i--;
                }
                else if(!currentCancel.getTeacher().equals(IndexedCancel.EMPTY_MESSAGE))
                {
                    IndexedCancel fixedCancel = day.getCancelList().get(i - 1);
                    fixedCancel.setTeacher(day.getCancelList().get(i - 1).getTeacher() + currentCancel.getTeacher());
                    day.setCancel(fixedCancel, i - 1);
                    day.removeCancel(i);
                    i--;
                }
                else if(!currentCancel.getType().equals(IndexedCancel.EMPTY_MESSAGE))
                {
                    IndexedCancel fixedCancel = day.getCancelList().get(i - 1);
                    fixedCancel.setType(day.getCancelList().get(i - 1).getType() + currentCancel.getType());
                    day.setCancel(fixedCancel, i - 1);
                    day.removeCancel(i);
                    i--;
                }
        }

        return day;
    }

    private static IndexedCancel parseCancel(ArrayList<String> contentList)
    {
        IndexedCancel cancel = new IndexedCancel();

        int vCount = 0;
        for(String s : contentList)
        {
            int start = s.indexOf(">");
            int end = s.indexOf("</");
            String value;
            if(start > 0 && end > 0)
            {
                value = s.substring(start + 1, end);
                cancel.autoAssign(vCount, value);
                vCount++;
            }
        }
        return cancel;
    }

    public static ArrayList<IndexedDay> getDayList() {
        return dayList;
    }
    public static IndexedDay getDay(int index)
    {
        return dayList.get(index);
    }
}