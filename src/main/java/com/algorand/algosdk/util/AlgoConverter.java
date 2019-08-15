package com.algorand.algosdk.util;


import java.math.BigDecimal;
import java.math.BigInteger;

public class AlgoConverter {

    private final static BigDecimal ALGOS_TO_MICROALGOS_RATIO = BigDecimal.valueOf(1000000L);

    /**
     * Convert microalgos to algos.
     * @param microalgos
     * @return algos
     */
    public static BigDecimal toAlgos(BigInteger microalgos) {
        BigDecimal algos = new BigDecimal(microalgos).divide(ALGOS_TO_MICROALGOS_RATIO);
        return algos;
    }

    /**
     * Convert algos to microalgos.
     * @param algos
     * @return microalgos
     */
    public static BigInteger toMicroAlgos(BigDecimal algos) {
        BigDecimal microalgos = algos.multiply(ALGOS_TO_MICROALGOS_RATIO);
        return microalgos.toBigInteger();
    }

}
