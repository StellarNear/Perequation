package stellarnear.perequation;

public class PairFamilyTranfertSum {

    private Family recivier;
    private int sumMoney=0;
    public PairFamilyTranfertSum(Family reciever,int sumMoney){
        this.recivier=reciever;
        this.sumMoney=sumMoney;
    }

    public Family getRecivier() {
        return recivier;
    }

    public int getSumMoney() {
        return sumMoney;
    }
}
