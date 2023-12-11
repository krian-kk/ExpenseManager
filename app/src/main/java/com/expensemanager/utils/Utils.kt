package com.expensemanager.utils

import java.text.SimpleDateFormat
import java.util.Date


class Utils {
    companion object {
        fun getDate(millis: Long): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy");
            return formatter.format(Date(millis))
        }
    }
}