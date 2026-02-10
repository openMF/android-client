package template.core.base.database

import kotlin.reflect.KClass

@Target(allowedTargets = [])
@Retention(AnnotationRetention.BINARY)
actual annotation class ForeignKey(
    actual val entity: KClass<*>,
    actual val parentColumns: Array<String>,
    actual val childColumns: Array<String>,
    actual val onDelete: Int,
    actual val onUpdate: Int,
    actual val deferred: Boolean
)

@Target(allowedTargets = [])
@Retention(AnnotationRetention.BINARY)
actual annotation class Index

@Target(allowedTargets = [])
@Retention(AnnotationRetention.BINARY)
actual annotation class Junction(
    actual val value: KClass<*>,
    actual val parentColumn: String,
    actual val entityColumn: String,
)

@Target(allowedTargets = [])
@Retention(AnnotationRetention.BINARY)
actual annotation class AutoMigration(
    actual val from: Int,
    actual val to: Int,
    actual val spec: KClass<*>,
)