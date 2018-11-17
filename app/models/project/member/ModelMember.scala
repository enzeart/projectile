package models.project.member

import enumeratum.values.{StringCirceEnum, StringEnum, StringEnumEntry}
import models.output.feature.ModelFeature
import util.JsonSerializers._

object ModelMember {
  sealed abstract class InputType(override val value: String) extends StringEnumEntry

  object InputType extends StringEnum[InputType] with StringCirceEnum[InputType] {
    case object PostgresTable extends InputType(value = "postgres-table")
    case object PostgresView extends InputType(value = "postgres-view")

    override val values = findValues
  }

  implicit val jsonEncoder: Encoder[ModelMember] = deriveEncoder
  implicit val jsonDecoder: Decoder[ModelMember] = deriveDecoder
}

case class ModelMember(
    input: String,
    inputType: ModelMember.InputType,

    key: String,
    pkg: Seq[String] = Nil,

    features: Set[ModelFeature] = Set.empty,
    ignored: Set[String] = Set.empty,
    overrides: Seq[MemberOverride] = Nil
) {
  def getOverride(key: String, default: => String) = overrides.find(_.k == key).map(_.v).getOrElse(default)
}