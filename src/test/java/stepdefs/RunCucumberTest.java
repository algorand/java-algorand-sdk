package stepdefs;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import org.junit.Test;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"progress"}, features = {"src/test/resources/features"})
public class RunCucumberTest {
    @Test
    public void test(){

    }
}
