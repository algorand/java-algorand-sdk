package com.algorand.algosdk.unit;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, tags = "@disabled.by.default", strict = true)
public class RunCucumberUnitTest {
}
