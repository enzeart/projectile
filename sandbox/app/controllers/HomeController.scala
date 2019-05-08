package controllers

import com.kyleu.projectile.controllers.AuthController
import com.kyleu.projectile.models.Application
import com.kyleu.projectile.models.config.NotificationService
import com.kyleu.projectile.services.audit.{AuditHelper, AuditService}
import com.kyleu.projectile.services.database.MigrateTask
import com.kyleu.projectile.util.tracing.TraceData

import scala.concurrent.{ExecutionContext, Future}

@javax.inject.Singleton
class HomeController @javax.inject.Inject() (override val app: Application, aud: AuditService)(implicit ec: ExecutionContext) extends AuthController("home") {
  val projectName = "sandbox"
  MigrateTask.migrate(app.db.source)(TraceData.noop)
  NotificationService.setCallback(f = user => Nil)
  AuditHelper.init(appName = projectName, service = aud)

  def home() = withSession("home") { implicit request => implicit td =>
    Future.successful(Ok(views.html.index(request.identity, app.cfg(Some(request.identity), admin = false), app.config.debug)))
  }

  def sandbox() = withSession("home") { implicit request => implicit td =>
    Future.successful(Ok(views.html.sandbox(request.identity, app.cfg(Some(request.identity), admin = false))))
  }
}