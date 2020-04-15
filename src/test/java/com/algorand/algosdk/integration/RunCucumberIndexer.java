package com.algorand.algosdk.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"progress"}, tags = "@indexer", strict = true)
public class RunCucumberIndexer {
}
