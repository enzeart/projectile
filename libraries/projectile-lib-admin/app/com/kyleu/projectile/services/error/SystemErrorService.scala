package com.kyleu.projectile.services.error

import java.sql.Connection

import com.kyleu.projectile.models.result.data.DataField
import com.kyleu.projectile.models.result.filter.Filter
import com.kyleu.projectile.models.result.orderBy.OrderBy
import com.kyleu.projectile.services.ModelServiceHelper
import com.kyleu.projectile.services.database.JdbcDatabase
import com.kyleu.projectile.util.{Credentials, CsvUtils}
import com.kyleu.projectile.util.tracing.{TraceData, TracingService}
import java.util.UUID

import com.google.inject.name.Named
import com.kyleu.projectile.models.error.SystemError
import com.kyleu.projectile.models.queries.error.SystemErrorQueries

import scala.concurrent.{ExecutionContext, Future}

@javax.inject.Singleton
class SystemErrorService @javax.inject.Inject() (
    @Named("system") val db: JdbcDatabase,
    override val tracing: TracingService
)(implicit ec: ExecutionContext) extends ModelServiceHelper[SystemError]("systemError", "error" -> "SystemError") {
  def getByPrimaryKey(creds: Credentials, id: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.primary.key")(td => db.queryF(SystemErrorQueries.getByPrimaryKey(id), conn)(td))
  }
  def getByPrimaryKeyRequired(creds: Credentials, id: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = getByPrimaryKey(creds, id).map { opt =>
    opt.getOrElse(throw new IllegalStateException(s"Cannot load systemError with id [$id]"))
  }
  def getByPrimaryKeySeq(creds: Credentials, idSeq: Seq[UUID], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (idSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.primary.key.seq")(td => db.queryF(SystemErrorQueries.getByPrimaryKeySeq(idSeq), conn)(td))
    }
  }

  override def countAll(creds: Credentials, filters: Seq[Filter] = Nil, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.all.count")(td => db.queryF(SystemErrorQueries.countAll(filters), conn)(td))
  }
  override def getAll(creds: Credentials, filters: Seq[Filter] = Nil, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.all")(td => db.queryF(SystemErrorQueries.getAll(filters, orderBys, limit, offset), conn)(td))
  }

  // Search
  override def searchCount(creds: Credentials, q: Option[String], filters: Seq[Filter] = Nil, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("search.count")(td => db.queryF(SystemErrorQueries.searchCount(q, filters), conn)(td))
  }
  override def search(
    creds: Credentials, q: Option[String], filters: Seq[Filter] = Nil, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("search")(td => db.queryF(SystemErrorQueries.search(q, filters, orderBys, limit, offset), conn)(td))
  }

  def searchExact(
    creds: Credentials, q: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None
  )(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("search.exact")(td => db.queryF(SystemErrorQueries.searchExact(q, orderBys, limit, offset), conn)(td))
  }

  def countByCls(creds: Credentials, cls: String, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.cls")(td => db.queryF(SystemErrorQueries.CountByCls(cls), conn)(td))
  }
  def getByCls(creds: Credentials, cls: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.cls")(td => db.queryF(SystemErrorQueries.GetByCls(cls, orderBys, limit, offset), conn)(td))
  }
  def getByClsSeq(creds: Credentials, clsSeq: Seq[String], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (clsSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.cls.seq") { td =>
        db.queryF(SystemErrorQueries.GetByClsSeq(clsSeq), conn)(td)
      }
    }
  }

  def countByContext(creds: Credentials, context: String, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.context")(td => db.queryF(SystemErrorQueries.CountByContext(context), conn)(td))
  }
  def getByContext(creds: Credentials, context: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.context")(td => db.queryF(SystemErrorQueries.GetByContext(context, orderBys, limit, offset), conn)(td))
  }
  def getByContextSeq(creds: Credentials, contextSeq: Seq[String], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (contextSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.context.seq") { td =>
        db.queryF(SystemErrorQueries.GetByContextSeq(contextSeq), conn)(td)
      }
    }
  }

  def countById(creds: Credentials, id: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.id")(td => db.queryF(SystemErrorQueries.CountById(id), conn)(td))
  }
  def getById(creds: Credentials, id: UUID, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.id")(td => db.queryF(SystemErrorQueries.GetById(id, orderBys, limit, offset), conn)(td))
  }
  def getByIdSeq(creds: Credentials, idSeq: Seq[UUID], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (idSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.id.seq") { td =>
        db.queryF(SystemErrorQueries.GetByIdSeq(idSeq), conn)(td)
      }
    }
  }

  def countByMessage(creds: Credentials, message: String, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.message")(td => db.queryF(SystemErrorQueries.CountByMessage(message), conn)(td))
  }
  def getByMessage(creds: Credentials, message: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.message")(td => db.queryF(SystemErrorQueries.GetByMessage(message, orderBys, limit, offset), conn)(td))
  }
  def getByMessageSeq(creds: Credentials, messageSeq: Seq[String], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (messageSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.message.seq") { td =>
        db.queryF(SystemErrorQueries.GetByMessageSeq(messageSeq), conn)(td)
      }
    }
  }

  def countByUserId(creds: Credentials, userId: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("count.by.userId")(td => db.queryF(SystemErrorQueries.CountByUserId(userId), conn)(td))
  }
  def getByUserId(creds: Credentials, userId: UUID, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    traceF("get.by.userId")(td => db.queryF(SystemErrorQueries.GetByUserId(userId, orderBys, limit, offset), conn)(td))
  }
  def getByUserIdSeq(creds: Credentials, userIdSeq: Seq[UUID], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "view") {
    if (userIdSeq.isEmpty) {
      Future.successful(Nil)
    } else {
      traceF("get.by.userId.seq") { td =>
        db.queryF(SystemErrorQueries.GetByUserIdSeq(userIdSeq), conn)(td)
      }
    }
  }

  // Mutations
  def insert(creds: Credentials, model: SystemError, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "edit") {
    traceF("insert")(td => db.executeF(SystemErrorQueries.insert(model), conn)(td).flatMap {
      case 1 => getByPrimaryKey(creds, model.id, conn)(td)
      case _ => throw new IllegalStateException("Unable to find newly-inserted System Error")
    })
  }
  def insertBatch(creds: Credentials, models: Seq[SystemError], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "edit") {
    traceF("insertBatch")(td => if (models.isEmpty) {
      Future.successful(0)
    } else {
      db.executeF(SystemErrorQueries.insertBatch(models), conn)(td)
    })
  }
  def create(creds: Credentials, fields: Seq[DataField], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "edit") {
    traceF("create")(td => db.executeF(SystemErrorQueries.create(fields), conn)(td).flatMap { _ =>
      getByPrimaryKey(creds, UUID.fromString(fieldVal(fields, "id")))
    })
  }

  def remove(creds: Credentials, id: UUID, conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "edit") {
    traceF("remove")(td => getByPrimaryKey(creds, id, conn)(td).flatMap {
      case Some(current) =>
        db.executeF(SystemErrorQueries.removeByPrimaryKey(id), conn)(td).map(_ => current)
      case None => throw new IllegalStateException(s"Cannot find SystemError matching [$id]")
    })
  }

  def update(creds: Credentials, id: UUID, fields: Seq[DataField], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "edit") {
    traceF("update")(td => getByPrimaryKey(creds, id, conn)(td).flatMap {
      case Some(current) if fields.isEmpty => Future.successful(current -> s"No changes required for System Error [$id]")
      case Some(_) => db.executeF(SystemErrorQueries.update(id, fields), conn)(td).flatMap { _ =>
        getByPrimaryKey(creds, fields.find(_.k == "id").flatMap(_.v).map(s => UUID.fromString(s)).getOrElse(id), conn)(td).map {
          case Some(newModel) =>
            newModel -> s"Updated [${fields.size}] fields of System Error [$id]"
          case None => throw new IllegalStateException(s"Cannot find SystemError matching [$id]")
        }
      }
      case None => throw new IllegalStateException(s"Cannot find SystemError matching [$id]")
    })
  }

  def updateBulk(creds: Credentials, pks: Seq[Seq[Any]], fields: Seq[DataField], conn: Option[Connection] = None)(implicit trace: TraceData) = checkPerm(creds, "edit") {
    db.executeF(SystemErrorQueries.updateBulk(pks, fields), conn)(trace).map { x =>
      s"Updated [${fields.size}] fields for [$x of ${pks.size}] System Errors"
    }
  }

  def csvFor(totalCount: Int, rows: Seq[SystemError])(implicit trace: TraceData) = {
    traceB("export.csv")(td => CsvUtils.csvFor(Some(key), totalCount, rows, SystemErrorQueries.fields)(td))
  }
}
