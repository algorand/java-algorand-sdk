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
            throw new IllegalArgumentException("Only source map version 3 is supported");
        }
        this.version = version;

        this.file = (String) sourceMap.get("file");
        this.mappings = (String) sourceMap.get("mappings");

        this.lineToPc = new HashMap<>();
        this.pcToLine = new HashMap<>();

        Integer lastLine = 0;
        String[] vlqs = this.mappings.split(";");
        for(int i=0; i<vlqs.length; i++){
            ArrayList<Integer> vals = VLQDecoder.decodeSourceMapLine(vlqs[i]);

        	// If the vals length >= 3 the lineDelta
            if(vals.size() >= 3){
                lastLine = lastLine + vals.get(2);
            }

            if(!this.lineToPc.containsKey(lastLine)){
                this.lineToPc.put(lastLine, new ArrayList<Integer>());
            }

            ArrayList<Integer> currList = this.lineToPc.get(lastLine);
            currList.add(i);
            this.lineToPc.put(lastLine, currList);

            this.pcToLine.put(i, lastLine);
        }

    }

    /**
    * Returns the Integer line number for the passed PC or null if not found
    * @param  pc   the pc (program counter) of the assembled file
    * @return      the line number or null if not found 
    */
    public Integer getLineForPc(Integer pc) {
        return this.pcToLine.get(pc);
    }

    /**
    * Returns the List of PCs for the passed line number 
    * @param  line the line number of the source file
    * @return      the list of PCs that line generated or empty array if not found 
    */
    public ArrayList<Integer> getPcsForLine(Integer line) {
        if(!this.pcToLine.containsKey(line)){
            return new ArrayList<Integer>();
        }
        return this.lineToPc.get(line);
    }

    private static class VLQDecoder {
        // VLQDecoder for decoding the VLQ values returned for source map
        // based on the decoder implementation here: https://github.com/algorand/go-algorand-sdk/blob/develop/logic/source_map.go

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

                value |= (digit & vlqMask) << shift;

                if((digit & vlqFlag) > 0)  {
                    shift += vlqShiftSize;
                    continue;
                }

                if((value&1)>0){
                    value = (value >> 1) * -1;
                }else{
                    value = value >> 1;
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
