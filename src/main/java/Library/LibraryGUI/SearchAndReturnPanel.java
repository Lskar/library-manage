package Library.LibraryGUI;

import Library.Utils.JDBCUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchAndReturnPanel extends JPanel {

    private JTextField readerField;
    private JTable recordTable;
    private DefaultTableModel tableModel;

    public SearchAndReturnPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 初始化界面组件
        JPanel inputPanel = createInputPanel();
        JPanel tablePanel = createTablePanel();
        JPanel buttonPanel = createReturnButtonPanel();

        add(inputPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JLabel label = new JLabel("读者 ID:");
        label.setFont(new Font("宋体", Font.BOLD, 20));
        readerField = new JTextField(15);
        readerField.setFont(new Font("宋体", Font.PLAIN, 20));
        JButton searchBtn = new JButton("查询");
        searchBtn.setFont(new Font("楷体", Font.BOLD, 20));

        searchBtn.addActionListener(this::searchRecords);

        panel.add(label);
        panel.add(readerField);
        panel.add(searchBtn);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"RecordID", "ISBN", "ReaderID", "BorrowingDate", "ReturnDate"};
        tableModel = new DefaultTableModel(columnNames, 0);
        recordTable = new JTable(tableModel);
        recordTable.setRowHeight(35);
        recordTable.setFont(new Font("微软雅黑", Font.PLAIN, 18));

        JTableHeader header = recordTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        JScrollPane scrollPane = new JScrollPane(recordTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("借阅记录"));
        scrollPane.setPreferredSize(new Dimension(800, 300));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReturnButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton returnButton = new JButton("还书");
        returnButton.setFont(new Font("楷体", Font.BOLD, 20));
        returnButton.addActionListener(this::handleReturnBook);

        panel.add(returnButton);
        return panel;
    }

    private void searchRecords(ActionEvent e) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String readerId = readerField.getText().trim();
        if (readerId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入读者 ID！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT * FROM record WHERE ReaderID = ?";

        try {
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, readerId);
            rs = ps.executeQuery();

            tableModel.setRowCount(0); // 清空旧数据

            while (rs.next()) {
                Object[] row = new Object[tableModel.getColumnCount()];
                row[0] = rs.getObject("RecordID");
                row[1] = rs.getString("ISBN");
                row[2] = rs.getString("ReaderID");
                row[3] = rs.getDate("BorrowingDate");
                row[4] = rs.getDate("ReturnDate");
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                throw new Exception("未找到该读者的借阅记录或所有书籍均已归还");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "查询失败", JOptionPane.ERROR_MESSAGE);
        } finally {
            JDBCUtil.close(conn, ps, rs);
        }
    }

    private void handleReturnBook(ActionEvent e) {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条借阅记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 1);
        String readerId = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, "确认要删除 ISBN 为 " + isbn + " 的借阅记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = JDBCUtil.getConnection();
            JDBCUtil.startTransaction(conn);

            String sql = "DELETE FROM record WHERE ISBN = ? AND ReaderID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, isbn);
            ps.setString(2, readerId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                tableModel.removeRow(selectedRow); // 刷新表格
            } else {
                throw new RuntimeException("删除失败，请重试。");
            }

            JDBCUtil.commitTransaction(conn);
        } catch (SQLException ex) {
            JDBCUtil.rollbackTransaction(conn);
            JOptionPane.showMessageDialog(this, "数据库错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            JDBCUtil.close(conn, ps, null);
        }
    }
}
