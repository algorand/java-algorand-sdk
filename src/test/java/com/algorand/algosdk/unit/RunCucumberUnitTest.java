package com.algorand.algosdk.unit;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"progress"}, tags = "@disabled.by.default", strict = true, extraGlue = "com.algorand.algosdk.cucumber.shared")
public class RunCucumberUnitTest {
}
