package fi.metatavu.rapurc.api.impl
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.ws.rs.ext.ParamConverter
import javax.ws.rs.ext.ParamConverterProvider
import javax.ws.rs.ext.Provider

/**
 * Provider for parameters of local date type
 */
@Provider
class LocalDateParamConverterProvider : ParamConverterProvider {
    override fun <T> getConverter(
        rawType: Class<T>, genericType: Type?,
        annotations: Array<Annotation?>?
    ): ParamConverter<T>? {
        return if (rawType == LocalDate::class.java) LocalDateConverter() as ParamConverter<T>? else null
    }
}

/**
 * Converter for local date type
 */
class LocalDateConverter : ParamConverter<LocalDate> {
    override fun fromString(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    override fun toString(value: LocalDate): String {
        return value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}