package com.algorand.algosdk.util;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.AppBoxReference;

import org.assertj.core.api.Assertions;
import org.bouncycastle.util.Strings;

import okio.ByteString;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConversionUtils {
    /**
     * Convert an array of arguments like "str:arg1,str:arg2" into a properly converted byte array.
     */
    public static List<byte[]> convertArgs(String args) {
        // Hell yeah, I missed this unreadable Java streams functional magic!
        return Arrays.stream(Strings.split(args, ','))
                .map(s -> {
                    String[] parts = Strings.split(s, ':');
                    byte[] converted = null;
                    switch (parts[0]) {
                        case "str":
                            converted = parts[1].getBytes();
                            break;
                        case "int":
                            converted = BigInteger.valueOf(Integer.parseInt(parts[1])).toByteArray();
                            break;
                        default:
                            Assertions.fail("Doesn't currently support '" + parts[0] + "' convertion.");
                    }
                    return converted;
                })
                .collect(Collectors.toList());
    }

    /**
     * Convert list of numbers to list of longs.
     */
    public static List<Long> convertForeignApps(String foreignApps) {
        return Arrays.stream(Strings.split(foreignApps, ','))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public static List<Long> convertForeignAssets(String foreignAssets) {
        return Arrays.stream(Strings.split(foreignAssets, ','))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public static Address convertOrFailTest(String addrString) {
        try {
            return new Address(addrString);
        } catch (NoSuchAlgorithmException e) {
            Assertions.fail("Failed to parse address: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<Address> convertAccounts(String accounts) {
        return Arrays.stream(Strings.split(accounts, ','))
                .map(ConversionUtils::convertOrFailTest)
                .collect(Collectors.toList());
    }

    public static List<AppBoxReference> convertBoxes(String boxesStr) {
        if (boxesStr.equals("")) {
            return null;
        }

        ArrayList<AppBoxReference> boxReferences = new ArrayList<>();
        String[] boxesArray = Strings.split(boxesStr, ',');
        for (int i = 0; i < boxesArray.length; i += 2) {
            Long appID = Long.parseLong(boxesArray[i]);
            byte[] name = null;
            String enc = Strings.split(boxesArray[i + 1], ':')[0];
            String strName = Strings.split(boxesArray[i + 1], ':')[1];
            if (enc.equals("str")) {
                name = strName.getBytes();
            } else {
                // b64 encoding
                name = ByteString.decodeBase64(strName).toByteArray();
            }

            boxReferences.add(new AppBoxReference(appID, name));
        }

        return boxReferences;
    }

}
