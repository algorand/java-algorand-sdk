package com.algorand.algosdk.unittest.indexer;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"progress"}, tags = "@indexer", strict = true)
public class RunCucumberTest {
}
