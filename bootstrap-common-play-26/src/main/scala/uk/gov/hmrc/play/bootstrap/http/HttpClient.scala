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

package uk.gov.hmrc.play.bootstrap.http

import akka.actor.ActorSystem
import com.typesafe.config.Config
import javax.inject.{Inject, Named, Singleton}
import play.api.Configuration
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.http.ws._

@Singleton
class DefaultHttpClient @Inject()(
  config: Configuration,
  val httpAuditing: HttpAuditing,
  override val wsClient: WSClient,
  override protected val actorSystem: ActorSystem
) extends uk.gov.hmrc.http.HttpClient
     with WSHttp {

  override lazy val configuration: Config = config.underlying

  override val hooks: Seq[HttpHook] = Seq(httpAuditing.AuditingHook)
}

class DefaultHttpAuditing @Inject()(
  val auditConnector: AuditConnector,
  @Named("appName") val appName: String
) extends HttpAuditing
