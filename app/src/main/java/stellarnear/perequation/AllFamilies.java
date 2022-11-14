package stellarnear.perequation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by jchatron on 05/01/2018.
 */

public class AllFamilies {

    private static AllFamilies instance=null;

    private FamilyList familyList = new FamilyList();
    private Context mC;

    private Calculation calculation=null;
    private TransfertManager transfertManager=null;

    private Tools tools=new Tools();

    public static AllFamilies getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instance==null){
            TinyDB tinyDB = new TinyDB(mC);
            ArrayList<Family> listDB = tinyDB.getListAllFamilies("localSaveAllFamilies");
            if (listDB.size() > 0) {
                instance = new AllFamilies(listDB,mC);
            }else{
                instance = new AllFamilies(mC);
            }
        }
        return instance;
    }

    public void eraseAllFamilies(){
        TinyDB tinyDB = new TinyDB(mC);
        tinyDB.putListAllFamilies("localSaveAllFamilies",new ArrayList<Family>());
        instance =null;
    }

    public void reset(){
        for (Family fam : familyList.asList()){
            fam.reset();
        }
    }

    private AllFamilies(Context mC) {
        this.mC = mC;
        buildFamiliesList();
    }

    private AllFamilies(ArrayList<Family> fromList,Context mC) {
        this.mC=mC;
        this.familyList = new FamilyList(fromList);
    }

    private void buildFamiliesList() {
        try {
            InputStream is = mC.getAssets().open("allfamilies.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("family");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Family fam = new Family(
                            readValue("name", element2),
                            tools.toInt(readValue("nMember", element2)),
                            tools.toInt(readValue("nChild", element2)),
                            readValue("branchId", element2));
                    familyList.add(fam);
                }
            }
            is.close();

            checkSharedSettings();
            saveLocalDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLocalDB() {
        TinyDB tinyDB = new TinyDB(mC);
        tinyDB.putListAllFamilies("localSaveAllFamilies", familyList.asList());
    }

    private String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }


    public List<Family> asList() {
        return familyList.asList();
    }

    public void checkSharedSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);

        for (Family family : familyList.asList()){
            int nMember=tools.toInt(settings.getString(family.getId()+"_member",String.valueOf(family.getnMember())));
            if(nMember!=family.getnMember()){
                family.setnMember(nMember);
            }

            int nChild=tools.toInt(settings.getString(family.getId()+"_child",String.valueOf(family.getnChild())));
            if(nChild!=family.getnChild()){
                family.setnChild(nChild);
            }

            String idAlim=settings.getString("alloc_alime",String.valueOf(mC.getResources().getString(R.string.alloc_alime_def)));
            if(idAlim.equalsIgnoreCase(family.getId())){
                family.setAlimentaire_bool(true);
            } else { family.setAlimentaire_bool(false); }
        }
        saveLocalDB();
    }

    public Calculation getCalculation(){
        if(calculation==null){
            calculation=new Calculation(mC,this.familyList);
        }
        return calculation;
    }

    public TransfertManager getTransfertManager(){
        if(transfertManager==null){
            transfertManager=new TransfertManager(mC,this.familyList);
        }
        return transfertManager;
    }


    public FamilyList getFamList() {
        return familyList;
    }

    public void removeFamily(Family tempRemoveFamily) {
        familyList.remove(tempRemoveFamily);
        saveLocalDB();
    }

    public void addFamily(Family fam) {
        familyList.add(fam);
        saveLocalDB();
    }
}
