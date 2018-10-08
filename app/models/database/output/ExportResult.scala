package models.database.output

import models.database.output.config.ExportConfiguration
import models.output.file.OutputFile

case class ExportResult(
    config: ExportConfiguration,
    models: Seq[ExportModel],
    views: Seq[ExportModel],
    enumFiles: Seq[OutputFile],
    sourceFiles: Seq[OutputFile],
    rootFiles: Seq[OutputFile],
    docFiles: Seq[OutputFile]
) {
  private[this] val startTime = System.currentTimeMillis
  private[this] val logs = collection.mutable.ArrayBuffer.empty[(Int, String)]

  def getTable(id: String) = models.find(t => t.propertyName == id || t.tableName == id)
  def getView(id: String) = views.find(t => t.propertyName == id || t.tableName == id)

  def log(msg: String) = logs += ((System.currentTimeMillis - startTime).toInt -> msg)
  val getLogs: Seq[(Int, String)] = logs

  def getMarkers(key: String) = sourceFiles.flatMap(_.markersFor(key)).distinct

  val fileCount = enumFiles.size + sourceFiles.size + rootFiles.size + docFiles.size
  lazy val files = Seq(enumFiles, sourceFiles, rootFiles, docFiles).flatten.distinct
  lazy val fileSizes = files.map(_.rendered.length).sum
}