package CloudCoinJavaSDK;

/*
  Copyright (c) 2018 Ben Ward, 15/06/18

  This work is licensed under the terms of the MIT license.
  For a copy, see <https://opensource.org/licenses/MIT>.
 */

import com.google.gson.annotations.SerializedName;

/**
 * {@code DepositResponse} saves a CloudServer's response from a successful CloudCoin exchange.
 */
public class DepositResponse extends BaseBankResponse {


    // Fields

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("receipt")
    public String receipt;


    // Constructor

    public DepositResponse() {

    }
}
