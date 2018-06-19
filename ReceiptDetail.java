package CloudCoinJavaSDK;

/*
  Copyright (c) 2018 Ben Ward, 15/06/18

  This work is licensed under the terms of the MIT license.
  For a copy, see <https://opensource.org/licenses/MIT>.
 */

import com.google.gson.annotations.SerializedName;

/**
 * {@code ReceiptDetail} contains detailed information about a single CloudCoin in a {@link  Receipt}.
 */
public class ReceiptDetail {


    //Fields

    @SerializedName("nn")
    public int nn;

    @SerializedName("sn")
    public int sn;

    @SerializedName("status")
    public String status;

    @SerializedName("pown")
    public String pown;

    @SerializedName("note")
    public String note;


    //Constructors

    public ReceiptDetail() {

    }

    public ReceiptDetail(int nn, int sn, String status, String pown, String note) {
        this.nn = nn;
        this.sn = sn;
        this.status = status;
        this.pown = pown;
        this.note = note;
    }
}
