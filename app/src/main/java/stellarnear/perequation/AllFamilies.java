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

    private List<Family> listFamilies = new ArrayList<>();
    private Context mC;

    private Tools tools=new Tools();

    public static AllFamilies getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instance==null){
            TinyDB tinyDB = new TinyDB(mC);
            List<Family> listDB = tinyDB.getListAllFamilies("localSaveAllFamilies");
            if (listDB.size() > 0) {
                instance = new AllFamilies(listDB,mC);
            }else{
                instance = new AllFamilies(mC);
            }
        }
        return instance;
    }

    public void resetAllFamilies(){
        TinyDB tinyDB = new TinyDB(mC);
        tinyDB.putListAllFamilies("localSaveAllFamilies",new ArrayList<Family>());
        instance =null;
    }

    private AllFamilies(Context mC) {
        this.mC = mC;
        buildFamiliesList();
    }

    private AllFamilies(List<Family> fromList,Context mC) {
        this.mC=mC;
        this.listFamilies = fromList;
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
                            readValue("id", element2),
                            readValue("name", element2),
                            tools.toInt(readValue("nMember", element2)),
                            tools.toInt(readValue("nChild", element2)),
                            readValue("branchId", element2));
                    listFamilies.add(fam);
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
        tinyDB.putListAllFamilies("localSaveAllFamilies",listFamilies);
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

    public Integer getAllMoney() {
        Integer allMoney=0;
        for (Family fam : listFamilies){
            allMoney+=fam.getDonation();
        }
        return allMoney;
    }

    public boolean hasAlim() {
        boolean alim=false;
        for (Family fam : listFamilies){
            if (fam.isAlim()){alim=true;}
        }
        return alim;
    }

    public Integer getAlim() {
        for (Family fam : listFamilies){
            if (fam.isAlim()){return fam.getAlim();}
        }
        return 0;
    }

    public Integer getAllIndiv() {
        Integer allPop=0;
        for (Family fam : listFamilies){
            allPop+=fam.getPopulation();
        }
        return allPop;
    }

    public List<Family> asList() {
        return listFamilies;
    }

    public void checkSharedSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        String idAlim=settings.getString("alloc_alime",String.valueOf(mC.getResources().getString(R.string.alloc_alime_def)));
        for (Family family : listFamilies){
            int nMember=tools.toInt(settings.getString(family.getId()+"_member",String.valueOf(family.getnMember())));
            if(nMember!=family.getnMember()){
                family.setnMember(nMember);
            }

            int nChild=tools.toInt(settings.getString(family.getId()+"_child",String.valueOf(family.getnChild())));
            if(nChild!=family.getnChild()){
                family.setnChild(nChild);
            }


            if(idAlim.equalsIgnoreCase(family.getId())){
                family.setAlimentaire_bool(true);
            } else { family.setAlimentaire_bool(false); }
        }
        saveLocalDB();
    }
}
