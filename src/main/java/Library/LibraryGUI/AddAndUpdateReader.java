package Library.LibraryGUI;

import Library.Utils.JDBCUtil;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class AddAndUpdateReader {

    private final JPanel inputPanel = new JPanel();
    private final JPanel addPanel = new JPanel(new GridBagLayout());
    private final JPanel updatePanel = new JPanel(new GridBagLayout());

    // 添加部分
    private final JTextField readerIDField = new JTextField(20);
    private final JTextField firstNameField = new JTextField(20);
    private final JTextField lastNameField = new JTextField(20);
    private final JTextField addressField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField limitsField = new JTextField(20);

    // 修改部分
    private final JTextField updateIDField = new JTextField(20);
    private final JTextField updateContentField = new JTextField(20);
    private final JComboBox<String> updateCombo = new JComboBox<>(new String[]{"名字", "姓氏", "地址", "电话", "借阅上限"});

    // 标签
    private final JLabel readerIDLabel = new JLabel("读者ID:");
    private final JLabel firstNameLabel = new JLabel("名:");
    private final JLabel lastNameLabel = new JLabel("姓:");
    private final JLabel addressLabel = new JLabel("地址:");
    private final JLabel phoneLabel = new JLabel("电话:");
    private final JLabel limitsLabel = new JLabel("借阅上限:");

    private final JLabel updateIDLabel = new JLabel("请输入读者ID:");
    private final JLabel updateContentLabel = new JLabel("修改内容:");
    private final JLabel updatePropertyLabel = new JLabel("修改属性:");


    // 字体
    private final Font labelFont = new Font("楷体", Font.PLAIN, 24);   // 标签字体更大
    private final Font fieldFont = new Font("楷体", Font.PLAIN, 20);   // 输入框字体略小
    private final Font titleFont = new Font("楷体", Font.BOLD, 26);    // 标题字体

    public AddAndUpdateReader() {
        initializeUI();
    }

    private void initializeUI() {
        inputPanel.setLayout(new BorderLayout(15, 15));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 设置标签字体并居中
        for (Component c : new Component[]{
                readerIDLabel, firstNameLabel, lastNameLabel, addressLabel,
                phoneLabel, limitsLabel, updateIDLabel, updateContentLabel,
                updatePropertyLabel}) {
            if (c != null) {
                c.setFont(labelFont);
                ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
            }
        }

        // 设置输入组件字体
        for (Component c : new Component[]{
                readerIDField, firstNameField, lastNameField, addressField,
                phoneField, limitsField, updateIDField, updateContentField, updateCombo}) {
            c.setFont(fieldFont);
        }

        // ===== 添加区域 =====
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;  // 默认居中对齐
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // 第一行：读者ID + 名字
        addPair(addPanel, gbc, row++, readerIDLabel, readerIDField, firstNameLabel, firstNameField);

        // 第二行：姓 + 地址
        addPair(addPanel, gbc, row++, lastNameLabel, lastNameField, addressLabel, addressField);

        // 第三行：电话 + 借阅上限
        addPair(addPanel, gbc, row++, phoneLabel, phoneField, limitsLabel, limitsField);

        // 添加按钮（用 FlowLayoutPanel 包裹以实现按钮定宽居中）
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        JButton smallAddButton = new JButton("添加");
        smallAddButton.setFont(new Font("楷体", Font.PLAIN, 24));
        smallAddButton.setMaximumSize(new Dimension(150, 40));
        smallAddButton.addActionListener(this::addReader);

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonWrapper.add(smallAddButton);
        addPanel.add(buttonWrapper, gbc);

        // ===== 修改区域 =====
        GridBagConstraints gbcUpdate = new GridBagConstraints();
        gbcUpdate.insets = new Insets(10, 20, 10, 20);
        gbcUpdate.anchor = GridBagConstraints.CENTER;
        gbcUpdate.fill = GridBagConstraints.HORIZONTAL;

        int row2 = 0;

        // 第一行：输入 ID
        addPair(updatePanel, gbcUpdate, row2++, updateIDLabel, updateIDField);

        // 第二行：输入修改内容 + 下拉选择
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

        // 修改按钮
        JButton smallUpdateButton = new JButton("修改");
        smallUpdateButton.setFont(new Font("楷体", Font.PLAIN, 24));
        smallUpdateButton.setMaximumSize(new Dimension(150, 40));
        smallUpdateButton.addActionListener(this::updateReader);

        // 删除按钮
        JButton deleteButton = new JButton("删除");
        deleteButton.setFont(new Font("楷体", Font.PLAIN, 24));
        deleteButton.setMaximumSize(new Dimension(150, 40));
        deleteButton.addActionListener(this::deleteReader);

        // 使用 FlowLayout 居中显示两个按钮
        JPanel buttonRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // 按钮间距20
        buttonRowPanel.add(smallUpdateButton);
        buttonRowPanel.add(deleteButton);
        updatePanel.add(buttonRowPanel, gbcUpdate);

        // 包裹成带标题的面板
        inputPanel.add(wrapPanelWithTitle(addPanel, "添加读者"), BorderLayout.NORTH);
        inputPanel.add(wrapPanelWithTitle(updatePanel, "修改读者信息"), BorderLayout.CENTER);
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

    // 单列结构工具方法（如输入 ID）
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

    public JPanel createAddAndUpdateReaderPanel() {
        return inputPanel;
    }

    // 添加读者
    private void addReader(ActionEvent e) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = JDBCUtil.getConnection();
            JDBCUtil.startTransaction(conn);

            String readerID = readerIDField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            String limitsStr = limitsField.getText().trim();

            if (readerID.isEmpty() || firstName.isEmpty() || lastName.isEmpty()
                    || address.isEmpty() || phone.isEmpty() || limitsStr.isEmpty()) {
                throw new RuntimeException("请填写完整信息");
            }

            int limits = Integer.parseInt(limitsStr);

            String sql = "INSERT INTO Reader (ReaderID, FirstName, LastName, Address, PhoneNumber, Limits) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, readerID);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, address);
            stmt.setString(5, phone);
            stmt.setInt(6, limits);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "添加成功");
                clearInputFields();
            } else {
                throw new RuntimeException("添加失败");
            }
            JDBCUtil.commitTransaction(conn);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "借阅上限必须为数字", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            JDBCUtil.rollbackTransaction(conn);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            JDBCUtil.close(conn, stmt, null);
        }
    }

    // 修改读者
    private void updateReader(ActionEvent e) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = JDBCUtil.getConnection();
            JDBCUtil.startTransaction(conn);

            String readerID = updateIDField.getText().trim();
            String newValue = updateContentField.getText().trim();
            String property = changeName(updateCombo.getSelectedItem().toString());

            if (readerID.isEmpty() || newValue.isEmpty() || property == null) {
                throw new RuntimeException("请输入完整信息");
            }

            String sql = String.format("UPDATE Reader SET %s = ? WHERE ReaderID = ?", property);
            stmt = conn.prepareStatement(sql);
            if (property.equals("Limits")) {
                stmt.setInt(1, Integer.parseInt(newValue));
            } else {
                stmt.setString(1, newValue);
            }
            stmt.setString(2, readerID);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "修改成功");
                clearInputFields();
            } else {
                throw new RuntimeException("未找到该读者");
            }
            JDBCUtil.commitTransaction(conn);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "借阅上限必须为数字", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            JDBCUtil.rollbackTransaction(conn);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            JDBCUtil.close(conn, stmt, null);
        }
    }

    // 删除读者
    private void deleteReader(ActionEvent e) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = JDBCUtil.getConnection();
            JDBCUtil.startTransaction(conn);

            String readerID = updateIDField.getText().trim();

            if (readerID.isEmpty()) {
                throw new RuntimeException("请输入读者ID");
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    "是否删除 ID 为 " + readerID + " 的读者？",
                    "删除确认",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            String sql = "DELETE FROM Reader WHERE ReaderID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, readerID);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "删除成功");
                updateIDField.setText(""); // 清空输入框
            } else {
                throw new RuntimeException("未找到该读者");
            }

            JDBCUtil.commitTransaction(conn);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            JDBCUtil.rollbackTransaction(conn);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            JDBCUtil.close(conn, stmt, null);
        }
    }

    // 清空输入框
    private void clearInputFields() {
        readerIDField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        phoneField.setText("");
        limitsField.setText("");
        updateIDField.setText("");
        updateContentField.setText("");
    }

    // 属性映射
    private String changeName(String name) {
        return switch (name) {
            case "名字" -> "FirstName";
            case "姓氏" -> "LastName";
            case "地址" -> "Address";
            case "电话" -> "PhoneNumber";
            case "借阅上限" -> "Limits";
            default -> null;
        };
    }
}
