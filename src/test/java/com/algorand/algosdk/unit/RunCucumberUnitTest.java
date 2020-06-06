package com.algorand.algosdk.unit;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"progress"}, tags = "@unit.transactions", strict = true)
public class RunCucumberUnitTest {
}
