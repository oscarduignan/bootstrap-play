/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.agent;

import com.typesafe.config.ConfigFactory;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private static Set<String> base64ConfigAllowSet = new HashSet<String>() {{
        add("conf1");
    }};

    private static String BASE64_SUFFIX = ".base64";

    public static void premain(String arguments, Instrumentation instrumentation) {
        Set<String> allBase64SuffixedConfigs = ConfigFactory.load().entrySet().stream()
                .filter(entry -> entry.getKey().endsWith(BASE64_SUFFIX))
                .map(entry -> {
                    String key = entry.getKey();
                    return entry.getKey().substring(0, key.length() - BASE64_SUFFIX.length());
                }).collect(Collectors.toSet());

        allBase64SuffixedConfigs.removeAll(base64ConfigAllowSet);

        if (!allBase64SuffixedConfigs.isEmpty()) {
            throw new IllegalStateException(String.format("Following configuration keys are not allowed to be suffixed with .base64 - [%s]", allBase64SuffixedConfigs));
        }
    }
}
