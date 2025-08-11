package com.goanderco.simplecharts.core.viewmodel

import com.goanderco.R

enum class FittingMethod(val method: String, val stringResID: Int) {
    LM("lm", R.string.linear_model),
    LOESS("loess", R.string.loess),
    LOWESS("lowess",R.string.lowess)

}