package net.westaystay

sealed trait Error

case class ValidationError(message: String) extends Error

case class ConfigurationError(message: String = "defaultTarget is not set in config") extends Error