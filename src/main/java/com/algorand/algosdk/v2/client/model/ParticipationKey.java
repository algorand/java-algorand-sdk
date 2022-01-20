package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.*;

/**
 * Represents a participation key used by the node.
 */
public class ParticipationKey extends PathResponse {

    /**
     * Address the key was generated for.
     */
    @JsonProperty("address")
    public void address(String address) throws NoSuchAlgorithmException {
        this.address = new Address(address);
    }
    @JsonProperty("address")
    public String address() throws NoSuchAlgorithmException {
        if (this.address != null) {
            return this.address.encodeAsString();
        } else {
            return null;
        }
    }
    public Address address;

    /**
     * When registered, this is the first round it may be used.
     */
    @JsonProperty("effective-first-valid")
    public java.math.BigInteger effectiveFirstValid;

    /**
     * When registered, this is the last round it may be used.
     */
    @JsonProperty("effective-last-valid")
    public java.math.BigInteger effectiveLastValid;

    /**
     * The key's ParticipationID.
     */
    @JsonProperty("id")
    public String id;

    /**
     * Key information stored on the account.
     */
    @JsonProperty("key")
    public AccountParticipation key;

    /**
     * Round when this key was last used to propose a block.
     */
    @JsonProperty("last-block-proposal")
    public Long lastBlockProposal;

    /**
     * Round when this key was last used to generate a state proof.
     */
    @JsonProperty("last-state-proof")
    public Long lastStateProof;

    /**
     * Round when this key was last used to vote.
     */
    @JsonProperty("last-vote")
    public Long lastVote;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ParticipationKey other = (ParticipationKey) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.effectiveFirstValid, other.effectiveFirstValid)) return false;
        if (!Objects.deepEquals(this.effectiveLastValid, other.effectiveLastValid)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;
        if (!Objects.deepEquals(this.key, other.key)) return false;
        if (!Objects.deepEquals(this.lastBlockProposal, other.lastBlockProposal)) return false;
        if (!Objects.deepEquals(this.lastStateProof, other.lastStateProof)) return false;
        if (!Objects.deepEquals(this.lastVote, other.lastVote)) return false;

        return true;
    }
}
