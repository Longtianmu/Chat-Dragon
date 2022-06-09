package net.ltm.ui

import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main(){
    application() {
        Window(
            onCloseRequest = ::exitApplication, title = "Chat-Dragon"
        ) {
            Text("Hello World")
        }
    }
}