package stellarnear.perequation;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

class Email extends AsyncTask {
    private static String user = "perequation.chatron@gmail.com";
    private static String pass = "fkeifwxikkjnlano";
    private Tools tools=new Tools();
    private Exception error=null;

    @Override
    protected Object doInBackground(Object[] objects) {
        String emailAdress = (String) objects[0];
        Family family = (Family) objects[1];
        ArrayList<PairFamilyTranfertSum> reciversForDonator = (ArrayList<PairFamilyTranfertSum>) objects[2];

        String text = "Merci cher " + family.getName() + " de faire parti des généreux donateurs, sans vous la belle péréquation de cette merveilleuse famille ne pourrait avoir lieu :)\n\nVoici un récapitulatif des différents transferts de fond :\n";
        for (PairFamilyTranfertSum pairFamilyTranfertSum : reciversForDonator) {
            text += pairFamilyTranfertSum.getSumMoney() + "€ pour " + pairFamilyTranfertSum.getRecivier().getName() + "\n";

        }

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true"); // TLS

        // Get the default Session object.
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {
            // Set From: header field of the header.
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(text.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(user));
            message.setSubject("Recapitulatif Péréquation");
            message.setDataHandler(handler);

            String []emails = emailAdress.split(",");
            for(int i=0;i<emails.length;i++) {
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(emails[i]));
            }
            Transport.send(message);
        } catch (Exception mex) {
            error=mex;
        }
        return null;
    }

    protected void onPostExecute(Activity mA) {
        if ( error != null ) {
            tools.customToast(mA,"Le mail n'a pas pu être envoyé\n\n"+error.getMessage());
        } else {
            tools.customToast(mA,"Mail envoyé!");
        }
    };
}



