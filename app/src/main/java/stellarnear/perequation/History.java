package stellarnear.perequation;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class History {
    private List<Record> history = new ArrayList<>();
    private TinyDB tinyDB;

    public History(Context mC) {
        tinyDB = new TinyDB(mC);
        try {
            refreshFromDB();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Load_HALL", "Error loading history " + e.getMessage());
            reset();
        }
    }

    // hall of frame

    private void refreshFromDB() {
        List<Record> listDB = tinyDB.getHistory("localSaveHistory");
        if (listDB.size() == 0) {
            initAllOfFame();
            saveLocalHallOfFame();
        } else {
            history = listDB;
        }
    }

    private void initAllOfFame() {
        this.history = new ArrayList<>();
    }

    private void saveLocalHallOfFame() { //sauvegarde dans local DB
        tinyDB.putHistory("localSaveHistory", history);
    }

    public List<Record> gettAllHistoryFamilies() {
        return history;
    }

    public void addRecord(Record entry) {
        history.add(entry);
        saveLocalHallOfFame();
    }

    public void refreshSave() {
        saveLocalHallOfFame();
    }

    public void reset() {
        this.history = new ArrayList<>();
        saveLocalHallOfFame();
    }

    public void loadFromSave() {
        refreshFromDB();
    }

    public Record getRecordForTimestamp(long id) {
        Record result=null;
        for(Record rec : history){
            if(rec.getTimestamp()==id){
                result = rec;
            }
        }
        return result;
    }

    public static class Record{
        private GregorianCalendar calendar;
        private FamilyList families;
        private double moneyPerIndiv;
        private long timestamp;

        public Record(GregorianCalendar calendar, FamilyList families, double moneyPerIndiv) {
            this.calendar=calendar;
            this.families=families;
            this.moneyPerIndiv=moneyPerIndiv;
            this.timestamp=calendar.getTimeInMillis();
        }

        public long getTimestamp() {
            return timestamp;
        }

        public FamilyList getFamilies() {
            return families;
        }

        public GregorianCalendar getCalendar() {
            return calendar;
        }

        public double getMoneyPerIndiv() {
            return moneyPerIndiv;
        }

    }
}
