package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.DryrunResponse;


/**
 * /v2/teal/dryrun
 */
public class TealDryRun extends Query {

    public TealDryRun(Client client) {
        super(client, new HttpMethod("post"));
    }

    /**
     * Transaction (or group) and any accompanying state-simulation data
     */
    public TealDryRun jsonobj(DryrunRequest jsonobj) {
        addToBody(jsonobj);
        return this;
    }

    /**
     * Transaction (or group) and any accompanying state-simulation data
     */
    public TealDryRun rawobj(byte[] rawobj) {
        addToBody(rawobj);
        return this;
    }

    @Override
    public Response<DryrunResponse> execute() throws Exception {
        Response<DryrunResponse> resp = baseExecute();
        resp.setValueType(DryrunResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("teal"));
        addPathSegment(String.valueOf("dryrun"));

        return qd;
    }
}