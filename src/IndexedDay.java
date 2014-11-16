import java.util.ArrayList;

/**
 * Created by Phil on 30.10.14.
 */
public class IndexedDay
{
    private ArrayList<IndexedCancel> cancelList;

    private String dayName;

    public IndexedDay(ArrayList<IndexedCancel> cancelList)
    {
        this.cancelList = cancelList;
    }

    public IndexedDay()
    {
        cancelList = new ArrayList<>();
    }

    @Override
    public String toString()
    {
        if(cancelList.size() > 0) {
            String returnString = "Cancels: \n";
            for (IndexedCancel c : cancelList)
                returnString += (c.toString() + " \n");
            return returnString;
        }
        else
            return "No plan released";
    }

    public ArrayList<IndexedCancel> getCancelList() {
        return cancelList;
    }
    public void setCancelList(ArrayList<IndexedCancel> cancelList) {
        this.cancelList = cancelList;
    }

    public ArrayList<IndexedCancel> getCancelByClass(String className)
    {
        ArrayList<IndexedCancel> returnList = new ArrayList<>();
        for(IndexedCancel c : cancelList)
            if(c.getClassName().equals(className))
                returnList.add(c);

        return returnList;
    }

    public void setDayName(String dayName)
    {
        this.dayName = dayName;
    }
    public String getDayName()
    {
        return dayName;
    }

    public void addCancel(IndexedCancel cancel)
    {
        cancelList.add(cancel);
    }
    public void setCancel(IndexedCancel cancel, int index)
    {
        cancelList.set(index, cancel);
    }
    public void removeCancel(int index)
    {
        cancelList.remove(index);
    }
}
