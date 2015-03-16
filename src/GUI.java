import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

/*
    GUI Class to offer a graphical presentation of parsed data
 */

public class GUI extends JFrame
{
    private static final int TABLE_WIDTH = 1000, TABLE_HEIGHT = 300;

    private JPanel controls;
    private JComboBox<String> selectClass;
    private JComboBox<String> selectDay;

    private JPanel graphics;
    private JTable cancelTable;
    private JScrollPane scrollPane;

    public GUI()
    {
        setTitle("Cancel Table Reader");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        controls = new JPanel();
        graphics = new JPanel();

        generateGraphics();
        generateControls();

        add(controls);
        add(graphics);

        setResizable(false);

        render();
    }

    private void render()
    {
        pack();
        setVisible(true);
    }

    private void generateGraphics()
    {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No Info", new String[]{});
        cancelTable = new JTable(model);
        cancelTable.setPreferredScrollableViewportSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        scrollPane = new JScrollPane(cancelTable);
        graphics.add(scrollPane);
    }

    private void generateControls()
    {
        selectClass = new JComboBox<>();
        for(int i = 5; i <= 13; i++)
            selectClass.addItem("Class " + i);
        selectClass.addItem("Special");
        selectClass.addItem("All Classes");
        selectClass.setSelectedIndex(selectClass.getItemCount() - 1);
        selectClass.addActionListener(new ACTListener());

        selectDay = new JComboBox<>();
        for(int i = 0; i < ParseCancel.getDayList().size(); i++)
            selectDay.addItem(ParseCancel.getDayList().get(i).getDayName());

        selectDay.addActionListener(new ACTListener());

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1; //get(Calendar.DAY_OF_WEEK) starts with SUNDAY as 1
        if(calendar.get(Calendar.HOUR_OF_DAY) >= 18)
            currentDay++;
        if(selectDay.getItemCount() >= currentDay)
            selectDay.setSelectedIndex(currentDay - 1); //index starts at 0
        else
            selectDay.setSelectedIndex(0);

        controls.add(selectClass);
        controls.add(selectDay);
    }

    class ACTListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() instanceof JComboBox &&
                    (e.getSource().equals(selectDay) || e.getSource().equals(selectClass)))
            {
                IndexedDay day = ParseCancel.getDay(selectDay.getSelectedIndex());
                ArrayList<IndexedCancel> cancelList;

                if(selectClass.getSelectedItem().equals("All Classes"))
                    cancelList = day.getCancelList();
                else if(selectClass.getSelectedItem().equals("Special"))
                    cancelList = day.getCancelByClass(IndexedCancel.EMPTY_MESSAGE);
                else
                    cancelList = day.getCancelByClass(String.valueOf(selectClass.getSelectedItem().toString().substring("Class ".length())));

                graphics.remove(scrollPane);
                if(!cancelList.isEmpty()) {
                    int rowCount = 0;
                    String[][] data = new String[cancelList.size()][10];
                    for (IndexedCancel c : cancelList) {
                        data[rowCount][0] = c.getLessonNumber();
                        data[rowCount][1] = c.getClassName();
                        data[rowCount][2] = c.getTeacher();
                        data[rowCount][3] = c.getCoverTeacher();
                        data[rowCount][4] = c.getSubject();
                        data[rowCount][5] = c.getCoverSubject();
                        data[rowCount][6] = c.getRoom();
                        data[rowCount][7] = c.getAlternativeRoom();
                        data[rowCount][8] = c.getType();
                        data[rowCount][9] = c.getComment();
                        rowCount++;
                    }
                    cancelTable = new JTable(new CancelTableModel(data));
                }
                else
                    cancelTable = new JTable(new DefaultTableModel(new String[1][1], new String[]{"No Info"}));

                cancelTable.setPreferredScrollableViewportSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
                cancelTable.setFillsViewportHeight(true);
                cancelTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                scrollPane = new JScrollPane(cancelTable);
                graphics.add(scrollPane);
                render();
            }
        }
    }

    class CancelTableModel extends AbstractTableModel
    {
        private String[] columnNames = new String[]{
                "Lesson",
                "Class",
                "Teacher",
                "Subject",
                "Cover Teacher",
                "Cover Subject",
                "Room",
                "Alt Room",
                "Type",
                "Comment"
        };

        private Object[][] data;

        public CancelTableModel(String[][] data) {
            this.data = data;
        }

        public int getRowCount() {
            return data.length;
        }
        public int getColumnCount() {
            return columnNames.length;
        }
        public Class getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }
    }
}