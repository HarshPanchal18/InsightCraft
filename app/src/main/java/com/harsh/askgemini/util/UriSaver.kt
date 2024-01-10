package com.harsh.askgemini.util

import android.net.Uri
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope

// Saves a list of Uris across configuration changes
class UriSaver : Saver<MutableList<Uri>, List<String>> {
    // Saver: describing how the object of Original class can be simplified and converted into something which is Saveable.

    // Convert the restored value back to the original Class. If null is returned the value will not be restored and would be initialized again instead.
    override fun restore(value: List<String>): MutableList<Uri> = value.map {
        Uri.parse(it)
    }.toMutableList()

    // Convert the value into a saveable one. If null is returned the value will not be saved.
    override fun SaverScope.save(value: MutableList<Uri>): List<String> =
        value.map { it.toString() }
}
