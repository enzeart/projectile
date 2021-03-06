// scalastyle:off file.size.limit
package com.kyleu.projectile.services.note

import java.sql.Connection
import java.time.LocalDateTime
import java.util.UUID

import com.google.inject.name.Named
import com.kyleu.projectile.models.module.ApplicationFeature
import com.kyleu.projectile.models.note.Note
import com.kyleu.projectile.models.queries.note.NoteQueries
import com.kyleu.projectile.models.result.data.DataField
import com.kyleu.projectile.models.result.filter.Filter
import com.kyleu.projectile.models.result.orderBy.OrderBy
import com.kyleu.projectile.services.ModelServiceHelper
import com.kyleu.projectile.services.database.JdbcDatabase
import com.kyleu.projectile.util.{Credentials, CsvUtils}
import com.kyleu.projectile.util.tracing.{TraceData, TracingService}

import scala.concurrent.{ExecutionContext, Future}

@javax.inject.Singleton
class NoteService @javax.inject.Inject() (
    @Named("system") db: JdbcDatabase,
    override val tracing: TracingService
)(implicit ec: ExecutionContext) extends ModelServiceHelper[Note]("note", "models" -> "Note") {

  def getFor(creds: Credentials, model: String, pk: Any*)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (ApplicationFeature.enabled(ApplicationFeature.Note)) {
      tracing.trace("get.by.model")(td => db.queryF(NoteQueries.GetByModel(model, pk.mkString("/")))(td))
    } else {
      Future.successful(Nil)
    }
  }

  def getForSeq(creds: Credentials, models: Seq[(String, String)])(implicit trace: TraceData) = checkPerm(creds, "view") {
    tracing.trace("get.by.model.seq") { td =>
      db.queryF(NoteQueries.GetByModelSeq(models.map(x => x._1 -> x._2.mkString("/"))))(td)
    }
  }

  def getByPrimaryKey(creds: Credentials, id: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = {
    traceF("get.by.primary.key")(td => db.queryF(NoteQueries.getByPrimaryKey(id), conn)(td))
  }
  def getByPrimaryKeyRequired(creds: Credentials, id: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    getByPrimaryKey(creds, id).map(opt => opt.getOrElse(throw new IllegalStateException(s"Cannot load note with id [$id]")))
  }
  def getByPrimaryKeySeq(creds: Credentials, idSeq: Seq[UUID], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (idSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.primary.key.seq")(td => db.queryF(NoteQueries.getByPrimaryKeySeq(idSeq), conn)(td))
    }
  }

  override def countAll(creds: Credentials, filters: Seq[Filter] = Nil, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.all.count")(td => db.queryF(NoteQueries.countAll(filters), conn)(td))
  }
  override def getAll(
    creds: Credentials, filters: Seq[Filter] = Nil, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.all")(td => db.queryF(NoteQueries.getAll(filters, orderBys, limit, offset), conn)(td))
  }

  // Search
  override def searchCount(creds: Credentials, q: Option[String], filters: Seq[Filter] = Nil, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("search.count")(td => db.queryF(NoteQueries.searchCount(q, filters), conn)(td))
  }
  override def search(
    creds: Credentials, q: Option[String], filters: Seq[Filter] = Nil, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("search")(td => db.queryF(NoteQueries.search(q, filters, orderBys, limit, offset), conn)(td))
  }

  def searchExact(
    creds: Credentials, q: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("search.exact")(td => db.queryF(NoteQueries.searchExact(q, orderBys, limit, offset), conn)(td))
  }

  def countByAuthor(creds: Credentials, author: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.author")(td => db.queryF(NoteQueries.CountByAuthor(author), conn)(td))
  }
  def getByAuthor(
    creds: Credentials, author: UUID, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.author")(td => db.queryF(NoteQueries.GetByAuthor(author, orderBys, limit, offset), conn)(td))
  }
  def getByAuthorSeq(creds: Credentials, authorSeq: Seq[UUID], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (authorSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.author.seq") { td =>
        db.queryF(NoteQueries.GetByAuthorSeq(authorSeq), conn)(td)
      }
    }
  }

  def countByCreated(creds: Credentials, created: LocalDateTime, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.created")(td => db.queryF(NoteQueries.CountByCreated(created), conn)(td))
  }
  def getByCreated(
    creds: Credentials, created: LocalDateTime, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.created")(td => db.queryF(NoteQueries.GetByCreated(created, orderBys, limit, offset), conn)(td))
  }
  def getByCreatedSeq(creds: Credentials, createdSeq: Seq[LocalDateTime], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (createdSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.created.seq") { td =>
        db.queryF(NoteQueries.GetByCreatedSeq(createdSeq), conn)(td)
      }
    }
  }

  def countById(creds: Credentials, id: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.id")(td => db.queryF(NoteQueries.CountById(id), conn)(td))
  }
  def getById(
    creds: Credentials, id: UUID, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.id")(td => db.queryF(NoteQueries.GetById(id, orderBys, limit, offset), conn)(td))
  }
  def getByIdSeq(creds: Credentials, idSeq: Seq[UUID], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (idSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.id.seq") { td =>
        db.queryF(NoteQueries.GetByIdSeq(idSeq), conn)(td)
      }
    }
  }

  def countByRelPk(creds: Credentials, relPk: String, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.relPk")(td => db.queryF(NoteQueries.CountByRelPk(relPk), conn)(td))
  }
  def getByRelPk(
    creds: Credentials, relPk: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.relPk")(td => db.queryF(NoteQueries.GetByRelPk(relPk, orderBys, limit, offset), conn)(td))
  }
  def getByRelPkSeq(creds: Credentials, relPkSeq: Seq[String], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (relPkSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.relPk.seq") { td =>
        db.queryF(NoteQueries.GetByRelPkSeq(relPkSeq), conn)(td)
      }
    }
  }

  def countByRelType(creds: Credentials, relType: String, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.relType")(td => db.queryF(NoteQueries.CountByRelType(relType), conn)(td))
  }
  def getByRelType(
    creds: Credentials, relType: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.relType")(td => db.queryF(NoteQueries.GetByRelType(relType, orderBys, limit, offset), conn)(td))
  }
  def getByRelTypeSeq(creds: Credentials, relTypeSeq: Seq[String], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (relTypeSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.relType.seq") { td =>
        db.queryF(NoteQueries.GetByRelTypeSeq(relTypeSeq), conn)(td)
      }
    }
  }

  def countByText(creds: Credentials, text: String, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.text")(td => db.queryF(NoteQueries.CountByText(text), conn)(td))
  }
  def getByText(
    creds: Credentials, text: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.text") { td => db.queryF(NoteQueries.GetByText(text, orderBys, limit, offset), conn)(td) }
  }
  def getByTextSeq(creds: Credentials, textSeq: Seq[String], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (textSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.text.seq") { td =>
        db.queryF(NoteQueries.GetByTextSeq(textSeq), conn)(td)
      }
    }
  }

  // Mutations
  def insert(creds: Credentials, model: Note, conn: Option[Connection] = None)(implicit trace: TraceData) = {
    traceF("insert") { td =>
      db.executeF(NoteQueries.insert(model), conn)(td).flatMap {
        case 1 => getByPrimaryKey(creds, model.id, conn)(td)
        case _ => throw new IllegalStateException("Unable to find newly-inserted Note")
      }
    }
  }
  def insertBatch(creds: Credentials, models: Seq[Note], conn: Option[Connection] = None)(implicit trace: TraceData) = {
    traceF("insertBatch")(td => db.executeF(NoteQueries.insertBatch(models), conn)(td))
  }
  def create(creds: Credentials, fields: Seq[DataField], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "edit") {
    traceF("create") { td =>
      db.executeF(NoteQueries.create(fields), conn)(td).flatMap { _ =>
        getByPrimaryKey(creds, UUID.fromString(fieldVal(fields, "id")))
      }
    }
  }

  def remove(creds: Credentials, id: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "edit") {
    traceF("remove")(td => getByPrimaryKey(creds, id, conn)(td).flatMap {
      case Some(current) =>
        db.executeF(NoteQueries.removeByPrimaryKey(id), conn)(td).map(_ => current)
      case None => throw new IllegalStateException(s"Cannot find Note matching [$id]")
    })
  }

  def update(creds: Credentials, id: UUID, fields: Seq[DataField], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "edit") {
    traceF("update")(td => getByPrimaryKey(creds, id, conn)(td).flatMap {
      case Some(current) if fields.isEmpty => Future.successful(current -> s"No changes required for Note [$id]")
      case Some(_) => db.executeF(NoteQueries.update(id, fields), conn)(td).flatMap { _ =>
        getByPrimaryKey(creds, id, conn)(td).map {
          case Some(newModel) =>
            newModel -> s"Updated [${fields.size}] fields of Note [$id]"
          case None => throw new IllegalStateException(s"Cannot find Note matching [$id]")
        }
      }
      case None => throw new IllegalStateException(s"Cannot find Note matching [$id]")
    })
  }

  def csvFor(totalCount: Int, rows: Seq[Note])(implicit trace: TraceData) = {
    traceB("export.csv")(td => CsvUtils.csvFor(Some(key), totalCount, rows, NoteQueries.fields)(td))
  }
}
