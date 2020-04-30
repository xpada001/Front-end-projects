
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class View extends JPanel implements Observer {

    private Model model;
    private ButtonGroup group;
    private JToggleButton grid, list;
    private JButton upload;
    private JLabel title;
    private JPanel p;
    private boolean gridPressed = false;
    private boolean listPressed = false;
    private JButton star_button1, star_button2,star_button3,star_button4,star_button5, clear_rating;


    /**
     * Create a new View.
     */
    public View(Model model) {
        // Set up the window.
        this.setBackground(Color.WHITE);
        ImageIcon star_on = new ImageIcon(this.getClass().getResource("star_on.png"));
        ImageIcon star_off = new ImageIcon(this.getClass().getResource("star_off.PNG"));
        ImageIcon clear = new ImageIcon(this.getClass().getResource("clear.PNG"));

        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setPreferredSize(new Dimension(770,50));
        group = new ButtonGroup();
        ActionListener actionListener = new ActionListener (){
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == grid){
                    model.buttonPressed = 1;
                    model.toGrid = true;
                    model.state = 1;
                    model.updatePanel(model.p.getComponentCount());
                    model.notifyObservers();
                }
                else if (e.getSource() == list){
                    model.buttonPressed = 0;
                    model.toList = true;
                    model.state = 0;
                    model.updatePanel(model.p.getComponentCount());
                    model.notifyObservers();
                }
                else if (e.getSource() == upload){
                    JFileChooser fc = new JFileChooser();
                    FileNameExtensionFilter filter_png = new FileNameExtensionFilter("png files","png");
                    FileNameExtensionFilter filter_jpeg = new FileNameExtensionFilter("jpeg files","jpg");
                    fc.setFileFilter(filter_png);
                    fc.addChoosableFileFilter(filter_jpeg);

                    int option = fc.showOpenDialog(p);
                    if (option == fc.APPROVE_OPTION){
                        File selected = fc.getSelectedFile();
                        model.loi.add(selected.getAbsolutePath());
                        model.notifyObservers();
                    }
                }
                if (e.getSource() == star_button1){
                    star_button1.setIcon(star_on);
                    star_button2.setIcon(star_off);
                    star_button3.setIcon(star_off);
                    star_button4.setIcon(star_off);
                    star_button5.setIcon(star_off);
                    model.filter = 1;
                    model.filtering = true;
                    model.notifyObservers();
                }
                if (e.getSource() == star_button2){
                    star_button1.setIcon(star_on);
                    star_button2.setIcon(star_on);
                    star_button3.setIcon(star_off);
                    star_button4.setIcon(star_off);
                    star_button5.setIcon(star_off);
                    model.filter = 2;
                    model.filtering = true;
                    model.notifyObservers();
                }
                if (e.getSource() == star_button3){
                    star_button1.setIcon(star_on);
                    star_button2.setIcon(star_on);
                    star_button3.setIcon(star_on);
                    star_button4.setIcon(star_off);
                    star_button5.setIcon(star_off);
                    model.filter = 3;
                    model.filtering = true;
                    model.notifyObservers();
                }
                if (e.getSource() == star_button4){
                    star_button1.setIcon(star_on);
                    star_button2.setIcon(star_on);
                    star_button3.setIcon(star_on);
                    star_button4.setIcon(star_on);
                    star_button5.setIcon(star_off);
                    model.filter = 4;
                    model.filtering = true;
                    model.notifyObservers();
                }
                if (e.getSource() == star_button5){
                    star_button1.setIcon(star_on);
                    star_button2.setIcon(star_on);
                    star_button3.setIcon(star_on);
                    star_button4.setIcon(star_on);
                    star_button5.setIcon(star_on);
                    model.filter = 5;
                    model.filtering = true;
                    model.notifyObservers();
                }
                if (e.getSource() == clear_rating){
                    star_button1.setIcon(star_off);
                    star_button2.setIcon(star_off);
                    star_button3.setIcon(star_off);
                    star_button4.setIcon(star_off);
                    star_button5.setIcon(star_off);
                    model.filter = 0;
                    model.filtering = true;
                    model.notifyObservers();
                }
            }
        };

        //grid button
        grid = new JToggleButton();
        Image newImage1 = new ImageIcon(this.getClass().getResource("grid.PNG")).getImage();
        grid.setIcon(new ImageIcon(newImage1));
        grid.addActionListener(actionListener);
        grid.setPreferredSize(new Dimension(40,40));
        group.add(grid);

        //list button
        list = new JToggleButton();
        Image newImage2 = new ImageIcon(this.getClass().getResource("list.PNG")).getImage();
        list.setIcon(new ImageIcon(newImage2));
        list.addActionListener(actionListener);
        list.setPreferredSize(new Dimension(40,40));
        list.setSelected(true);
        group.add(list);

        //title
        title = new JLabel("Fotag!");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setPreferredSize(new Dimension(300,40));

        //upload button
        upload = new JButton();
        Image newImage3 = new ImageIcon(this.getClass().getResource("upload.PNG")).getImage();
        upload.setIcon(new ImageIcon(newImage3));
        upload.setContentAreaFilled(false);
        upload.setBorderPainted(false);
        upload.addActionListener(actionListener);
        upload.setPreferredSize(new Dimension(40,40));

        //filter
        star_button1 = new JButton(star_off);
        star_button1.setContentAreaFilled(false);
        star_button1.setBorderPainted(false);
        star_button1.addActionListener(actionListener);

        star_button2 = new JButton(star_off);
        star_button2.setContentAreaFilled(false);
        star_button2.setBorderPainted(false);
        star_button2.addActionListener(actionListener);

        star_button3 = new JButton(star_off);
        star_button3.setContentAreaFilled(false);
        star_button3.setBorderPainted(false);
        star_button3.addActionListener(actionListener);

        star_button4 = new JButton(star_off);
        star_button4.setContentAreaFilled(false);
        star_button4.setBorderPainted(false);
        star_button4.addActionListener(actionListener);

        star_button5 = new JButton(star_off);
        star_button5.setContentAreaFilled(false);
        star_button5.setBorderPainted(false);
        star_button5.addActionListener(actionListener);

        clear_rating = new JButton(clear);
        clear_rating.setContentAreaFilled(false);
        clear_rating.setBorderPainted(false);
        clear_rating.addActionListener(actionListener);

        JPanel ratingPanel = new JPanel();
        ratingPanel.setLayout(new BoxLayout(ratingPanel,BoxLayout.X_AXIS));
        ratingPanel.setPreferredSize(new Dimension(300,40));
        ratingPanel.setMaximumSize(new Dimension(300,40));
        ratingPanel.add(star_button1);
        ratingPanel.add(star_button2);
        ratingPanel.add(star_button3);
        ratingPanel.add(star_button4);
        ratingPanel.add(star_button5);
        ratingPanel.add(clear_rating);

        p.add(grid);
        p.add(list);
        p.add(Box.createHorizontalStrut(50));
        p.add(title);
        p.add(Box.createHorizontalGlue());
        p.add(upload);
        p.add(ratingPanel);
        this.add(p);
        this.model = model;

        setVisible(true);

    }

    /**
     * Update with data from the model.
     */
    public void update(Object observable) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.
        if (model.toList || model.toGrid){
            p.setPreferredSize(new Dimension(model.mainFrame.getWidth()-30, 50));
            p.setMaximumSize(new Dimension(model.mainFrame.getWidth()-30, 50));
            this.revalidate();
            model.p.repaint();
        }

    }
}
