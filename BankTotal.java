package CloudCoinJavaSDK;

import com.google.gson.annotations.SerializedName;

public class BankTotal extends BaseBankResponse {


    // Fields

    @SerializedName("ones")
    public int ones;

    @SerializedName("fives")
    public int fives;

    @SerializedName("twentyfives")
    public int twentyfives;

    @SerializedName("hundreds")
    public int hundreds;

    @SerializedName("twohundredfifties")
    public int twohundredfifties;

    @SerializedName("status")
    public String status;

    // Constructors

    public BankTotal() {

    }

    public BankTotal(int ones, int fives, int twentyfives, int hundreds, int twohundredfifties) {
        this.ones = ones;
        this.fives = fives;
        this.twentyfives = twentyfives;
        this.hundreds = hundreds;
        this.twohundredfifties = twohundredfifties;
    }
}
