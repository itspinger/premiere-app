package com.premiere.util

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitYear

actual fun currentYear(): Int =
    NSCalendar.currentCalendar.component(NSCalendarUnitYear, fromDate = platform.Foundation.NSDate())
        .toInt()
