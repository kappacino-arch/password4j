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

import org.apache.commons.lang3.StringUtils;

public class HashChecker<C extends HashChecker<?>>
{
    private String hash;

    private String salt;

    private String pepper;

    private String plain;

    private HashChecker()
    {
        //
    }

    public HashChecker(String plain, String hash)
    {
        this.hash = hash;
        this.plain = plain;
    }

    public C addPepper(String pepper)
    {
        this.pepper = pepper;
        return (C) this;
    }

    public C addPepper()
    {
        this.pepper = PepperGenerator.get();
        return (C) this;
    }

    public C addSalt(String salt)
    {
        this.salt = salt;
        return (C) this;
    }

    public boolean with(HashingFunction hashingFunction)
    {
        if (plain == null)
        {
            return false;
        }

        String peppered = plain;
        if (StringUtils.isNotEmpty(this.pepper))
        {
            peppered = this.pepper + peppered;
        }

        return hashingFunction.check(peppered, hash, salt);
    }

    public boolean withPBKDF2()
    {
        PBKDF2Function pbkdf2 = AlgorithmFinder.getPBKDF2Instance();
        return with(pbkdf2);
    }

    public boolean withCompressedPBKDF2()
    {
        PBKDF2Function pbkdf2 = AlgorithmFinder.getCompressedPBKDF2Instance();
        return with(pbkdf2);
    }

    public boolean withSCrypt()
    {
        SCryptFunction scrypt = AlgorithmFinder.getSCryptInstance();
        return with(scrypt);
    }

    public boolean withBCrypt()
    {
        return with(AlgorithmFinder.getBCryptInstance());
    }


}
