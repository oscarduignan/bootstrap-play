/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.play.bootstrap.http.utils

import akka.actor.{ActorSystem, CoordinatedShutdown}
import play.api.libs.json.{JsObject, Writes}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.config.AuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{AuditChannel, AuditConnector, AuditCountScheduler, AuditCounter, AuditResult}
import uk.gov.hmrc.play.audit.model.{DataEvent, ExtendedDataEvent, MergedDataEvent}

import scala.concurrent.{ExecutionContext, Future}

object TestAuditCountScheduler extends AuditCountScheduler {
  override def actorSystem: ActorSystem = ???
  override def coordinatedShutdown: CoordinatedShutdown = ???
  override implicit def ec: ExecutionContext = ???
  override def watch(auditCounter: AuditCounter): Unit = ()
}

class TestAuditConnector extends AuditConnector(null, null, null, TestAuditCountScheduler) {
  override def sendEvent(event: DataEvent)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = Future.successful(AuditResult.Success)

  override def sendExplicitAudit(auditType: String, detail: JsObject)(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit = ()

  override def sendExplicitAudit(auditType: String, detail: Map[String, String])(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit = ()

  override def sendExplicitAudit[T](auditType: String, detail: T)(implicit hc: HeaderCarrier, ec: ExecutionContext, writes: Writes[T]): Unit = ()

  override def sendExtendedEvent(event: ExtendedDataEvent)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = Future.successful(AuditResult.Success)

  override def sendMergedEvent(event: MergedDataEvent)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = Future.successful(AuditResult.Success)

}
