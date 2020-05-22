package com.algorand.algosdk.unit;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"progress"}, tags = "@disabled.by.default", strict = true)
public class RunCucumberUnitTest {
}
