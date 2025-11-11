package stellarnear.perequation;

import android.app.Activity;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SendMail {

    private static final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);

    public static void sendDownloadEmail(Activity mA, String emailAdress, Family family, ArrayList<PairFamilyTranfertSum> reciversForDonator) throws Exception {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("vnd.android.cursor.dir/email");
        String[] to = emailAdress.split(",");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Perequation - Virements à faire " + BuildConfig.APPLICATION_ID.replace("stellarnear.", "") + " " + formater.format(new Date()));


        String text = "Merci cher " + family.getName() + " de faire parti des généreux donateurs, sans vous la belle péréquation de cette merveilleuse famille ne pourrait avoir lieu :)\n\nVoici un récapitulatif des différents transferts de fond :\n";
        for (PairFamilyTranfertSum pairFamilyTranfertSum : reciversForDonator) {
            text += pairFamilyTranfertSum.getSumMoney() + "€ pour " + pairFamilyTranfertSum.getRecivier().getName() + "\n";

        }

        emailIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooser = Intent.createChooser(emailIntent, "Transferts monétaires");

        mA.startActivity(chooser);
    }
}
