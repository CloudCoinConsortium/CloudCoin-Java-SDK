package CloudCoinJavaSDK;

/*
  Copyright (c) 2018 Ben Ward, 15/06/18

  This work is licensed under the terms of the MIT license.
  For a copy, see <https://opensource.org/licenses/MIT>.
 */

/**
 * {@code BankKeys} holds sensitive encryption keys of the current account.
 *
 * <h3>Usage</h3>
 * <p>
 * A {@code BankKeys} instance must be initialized in order to use a {@link CloudBankUtils} instance.
 * A {@code BankKeys} object requires encryption keys for an account, and the accounts email.
 *
 * <p>
 * <br>
 * <code>BankKeys bankKeys = new BankKeys(publicKey, privateKey, email);
 * </code>
 */
public class BankKeys extends BaseKeys {


    // Constructors

    public BankKeys() {

    }

    public BankKeys(String publickey, String privatekey, String email) {
        this.publickey = publickey;
        this.privatekey = privatekey;
        this.email = email;
    }
}
