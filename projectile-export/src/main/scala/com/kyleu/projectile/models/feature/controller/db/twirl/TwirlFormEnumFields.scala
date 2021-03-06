package com.kyleu.projectile.models.feature.controller.db.twirl

import com.kyleu.projectile.models.export.config.ExportConfiguration
import com.kyleu.projectile.models.export.{ExportEnum, ExportField}
import com.kyleu.projectile.models.output.file.OutputFile

object TwirlFormEnumFields {
  def enumField(config: ExportConfiguration, field: ExportField, enum: ExportEnum, file: OutputFile) = {
    val prop = field.propertyName
    file.add("""<div class="input-field">""", 1)
    file.add(s"""<select id="input-$prop" name="$prop">""", 1)
    if (field.optional) {
      file.add(s"""<option value="@util.NullUtils.str" @if(model.${field.propertyName}.isEmpty) { selected="selected" }>@util.NullUtils.str (null)</option>""")
    }
    file.add(s"""@${enum.modelPackage(config).mkString(".")}.${enum.className}.values.map { v =>""", 1)
    if (field.optional) {
      file.add(s"""<option @if(model.${field.propertyName}.contains(v)) { selected="selected" } value="@v.value">@v.value</option>""")
    } else {
      file.add(s"""<option @if(model.${field.propertyName} == v) { selected="selected" } value="@v.value">@v.value</option>""")
    }
    file.add("}", -1)
    file.add("</select>", -1)
    file.add("</div>", -1)
  }
}
