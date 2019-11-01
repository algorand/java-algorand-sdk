/*
 * Algod REST API.
 * API Endpoint for AlgoD Operations.
 *
 * OpenAPI spec version: 0.0.1
 * Contact: contact@algorand.com
 *
 */

package com.algorand.algosdk.algod.client.model;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "AssetParams specifies the parameters for an asset")
public class AssetHolding {
  @SerializedName("creator")
  private String creator = null;

  @SerializedName("amount")
  private java.math.BigInteger amount = null;

  @SerializedName("frozen")
  private Boolean frozen = null;
  
  public java.math.BigInteger getAmount() {
      return amount;
  }
}
