package Library.LibraryGUI;

import Library.Utils.JDBCUtil;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class AddAndUpdateBookPanel {

    private JPanel inputPanel = new JPanel();
    private JPanel addPanel = new JPanel(new GridBagLayout());
    private JPanel updatePanel = new JPanel(new GridBagLayout());

    // 添加部分字段
    private JTextField isbnField = new JTextField(20);
    private JTextField titleField = new JTextField(20);
    private JTextField authorField = new JTextField(20);
    private JTextField publisherField = new JTextField(20);
    private JTextField editionNumberField = new JTextField(20);
    private JTextField publishDateField = new JTextField(20);
    private JTextField typeField = new JTextField(20);

    // 修改部分字段
    private JTextField updateISBNField = new JTextField(20);
    private JTextField updateContentField = new JTextField(20);
    private JComboBox<String> updateCombo = new JComboBox<>(new String[]{"书名", "作者", "出版社", "编辑", "出版日期", "种类"});

    // 标签
    private JLabel isbnLabel = new JLabel("ISBN:");
    private JLabel titleLabel = new JLabel("书名:");
    private JLabel authorLabel = new JLabel("作者:");
    private JLabel publisherLabel = new JLabel("出版社:");
    private JLabel editionNumberLabel = new JLabel("版次:");
    private JLabel publicationDateLabel = new JLabel("出版日期:");
    private JLabel typeLabel = new JLabel("种类:");

    private JLabel updateISBNLabel = new JLabel("请输入ISBN:");
    private JLabel updateContentLabel = new JLabel("修改的内容:");
    private JLabel updatePropertyLabel = new JLabel("修改的属性:");

    private JButton addButton = new JButton("添加");
    private JButton updateButton = new JButton("修改");
    private JButton deleteButton = new JButton("删除");

    // 字体
    private Font labelFont = new Font("楷体", Font.PLAIN, 24);   // 标签字体更大
    private Font fieldFont = new Font("楷体", Font.PLAIN, 20);   // 输入框字体略小
    private Font titleFont = new Font("楷体", Font.BOLD, 26);    // 面板标题字体

    public AddAndUpdateBookPanel() {
        initializeUI();
    }

    private void initializeUI() {
        inputPanel.setLayout(new BorderLayout(15, 15));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 设置标签字体并居中
        for (Component c : new Component[]{
                isbnLabel, titleLabel, authorLabel, publisherLabel,
                editionNumberLabel, publicationDateLabel, typeLabel,
                updateISBNLabel, updateContentLabel, updatePropertyLabel}) {
            if (c instanceof JLabel) {
                ((JLabel)c).setFont(labelFont);
                ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
            }
        }

        // 设置输入组件字体
        for (Component c : new Component[]{
                isbnField, titleField, authorField, publisherField,
                editionNumberField, publishDateField, typeField,
                updateISBNField, updateContentField, updateCombo}) {
            c.setFont(fieldFont);
        }

        // ===== 添加区域 =====
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // 第一行：ISBN + 书名
        addPair(addPanel, gbc, row++, isbnLabel, isbnField, titleLabel, titleField);

        // 第二行：作者 + 出版社
        addPair(addPanel, gbc, row++, authorLabel, authorField, publisherLabel, publisherField);

        // 第三行：版次 + 出版日期
        addPair(addPanel, gbc, row++, editionNumberLabel, editionNumberField, publicationDateLabel, publishDateField);

        // 第四行：种类 + 空白
        addPair(addPanel, gbc, row++, typeLabel, typeField);

        // 添加按钮（单独处理使其居中）
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        JButton smallAddButton = new JButton("添加");
        smallAddButton.setFont(new Font("楷体", Font.PLAIN, 24));
        smallAddButton.setMaximumSize(new Dimension(150, 40));
        smallAddButton.addActionListener(this::addBook);

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonWrapper.add(smallAddButton);
        addPanel.add(buttonWrapper, gbc);

        // ===== 修改区域 =====
        GridBagConstraints gbcUpdate = new GridBagConstraints();
        gbcUpdate.insets = new Insets(10, 20, 10, 20);
        gbcUpdate.anchor = GridBagConstraints.CENTER;
        gbcUpdate.fill = GridBagConstraints.HORIZONTAL;

        int row2 = 0;

        // 第一行：输入 ISBN
        addPair(updatePanel, gbcUpdate, row2++, updateISBNLabel, updateISBNField);

        // 第二行：输入内容 + 下拉选择
        gbcUpdate.gridx = 0;
        gbcUpdate.gridy = row2;
        updatePanel.add(updateContentLabel, gbcUpdate);

        gbcUpdate.gridx = 1;
        updatePanel.add(updateContentField, gbcUpdate);

        gbcUpdate.gridx = 2;
        updateCombo.setFont(fieldFont);
        updatePanel.add(updateCombo, gbcUpdate);
        row2++;

        // 修改和删除按钮放在同一行
        gbcUpdate.gridx = 0;
        gbcUpdate.gridy = row2;
        gbcUpdate.gridwidth = 3;
        gbcUpdate.anchor = GridBagConstraints.CENTER;
        gbcUpdate.fill = GridBagConstraints.NONE;

        JButton smallUpdateButton = new JButton("修改");
        smallUpdateButton.setFont(new Font("楷体", Font.PLAIN, 24));
        smallUpdateButton.setMaximumSize(new Dimension(150, 40));
        smallUpdateButton.addActionListener(this::updateBook);

        JButton smallDeleteButton = new JButton("删除");
        smallDeleteButton.setFont(new Font("楷体", Font.PLAIN, 24));
        smallDeleteButton.setMaximumSize(new Dimension(150, 40));
        smallDeleteButton.addActionListener(this::deleteBook);

        JPanel buttonRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // 按钮间距20
        buttonRowPanel.add(smallUpdateButton);
        buttonRowPanel.add(smallDeleteButton);
        updatePanel.add(buttonRowPanel, gbcUpdate);

        // 包裹成带标题的面板
        inputPanel.add(wrapPanelWithTitle(addPanel, "添加书籍"), BorderLayout.NORTH);
        inputPanel.add(wrapPanelWithTitle(updatePanel, "修改书籍信息"), BorderLayout.CENTER);
    }

    // 获取面板用于 TabbedPane
    public JPanel creatAddAndUpdateBookPanel() {
        return inputPanel;
    }

    // 工具方法：添加两列结构（label + field）
    private void addPair(JPanel panel, GridBagConstraints gbc, int row,
                         JComponent label1, JComponent field1,
                         JComponent label2, JComponent field2) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label1, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.3;
        panel.add(label2, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.7;
        panel.add(field2, gbc);
    }

    // 单列结构工具方法（用于修改区域）
    private void addPair(JPanel panel, GridBagConstraints gbc, int row,
                         JComponent label1, JComponent field1) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        panel.add(label1, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field1, gbc);
    }

    // 工具方法：包装面板并加标题
    private JPanel wrapPanelWithTitle(JPanel panel, String title) {
        JPanel titledPanel = new JPanel(new BorderLayout());
        titledPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title, TitledBorder.CENTER, TitledBorder.TOP, titleFont));
        titledPanel.add(panel, BorderLayout.NORTH);
        return titledPanel;
    }

    // 添加书籍
    private void addBook(ActionEvent e) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = JDBCUtil.getConnection();
            JDBCUtil.startTransaction(conn);

            String isbn = isbnField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String publisher = publisherField.getText().trim();
            String editionStr = editionNumberField.getText().trim();
            String date = publishDateField.getText().trim();
            String type = typeField.getText().trim();

            if (isbn.isEmpty() || title.isEmpty() || author.isEmpty()
                    || publisher.isEmpty() || editionStr.isEmpty() || date.isEmpty() || type.isEmpty()) {
                throw new RuntimeException("请填写完整信息");
            }

            int edition = Integer.parseInt(editionStr);
            String sql = "INSERT INTO books (ISBN, Title, Authors, Publisher, EditionNumber, PublicationDate, Type) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setString(4, publisher);
            stmt.setInt(5, edition);
            stmt.setDate(6, Date.valueOf(date));
            stmt.setString(7, type);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "添加成功");
                clearInputFields();
            } else {
                throw new RuntimeException("添加失败！");
            }
            JDBCUtil.commitTransaction(conn);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "版次必须为数字", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JDBCUtil.rollbackTransaction(conn);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            JDBCUtil.close(conn, stmt, null);
        }
    }

    // 修改书籍
    private void updateBook(ActionEvent e) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = JDBCUtil.getConnection();
            JDBCUtil.startTransaction(conn);

            String isbn = updateISBNField.getText().trim();
            String newValue = updateContentField.getText().trim();
            String property = changeName(updateCombo.getSelectedItem().toString());

            if (isbn.isEmpty() || newValue.isEmpty() || property == null) {
                throw new RuntimeException("请输入完整的信息");
            }

            String sql = String.format("UPDATE books SET %s = ? WHERE ISBN = ?", property);
            stmt = conn.prepareStatement(sql);
            stmt.setObject(1, newValue);
            stmt.setString(2, isbn);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "修改成功");
                clearInputFields();
            } else {
                throw new RuntimeException("未找到该书籍");
            }

            JDBCUtil.commitTransaction(conn);
        } catch (Exception ex) {
            ex.printStackTrace();
            JDBCUtil.rollbackTransaction(conn);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            JDBCUtil.close(conn, stmt, null);
        }
    }

    // 删除书籍
    private void deleteBook(ActionEvent e) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = JDBCUtil.getConnection();
            JDBCUtil.startTransaction(conn);

            String isbn = updateISBNField.getText().trim();

            if (isbn.isEmpty()) {
                throw new RuntimeException("请输入要删除的 ISBN");
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    "确定要删除 ISBN 为 " + isbn + " 的书籍吗？",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            String sql = "DELETE FROM books WHERE ISBN = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);

            int result = stmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(null, "删除成功");
                clearInputFields();
            } else {
                throw new RuntimeException("未找到该书籍");
            }

            JDBCUtil.commitTransaction(conn);
        } catch (Exception ex) {
            ex.printStackTrace();
            JDBCUtil.rollbackTransaction(conn);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            JDBCUtil.close(conn, stmt, null);
        }
    }

    // 清空所有输入框
    private void clearInputFields() {
        isbnField.setText("");
        titleField.setText("");
        authorField.setText("");
        publisherField.setText("");
        editionNumberField.setText("");
        publishDateField.setText("");
        typeField.setText("");
        updateISBNField.setText("");
        updateContentField.setText("");
    }

    // 属性映射
    private String changeName(String name) {
        return switch (name) {
            case "书名" -> "Title";
            case "作者" -> "Authors";
            case "出版社" -> "Publisher";
            case "编辑" -> "EditionNumber";
            case "出版日期" -> "PublicationDate";
            case "种类" -> "Type";
            default -> null;
        };
    }
}
