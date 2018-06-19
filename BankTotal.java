package CloudCoinJavaSDK;

/*
  Copyright (c) 2018 Ben Ward, 15/06/18

  This work is licensed under the terms of the MIT license.
  For a copy, see <https://opensource.org/licenses/MIT>.
 */

import com.google.gson.annotations.SerializedName;

/**
 * {@code BankTotal} contains the balance of an account. The balance is separated into denominations
 * for detailed CloudCoin management.
 */
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
