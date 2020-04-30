import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;

public class View2 extends JPanel implements Observer {

    private Model model;
    private List<Image> images = new ArrayList<Image>();
    private ArrayList<ArrayList<JButton>> buttons = new ArrayList<ArrayList<JButton>>();
    protected List<String> dates = new ArrayList<String>();
    protected List<String> names = new ArrayList<String>();
    private List<JPanel> subJPanels = new ArrayList<JPanel>();
    private Integer button_pos = 0;
    private JPanel ratingPanel;
    JButton star_button1, star_button2,star_button3,star_button4,star_button5, newImage, clear_rating;
    protected Integer rating = 0;
    ImageIcon star_on = new ImageIcon(this.getClass().getResource("star_on.PNG"));
    ImageIcon star_off = new ImageIcon(this.getClass().getResource("star_off.PNG"));
    ImageIcon clear = new ImageIcon(this.getClass().getResource("clear.PNG"));

    ActionListener actionListener = new ActionListener () {
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < buttons.size(); i++){
                if (e.getSource() == buttons.get(i).get(0)){
                    buttons.get(i).get(0).setIcon(star_on);
                    buttons.get(i).get(1).setIcon(star_off);
                    buttons.get(i).get(2).setIcon(star_off);
                    buttons.get(i).get(3).setIcon(star_off);
                    buttons.get(i).get(4).setIcon(star_off);
                    rating = 1;
                    model.ratings.set(i, rating);
                    setFilter(model.filter);
                }
                else if (e.getSource() == buttons.get(i).get(1)){
                    buttons.get(i).get(0).setIcon(star_on);
                    buttons.get(i).get(1).setIcon(star_on);
                    buttons.get(i).get(2).setIcon(star_off);
                    buttons.get(i).get(3).setIcon(star_off);
                    buttons.get(i).get(4).setIcon(star_off);
                    rating = 2;
                    model.ratings.set(i, rating);
                    setFilter(model.filter);
                }
                else if (e.getSource() == buttons.get(i).get(2)){
                    buttons.get(i).get(0).setIcon(star_on);
                    buttons.get(i).get(1).setIcon(star_on);
                    buttons.get(i).get(2).setIcon(star_on);
                    buttons.get(i).get(3).setIcon(star_off);
                    buttons.get(i).get(4).setIcon(star_off);
                    rating = 3;
                    model.ratings.set(i, rating);
                    setFilter(model.filter);

                }
                else if (e.getSource() == buttons.get(i).get(3)){
                    buttons.get(i).get(0).setIcon(star_on);
                    buttons.get(i).get(1).setIcon(star_on);
                    buttons.get(i).get(2).setIcon(star_on);
                    buttons.get(i).get(3).setIcon(star_on);
                    buttons.get(i).get(4).setIcon(star_off);
                    rating = 4;
                    model.ratings.set(i, rating);
                    setFilter(model.filter);
                }
                else if (e.getSource() == buttons.get(i).get(4)){
                    buttons.get(i).get(0).setIcon(star_on);
                    buttons.get(i).get(1).setIcon(star_on);
                    buttons.get(i).get(2).setIcon(star_on);
                    buttons.get(i).get(3).setIcon(star_on);
                    buttons.get(i).get(4).setIcon(star_on);
                    rating = 5;
                    model.ratings.set(i, rating);
                    setFilter(model.filter);
                }

                else if (e.getSource() == buttons.get(i).get(5)){
                    JFrame frame = new JFrame ("image");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    ImageIcon rawIcon = (ImageIcon)buttons.get(i).get(5).getIcon();
                    Image rawImage = rawIcon.getImage();
                    Image scaledImage = getScaledImage((BufferedImage)rawImage, 500,500);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    JLabel img = new JLabel(scaledIcon);
                    frame.add(img);

                    frame.pack();
                    frame.setVisible(true);
                }
                else if (e.getSource() == buttons.get(i).get(6)){
                    buttons.get(i).get(0).setIcon(star_off);
                    buttons.get(i).get(1).setIcon(star_off);
                    buttons.get(i).get(2).setIcon(star_off);
                    buttons.get(i).get(3).setIcon(star_off);
                    buttons.get(i).get(4).setIcon(star_off);
                    rating = 0;
                    model.ratings.set(i, rating);
                    setFilter(model.filter);
                }
            }
        }
    };

    /**
     * Create a new View.
     */
    public View2(Model model) {
//        this.setSize(new Dimension(800, 500));
        this.setBackground(Color.WHITE);

        model.p = new JPanel();
        model.p.setPreferredSize(new Dimension(800, 500));
        model.p.setBackground(Color.WHITE);

        this.add(model.p);
        this.model = model;
        setVisible(true);
    }

    public void update_images(){
        BufferedImage lastImage = null;
        try{
            lastImage = ImageIO.read(new File(model.loi.get(model.loi.size()-1)));
        }catch (IOException e){
            e.printStackTrace();
        }
        Image scaledImage = getScaledImage(lastImage, 300,200);
        images.add(scaledImage);

        //get image
        ImageIcon newIcon = new ImageIcon(scaledImage);
        newImage = new JButton(newIcon);
        newImage.setContentAreaFilled(false);
        newImage.setBorderPainted(false);
        newImage.setPreferredSize(new Dimension(300,200));
        newImage.setMaximumSize(new Dimension(300,200));
        newImage.addActionListener(actionListener);
        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(300,200));
        imagePanel.setMaximumSize(new Dimension(300,200));
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagePanel.setBackground(Color.WHITE);
        imagePanel.add(newImage);

        File file = new File(model.loi.get(model.loi.size()-1));

        //get image name
        String fileName = file.getName();
        JLabel imageName = new JLabel(fileName);
        JPanel namePanel = new JPanel();
        namePanel.setPreferredSize(new Dimension(300,30));
        namePanel.setMaximumSize(new Dimension(300,30));
        namePanel.setBackground(Color.WHITE);
        namePanel.add(imageName);
        names.add(fileName);

        //get image date
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(Paths.get(file.toURI()), BasicFileAttributes.class);
        }catch (IOException ie){
            ie.printStackTrace();
        }

        FileTime filetime = attr.creationTime();
        Long raw_date = filetime.toMillis();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String date = df.format(raw_date);
        dates.add(date);
        JLabel imageDate = new JLabel(date);
        JPanel datePanel = new JPanel();
        datePanel.setPreferredSize(new Dimension(300,30));
        datePanel.setMaximumSize(new Dimension(300,30));
        datePanel.setBackground(Color.WHITE);
        datePanel.add(imageDate);

        //panel for rating
        ratingPanel = new JPanel();
        ratingPanel.setBackground(Color.WHITE);
        ratingPanel.setPreferredSize(new Dimension(300,40));
        ratingPanel.setMaximumSize(new Dimension(300,40));
        ratingPanel.setLayout(new GridLayout(1,6,0,0));
        model.ratings.add(0);
        update_rating(ratingPanel);

        //panel for meta data
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setPreferredSize(new Dimension(300,100));
        dataPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePanel.setBackground(Color.WHITE);
        dataPanel.add(namePanel);
        dataPanel.add(datePanel);
        dataPanel.add(ratingPanel);

        //panel for one image and its metadata
        JPanel sub = new JPanel();

        if (model.state == 0){
            sub.setPreferredSize(new Dimension(600,(model.imageSizeList + model.imageSpace)));
            sub.setMaximumSize(new Dimension(600,(model.imageSizeList + model.imageSpace)));
            sub.setLayout(new BoxLayout(sub,BoxLayout.X_AXIS));
        }else{
            sub.setLayout(new BoxLayout(sub,BoxLayout.Y_AXIS));
            sub.setPreferredSize(new Dimension(300,300));
            sub.setMaximumSize(new Dimension(300,300));
        }

        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBackground(Color.WHITE);
        sub.add(imagePanel);
        sub.add(dataPanel);

        if (model.filter == 0) model.p.add(sub);
        subJPanels.add(sub);

        //update layout accordingly if no filter is present
        if (model.filter == 0){
            model.p.add(sub);
            model.updatePanel(model.p.getComponentCount());
            if (model.state == 0){
                toListLayout();
            }
            else if (model.state == 1){
                toGridLayout();
            }
        }
        model.p.revalidate();
    }

    public void update_rating(JPanel p){
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

        p.add(star_button1);
        p.add(star_button2);
        p.add(star_button3);
        p.add(star_button4);
        p.add(star_button5);
        p.add(clear_rating);

        ArrayList<JButton> sub_button = new ArrayList<JButton>();
        buttons.add(sub_button);

        buttons.get(button_pos).add(star_button1);
        buttons.get(button_pos).add(star_button2);
        buttons.get(button_pos).add(star_button3);
        buttons.get(button_pos).add(star_button4);
        buttons.get(button_pos).add(star_button5);
        buttons.get(button_pos).add(newImage);
        buttons.get(button_pos).add(clear_rating);
        button_pos ++;
    }

    public void create_rating(JPanel p, Integer r){

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

        if (r == 1){
            star_button1.setIcon(star_on);
            star_button2.setIcon(star_off);
            star_button3.setIcon(star_off);
            star_button4.setIcon(star_off);
            star_button5.setIcon(star_off);
        }
        else if(r == 2){
            star_button1.setIcon(star_on);
            star_button2.setIcon(star_on);
            star_button3.setIcon(star_off);
            star_button4.setIcon(star_off);
            star_button5.setIcon(star_off);
        }
        else if (r == 3){
            star_button1.setIcon(star_on);
            star_button2.setIcon(star_on);
            star_button3.setIcon(star_on);
            star_button4.setIcon(star_off);
            star_button5.setIcon(star_off);
        }
        else if (r == 4){
            star_button1.setIcon(star_on);
            star_button2.setIcon(star_on);
            star_button3.setIcon(star_on);
            star_button4.setIcon(star_on);
            star_button5.setIcon(star_off);
        }
        else if (r == 5){
            star_button1.setIcon(star_on);
            star_button2.setIcon(star_on);
            star_button3.setIcon(star_on);
            star_button4.setIcon(star_on);
            star_button5.setIcon(star_on);
        }

        p.add(star_button1);
        p.add(star_button2);
        p.add(star_button3);
        p.add(star_button4);
        p.add(star_button5);
        p.add(clear_rating);

        ArrayList<JButton> sub_button = new ArrayList<JButton>();
        buttons.add(sub_button);

        buttons.get(button_pos).add(star_button1);
        buttons.get(button_pos).add(star_button2);
        buttons.get(button_pos).add(star_button3);
        buttons.get(button_pos).add(star_button4);
        buttons.get(button_pos).add(star_button5);
        buttons.get(button_pos).add(newImage);
        buttons.get(button_pos).add(clear_rating);
        button_pos ++;
    }

    public void loadImages(){
        for(int i=0; i < model.loi.size(); i++){

            BufferedImage Image1 = null;
            try{
                Image1 = ImageIO.read(new File(model.loi.get(i)));
            }catch (IOException e){
                e.printStackTrace();
            }
            Image scaledImage = getScaledImage(Image1, 300,200);
            images.add(scaledImage);

            //add image
            ImageIcon newIcon = new ImageIcon(scaledImage);
            newImage = new JButton(newIcon);
            newImage.setContentAreaFilled(false);
            newImage.setBorderPainted(false);
            newImage.setPreferredSize(new Dimension(300,200));
            newImage.setMaximumSize(new Dimension(300,200));
            newImage.addActionListener(actionListener);
            JPanel imagePanel = new JPanel();
            imagePanel.setPreferredSize(new Dimension(300,200));
            imagePanel.setMaximumSize(new Dimension(300,200));
            imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            imagePanel.setBackground(Color.WHITE);
            imagePanel.add(newImage);

            File file = new File(model.loi.get(i));

            //get image name
            String fileName = file.getName();
            JLabel imageName = new JLabel(fileName);
            JPanel namePanel = new JPanel();
            namePanel.setPreferredSize(new Dimension(300,30));
            namePanel.setMaximumSize(new Dimension(300,30));
            namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            namePanel.setBackground(Color.WHITE);
            namePanel.add(imageName);

            names.add(fileName);

            //get image date
            BasicFileAttributes attr = null;
            try {
                attr = Files.readAttributes(Paths.get(file.toURI()), BasicFileAttributes.class);
            }catch (IOException ie){
                ie.printStackTrace();
            }
            FileTime filetime = attr.creationTime();
            Long raw_date = filetime.toMillis();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            String date = df.format(raw_date);
            dates.add(date);
            JLabel imageDate = new JLabel(date);
            JPanel datePanel = new JPanel();
            datePanel.setPreferredSize(new Dimension(300,30));
            datePanel.setMaximumSize(new Dimension(300,30));
            datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            datePanel.setBackground(Color.WHITE);
            datePanel.add(imageDate);

            //get rating panel
            ratingPanel = new JPanel();
            ratingPanel.setBackground(Color.WHITE);
            ratingPanel.setPreferredSize(new Dimension(300,40));
            ratingPanel.setMaximumSize(new Dimension(300,40));
            ratingPanel.setLayout(new GridLayout(1,6,0,0));
            ratingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            create_rating(ratingPanel, model.ratings.get(i));

            JPanel dataPanel = new JPanel();
            dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
            dataPanel.setPreferredSize(new Dimension(300,100));
            dataPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            datePanel.setBackground(Color.WHITE);
            dataPanel.add(namePanel);
            dataPanel.add(datePanel);
            dataPanel.add(ratingPanel);

            //get panel for the image and metadata
            JPanel sub = new JPanel();
            sub.setLayout(new BoxLayout(sub,BoxLayout.Y_AXIS));
            sub.setPreferredSize(new Dimension(300,300));
            sub.setMaximumSize(new Dimension(300,300));
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            sub.setBackground(Color.WHITE);
            sub.add(imagePanel);
            sub.add(dataPanel);

            model.p.add(sub);
            subJPanels.add(sub);
        }
        model.p.revalidate();

    }

    public Image getScaledImage(BufferedImage src, int width, int height){
        BufferedImage resizeImg = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = (Graphics2D)resizeImg.createGraphics();
        g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();

        return resizeImg;
    }

    public void setFilter(Integer r){
        ArrayList<Integer> removeList = new ArrayList<Integer>();
        model.p.removeAll();
        for (int i = 0; i < subJPanels.size(); i++){
            model.p.add(subJPanels.get(i));
        }
        for (int i = 0; i < model.p.getComponentCount(); i++){
            if (r > model.ratings.get(i)){
                removeList.add(i);
            }
        }
        for (int i = removeList.size()-1; i >= 0 ; i--){
            model.p.remove(model.p.getComponent(removeList.get(i)));
        }

        model.updatePanel(model.p.getComponentCount());

        if (model.state == 1){
            model.p.setLayout(new GridLayout(model.panelRow,model.panelCol,0,0));
        }
        this.revalidate();
        model.p.repaint();
    }

    public void toListLayout(){
        model.p.setLayout(new BoxLayout(model.p,BoxLayout.Y_AXIS));
        for(int i = 0; i < subJPanels.size(); i++){
            JPanel oneImage = subJPanels.get(i);
            oneImage.setPreferredSize(new Dimension(600,(model.imageSizeList + model.imageSpace)));
            oneImage.setMaximumSize(new Dimension(600,(model.imageSizeList + model.imageSpace)));
            oneImage.setLayout(new BoxLayout(oneImage,BoxLayout.X_AXIS));
        }
    }

    public void toGridLayout(){
        for(int i = 0; i < subJPanels.size(); i++){
            JPanel oneImage = subJPanels.get(i);
            oneImage.setPreferredSize(new Dimension(300,300));
            oneImage.setLayout(new BoxLayout(oneImage, BoxLayout.Y_AXIS));
        }

        model.p.setLayout(new GridLayout(model.panelRow,model.panelCol,30,30));
    }

    /**
     * Update with data from the model.
     */
    public void update(Object observable) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.

        if (model.firstOpen) {
            loadImages();
            toListLayout();
            model.firstOpen = false;
        }
        else if (model.filtering){
            setFilter(model.filter);
            model.filtering = false;
        } else if (model.toList){
            model.state = 0;
            toListLayout();
            model.toList = false;
        } else if (model.toGrid){
            model.state = 1;
            toGridLayout();
            model.toGrid = false;
        }else{
            update_images();
        }

    }
}
