package Library.LibraryGUI;

import Library.Utils.JDBCUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchAndBorrowPanel extends JPanel {

    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField publisherField;
    private JTextField publishDateField;
    private JTextField typeField;
    private JTextField editionNumberField; // 新增字段
    private JComboBox<String> sortCombo;

    private JTable resultTable;
    private DefaultTableModel tableModel;

    public SearchAndBorrowPanel() {
        setLayout(new BorderLayout());

        // 创建输入面板
        JPanel inputPanel = createInputPanel();

        // 创建表格面板
        JPanel tablePanel = createTablePanel();

        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();

        // 添加组件到主面板
        add(inputPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        Font labelFont = new Font("楷体", Font.PLAIN, 20);
        Font fieldFont = new Font("楷体", Font.PLAIN, 18);

        JLabel isbnLabel = new JLabel("ISBN:");
        JLabel titleLabel = new JLabel("书名:");
        JLabel authorLabel = new JLabel("作者:");
        JLabel publisherLabel = new JLabel("出版社:");
        JLabel publicationDateLabel = new JLabel("出版日期:");
        JLabel typeLabel = new JLabel("种类:");
        JLabel editionNumberLabel = new JLabel("版次:"); // 新增标签
        JLabel sortLabel = new JLabel("排序方式:");

        isbnField = new JTextField(20);
        titleField = new JTextField(20);
        authorField = new JTextField(20);
        publisherField = new JTextField(20);
        publishDateField = new JTextField(20);
        typeField = new JTextField(20);
        editionNumberField = new JTextField(20); // 新增输入框
        sortCombo = new JComboBox<>(new String[]{"ISBN", "书名", "出版日期", "版次"}); // 新增选项

        // 设置字体
        for (Component c : new Component[]{
                isbnLabel, titleLabel, authorLabel, publisherLabel,
                publicationDateLabel, typeLabel, editionNumberLabel, sortLabel,
                isbnField, titleField, authorField, publisherField,
                publishDateField, typeField, editionNumberField, sortCombo}) {
            if (c instanceof JLabel || c instanceof JTextField || c instanceof JComboBox) {
                c.setFont(fieldFont);
            }
        }

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);

        // ISBN 行
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(isbnLabel, gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);

        // 书名行
        gbc.gridx = 2;
        panel.add(titleLabel, gbc);
        gbc.gridx = 3;
        panel.add(titleField, gbc);

        // 作者行
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(authorLabel, gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        // 出版社行
        gbc.gridx = 2;
        panel.add(publisherLabel, gbc);
        gbc.gridx = 3;
        panel.add(publisherField, gbc);

        // 出版日期行
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(publicationDateLabel, gbc);
        gbc.gridx = 1;
        panel.add(publishDateField, gbc);

        // 类型行
        gbc.gridx = 2;
        panel.add(typeLabel, gbc);
        gbc.gridx = 3;
        panel.add(typeField, gbc);

        // 版次行
        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(editionNumberLabel, gbc);
        gbc.gridx = 1;
        panel.add(editionNumberField, gbc);

        // 排序方式行
        gbc.gridy = 3; gbc.gridx = 2;
        panel.add(sortLabel, gbc);
        gbc.gridx = 3;
        panel.add(sortCombo, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ISBN", "书名", "作者", "出版社", "版次", "出版日期", "类型"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(30);
        resultTable.setFont(new Font("微软雅黑", Font.PLAIN, 17));

        // 设置表头字体更大
        resultTable.getTableHeader().setFont(new Font("微软雅黑", Font.PLAIN, 20));

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 边距

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton searchBtn = new JButton("查询");
        JButton borrowButton = new JButton("借阅");

        Font btnFont = new Font("楷体", Font.PLAIN, 20);
        searchBtn.setFont(btnFont);
        borrowButton.setFont(btnFont);

        searchBtn.addActionListener(this::searchBook);
        borrowButton.addActionListener(e -> handleBorrow());

        panel.add(searchBtn);
        panel.add(borrowButton);

        return panel;
    }

    private void searchBook(ActionEvent e) {
        String sql = "SELECT * FROM books WHERE 1=1";
        List<String> params = new ArrayList<>();

        if (!isbnField.getText().trim().isEmpty()) {
            sql += " AND ISBN LIKE ?";
            params.add("%" + isbnField.getText().trim() + "%");
        }
        if (!titleField.getText().trim().isEmpty()) {
            sql += " AND Title LIKE ?";
            params.add("%" + titleField.getText().trim() + "%");
        }
        if (!authorField.getText().trim().isEmpty()) {
            sql += " AND Authors LIKE ?";
            params.add("%" + authorField.getText().trim() + "%");
        }
        if (!publisherField.getText().trim().isEmpty()) {
            sql += " AND Publisher LIKE ?";
            params.add("%" + publisherField.getText().trim() + "%");
        }
        if (!publishDateField.getText().trim().isEmpty()) {
            sql += " AND PublicationDate LIKE ?";
            params.add("%" + publishDateField.getText().trim() + "%");
        }
        if (!typeField.getText().trim().isEmpty()) {
            sql += " AND Type LIKE ?";
            params.add("%" + typeField.getText().trim() + "%");
        }
        if (!editionNumberField.getText().trim().isEmpty()) { // 新增条件
            sql += " AND EditionNumber LIKE ?";
            params.add("%" + editionNumberField.getText().trim() + "%");
        }

        String sortBy = changeName((String) sortCombo.getSelectedItem());
        if (sortBy != null) {
            sql += " ORDER BY " + sortBy;
        }

        executeQuery(sql, params);
    }

    private void executeQuery(String sql, List<String> params) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            rs = ps.executeQuery();

            tableModel.setRowCount(0); // 清空表格

            while (rs.next()) {
                Object[] row = new Object[tableModel.getColumnCount()];
                row[0] = rs.getString("ISBN");
                row[1] = rs.getString("Title");
                row[2] = rs.getString("Authors");
                row[3] = rs.getString("Publisher");
                row[4] = rs.getString("EditionNumber"); // 填充版次
                row[5] = rs.getString("PublicationDate");
                row[6] = rs.getString("Type");
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                throw new Exception("未找到符合条件的书籍");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } finally {
            JDBCUtil.close(conn, ps, rs);
        }
    }

    private void handleBorrow() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一本书");
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 0);

        // 弹出输入读者ID的对话框
        String readerId = JOptionPane.showInputDialog(this, "请输入读者ID：");
        if (readerId == null || readerId.trim().isEmpty()) {
            return;
        }

        borrowBook(isbn, readerId.trim());
    }

    private void borrowBook(String isbn, String readerId) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = JDBCUtil.getConnection();
            JDBCUtil.startTransaction(conn);

            checkIsBorrowed(conn, isbn);
            checkReaderLimit(conn, readerId);
            insertBorrowRecord(conn, isbn, readerId);

            JDBCUtil.commitTransaction(conn);
            JOptionPane.showMessageDialog(this, "借书成功！");
        } catch (Exception e) {
            JDBCUtil.rollbackTransaction(conn);
            JOptionPane.showMessageDialog(this, e.getMessage());
        } finally {
            JDBCUtil.close(conn, ps, null);
        }
    }

    private void checkIsBorrowed(Connection conn, String isbn) throws Exception {
        String sql = "SELECT * FROM record WHERE ISBN = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                throw new RuntimeException("该书已被借出");
            }
        }
    }

    private void checkReaderLimit(Connection conn, String readerId) throws Exception {
        String limitSQL = "SELECT Limits FROM reader WHERE ReaderID = ?";
        String countSQL = "SELECT COUNT(*) AS Current FROM record WHERE ReaderID = ?";

        try (PreparedStatement limitStmt = conn.prepareStatement(limitSQL);
             PreparedStatement countStmt = conn.prepareStatement(countSQL)) {

            limitStmt.setString(1, readerId);
            ResultSet limitRs = limitStmt.executeQuery();
            if (!limitRs.next()) {
                throw new RuntimeException("读者不存在");
            }
            int limit = limitRs.getInt("Limits");

            countStmt.setString(1, readerId);
            ResultSet countRs = countStmt.executeQuery();
            countRs.next();
            int current = countRs.getInt("Current");

            if (current >= limit) {
                throw new RuntimeException("已超过您的最大借阅数目");
            }
        }
    }

    private void insertBorrowRecord(Connection conn, String isbn, String readerId) throws Exception {
        String sql = "INSERT INTO record (ISBN, ReaderID, ReturnDate) VALUES (?, ?, DATE_ADD(CURDATE(), INTERVAL 14 DAY))";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.setString(2, readerId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected <= 0) {
                throw new RuntimeException("借书失败");
            }
        }
    }

    private String changeName(String name) {
        return switch (name) {
            case "书名" -> "Title";
            case "出版日期" -> "PublicationDate";
            case "版次" -> "EditionNumber"; // 新增排序字段
            default -> null;
        };
    }
}
