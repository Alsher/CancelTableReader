import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Phil on 01.11.14.
 */
public class GUI extends JFrame
{
    private static final int TABLEWIDTH = 825, TABLEHEIGHT = 300;

    private JPanel controlls;
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

        controlls = new JPanel();
        graphics = new JPanel();

        generateControlls();
        generateGraphics();

        add(controlls);
        add(graphics);

        setResizable(false);

        render();
    }

    private void render()
    {
        pack();
        setVisible(true);
    }

    private void generateControlls()
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

        controlls.add(selectClass);
        controlls.add(selectDay);
    }

    private void generateGraphics()
    {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No Info", new String[]{});
        cancelTable = new JTable(model);
        cancelTable.setPreferredScrollableViewportSize(new Dimension(TABLEWIDTH, TABLEHEIGHT));
        scrollPane = new JScrollPane(cancelTable);
        graphics.add(scrollPane);
    }

    class ACTListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() instanceof JComboBox)
            {
                JComboBox sourceBox = (JComboBox) e.getSource();
                if(sourceBox.equals(selectClass) || sourceBox.equals(selectDay))
                {
                    IndexedDay day = ParseCancel.getDay(selectDay.getSelectedIndex());
                    ArrayList<IndexedCancel> cancelList;

                    if(selectClass.getSelectedItem().equals("All Classes"))
                        cancelList = day.getCancelList();
                    else if(selectClass.getSelectedItem().equals("Special"))
                        cancelList = day.getCancelByClass(IndexedCancel.EMPTY_MESSAGE);
                    else
                        cancelList = day.getCancelByClass(String.valueOf(selectClass.getSelectedItem().toString().substring("Class ".length())));

                    if(!cancelList.isEmpty())
                    {
                        int rowCount = 0;
                        String[][] data = new String[cancelList.size()][11];
                        for(IndexedCancel c : cancelList) {
                            data[rowCount][0] = c.getLessonNumber();
                            data[rowCount][1] = c.getDate();
                            data[rowCount][2] = c.getClassName();
                            data[rowCount][3] = c.getTeacher();
                            data[rowCount][4] = c.getSubject();
                            data[rowCount][5] = c.getCoverSubject();
                            data[rowCount][6] = c.getCoverTeacher();
                            data[rowCount][7] = c.getRoom();
                            data[rowCount][8] = c.getAlternativeRoom();
                            data[rowCount][9] = c.getType();
                            data[rowCount][10] = c.getComment();
                            rowCount++;
                        }

                        graphics.remove(scrollPane);
                        cancelTable = new JTable(new CancelTableModel(data));
                        cancelTable.setPreferredScrollableViewportSize(new Dimension(TABLEWIDTH, TABLEHEIGHT));
                        cancelTable.setFillsViewportHeight(true);
                        cancelTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        scrollPane = new JScrollPane(cancelTable);
                        graphics.add(scrollPane);
                        render();
                    }
                    else
                    {
                        graphics.remove(scrollPane);
                        DefaultTableModel model = new DefaultTableModel();
                        model.addColumn("No Info", new String[]{});
                        cancelTable = new JTable(model);
                        cancelTable.setPreferredScrollableViewportSize(new Dimension(TABLEWIDTH, TABLEHEIGHT));
                        cancelTable.setFillsViewportHeight(true);
                        cancelTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        scrollPane = new JScrollPane(cancelTable);
                        graphics.add(scrollPane);
                        render();
                    }
                }
            }
        }
    }

    class CancelTableModel extends AbstractTableModel
    {
        private String[] columnNames = new String[]{
                "Lesson",
                "Date",
                "Class",
                "Teacher",
                "Subject",
                "Cover Subject",
                "Cover Teacher",
                "Room",
                "Alt Room",
                "Type",
                "Comment"
        };

        private Object[][] data;

        public CancelTableModel(String[][] data)
        {
            this.data = data;
        }

        public int getRowCount() {
            return data.length;
        }
        public int getColumnCount() {
            return columnNames.length;
        }
        public Class getColumnClass(int columnIndex)
        {
            return getValueAt(0, columnIndex).getClass();
        }
        public String getColumnName(int columnIndex)
        {
            return columnNames[columnIndex];
        }
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }
    }
}
