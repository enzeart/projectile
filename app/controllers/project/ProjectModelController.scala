package controllers.project

import controllers.BaseController
import models.database.input.PostgresInput
import models.project.member.ProjectMember
import models.project.member.ProjectMember.InputType
import util.web.ControllerUtils

import scala.concurrent.Future

@javax.inject.Singleton
class ProjectModelController @javax.inject.Inject() () extends BaseController {
  def detail(key: String, model: String) = Action.async { implicit request =>
    val p = projectile.getProject(key)
    val m = p.getModel(model)

    val i = projectile.getInput(m.input)
    val em = i.exportModel(model)

    Future.successful(Ok(views.html.project.member.detailModel(projectile, key, em)))
  }

  def formNew(key: String) = Action.async { implicit request =>
    val p = projectile.getProject(key)
    val inputModels = projectile.listInputs().map { input =>
      input.key -> (projectile.getInput(input.key) match {
        case pi: PostgresInput =>
          val ts = pi.tables.map(m => (m.name, InputType.PostgresTable.value, p.models.exists(x => x.input == pi.key && x.inputKey == m.name)))
          val vs = pi.views.map(v => (v.name, InputType.PostgresView.value, p.models.exists(x => x.input == pi.key && x.inputKey == v.name)))
          ts ++ vs
        case x => throw new IllegalStateException(s"Unhandled input [$x]")
      })
    }
    Future.successful(Ok(views.html.project.member.formNewModel(projectile, key, inputModels)))
  }

  def form(key: String, model: String) = Action.async { implicit request =>
    val p = projectile.getProject(key)
    val m = p.getModel(model)
    Future.successful(Ok(views.html.project.member.formModel(projectile, key, m)))
  }

  def add(key: String, input: String, inputType: String, inputKey: String) = Action.async { implicit request =>
    val p = projectile.getProject(key)
    val modelFeatures = p.features.filter(_.appliesToModel)
    inputKey match {
      case "all" =>
        val i = projectile.getInput(input)
        val toSave = i.exportModels.flatMap {
          case m if p.getModelOpt(m.name).isDefined => None
          case m => Some(ProjectMember(input = input, inputType = m.inputType, inputKey = m.name, outputKey = m.name, features = modelFeatures))
        }
        val saved = projectile.saveProjectMembers(key, toSave)
        val redir = Redirect(controllers.project.routes.ProjectController.detail(key))
        Future.successful(redir.flashing("success" -> s"Saved ${saved.size} models"))
      case _ =>
        val it = ProjectMember.InputType.withValue(inputType)
        val m = ProjectMember(input = input, inputType = it, inputKey = inputKey, outputKey = inputKey, features = modelFeatures)
        projectile.saveProjectMembers(key, Seq(m))
        val redir = Redirect(controllers.project.routes.ProjectModelController.detail(key, m.outputKey))
        Future.successful(redir.flashing("success" -> s"Saved ${m.outputType} [${m.outputKey}]"))
    }
  }

  def save(key: String) = Action.async { implicit request =>
    val form = ControllerUtils.getForm(request.body)
    val m = ProjectMember(
      input = form("input"),
      inputType = ProjectMember.InputType.withValue(form("inputType")),
      inputKey = form("inputKey"),
      outputKey = form("outputKey"),
      ignored = form("ignored").split(',').map(_.trim).filter(_.nonEmpty),
      overrides = Nil
    )
    projectile.saveProjectMembers(key, Seq(m))
    val redir = Redirect(controllers.project.routes.ProjectModelController.detail(key, m.outputKey))
    Future.successful(redir.flashing("success" -> s"Saved ${m.outputType} [${m.outputKey}]"))
  }

  def remove(key: String, member: String) = Action.async { implicit request =>
    projectile.removeProjectMember(key, ProjectMember.OutputType.Model, member)
    Future.successful(Redirect(controllers.project.routes.ProjectController.detail(key)).flashing("success" -> s"Removed model [$member]"))
  }
}