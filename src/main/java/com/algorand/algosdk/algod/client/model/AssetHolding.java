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

@ApiModel(description = "AssetParams specifies the holdings of a particular asset.")
public class AssetHolding {
  @SerializedName("creator")
  private String creator = null;

  @SerializedName("amount")
  private java.math.BigInteger amount = null;

  @SerializedName("frozen")
  private Boolean frozen = null;
  

  /**
   * Creator specifies the address that created this asset. This is the address
   * where the parameters for this asset can be found, and also the address
   * where unwanted asset units can be sent in the worst case.
   * @return creator
   */
  @ApiModelProperty(value = "Creator specifies the address that created this asset. This is the address where the parameters for this asset can be found, and also the address where unwanted asset units can be sent in the worst case.")
  public String getCreator() {
    return creator;
  }

  /**
   * Amount specifies the number of units held.
   * @return amount
   */
  @ApiModelProperty(value = "Amount specifies the number of units held.")
  public java.math.BigInteger getAmount() {
      return amount;
  }

  /**
   * Frozen specifies whether this holding is frozen.
   * @return frozen
   */
  @ApiModelProperty(value = "Frozen specifies whether this holding is frozen.")
  public Boolean getFrozen() {
    return frozen;
  }
}
