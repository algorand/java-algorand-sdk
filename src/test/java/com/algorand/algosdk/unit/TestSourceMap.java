package com.algorand.algosdk.unit;

import com.algorand.algosdk.logic.SourceMap;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.ResourceUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class TestSourceMap {
    SourceMap srcMap; 

    @Given("a source map json file {string}")
    public void a_source_map_json_file(String srcMapPath) throws IOException {
        String srcMapStr = new String(ResourceUtils.readResource(srcMapPath), StandardCharsets.UTF_8);
        HashMap<String, Object> map = new HashMap<>(Encoder.decodeFromJson(srcMapStr, Map.class));
        this.srcMap = new SourceMap(map);
    }

    @Then("the string composed of pc:line number equals {string}")
    public void the_string_composed_of_pc_line_number_equals(String expected) {
        List<String> strs = new ArrayList<>();
        for(Map.Entry<Integer,Integer> entry: this.srcMap.pcToLine.entrySet()){
            strs.add(String.format("%d:%d", entry.getKey(), entry.getValue()));
        }

        String actual = StringUtils.join(strs, ";");
        assertThat(actual).isEqualTo(expected);
    }

    @Then("getting the last pc associated with a line {string} equals {string}")
    public void getting_the_last_pc_associated_with_a_line_equals(String lineStr, String pcStr) {
        Integer line = Integer.valueOf(lineStr);
        Integer pc = Integer.valueOf(pcStr);

        ArrayList<Integer> actualPcs = this.srcMap.getPcsForLine(line);
        assertThat(actualPcs.get(actualPcs.size() - 1)).isEqualTo(pc);
    }


    @Then("getting the line associated with a pc {string} equals {string}")
    public void getting_the_line_associated_with_a_pc_equals(String pcStr, String lineStr) {
        Integer line = Integer.valueOf(lineStr);
        Integer pc = Integer.valueOf(pcStr);

        Integer actualLine = this.srcMap.getLineForPc(pc);
        assertThat(actualLine).isEqualTo(line);
    }

}
