package com.algorand.algosdk.unit;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(publish = false, plugin = {"progress"}, tags = "@unit.responses.stateproof", extraGlue = "com.algorand.algosdk.cucumber.shared")
public class RunCucumberUnitTest {
}
