// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.maths.random;

import java.security.GeneralSecurityException;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.uncommons.maths.Maths;

/**
 * Unit test for the cellular automaton RNG.
 * @author Daniel Dyer
 */
public class CellularAutomatonRNGTest
{
    /**
     * Test to ensure that two distinct RNGs with the same seed return the
     * same sequence of numbers.
     */
    @Test
    public void testRepeatability()
    {
        CellularAutomatonRNG rng = new CellularAutomatonRNG();
        // Create second RNG using same seed.
        CellularAutomatonRNG duplicateRNG = new CellularAutomatonRNG(rng.getSeed());
        assert RNGTestUtils.testEquivalence(rng, duplicateRNG, 1000) : "Generated sequences do not match.";
    }


    /**
     * Test to ensure that the output from the RNG is broadly as expected.  This will not
     * detect the subtle statistical anomalies that would be picked up by Diehard, but it
     * provides a simple check for major problems with the output.
     */
    @Test(groups = "non-deterministic",
          dependsOnMethods = "testRepeatability")
    public void testDistribution() throws SeedException
    {
        CellularAutomatonRNG rng = new CellularAutomatonRNG(DefaultSeedGenerator.getInstance());
        double pi = RNGTestUtils.calculateMonteCarloValueForPi(rng, 100000);
        Reporter.log("Monte Carlo value for Pi: " + pi);
        assert Maths.approxEquals(pi, Math.PI, 0.01) : "Monte Carlo value for Pi is outside acceptable range:" + pi;
    }


    /**
     * Test to ensure that the output from the RNG is broadly as expected.  This will not
     * detect the subtle statistical anomalies that would be picked up by Diehard, but it
     * provides a simple check for major problems with the output.
     */
    @Test(groups = "non-deterministic",
          dependsOnMethods = "testRepeatability")
    public void testStandardDeviation()
    {
        CellularAutomatonRNG rng = new CellularAutomatonRNG();
        // Expected standard deviation for a uniformly distributed population of values in the range 0..n
        // approaches n/sqrt(12).
        int n = 100;
        double observedSD = RNGTestUtils.calculateSampleStandardDeviation(rng, n, 10000);
        double expectedSD = n / Math.sqrt(12);
        Reporter.log("Expected SD: " + expectedSD + ", observed SD: " + observedSD);
        assert Maths.approxEquals(observedSD, expectedSD, 0.02) : "Standard deviation is outside acceptable range: " + observedSD;
    }


    /**
     * Make sure that the RNG does not accept seeds that are too small since
     * this could affect the distribution of the output.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidSeedSize()
    {
        new CellularAutomatonRNG(new byte[]{1, 2, 3}); // One byte too few, should cause an IllegalArgumentException.
    }


    /**
     * RNG must not accept a null seed otherwise it will not be properly initialised.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullSeed() throws GeneralSecurityException
    {
        new CellularAutomatonRNG((byte[]) null);
    }
}
