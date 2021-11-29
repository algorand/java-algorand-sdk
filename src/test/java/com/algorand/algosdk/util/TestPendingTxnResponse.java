package com.algorand.algosdk.util;

import com.algorand.algosdk.abi.Type;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.*;

public class TestPendingTxnResponse {

    /**
     * {
     *   "confirmed-round" : 14738,
     *   "global-state-delta" : [ ],
     *   "inner-txns" : [ ],
     *   "local-state-delta" : [ ],
     *   "logs" : [ "FR98dQAAAAAAAAAC" ],
     *   "txn" : {
     *     "sig" : "8EoeJD73fSuoqgOKDaBhqQnPFFo5HdymGqOSne0hzqOsOoOKljmJP2zUbBfYiRSs2farw1VOTEsu49/PYY9MCw==",
     *     "txn" : {
     *       "apaa" : [ "/mvfaQ==", "AAAAAAAAAAE=", "AAAAAAAAAAE=" ],
     *       "apid" : 390,
     *       "fee" : 1000,
     *       "fv" : 14732,
     *       "gen" : "testnetwork-v1",
     *       "gh" : "ipnyRzqHONkFat3M59UYFo3IPI4MWYuytrm5gTIR12o=",
     *       "grp" : "YAOKW9YVvCzjocYxGThCjg7WKWKSwQcRT6BBB4hz0Ns=",
     *       "lv" : 15732,
     *       "snd" : "TLvOoxFBq21lyzRr60Iak+nvNXGuNprQFBMcb+RnlK4=",
     *       "type" : "appl"
     *     }
     *   }
     * }
     * {
     *   "confirmed-round" : 14750,
     *   "global-state-delta" : [ ],
     *   "inner-txns" : [ ],
     *   "local-state-delta" : [ ],
     *   "logs" : [ "FR98dYA=" ],
     *   "txn" : {
     *     "sig" : "rztuI4DCmqsVV9gPa0KzRz/YmviDiwolflfCLXw8ixN/rGj3D34y4kMB0CvzUdElqkP6aKNlX/vK7FpZiEbYAQ==",
     *     "txn" : {
     *       "apaa" : [ "U1pHug==", "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=" ],
     *       "apid" : 398,
     *       "fee" : 1000,
     *       "fv" : 14744,
     *       "gen" : "testnetwork-v1",
     *       "gh" : "ipnyRzqHONkFat3M59UYFo3IPI4MWYuytrm5gTIR12o=",
     *       "grp" : "eM+mfCfxXAd32GCNiXQqFPQycoC4ySGk1tfZJHQ6RrY=",
     *       "lv" : 15744,
     *       "snd" : "kkBUGlUbl/Bvu9FK4AhMNCPZ4RuX4bq5xOsTSdcO0XU=",
     *       "type" : "appl"
     *     }
     *   }
     * }
     */
    @ParameterizedTest
    @ValueSource(
            strings = {
                    "g69jb25maXJtZWQtcm91bmTNOZKkbG9nc5HEDBUffHUAAAAAAAAAAqN0eG6Co3NpZ8RA8EoeJD73fSuoqgOKDaBhqQnPFFo5HdymGqOSne0hzqOsOoOKljmJP2zUbBfYiRSs2farw1VOTEsu49/PYY9MC6N0eG6KpGFwYWGTxAT+a99pxAgAAAAAAAAAAcQIAAAAAAAAAAGkYXBpZM0BhqNmZWXNA+iiZnbNOYyjZ2VurnRlc3RuZXR3b3JrLXYxomdoxCCKmfJHOoc42QVq3czn1RgWjcg8jgxZi7K2ubmBMhHXaqNncnDEIGADilvWFbws46HGMRk4Qo4O1iliksEHEU+gQQeIc9Dbomx2zT10o3NuZMQgTLvOoxFBq21lyzRr60Iak+nvNXGuNprQFBMcb+RnlK6kdHlwZaRhcHBs",
                    "g69jb25maXJtZWQtcm91bmTNOZ6kbG9nc5HEBRUffHWAo3R4boKjc2lnxECvO24jgMKaqxVX2A9rQrNHP9ia+IOLCiV+V8ItfDyLE3+saPcPfjLiQwHQK/NR0SWqQ/poo2Vf+8rsWlmIRtgBo3R4boqkYXBhYZLEBFNaR7rEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9pGFwaWTNAY6jZmVlzQPoomZ2zTmYo2dlbq50ZXN0bmV0d29yay12MaJnaMQgipnyRzqHONkFat3M59UYFo3IPI4MWYuytrm5gTIR12qjZ3JwxCB4z6Z8J/FcB3fYYI2JdCoU9DJygLjJIaTW19kkdDpGtqJsds09gKNzbmTEIJJAVBpVG5fwb7vRSuAITDQj2eEbl+G6ucTrE0nXDtF1pHR5cGWkYXBwbA==",
            }
    )
    public void decodeFromB64(String str) throws IOException {
        byte[] decodedBytes = Encoder.decodeFromBase64(str);

        PendingTransactionResponse decoded =
                Encoder.decodeFromMsgPack(decodedBytes, PendingTransactionResponse.class);
        assertThat(decoded.logs.size()).isEqualTo(1);

        byte[] retLog = decoded.logs.get(0);
        assertThat(retLog.length).isGreaterThan(4);
        byte[] retPrefix = new byte[]{0x15, 0x1f, 0x7c, 0x75};
        for (int i = 0; i < 4; i++)
            assertThat(retLog[i]).isEqualTo(retPrefix[i]);

        byte[] retVal = new byte[retLog.length - 4];
        System.arraycopy(retLog, 4, retVal, 0, retLog.length -  4);

        if (retVal.length == 1) {
            assertThat(Type.fromString("bool").decode(retVal)).isEqualTo(true);
        } else {
            assertThat(Type.fromString("uint64").decode(retVal)).isEqualTo(BigInteger.valueOf(2));
        }
    }
}
