package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * All logs emitted in the given round. Each app call, whether top-level or inner,
 * that contains logs results in a separate AppCallLogs object. Therefore there may
 * be multiple AppCallLogs with the same application ID and outer transaction ID in
 * the event of multiple inner app calls to the same app. App calls with no logs
 * are not included in the response. AppCallLogs are returned in the same order
 * that their corresponding app call appeared in the block (pre-order traversal of
 * inner app calls)
 */
public class BlockLogsResponse extends PathResponse {

    @JsonProperty("logs")
    public List<AppCallLogs> logs = new ArrayList<AppCallLogs>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BlockLogsResponse other = (BlockLogsResponse) o;
        if (!Objects.deepEquals(this.logs, other.logs)) return false;

        return true;
    }
}
