package com.zapplications.calendarview.extensions

import androidx.viewbinding.ViewBinding

fun ViewBinding.getColor(color: Int) = root.resources.getColor(color, null)
