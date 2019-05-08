package com.kyleu.projectile.controllers

import javax.inject._
import play.api.http.HttpErrorHandler

object Assets {
  def path(p: String) = com.kyleu.projectile.controllers.routes.Assets.versioned(p).url
}

class Assets @Inject() (eh: HttpErrorHandler, md: controllers.AssetsMetadata) extends controllers.AssetsBuilder(eh, md)