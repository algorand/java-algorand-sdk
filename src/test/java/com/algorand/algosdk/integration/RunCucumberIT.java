package com.algorand.algosdk.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"progress"}, tags = "@algod or @assets or @auction or @kmd or @unit or @send or @template or @indexer", strict = true)
public class RunCucumberIT {
}
