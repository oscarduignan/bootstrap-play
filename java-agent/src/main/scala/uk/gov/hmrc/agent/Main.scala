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

package uk.gov.hmrc.agent

import java.lang.instrument.Instrumentation

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

object Main {

  private val base64ConfigAllowSet = Set("conf1")

  def premain(arguments: String, instrumentation: Instrumentation): Unit = {
    val notAllowedBase64Config = ConfigFactory.load().entrySet().asScala.collect {
      case e if e.getKey.endsWith(".base64") => e.getKey.stripSuffix(".base64")
    } -- base64ConfigAllowSet

    if (notAllowedBase64Config.nonEmpty) {
      throw new IllegalStateException(
        s"Following configuration keys are not allowed to be suffixed with .base64 - [${notAllowedBase64Config
          .mkString(",")}]"
      )
    }
  }
}
