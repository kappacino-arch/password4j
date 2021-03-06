/*
 *  (C) Copyright 2020 Password4j (http://password4j.com/).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.password4j;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class PBKDF2FunctionTest
{


    @Test
    public void testPBKDF2()
    {
        // GIVEN
        HashingFunction strategy = CompressedPBKDF2Function.getInstance(PBKDF2Function.Algorithm.SHA256, 10_000, 256);
        String password = "password";
        String salt = "abc";

        // WHEN
        Hash hash = strategy.hash(password, salt);

        // THEN
        Assert.assertEquals("$3$42949672960256$YWJj$/WTQfTTc8Hg8GlplP0LthpgdElUG+I3MyuvK8MI4MnQ=", hash.getResult());
    }

    @Test
    public void testPBKDF2EachVariants()
    {
        for (PBKDF2Function.Algorithm alg : PBKDF2Function.Algorithm.values())
        {
            // GIVEN
            HashingFunction strategy = CompressedPBKDF2Function.getInstance(alg, 10_000, 256);
            String password = "password";
            String salt = "abc";

            // WHEN
            Hash hash = strategy.hash(password, salt);

            // THEN
            Assert.assertTrue(hash.getResult().startsWith("$" + alg.code() + "$"));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPBKDF2WrongAlgorithm()
    {
        // GIVEN
        HashingFunction strategy = PBKDF2Function.getInstance("notAnAlgorithm", 10_000, 256);
        String password = "password";
        String salt = "abc";

        // WHEN
        strategy.hash(password, salt);

        // THEN
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPBKDF2WrongAlgorithm2()
    {
        // GIVEN
        HashingFunction strategy = CompressedPBKDF2Function.getInstance("notAnAlgorithm", 10_000, 256);
        String password = "password";
        String salt = "abc";

        // WHEN
        strategy.hash(password, salt);

        // THEN
    }

    @Test(expected = BadParametersException.class)
    public void testPBKDF2WrongSalt()
    {
        // GIVEN
        HashingFunction strategy = PBKDF2Function.getInstance(PBKDF2Function.Algorithm.SHA224, 10_000, 224);
        String password = "password";
        String salt = new String(new byte[0]);

        // WHEN
        strategy.hash(password, salt);

        // THEN
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPBKDF2WrongAlgorithmSalt()
    {
        // GIVEN
        HashingFunction strategy = PBKDF2Function.getInstance("notAnAlgorithm", 10_000, 256);
        String password = "password";
        String salt = new String(new byte[0]);

        // WHEN
        strategy.hash(password, salt);

        // THEN
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPBKDF2WrongCheck()
    {
        // GIVEN
        HashingFunction strategy = AlgorithmFinder.getPBKDF2Instance();
        String password = "password";
        String salt = "salt";
        Hash hash = strategy.hash(password, salt);

        // WHEN
        strategy.check(password, hash.getResult());
    }

    @Test
    public void testPBKDF2Check()
    {
        // GIVEN
        String hashed = "$3$42949672960256$YWJj$/WTQfTTc8Hg8GlplP0LthpgdElUG+I3MyuvK8MI4MnQ=";
        String userSubmittedPassword = "password";

        // WHEN
        HashingFunction strategy = CompressedPBKDF2Function.getInstanceFromHash(hashed);

        // THEN
        Assert.assertTrue(strategy.check(userSubmittedPassword, hashed));
    }


    @Test(expected = BadParametersException.class)
    public void testPBKDF2WrongCheck2()
    {
        // GIVEN
        String hashed = "$3$42949672960256$YWJj$/WTQfTTc8Hg8GlplP0LthpgdElUG+I3MyuvK8MI4MnQ=";
        String badHash = "$342949672960256$YWJj$/WTQfTTc8Hg8GlplP0LthpgdElUG+I3MyuvK8MI4MnQ=";
        String userSubmittedPassword = "password";

        // WHEN
        HashingFunction strategy = CompressedPBKDF2Function.getInstanceFromHash(hashed);

        // THEN
        Assert.assertTrue(strategy.check(userSubmittedPassword, badHash));
    }


    @Test(expected = BadParametersException.class)
    public void testPBKDF2BadCheck()
    {
        // GIVEN
        String hashed = "$342949672960256$YWJj$/WTQfTTc8Hg8GlplP0LthpgdElUG+I3MyuvK8MI4MnQ=";
        String userSubmittedPassword = "password";

        // WHEN
        CompressedPBKDF2Function.getInstanceFromHash(hashed);


    }

    @Test
    public void testAlgorithmFromCode()
    {
        // GIVEN

        // WHEN
        PBKDF2Function.Algorithm algNull = PBKDF2Function.Algorithm.fromCode(-100);
        for (PBKDF2Function.Algorithm enumAlg : PBKDF2Function.Algorithm.values())
        {
            PBKDF2Function.Algorithm alg = PBKDF2Function.Algorithm.fromCode(enumAlg.code());


            // THEN
            Assert.assertNotNull(alg);
            Assert.assertEquals(enumAlg.code(), alg.code());
            Assert.assertEquals(enumAlg.bits(), alg.bits());
        }
        Assert.assertNull(algNull);


    }

    @Test
    public void testPBKDF2Coherence()
    {
        // GIVEN
        String password = "password";

        // WHEN
        Hash hash = PBKDF2Function.getInstance(PBKDF2Function.Algorithm.SHA256, 8_777, 256).hash(password);

        // THEN
        Assert.assertTrue(hash.check(password));

    }

    @Test
    public void testPBKDF2CheckWithFixedConfigurations()
    {
        // GIVEN
        String hashed = "$3$42949672960256$YWJj$/WTQfTTc8Hg8GlplP0LthpgdElUG+I3MyuvK8MI4MnQ=";
        String userSubmittedPassword = "password";

        // WHEN
        HashingFunction strategy = new CompressedPBKDF2Function(PBKDF2Function.Algorithm.SHA256, 10_000, 256);

        // THEN
        Assert.assertTrue(strategy.check(userSubmittedPassword, hashed));
    }


    @Test
    public void testPBKDF2equality()
    {
        // GIVEN
        PBKDF2Function strategy1 = PBKDF2Function.getInstance(PBKDF2Function.Algorithm.SHA256, 10_000, 256);
        PBKDF2Function strategy2 = PBKDF2Function.getInstance(PBKDF2Function.Algorithm.SHA256, 10_000, 256);
        PBKDF2Function strategy3 = PBKDF2Function.getInstance(PBKDF2Function.Algorithm.SHA1, 10_000, 256);
        PBKDF2Function strategy4 = PBKDF2Function.getInstance(PBKDF2Function.Algorithm.SHA256, 64_000, 256);
        PBKDF2Function strategy5 = PBKDF2Function.getInstance(PBKDF2Function.Algorithm.SHA256, 64_000, 123);


        // WHEN
        Map<PBKDF2Function, String> map = new HashMap<>();
        map.put(strategy1, strategy1.toString());
        map.put(strategy2, strategy2.toString());
        map.put(strategy3, strategy3.toString());
        map.put(strategy4, strategy4.toString());
        map.put(strategy5, strategy5.toString());


        // THEN
        Assert.assertEquals(4, map.size());
        Assert.assertEquals(strategy1, strategy2);
    }

}
