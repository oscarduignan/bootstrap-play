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
import java.util.concurrent.Callable

import net.bytebuddy.agent.builder.AgentBuilder
import net.bytebuddy.agent.builder.AgentBuilder._
import net.bytebuddy.description.`type`.TypeDescription
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.SuperCall
import net.bytebuddy.matcher.ElementMatchers._
import net.bytebuddy.utility.JavaModule
import play.api.ApplicationLoader.Context
import play.api.inject.guice.GuiceApplicationBuilder

object ApplicationLoaderInterceptor {

  private val base64ConfigAllowSet = Set("conf1")

  def intercept(
    context: Context,
    @SuperCall originalCall: Callable[GuiceApplicationBuilder]
  ): GuiceApplicationBuilder = {

    val notAllowedBase64Config = context.initialConfiguration.keys.collect {
      case k if k.endsWith(".base64") => k.stripSuffix(".base64")
    } -- base64ConfigAllowSet

    if (notAllowedBase64Config.isEmpty) originalCall.call()
    else
      throw new IllegalStateException(
        s"Following configuration keys are not allowed to be suffixed with .base64 - [${notAllowedBase64Config
          .mkString(",")}]"
      )
  }
}

object Main {

  def premain(arguments: String, instrumentation: Instrumentation): Unit = {
    new AgentBuilder.Default()
      .`type`(
        named[TypeDescription]("uk.gov.hmrc.play.bootstrap.ApplicationLoader")
      )
      .transform(new Transformer {
        override def transform(builder: DynamicType.Builder[_],
                               typeDescription: TypeDescription,
                               classLoader: ClassLoader,
                               module: JavaModule): DynamicType.Builder[_] =
          builder
            .method(
              named("builder")
                .and(takesArgument[MethodDescription](0, classOf[Context]))
                .and(returns(classOf[GuiceApplicationBuilder]))
            )
            .intercept(MethodDelegation.to(ApplicationLoaderInterceptor))
      })
      .installOn(instrumentation)
  }
}
