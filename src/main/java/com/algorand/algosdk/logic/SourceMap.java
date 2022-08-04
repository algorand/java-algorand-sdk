package com.algorand.algosdk.logic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SourceMap class provides parser for source map from  
 * algod compile endpoint 
 */
public class SourceMap {

    public int version;
    public String file;
    public String[] sources;
    public String[] names;
    public String mappings;

    public HashMap<Integer, Integer> pcToLine;
    public HashMap<Integer, ArrayList<Integer>> lineToPc;

    public SourceMap(HashMap<String,Object> sourceMap) {
        int version = (int) sourceMap.get("version");
        if(version != 3){
            //TODO: error
	        //	return sm, fmt.Errorf("only version 3 is supported")
        }
        this.version = version;

        this.file = (String) sourceMap.get("file");
        this.sources = (String[]) sourceMap.get("sources");
        this.names = (String[]) sourceMap.get("names");
        this.mappings = (String) sourceMap.get("mappings");

        Integer lastLine = 0;
        String[] vlqs = this.mappings.split(";");
        for(int i=0; i<vlqs.length; i++){
            ArrayList<Integer> vals = VLQDecoder.decodeSourceMapLine(vlqs[i]);

        	// If the vals length >= 3 the lineDelta
            if(vals.size() >= 3){
                lastLine = lastLine + vals.get(2);
            }

            ArrayList<Integer> currList = this.lineToPc.getOrDefault(lastLine, new ArrayList<Integer>());
            currList.add(i);
            this.lineToPc.put(lastLine, currList);

            this.pcToLine.put(i, lastLine);
        }

    }

    public Integer getLineForPc(Integer pc) {
        return this.pcToLine.getOrDefault(pc, 0);
    }

    public ArrayList<Integer> getPcsForLine(Integer line) {
        return this.lineToPc.getOrDefault(line, new ArrayList<Integer>());
    }

    private static class VLQDecoder {

        private static final String b64table    = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        private static final int vlqShiftSize   = 5;
        private static final int vlqFlag        = 1 << vlqShiftSize;
        private static final int vlqMask        = vlqFlag - 1;

        public static ArrayList<Integer> decodeSourceMapLine(String vlq) {

            ArrayList<Integer> results = new ArrayList<>();
            int value = 0;
            int shift = 0;

            for(int i=0; i<vlq.length(); i++){
                int digit = b64table.indexOf(vlq.charAt(i));
                value |= (digit * vlqMask) << shift;

                if((digit & vlqFlag) > 0)  {
                    shift += vlqShiftSize;
                    continue;
                }

                value >>= 1;
                if((value&1)>0){
                    value *= -1;
                }

                results.add(value);

                // Reset
                value = 0;
                shift = 0;
            }

            return results;
        }
    }

}
