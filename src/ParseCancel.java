import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
    Parser Class to convert collected HTML data to usable data
 */

public class ParseCancel
{
    private static final int cancelLength = 11;
    private static ArrayList<IndexedDay> dayList;

    public static void parse(Document doc) throws IOException
    {
        if(dayList == null)
            dayList = new ArrayList<>();

        /** convert from elem.toString() to a '\n'-separated ArrayList **/
        ArrayList<String> contentList = new ArrayList<>();
        for(String s : doc.toString().split("\n"))
                contentList.add(s.trim());

        /** get and parse days **/
        int start = 0;
        int end = 0;
        for(int i = 0; i < contentList.size(); i++)
        {
            String currentLine = contentList.get(i);
            if((currentLine.startsWith("<p> <a name=") && !contentList.get(i + 1).startsWith("<br")) || (currentLine.startsWith("<b>") && currentLine.endsWith("</b> |")))
                start = i;

            else if(currentLine.startsWith("</table") && (contentList.get(i + 1).startsWith("<p> <a name=") || contentList.get(i + 6).startsWith("</html>")))
                end = i;

            if(start != 0 && end != 0)
            {
                dayList.add(parseDay(contentList.subList(start, end)));
                start = 0;
                end = 0;
            }
        }
    }

    private static IndexedDay parseDay(List<String> contentList)
    {
        IndexedDay day = new IndexedDay();

        /** get current Day Name **/
        String line = contentList.get(0);
        char[] carr = line.toCharArray();
        int start = -1, end = -1;
        for(int i = 0; i < line.length(); i++)
        {
            if(carr[i] == '<' && carr[i + 1] == 'b' && carr[i + 2] == '>')
                start = (i + 3);
            else if(carr[i] == '<' && carr[i + 1] == '/' && carr[i + 2] == 'b' && carr [i + 3] == '>')
                end = i;
            if(start != -1 && end != -1)
                break;
        }
        day.setDayName(line.substring(start, end));

        /** add data **/

        if(!(contentList.get(contentList.size() - 1).contains("Vertretungen sind nicht freigegeben"))) //make sure cancel data is valid
        {
            int j = 0;
            for (int i = 0; i < contentList.size(); i++)
                if (contentList.get(i).startsWith("<tr class=\"list ")) {
                    IndexedCancel cancel = parseCancel(new ArrayList<>(contentList.subList(i + 1, i + cancelLength)));

                    /** attempt to auto-fix some known table issues **/
                    if (cancel.getLessonNumber().equals(IndexedCancel.EMPTY_MESSAGE))
                    {
                        IndexedCancel fixedCancel = day.getCancelList().get(j - 1);
                        if (!cancel.getComment().equals(IndexedCancel.EMPTY_MESSAGE))       { fixedCancel.setComment(fixedCancel.getComment() + " " + cancel.getComment()); }
                        if (!cancel.getSubject().equals(IndexedCancel.EMPTY_MESSAGE))       { fixedCancel.setSubject(fixedCancel.getSubject() + cancel.getSubject()); }
                        if (!cancel.getCoverSubject().equals(IndexedCancel.EMPTY_MESSAGE))  { fixedCancel.setCoverSubject(fixedCancel.getCoverTeacher() + cancel.getCoverSubject()); }
                        if (!cancel.getTeacher().equals(IndexedCancel.EMPTY_MESSAGE))       { fixedCancel.setTeacher(fixedCancel.getTeacher() + cancel.getTeacher()); }
                        if (!cancel.getType().equals(IndexedCancel.EMPTY_MESSAGE))          { fixedCancel.setType(fixedCancel.getType() + " " + cancel.getType()); }
                        day.setCancel(fixedCancel, j - 1);
                    }
                    else
                        day.addCancel(cancel);
                    j = day.getCancelList().size();
                }
        }
        return day;
    }

    private static IndexedCancel parseCancel(ArrayList<String> content)
    {
        ArrayList<String> values = new ArrayList<>();

        for (String s : content)
        {
            int start = s.indexOf(">");
            int end = s.indexOf("</td>");

            if (start != -1 && end != -1)
            {
                String value = s.substring(start + 1, end);

                if (value.startsWith("<b>") && value.endsWith("</b>"))
                    value = value.substring(3, value.length() - 4);

                values.add(value);
            }
        }

        return new IndexedCancel(values);
    }

    public static ArrayList<IndexedDay> getDayList() {
        return dayList;
    }
    public static IndexedDay getDay(int index)
    {
        return dayList.get(index);
    }
}