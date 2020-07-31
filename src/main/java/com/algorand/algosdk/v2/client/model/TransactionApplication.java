package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields for application transactions.
 * Definition:
 * data/transactions/application.go : ApplicationCallTxnFields
 */
public class TransactionApplication extends PathResponse {

    /**
     * (apat) List of accounts in addition to the sender that may be accessed from the
     * application's approval-program and clear-state-program.
     */
    @JsonProperty("accounts")
    public void accounts(List<String> accounts) throws NoSuchAlgorithmException {
        this.accounts = new ArrayList<Address>();
        for (String val : accounts) {
            this.accounts.add(new Address(val));
        }
    }
    @JsonProperty("accounts")
    public List<String> accounts() throws NoSuchAlgorithmException {
        ArrayList<String> ret = new ArrayList<String>();
        for (Address val : this.accounts) {
            ret.add(val.encodeAsString());
        }
        return ret;
    }
    public List<Address> accounts = new ArrayList<Address>();

    /**
     * (apaa) transaction specific arguments accessed from the application's
     * approval-program and clear-state-program.
     */
    @JsonProperty("application-args")
    public void applicationArgs(List<String> base64Encoded) {
         this.applicationArgs = new ArrayList<byte[]>();
         for (String val : base64Encoded) {
             this.applicationArgs.add(Encoder.decodeFromBase64(val));
         }
     }
     @JsonProperty("application-args")
     public List<String> applicationArgs() {
         ArrayList<String> ret = new ArrayList<String>();
         for (byte[] val : this.applicationArgs) {
             ret.add(Encoder.encodeToBase64(val));
         }
         return ret; 
     }
    public List<byte[]> applicationArgs;

    /**
     * (apid) ID of the application being configured or empty if creating.
     */
    @JsonProperty("application-id")
    public Long applicationId;

    /**
     * (apap) Logic executed for every application transaction, except when
     * on-completion is set to "clear". It can read and write global state for the
     * application, as well as account-specific local state. Approval programs may
     * reject the transaction.
     */
    @JsonProperty("approval-program")
    public void approvalProgram(String base64Encoded) {
        this.approvalProgram = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("approval-program")
    public String approvalProgram() {
        return Encoder.encodeToBase64(this.approvalProgram);
    }
    public byte[] approvalProgram;

    /**
     * (apsu) Logic executed for application transactions with on-completion set to
     * "clear". It can read and write global state for the application, as well as
     * account-specific local state. Clear state programs cannot reject the
     * transaction.
     */
    @JsonProperty("clear-state-program")
    public void clearStateProgram(String base64Encoded) {
        this.clearStateProgram = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("clear-state-program")
    public String clearStateProgram() {
        return Encoder.encodeToBase64(this.clearStateProgram);
    }
    public byte[] clearStateProgram;

    /**
     * (apfa) Lists the applications in addition to the application-id whose global
     * states may be accessed by this application's approval-program and
     * clear-state-program. The access is read-only.
     */
    @JsonProperty("foreign-apps")
    public List<Long> foreignApps = new ArrayList<Long>();

    /**
     * (apas) lists the assets whose parameters may be accessed by this application's
     * ApprovalProgram and ClearStateProgram. The access is read-only.
     */
    @JsonProperty("foreign-assets")
    public List<Long> foreignAssets = new ArrayList<Long>();

    /**
     * Represents a (apls) local-state or (apgs) global-state schema. These schemas
     * determine how much storage may be used in a local-state or global-state for an
     * application. The more space used, the larger minimum balance must be maintained
     * in the account holding the data.
     */
    @JsonProperty("global-state-schema")
    public StateSchema globalStateSchema;

    /**
     * Represents a (apls) local-state or (apgs) global-state schema. These schemas
     * determine how much storage may be used in a local-state or global-state for an
     * application. The more space used, the larger minimum balance must be maintained
     * in the account holding the data.
     */
    @JsonProperty("local-state-schema")
    public StateSchema localStateSchema;

    /**
     * (apan) defines the what additional actions occur with the transaction.
     * Valid types:
     *   noop
     *   optin
     *   closeout
     *   clear
     *   update
     *   update
     *   delete
     */
    @JsonProperty("on-completion")
    public Enums.OnCompletion onCompletion;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionApplication other = (TransactionApplication) o;
        if (!Objects.deepEquals(this.accounts, other.accounts)) return false;
        if (!Objects.deepEquals(this.applicationArgs, other.applicationArgs)) return false;
        if (!Objects.deepEquals(this.applicationId, other.applicationId)) return false;
        if (!Objects.deepEquals(this.approvalProgram, other.approvalProgram)) return false;
        if (!Objects.deepEquals(this.clearStateProgram, other.clearStateProgram)) return false;
        if (!Objects.deepEquals(this.foreignApps, other.foreignApps)) return false;
        if (!Objects.deepEquals(this.foreignAssets, other.foreignAssets)) return false;
        if (!Objects.deepEquals(this.globalStateSchema, other.globalStateSchema)) return false;
        if (!Objects.deepEquals(this.localStateSchema, other.localStateSchema)) return false;
        if (!Objects.deepEquals(this.onCompletion, other.onCompletion)) return false;

        return true;
    }
}
