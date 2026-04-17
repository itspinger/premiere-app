package com.premiere.util

import java.util.Calendar

actual fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
