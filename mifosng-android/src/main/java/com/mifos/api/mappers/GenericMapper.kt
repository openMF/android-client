package com.mifos.api.mappers

import android.util.Log
import com.google.gson.Gson
import com.mifos.api.mappers.accounts.savings.SavingsProductTemplateMapper

object GenericMapper {

    val gson = Gson()

    inline fun <IN, reified OUT> convert(entity: IN): OUT {
        val json = gson.toJson(entity)
        Log.i("convert", json)
        return gson.fromJson(json, OUT::class.java)
    }

    inline fun <IN, reified OUT> convert(entity: List<IN>): List<OUT> {
        return entity.map { convert<IN, OUT>(it) }
    }

    inline fun <reified OUT> convert(json: String): OUT {
        return gson.fromJson(json, OUT::class.java)
    }
}