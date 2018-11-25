package com.projectile.models.database.query

import java.time.{LocalDate, LocalDateTime}

import com.projectile.util.DateUtils

object Conversions {
  @SuppressWarnings(Array("MethodReturningAny"))
  def convert(x: AnyRef): AnyRef = x match {
    case num: BigDecimal => num.underlying()
    case num: BigInt => BigDecimal(num).underlying()

    case d: LocalDate => new java.sql.Date(DateUtils.toMillis(d.atStartOfDay))
    case d: LocalDateTime => new java.sql.Date(DateUtils.toMillis(d))

    // Pass everything else through.
    case _ => x
  }
}