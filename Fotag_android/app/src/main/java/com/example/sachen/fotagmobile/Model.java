package com.example.sachen.fotagmobile;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;
import java.util.*;
import java.util.List;

public class Model extends Observable {
    private static final Model ourInstance = new Model();
    static Model getInstance()
    {
        return ourInstance;
    }
    public void initObservers()
    {
        setChanged();
        notifyObservers();
    }

    @Override
    public synchronized void deleteObserver(Observer o)
    {
        super.deleteObserver(o);
    }

    @Override
    public synchronized void addObserver(Observer o)
    {
        super.addObserver(o);
    }

    @Override
    public synchronized void deleteObservers()
    {
        super.deleteObservers();
    }

    @Override
    public void notifyObservers()
    {
        super.notifyObservers();
    }
}
