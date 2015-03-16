import java.util.ArrayList;

public class IndexedCancel
{

    private static final int LESSONCOUNT_ID = 0;
    private static final int CLASS_ID = 1;
    private static final int TEACHER_ID = 2;
    private static final int COVERTEACHER_ID = 3;
    private static final int SUBJECT_ID = 4;
    private static final int COVERSUBJECT_ID = 5;
    private static final int ROOM_ID = 6;
    private static final int ALTERNATIVEROOM_ID = 7;
    private static final int TYPE_ID = 8;
    private static final int COMMENT_ID = 9;

    public static final String EMPTY_MESSAGE = "---";

    private String lessonNumber;
    private String date;
    private String className;
    private String teacher;
    private String subject;
    private String coverSubject;
    private String coverTeacher;
    private String room;
    private String alternativeRoom;
    private String type;
    private String comment;

    public IndexedCancel(ArrayList<String> values)
    {
        for(int i = 0; i < values.size(); i++)
            autoAssign(i, values.get(i));
    }

    public void autoAssign(int id, String value)
    {
        assert(id > 0 || id < 10) : ("Attribute count: " + id + " is not supported");

        if(value.equals("&nbsp;"))
            value = EMPTY_MESSAGE;

        if(value.contains("&") && value.contains(";"))
            value = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml3(value);

        switch (id)
        {
            case LESSONCOUNT_ID:
                lessonNumber = value; break;
            case CLASS_ID:
                className = value; break;
            case TEACHER_ID:
                teacher = value; break;
            case SUBJECT_ID:
                subject = value; break;
            case COVERTEACHER_ID:
                coverTeacher = value; break;
            case COVERSUBJECT_ID:
                coverSubject = value; break;
            case ROOM_ID:
                room = value; break;
            case ALTERNATIVEROOM_ID:
                alternativeRoom = value; break;
            case TYPE_ID:
                type = value; break;
            case COMMENT_ID:
                comment = value; break;

            default: break;
        }
    }

    @Override
    public String toString()
    {
        return "[Lesson: " + lessonNumber + " | Date: " + date +
                " | Class: " + className + " | Teacher: " + teacher + " | Subject: " + subject +
                " | Cover Subject: " + coverSubject + " | Cover Teacher: " + coverTeacher +
                " | Room: " + room + " | Alt Room: " + alternativeRoom +
                " | Type: " + type + " | Comment: " + comment + "]";
    }

    public String getLessonNumber() {
        return lessonNumber;
    }
    public void setLessonNumber(String lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacher() {
        return teacher;
    }
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCoverSubject() {
        return coverSubject;
    }
    public void setCoverSubject(String coverSubject) {
        this.coverSubject = coverSubject;
    }

    public String getCoverTeacher() {
        return coverTeacher;
    }
    public void setCoverTeacher(String coverTeacher) {
        this.coverTeacher = coverTeacher;
    }

    public String getRoom() {
        return room;
    }
    public void setRoom(String room) {
        this.room = room;
    }

    public String getAlternativeRoom() {
        return alternativeRoom;
    }
    public void setAlternativeRoom(String alternativeRoom) {
        this.alternativeRoom = alternativeRoom;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
