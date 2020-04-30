import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("CS349 A3");
        frame.setMinimumSize(new Dimension(650,400));
        Container content = frame.getContentPane();
        Model model = new Model();
        model.mainFrame = frame;

        View view = new View(model);
        model.addObserver(view);

        View2 view2 = new View2(model);
        model.addObserver(view2);

        content.setLayout(new BorderLayout());
        content.add(view, BorderLayout.NORTH);
        content.add(view2,BorderLayout.WEST);
        content.setBackground(Color.WHITE);

        frame.setTitle("Fotag!");
        frame.pack();
        frame.setPreferredSize(new Dimension(800,600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                File fileToSave = new File("saved_state");
                FileOutputStream fout = null;
                ObjectOutputStream oos = null;

                try {
                    fout = new FileOutputStream("saved_state");
                } catch (FileNotFoundException err) { err.printStackTrace(); }
                try {
                    oos = new ObjectOutputStream(fout);
                    ArrayList<ArrayList<Object>> result = model.savingObjects(model.loi, model.ratings);
                    oos.writeObject(result);
                    oos.close();
                } catch (IOException err) { err.printStackTrace(); }

                e.getWindow().dispose();
            }
            public void windowOpened(WindowEvent e) {
                File test = new File("saved_state");
                if (test.exists() && !test.isDirectory()) {
                    FileInputStream fin = null;
                    try {
                        fin = new FileInputStream("saved_state");
                        ObjectInputStream ois = new ObjectInputStream(fin);
                        model.savedObject = (ArrayList<ArrayList<Object>>) ois.readObject();
                        fin.close();
                        ois.close();

                        model.firstOpen = true;
                        model.updatePanel(model.savedObject.size());
                        model.updateData();
                        model.notifyObservers();
                    } catch (IOException err) {
                        err.printStackTrace();
                    } catch (ClassNotFoundException err) {
                    }
                }
                model.scroll = new JScrollPane(model.p);
                model.scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                model.scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                content.add(model.scroll);
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                if (model.state == 0){
                    model.toList = true;
                }
                else if (model.state == 1){
                    model.toGrid = true;
                }
                model.updatePanel(model.p.getComponentCount());
                model.notifyObservers();
            }
        });
    }
}
