package com.projectile.models.graphql.parse

import com.projectile.models.export.ExportField
import com.projectile.models.output.ExportHelper
import sangria.ast._
import sangria.schema.Schema

object GraphQLFieldParser {
  def getField(ctx: String, schema: Schema[_, _], doc: Document, name: String, t: Type, idx: Int, defaultValue: Option[Value]) = {
    val (required, newT) = GraphQLTypeParser.getType(s"$ctx($name: ${t.renderCompact})", schema, doc, t)
    ExportField(
      key = name,
      propertyName = ExportHelper.toIdentifier(name),
      title = ExportHelper.toDefaultTitle(name),
      description = None,
      idx = idx,
      t = newT,
      defaultValue = defaultValue.map(_.toString),
      required = required
    )
  }
}
