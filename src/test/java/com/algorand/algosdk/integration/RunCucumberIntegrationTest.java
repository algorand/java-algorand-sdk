package com.algorand.algosdk.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, tags = "@indexer.applications", strict = true, extraGlue = "com.algorand.algosdk.cucumber.shared")
public class RunCucumberIntegrationTest {
}
