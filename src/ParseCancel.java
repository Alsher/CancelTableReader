import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class ParseCancel
{
    private static final int cancelLength = 12;
    private static ArrayList<IndexedDay> dayList;

    public static void parse(Document doc) throws IOException
    {
        if(dayList == null)
            dayList = new ArrayList<>();

        /** convert from elem.toString() to a '\n'-separated ArrayList **/
        ArrayList<String> contentList = new ArrayList<>();
        for(String s : doc.body().toString().split("\n"))
                contentList.add(s.trim());

        /** get and parse days **/
        int startDay = 0;
        int endDay = 0;
        for(int i = 0; i < contentList.size(); i++)
        {
            String currentLine = contentList.get(i);
            /** check for day identifier **/
            if(currentLine.startsWith("<p>") && currentLine.contains("<b>"))
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
                else
                {
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

        /** get current Day Name **/
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

        /** add data **/
        int j = 0;
        if(!(contentList.get(contentList.size() - 1).contains("Vertretungen sind nicht freigegeben"))) //make sure cancel data is valid
            for(int i = 0; i < contentList.size(); i++)
                if(contentList.get(i).startsWith("<tr class=\"list "))
                {
                    IndexedCancel cancel = parseCancel(new ArrayList<>(contentList.subList(i, i + cancelLength)));
                    /** attempt to auto-fix some known table issues **/
                    boolean shouldAdd = true;
                    if(cancel.getLessonNumber().equals(IndexedCancel.EMPTY_MESSAGE))
                    {
                        shouldAdd = false;
                        IndexedCancel fixedCancel = day.getCancelList().get(j - 1);
                        if(!cancel.getComment().equals(IndexedCancel.EMPTY_MESSAGE))
                            fixedCancel.setComment(day.getCancelList().get(j - 1).getComment() + cancel.getComment());
                        else if (!cancel.getSubject().equals(IndexedCancel.EMPTY_MESSAGE))
                            fixedCancel.setSubject(day.getCancelList().get(j - 1).getSubject() + cancel.getSubject());
                        else if (!cancel.getTeacher().equals(IndexedCancel.EMPTY_MESSAGE))
                            fixedCancel.setTeacher(day.getCancelList().get(j - 1).getTeacher() + cancel.getTeacher());
                        else if (!cancel.getType().equals(IndexedCancel.EMPTY_MESSAGE))
                            fixedCancel.setType(day.getCancelList().get(j - 1).getType() + cancel.getType());
                        else
                            shouldAdd = true;
                        day.setCancel(fixedCancel, j - 1);
                    }
                    if(shouldAdd)
                        day.addCancel(cancel);
                    j = day.getCancelList().size();
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