
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

public class Model {
    /** The observers that are watching this model for changes. */
    private List<Observer> observers;
    protected ArrayList<String> loi = new ArrayList<String>();
    protected ArrayList<Integer> ratings = new ArrayList<Integer>();
    protected ArrayList<ArrayList<Object>> savedObject = new ArrayList<ArrayList<Object>>();
    protected boolean firstOpen = false;
    protected Integer filter = 0;
    protected boolean filtering = false;
    protected boolean toGrid = false;
    protected boolean toList = false;
    protected JScrollPane scroll;
    protected ArrayList<Integer> frameSize = new ArrayList<Integer>();
    protected int panelRow, panelCol;
    protected JFrame mainFrame;
    protected JPanel p;
    protected int imageSizeGrid = 300;
    protected int imageSizeList = 250;
    protected int imageSpace = 30;
    protected int state = 0; //0 means list, 1 means grid
    protected int buttonPressed = 0; //0 means list, 1 means grid

    /**
     * Create a new model.
     */
    public Model() {
        this.observers = new ArrayList();
    }

    /**
     * Add an observer to be notified when this model changes.
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Remove an observer from this model.
     */
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Notify all observers that the model has changed.
     */
    public void notifyObservers() {
        for (Observer observer: this.observers) {
            observer.update(this);
        }
    }

    public ArrayList<ArrayList<Object>> savingObjects(ArrayList<String> loi, ArrayList<Integer> ratings){
        ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>();
        for (int i = 0; i < loi.size(); i++){
            result.add(new ArrayList<Object>());
            result.get(i).add(loi.get(i));
            result.get(i).add(ratings.get(i));
        }

        return result;
    }

    public void updateData(){
        for (int i = 0; i < savedObject.size(); i++){
            loi.add((String)savedObject.get(i).get(0));
            ratings.add((Integer)savedObject.get(i).get(1));
        }
    }

    public void updatePanel(int image_num) {
        if (image_num != 0) {
            if (state == 1) { // update grid
                panelCol = mainFrame.getWidth() / (imageSizeGrid+imageSpace);
                if(image_num % panelCol != 0) {
                    panelRow = image_num / panelCol + 1;
                } else {
                    panelRow = image_num / panelCol;
                }
                int height = panelRow * (imageSizeGrid + imageSpace);
                p.setPreferredSize(new Dimension(mainFrame.getWidth()-100,height));
                p.revalidate();
                p.repaint();
            }
            else if (state == 0){ // update list
                panelCol = 1;
                panelRow = image_num;
                int height = panelRow * (imageSizeList + imageSpace);
                p.setPreferredSize(new Dimension(mainFrame.getWidth()-100,height));
                p.revalidate();
                p.repaint();
            }
        }else{
            panelCol = 1;
            panelRow = 1;
        }
    }
}
