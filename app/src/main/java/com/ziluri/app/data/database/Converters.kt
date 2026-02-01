package com.ziluri.app.data.database

import androidx.room.TypeConverter
import com.ziluri.app.data.model.LoginType
import com.ziluri.app.data.model.MemoCheckItem
import com.ziluri.app.data.model.Priority
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromLoginType(loginType: LoginType): String = loginType.name

    @TypeConverter
    fun toLoginType(value: String): LoginType = LoginType.valueOf(value)

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return JSONArray(list).toString()
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isEmpty()) return emptyList()
        val jsonArray = JSONArray(value)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }

    @TypeConverter
    fun fromMemoCheckItems(items: List<MemoCheckItem>): String {
        val jsonArray = JSONArray()
        items.forEach { item ->
            val jsonObject = JSONObject().apply {
                put("id", item.id)
                put("content", item.content)
                put("isChecked", item.isChecked)
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toMemoCheckItems(value: String): List<MemoCheckItem> {
        if (value.isEmpty()) return emptyList()
        val jsonArray = JSONArray(value)
        val list = mutableListOf<MemoCheckItem>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            list.add(MemoCheckItem(
                id = jsonObject.getString("id"),
                content = jsonObject.getString("content"),
                isChecked = jsonObject.getBoolean("isChecked")
            ))
        }
        return list
    }
}
