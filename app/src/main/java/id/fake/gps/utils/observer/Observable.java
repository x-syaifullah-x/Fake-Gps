package id.fake.gps.utils.observer;

public interface Observable {
    public void addObserver(Observer o);

    public void removeObserver(Observer o);

    public void notifyObserver();
}