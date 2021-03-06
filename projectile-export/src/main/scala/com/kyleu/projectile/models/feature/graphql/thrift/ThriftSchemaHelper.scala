package com.kyleu.projectile.models.feature.graphql.thrift

import com.kyleu.projectile.models.export.config.ExportConfiguration
import com.kyleu.projectile.models.export.typ.FieldType
import com.kyleu.projectile.models.output.ExportHelper

object ThriftSchemaHelper {
  def graphQlTypeFor(t: FieldType, config: ExportConfiguration, req: Boolean = true): String = t match {
    case _ if !req => s"OptionType(${graphQlTypeFor(t, config)})"
    case FieldType.LongType => "LongType"
    case FieldType.SerialType => "LongType"
    case FieldType.DoubleType => "FloatType"
    case FieldType.FloatType => "FloatType"
    case FieldType.IntegerType => "IntType"
    case FieldType.StringType => "StringType"
    case FieldType.BooleanType => "BooleanType"
    case FieldType.UnitType => "StringType"
    case FieldType.ListType(typ) => s"ListType(${graphQlTypeFor(typ, config)})"
    case FieldType.SetType(typ) => s"ListType(${graphQlTypeFor(typ, config)})"
    case FieldType.MapType(_, _) => "StringType"
    case FieldType.EnumType(key) => config.getEnum(key, "graphql type").propertyName + "EnumType"
    case FieldType.StructType(key, _) => config.getModel(key, "graphql type").propertyName + "Type"
    case x => ExportHelper.toIdentifier(x.value) + "Type"
  }
}
