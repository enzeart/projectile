package com.kyleu.projectile.services.database

import scala.concurrent.ExecutionContext.Implicits.global
import com.kyleu.projectile.models.database.{DatabaseConfig, RawQuery, Statement}
import com.kyleu.projectile.util.Logging
import com.kyleu.projectile.util.tracing.{TraceData, TracingService}

import scala.concurrent.Future

trait Database[Conn] extends Logging {
  protected[this] def key: String

  def transaction[A](f: (TraceData, Conn) => A)(implicit traceData: TraceData): A

  def execute(statement: Statement, conn: Option[Conn] = None)(implicit traceData: TraceData): Int
  def executeF(statement: Statement, conn: Option[Conn] = None)(implicit traceData: TraceData): Future[Int] = Future(execute(statement, conn))
  def query[A](q: RawQuery[A], conn: Option[Conn] = None)(implicit traceData: TraceData): A
  def queryF[A](q: RawQuery[A], conn: Option[Conn] = None)(implicit traceData: TraceData): Future[A] = Future(query(q, conn))

  def close(): Boolean

  private[this] var tracingServiceOpt: Option[TracingService] = None
  protected def tracing = tracingServiceOpt.getOrElse {
    throw new IllegalStateException("Tracing service not configured. Did you forget to call \"open\"?")
  }

  private[this] var config: Option[DatabaseConfig] = None
  def getConfig = config.getOrElse(throw new IllegalStateException("Database not open"))

  protected[this] def start(cfg: DatabaseConfig, svc: TracingService) = {
    tracingServiceOpt = Some(svc)
    config = Some(cfg)
  }

  protected[this] def prependComment(obj: Object, sql: String) = s"/* ${obj.getClass.getSimpleName.replace("$", "")} */ $sql"

  protected[this] def trace[A](traceName: String)(f: TraceData => A)(implicit traceData: TraceData) = tracing.traceBlocking(key + "." + traceName) { td =>
    f(td)
  }
}
