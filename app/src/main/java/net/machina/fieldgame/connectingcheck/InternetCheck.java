package net.machina.fieldgame.connectingcheck;

import android.os.AsyncTask;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *  Klasa mająca za zadanie sprawdzenie czy użytkownik jest połaczony z internetem
 */
public class InternetCheck extends AsyncTask<Void, Void, Boolean> {

    /**
     * Interfejs reprezentujący klasę próbującą połączyć sie z siecią
     */
    public interface Consumer{
        /**
         * Abstrakcyjna fukcja w której wykonywane są dalsze operacje w zależności od tego, czy mamy połączenie z internetem, czy nie
         * @param internet informacja o tym czy użytkownikowi udało połączyć sie z siecią
         */
        void accept(boolean internet);
    }

    /**
     * Obiekt typu Consumer przekazywany w konstruktorze klasy.
     */
    Consumer consumer;

    /**
     * Konstruktor klasy, przypisuje parametr Consumer podany w parametrze do obiektu w klasie
     * wywołuje metode "execute" inicjalizującą sprawdzenie połączenia z siecią
     * @param consumer obiekt dla którego ma zostać sprawdzone połaczenie
     */
    public InternetCheck(Consumer consumer){
        this.consumer = consumer;
        execute();
    }

    /**
     * Metodawa wykonująca sie asynchronicznie w tle wysyłająca zapytanie do internetu.
     * W przypadku nie uzyskania połączenia z internetem w przeciągu 1,5 sekundy zwraca informacje o braku połaczenia.
     * @param voids pusty parametr
     * @return      zwraca wartość typu boolean mówiącą czy uzytkownikowi udało sie połączyć z siecią
     */
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80), 1500);
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Metoda wywołująca się po sprawdzeniu połączenia z siecią.
     * Przekazuję czy urządzenie połączone jest z interentem do obiektu typu Consumer.
     * @param aBoolean informacja czy udało się uzyskać połączenie
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        consumer.accept(aBoolean);
    }
}
