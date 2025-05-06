package Library.Utils;

import Library.LibraryGUI.AddAndUpdateBookPanel;
import Library.LibraryGUI.AddAndUpdateReader;
import Library.LibraryGUI.SearchAndBorrowPanel;
import Library.LibraryGUI.SearchAndReturnPanel;

import javax.swing.*;

public class LibraryCore extends JFrame {

   public LibraryCore() {
       this.setTitle("Library Management System");
       this.setSize(1300, 600);

       //this.setAlwaysOnTop(true);
       //中央放置
       this.setLocationRelativeTo(null);
       this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       JTabbedPane tabbedPane = new JTabbedPane();
       tabbedPane.addTab("查询与借阅", new SearchAndBorrowPanel());
       tabbedPane.addTab("查询与归还", new SearchAndReturnPanel());
       tabbedPane.addTab("添加和更改图书", new AddAndUpdateBookPanel().creatAddAndUpdateBookPanel());
       tabbedPane.addTab("添加和更改读者",new AddAndUpdateReader().createAddAndUpdateReaderPanel());
       add(tabbedPane);
       setVisible(true);
   }
}
