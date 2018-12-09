package com.projectile.models.feature.slick

import com.projectile.models.export.ExportModel
import com.projectile.models.export.config.ExportConfiguration
import com.projectile.models.export.typ.FieldType.EnumType
import com.projectile.models.feature.ModelFeature
import com.projectile.models.output.OutputPath
import com.projectile.models.output.file.ScalaFile

object TableFile {
  def export(config: ExportConfiguration, model: ExportModel) = {
    val path = if (model.features(ModelFeature.Shared)) { OutputPath.SharedSource } else { OutputPath.ServerSource }
    val file = ScalaFile(path = path, dir = config.applicationPackage ++ model.slickPackage, key = model.className + "Table")

    config.addCommonImport(file, "SlickQueryService", "imports", "_")

    model.fields.foreach(_.t match {
      case EnumType(key) =>
        val e = config.getEnum(key, "table file")
        file.addImport(config.applicationPackage ++ e.slickPackage :+ s"${e.className}ColumnType", s"${e.propertyName}ColumnType")
      case _ => // noop
    })

    file.add(s"object ${model.className}Table {", 1)
    file.add(s"val query = TableQuery[${model.className}Table]")
    TableHelper.addQueries(config, file, model)
    TableHelper.addReferences(config, file, model)
    file.add("}", -1)
    file.add()

    file.addImport(config.applicationPackage ++ model.modelPackage, model.className)

    file.add(s"""class ${model.className}Table(tag: slick.lifted.Tag) extends Table[${model.className}](tag, "${model.key}") {""", 1)

    TableHelper.addFields(config, model, file)
    file.add()

    if (model.pkFields.size > 1) {
      val pkProps = model.pkFields match {
        case h :: Nil => h.propertyName
        case x => "(" + x.map(_.propertyName).mkString(", ") + ")"
      }
      file.add(s"""val modelPrimaryKey = primaryKey("pk_${model.key}", $pkProps)""")
      file.add()
    }
    if (model.fields.lengthCompare(22) > 0) {
      file.addImport(Seq("shapeless"), "HNil")
      file.addImport(Seq("shapeless"), "Generic")
      file.addImport(Seq("slickless"), "_")

      val fieldStr = model.fields.map(_.propertyName).mkString(" :: ")
      file.addImport(config.applicationPackage ++ model.modelPackage, model.className)
      file.add(s"override val * = ($fieldStr :: HNil).mappedWith(Generic[${model.className}])")
    } else {
      val propSeq = model.fields.map(_.propertyName).mkString(", ")
      file.add(s"override val * = ($propSeq) <> (", 1)
      file.add(s"(${model.className}.apply _).tupled,")
      file.add(s"${model.className}.unapply")
      file.add(")", -1)
    }

    file.add("}", -1)
    file.add()
    file
  }
}
