package uk.co.pilllogger.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import uk.co.pilllogger.RobolectricGradleTestRunner;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by Alex on 26/09/2014
 * in uk.co.pilllogger.helpers.
 */
@Config(emulateSdk = 18) //Robolectric support API level 18,17, 16, but not 19
@RunWith(RobolectricGradleTestRunner.class)
public class NumberHelperTest {
    @Test
    public void getNiceFloatString_truncates_round_numbers(){
        String niceString = NumberHelper.getNiceFloatString(4.0f);
        assertThat(niceString).isEqualTo("4");
    }

    @Test
    public void getNiceFloatString_leaves_decimals_intact(){
        String niceString = NumberHelper.getNiceFloatString(4.5f);
        assertThat(niceString).isEqualTo("4.5");
    }
}
